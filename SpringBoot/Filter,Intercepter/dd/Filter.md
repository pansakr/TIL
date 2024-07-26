### Filter

* 서블릿이 지원하는 공통 관심사를 처리하는데 사용하는 인터페이스

* 필터를 적용하면 필터가 호출된 후 서블릿이 호출된다.

* 필터는 체인으로 구성되는데 중간에 필터를 추가할 수 있다.

```
요청 -> was -> 필터1 -> 필터2 -> 서블릿 -> 컨트롤러
```
* 필터 인터페이스

```java
public interface Filter {

  public default void init(FilterConfig filterConfig) throws ServletException{}

  public void doFilter(ServletRequest request, ServletResponse response,FilterChain chain) throws IOException, ServletException
  
  public default void destroy() {}
}

// 필터 인터페이스를 구현하고 등록하면 서블릿 컨테이너가 필터를 싱글톤 객체로 생성하고, 관리한다.
// init(): 필터 초기화 메서드, 서블릿 컨테이너가 생성될 때 호출된다.
// doFilter(): 고객의 요청이 올 때 마다 해당 메서드가 호출된다. 필터의 로직을 구현하면 된다.
// destroy(): 필터 종료 메서드, 서블릿 컨테이너가 종료될 때 호출된다.
```

### Filter 사용방법

* 로그 필터

```java
@Slf4j
public class LogFilter implements Filter { // 필터를 사용하기 위해 필터 인터페이스 구현

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  log.info("log filter init");
  }

  // HTTP 요청이 오면 doFilter 가 호출됨
  // ServletRequest request 는 HTTP 요청이 아닌 경우까지 고려해서 만든 인터페이스다
  // HTTP를 사용하면 아래와 같이 다운 케스팅해 사용
  @Override
  public void doFilter(ServletRequest request, ServletResponse response,
                            FilterChain chain) throws IOException, ServletException {

      HttpServletRequest httpRequest = (HttpServletRequest) request;

      String requestURI = httpRequest.getRequestURI();

      // HTTP 요청을 구분하기 위해 요청당 uuid 생성
      String uuid = UUID.randomUUID().toString();

      try {
        log.info("REQUEST [{}][{}]", uuid, requestURI);

        // 다음 필터가 있으면 필터를 호출하고, 필터가 없으면 서블릿을 호출
        // 이 로직을 호출하지 않으면 다음 단계로 진행되지 않아 서블릿이 호출되지 않는다
        chain.doFilter(request, response);
        } catch (Exception e) {
          throw e;
        } finally {
          log.info("RESPONSE [{}][{}]", uuid, requestURI);
        }
  }

  @Override
  public void destroy() {
    log.info("log filter destroy");
  }
}
```
```java
// 스프링 부트 사용시 FilterRegistrationBean 를 사용해서 필터 등록
@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean logFilter(){
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        // 등록할 필터를 지정
        filterRegistrationBean.setFilter(new LogFilter());
        // 필터는 체인으로 동작하기 때문에 순서가 필요하다. 낮을 수록 먼저 동작
        filterRegistrationBean.setOrder(1);
        // 필터를 적용할 url 패턴. 여러 패턴을 지정할 수 있다
        filterRegistrationBean.addUrlPatterns("/*");

        return filterRegistrationBean;
    }
}
```

* 로그인 필터

```java
@Slf4j
public class LoginCheckFilter implements Filter {

    // 화이트 리스트에 해당하는 URL은 인증 체크X
    private static final String[] whitelist = {"/", "/members/add", "/login", "/logout", "/css/*"};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            log.info("인증 체크 필터 시작{}", requestURI);

            if(isLoginCheckPath(requestURI)){

                log.info("인증 체크 로직 실행{}", requestURI);

                HttpSession session = httpRequest.getSession(false);

                if(session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null){

                    log.info("미인증 사용자 요청 {}", requestURI);

                    // 로그인으로 redirect
                    // redirect할때 사용자가 요청했던 url을 붙여준다
                    // 어떤 문제로 url에 접근하지 못했을때(여기선 로그인) 문제를 해결하면 자동으로 해당 url로 이동되게 하기 위함  
                    httpResponse.sendRedirect("/login?redirectURL=" + requestURI);
                    return;
                }
            }
            chain.doFilter(request, response);

        } catch (Exception e){
            // 예외 로깅 가능 하지만, 톰캣까지 예외를 보내주어야 함
            throw e;
        } finally {
            log.info("인증 체크 필터 종료{}", requestURI);
        }
    }

    /**
     * 화이트 리스트의 경우 인증 체크X
     */
    private boolean isLoginCheckPath(String requestURI){
        return !PatternMatchUtils.simpleMatch(whitelist, requestURI);
    }
}
```
```java
// 필터 등록
@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean logCheckFilter(){
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LoginCheckFilter());
        filterRegistrationBean.setOrder(2);
        filterRegistrationBean.addUrlPatterns("/*");

        return filterRegistrationBean;
    }
}
```
```java
@Controller
public class LoginController{

    // filter에서 로그인 하지 않은 사용자가 인증이 필요한 페이지에 접근시 사용자가 요청한 url을 포함시켜 get: /login으로 리다이렉트 시키고,
    // 리다이렉트 된 로그인 페이지에서 로그인 시 로그인 정보와, 사용자가 요청한 url이 post: /login으로 넘어온다 
    @PostMapping("/login")
    public String loginV4(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult,
                          @RequestParam(defaultValue = "/") String redirectURL, // defaultValue - 사용자가 요청한 url이 없을때 기본값 설정
                          HttpServletRequest request){
        // 검증..

        // 로그인 처리..

        // 사용자가 요청한 url로 리다이렉트
        // 로그인 성공했으니 사용자가 접근하지 못했던 페이지로 보내준다
        return "redirect:" + redirectURL;
    }
}
```
