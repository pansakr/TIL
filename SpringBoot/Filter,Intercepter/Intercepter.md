### Intercepter

* 스프링 mvc가 제공하는 공통 관심사를 처리하는데 사용하는 인터페이스

* 인터셉터에서 적절하지 않은 요청이라고 판단하면 컨트롤러를 호출하지 않고 바로 요청을 끝낼 수 있다

```
// 스프링 인터셉터 흐름
요청 -> WAS -> 필터 -> 서블릿 -> 스프링 인터셉터 -> 컨트롤러

// 스프링 인터셉터 체인
요청 -> WAS -> 필터 -> 서블릿 -> 스프링 인터셉터1 -> 인터셉터2 -> 컨트롤러 
```

* 스프링 인터셉터 인터페이스

```java
public interface HandlerInterceptor {

    // 컨트롤러 호출 전 호출
    default boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                              Object handler) throws Exception {}

    // 컨트롤러 호출 후 호출
    default void postHandle(HttpServletRequest request, HttpServletResponse response,
                            Object handler, @Nullable ModelAndView modelAndView) throws Exception {}

    // 뷰 렌더링 된 이후 호출
    default void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, @Nullable Exception ex) throws Exception {}

}
```

### Interceptor 사용 방법

```java
// log 기록용 인터셉터
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {  // HandlerInterceptor 구현

    public static final String LOG_ID = "logId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        String uuid = UUID.randomUUID().toString();

        // 인터셉터는 호출 시점이 완전히 분리되어 있어 preHandle 에서 지정한 값을 postHandle, afterCompletion 사용하려면 어딘가에 담아두어야 한다.
        // LogInterceptor 도 싱글톤 처럼 사용되기 때문에 맴버변수를 사용하면 위험하다.
        // 따라서 request 에 담아두었다. 이 값은 afterCompletion 에서 request.getAttribute(LOG_ID) 로 찾아서 사용한다
        request.setAttribute(LOG_ID, uuid);


        if(handler instanceof HandlerMethod){

            //호출할 컨트롤러 메서드의 모든 정보가 포함되어 있다.
            HandlerMethod hm = (HandlerMethod) handler;
        }

        log.info("REQUEST [{}][{}][{}]", uuid, requestURI, handler);

        // true 면 정상 호출. 다음 인터셉터나 컨트롤러가 호출된다.
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHandle [{}]", modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        String requestURI = request.getRequestURI();
        String logId = (String) request.getAttribute(LOG_ID);
        log.info("RESPONSE [{}][{}][{}]", logId, requestURI, handler);

        if (ex != null){
            log.error("afterCompletion error!!", ex);
        }
    }
}
```
```java
// 인터셉터 등록
@Configuration
public class WebConfig implements WebMvcConfigurer { // WebMvcConfigurer 구현

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 인터셉터 등록
        registry.addInterceptor(new LoginInterceptor())
                // 호출 순서 지정. 낮을수록 먼저 호출
                .order(1)
                // 인터셉터를 적용할 URL 패턴 지정
                .addPathPatterns("/**")
                // 인터셉터에서 제외할 패턴 지정
                .excludePathPatterns("/css/**", "/*ico", "/error");
    }
```

###  Interceptor로 인증 체크

```java
@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    // 인증은 컨트롤러 전에만 호출하면 되기 때문에 preHandle만 구현
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();

        log.info("인증 체크 인터셉터 실행", requestURI);

        HttpSession session = request.getSession();

        if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null){
            log.info("미인증 사용자 요청");

            response.sendRedirect("/login?redirectURL=" + requestURI);
            return false;
        }
        return true;
    }
}
```
```
// 로그인 인터셉터 등록
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new LoginCheckInterceptor())
                .order(2)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/members/add", "/login", "/logout", "/css/**", "/*.ico", "/error");
    }
```
