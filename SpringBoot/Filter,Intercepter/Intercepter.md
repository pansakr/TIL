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
