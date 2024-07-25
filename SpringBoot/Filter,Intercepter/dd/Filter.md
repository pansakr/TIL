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
