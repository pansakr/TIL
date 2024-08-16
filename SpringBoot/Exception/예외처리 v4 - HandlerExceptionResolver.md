### HandlerExceptionResolver

* 컨트롤러에서 발생한 예외를 처리해주는 인터페이스

* HandlerExceptionResolver를 구현한 클래스를 등록하면 예외 발생 시 해당 클래스가 예외를 처리 후 DispatcherServlet 으로 응답한다

* 응답값에는 빈 ModelAndView, ModelAndView 지정, null 이 있다

* 빈 ModelAndView - 뷰를 렌더링 하지 않고, 정상 흐름으로 was에 서블릿 리턴. 이해 어려움 아래 코드 참조

* ModelAndView 지정 - 지정된 model의 정보로 view 렌더링

* null - 다음 ExceptionResolver 실행. 예외처리 모두 실패시 기존에 발생한 예외를 서블릿 밖으로 던져서 was 기본값으로 클라이언트에게 응답됨

```
// ExceptionResolver 적용시 예외 흐름
1. 컨트롤러(예외 발생) -> DispatcherServlet이 ExceptionResolver 호출
2. ExceptionResolver 에서 DispatcherServlet 로 응답
3. 응답값에 따라 실행
```

```java
// 클라이언트가 api/members/bad 로 요청시 IllegalArgumentException 발생
@Controller
public class ApiExceptionController {

    @GetMapping("/api/members/{id}")
    public MemberDto getMember(@PathVariable("id") String id) {
        if (id.equals("ex")) {
            throw new RuntimeException("잘못된 사용자");
        }

        if (id.equals("bad")) {
            throw new IllegalArgumentException("잘못된 입력 값");
        }
        return new MemberDto(id, "hello " + id);
    }
```

```java
// HandlerExceptionResolver를 구현한 예외처리 클래스
public class MyHandlerExceptionResolver implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        try {
            // 발생한 예외가 IllegalArgumentException 타입이면 예외 받아줌
            if(ex instanceof IllegalArgumentException){

                log.info("IllegalArgumentException resolver to 400");

                // SC_BAD_REQUEST 발생시킴 (에러코드 400)
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());

                // DispatcherServlet로 응답
                // 빈 ModelAndView이기에 렌더링되지 않고, was까지 응답이 되면 was는 sendError 400을 확인하고 에러 응답을 위한 재요청 후 클라이언트에게 최종 응답 
                return new ModelAndView();

            }
        } catch (IOException e) {
            log.error("resolver ex", e);
        }

        return null;
    }

    // IllegalArgumentException 발생시 예외를 받아줘서 해당 예외를 소멸시키고 400에러 코드를 생성해 DispatcherServlet 으로 전달
    // 특정 예외 발생시 내가 원하는 에러코드를 응답하기 위함
}
```
```java
// 에외처리 클래스 등록
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add(new MyHandlerExceptionResolver());
    }
}
```

### HandlerExceptionResolver 활용

* 지금까지는 예외가 발생하면 was까지 전달되고, was에서 오류 페이지 정보를 찾아 내부적으로 다시 재요청했다

* HandlerExceptionResolver 를 잘 활용해 예외를 was로 전달하지 않고 해결할 수 있다

```
// ExceptionResolver 미사용시 예외 흐름
컨트롤러(예외발생) -> 예외가 was까지 전달 -> was는 오류 페이지 정보를 위해 내부적으로 재요청 -> 클라이언트 응답

// ExceptionResolver 사용시 예외 흐름
컨트롤러(예외발생) -> DispatcherServlet에서 ExceptionResolver 호출 -> 예외처리후 클라이언트 응답
```

```java
// 사용자 지정 예외 클래스 UserException 생성
public class UserException extends RuntimeException{

    ...
}
```
```java
// /api/members/user-ex 호출 시 UserException 발생
@Controller
public class ApiExceptionController {

    @GetMapping("/api/members/{id}")
    public MemberDto getMember(@PathVariable("id") String id) {

        if (id.equals("user-ex")){
            throw new UserException("사용자 오류");
        }

        return new MemberDto(id, "hello " + id);
    }
```
```java
// 예외 처리 로직 클래스
// UserException 예외일 경우 모든 처리를 여기서 마무리한다
@Slf4j
public class UserHandlerExceptionResolver implements HandlerExceptionResolver {

    // Json 형식으로 변환하기 위해 사용
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        try {

            if (ex instanceof UserException){
                log.info("UserException resolver to 400");
                String acceptHeader = request.getHeader("accept");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                // 헤더가 application/json 일때 실행
                if ("application/json".equals(acceptHeader)){
                    Map<String, Object> errorResult = new HashMap<>();
                    errorResult.put("ex", ex.getClass());
                    errorResult.put("message", ex.getMessage());

                    // json 형식으로 변환
                    String result = objectMapper.writeValueAsString(errorResult);

                    response.setContentType("application/json");
                    response.setCharacterEncoding("utf-8");
                    response.getWriter().write(result);

                    return new ModelAndView();

                } else {
                    // 헤더가 text/html 일 경우 
                    return new ModelAndView("error/500");
                }
            }

        } catch (IOException e){
            log.error("resolver ex", e);
        }

        return null;
    }
}
```
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add(new UserHandlerExceptionResolver());
    }
}
```

* 지금까지는 ExceptionResolver 를 직접 구현한 것이고, 스프링이 제공하는 ExceptionResolver 를 사용하면 더 쉽게 예외처리를 할 수 있다

### 스프링이 제공하는 ExceptionResolver 종류

* 1.ExceptionHandlerExceptionResolver - api 예외 처리

* 2.ResponseStatusExceptionResolver - http 상태 코드 지정
```java
// BadRequestException 발생시 404 코드가 응답됨
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class BadRequestException extends RuntimeException {
}
```

* 3.DefaultHandlerExceptionResolver - 스프링 내부 예외 처리
```
파라미터 바인딩 시점에 타입이 맞지 않으면 TypeMismatchException 이 발생하고 서블릿 컨테이너까지 예외가 올라가 다시 내부 재요청해 500 오류가 발생한다

그런데 파라미터 바인딩은 클라이언트가 요청 정보를 잘못 호출해서 발생하는 문제로 이럴 때는 상태코드 400을 사용하게 되어있다

스프링의 DefaultHandlerExceptionResolver 는 이것을 500이 아니라 400으로 변경해준다

내부를 보면 response.sendError(HttpServletResponse.SC_BAD_REQUEST) (400) 로 response.sendError() 를 통해 문제를 해결한다

// 예외 흐름
컨트롤러(예외 발생) -> ExceptionResolver -> response.sendError(...) -> was까지 응답 후 was가 다시 오류 페이지(/error) 내부 요청 -> 최종 응답
```

* 우선 순위는 1 -> 2 -> 3 이다
