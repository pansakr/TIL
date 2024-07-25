### 세션 로그인

* [세션이란?](https://github.com/pansakr/TIL/blob/main/SpringBoot/%EC%8A%A4%ED%94%84%EB%A7%81%20%EA%B8%B0%EB%B3%B8%EA%B0%9C%EB%85%90/%EC%84%B8%EC%85%98%2C%20%EC%BF%A0%ED%82%A4.md)


#### 사용자 정의 세션 

```java
// 스프링이 지원하는 세션을 사용하지 않고 임의로 만듬
// 세션 생성, 조회, 만료 이해
@Component
public class SessionManager {

    public static final String SESSIONID_COOKIE_NAME = "mySessionId";
    private Map<String, Object> sessionStore = new ConcurrentHashMap<>();

    /**
     * 세션 생성
     * sessionId 생성 (임의의 추정 불가능한 랜덤 값)
     * 세션 저장소에 sessionId와 보관할 값 저장
     * sessionId로 응답 쿠키를 생성해서 클라이언트에 전달
     */
    public void createSession(Object value, HttpServletResponse response){

        // 세션 id를 생성하고, 값을 세션에 저장
        String sessionId = UUID.randomUUID().toString();
        sessionStore.put(sessionId, value);

        // 쿠키 생성
        Cookie mySessionCookie = new Cookie(SESSIONID_COOKIE_NAME, sessionId);
        response.addCookie(mySessionCookie);
    }

    /**
     * 세션 조회
     */
    public Object getSession(HttpServletRequest request){
        Cookie sessionCookie = findCookie(request, SESSIONID_COOKIE_NAME);
        if (sessionCookie == null){
            return null;
        }
        return sessionStore.get(sessionCookie.getValue());
    }

    /**
     * 세션 만료
     */
    public void expire(HttpServletRequest request){
        Cookie sessionCookie = findCookie(request, SESSIONID_COOKIE_NAME);
        if (sessionCookie != null){
            sessionStore.remove(sessionCookie.getValue());
        }
    }

    public Cookie findCookie(HttpServletRequest request, String cookieName){
        if(request.getCookies() == null){
            return null;
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findAny()
                .orElse(null);
    }

}
```
```java
// 테스트
public class SessionManagerTest {

    SessionManager sessionManager = new SessionManager();

    @Test
    void sessionTest(){

        // MockHttpServletResponse - 테스트를 위한 가짜 응답 객체
        MockHttpServletResponse response = new MockHttpServletResponse();

        // 세션 생성
        Member member = new Member();
        sessionManager.createSession(member, response);

        // 요청에 응답 쿠키가 저장되었는지 확인
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(response.getCookies());

        // 세션 조회
        Object result = sessionManager.getSession(request);
        assertThat(result).isEqualTo(member);

        // 세션 만료
        sessionManager.expire(request);
        Object expired = sessionManager.getSession(request);
        assertThat(expired).isNull();
    }
}
```

### 서블릿이 제공하는 세션

* 서블릿을 통해 Http 세션을 생성하면 이름이 'JESSIONID', 값은 추정 불가능한 랜덤 값을 가진 쿠키를 생성한다

```java
@Controller
..class{
    @PostMapping("/login")
    public String loginV3(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletRequest request){

        // 검증
        if(bindingResult.hasErrors()){
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        if(loginMember == null){
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다");
            return "login/loginForm";
        }

        // 로그인 성공 처리
        // getSEssion(true) - 세션이 있으면 있는 세션 반환, 없으면 신규 세션을 생성. 기본값
        // getSEssion(true) - 세션이 있으면 있는 세션 반환, 없으면 null 반환
        HttpSession session = request.getSession();

        // 세션에 로그인 회원 정보 보관
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        return "redirect:/";
    }
}

    @PostMapping("/logout")
    public String logoutV3(HttpServletRequest request){

        // 로그인중인 유저의 세션을 삭제할 거니까 getSession(false)을 사용함.
        // getSession(true)를 사용하면 세션이 없으면 만들기 때문에 적합하지 않음
        HttpSession session = request.getSession(false);

        if (session != null){
            // 세션 삭제
            session.invalidate();
        }
        return "redirect:/";
    }
```

```java
@Controller
..HomeController{

    @GetMapping("/")
    public String homeLoginV3(HttpServletRequest request, Model model){

        // 세션이 있다면 회원이니 세션 정보를 가져와 회원 페이지를 보여주고, 없다면 비회원이니 일반 화면을 보여준다
        HttpSession session = request.getSession(false);

        if(session == null){
            return "home";
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        // 세션에 회원 데이터가 없으면 home
        if(loginMember == null){
            return "home";
        }

        // 세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);
        return "loginHome";
    }
}
```

#### 스프링이 제공하는 어노테이션으로 세션 가져오기

* @SessionAttribute 사용

```java
    //  @SessionAttribute가 세션이 있다면 name 속성에 명시된 값으로 세션 정보를 찾아와 logonMember에 넣어준다 
    //  @SessionAttribute는 세션을 새로 생성하지는 않는다
    @GetMapping("/")
    public String homeLoginV3Spring(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember, Model model){

        // 세션에 회원 데이터가 없으면 home
        if(loginMember == null){
            return "home";
        }

        // 세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);
        return "loginHome";
    }
```

### TrackingModes

* 로그인을 처음 시도하면 url에 jsessionId가 표시되는데, 이것은 웹 브라우저가 쿠키를 지원하지 않을 때 쿠키 대신 URL을 통해서 세션을 유지하는 방법이다

* 이 방법을 사용하려면 URL에 이 값을 계속 포함해서 전달해야 한다

* 서버 입장에서 웹 브라우저가 쿠키를 지원하는지 하지 않는지 최초에는 판단하지 못하므로, 쿠키 값도 전달하고, URL에 `jsessionid` 도 함께 전달한다

* 거의 사용되지 않는 방법이다

* URL 전달 방식을 끄고 항상 쿠키를 통해서만 세션을 유지하고 싶으면 아래 옵션을 사용하면 된다

```
// application.properties
server.servlet.session.tracking-modes=cookie
```

### 세션 정보

```java

    @GetMapping("/session-info")
    public String sessionInfo(HttpServletRequest request){

        HttpSession session = request.getSession(false);
        if (session == null) {
            return "세션이 없습니다";
        }

        // 세션 데이터 출력
        session.getAttributeNames().asIterator()
                .forEachRemaining(name -> log.info("session name={}, value={}", name, session.getAttribute(name)));

        // 세션 id. jsessionId의 값
        log.info("sessionId={}", session.getId());

        // 세션의 유효 시간
        log.info("maxInactiveInterval={}", session.getMaxInactiveInterval());

        // 세션 생성일시
        log.info("creationTime={}", new Date(session.getCreationTime()));

        // 세션과 연결된 사용자가 최근에 서버에 접근한 시간. 클라이언트에서 서버로 sessionId를 요청한 경우 갱신
        log.info("lastAccessedTime={}", new Date(session.getLastAccessedTime()));

        // 새로 생성된 세션인지, 아니면 이미 만들어져 있던 세션을 조회한 것인지 여부
        log.info("isNew={}", session.isNew());

        return "세션 출력";
    }
```

### 세션 타임아웃 설정

* 세션은 사용자가 로그아웃을 눌러 session.invalidate()가 호출되는 경우 삭제된다

* 그런데 대부분의 사용자는 로그아웃을 하지 않고 그냥 웹브라우저를 종료한다. 문제는 http는 비연결성 이므로 서버 입장에서는 사용자가 웹브라우저를 종료한것인지 알 수 없다

* 따라서 서버에서 세션 데이터를 언제 삭제해야 하는지 판단하기 어렵다

* 그렇다고 세션을 무한정 보관한다면 세션은 기본적으로 메모리에 생성되기 때문에 메모리가 부족해질 수 있다

* 알맞은 대안은 사용자가 서버에 최근에 요청한 시간을 기준으로 30분 정도를 유지해주는 것이다

* 이렇게 하면 사용자가 서비스를 사용하고 있으면(서비스 사용중 요청을 계속 보내기 때문에) 세션의 생존 시간이 30분으로 계속 늘어나게 된다

```java
// application.properties
// 글로벌 설정은 분단위로 해야한다 (60 - 1분, 120 - 2분)
server.servlet.session.timeout=60

// 클래스에서 설정시
session.setMaxInactiveInterval(1800); //1800초
```

* 세션의 타임아웃 시간은 해당 세션과 관련된 jsessionId를 전달하는 http요청이 있으면 현재 시간으로 다시 초기화 되며 세션 타임아웃으로 설정한 시간동안 세션을 추가로 사용할 수 있다

* 주의할 점은 세션에는 최소한의 데이터만 보관해야 한다.

* 보관한 데이터 용량 * 사용자 수로 세션의 메모리 사용량이 늘어나서 장애로 이어질 수 있고, 추가로 세션의 시간을 길게 가져가면 메모리 사용이 계속 누적 될 수
있으므로 적당한 시간을 선택하는 것이 필요하다. 30분을 기준으로 고민하면 된다.
