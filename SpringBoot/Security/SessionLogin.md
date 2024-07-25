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

#### 스프링이 제공하는 어노테이션으로 세션 처리

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
