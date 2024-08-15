### api 예외처리

* api 통신에서는 주로 json 형태로 데이터를 주고받기 때문에 오류가 발생해도 json 형식으로 응답을 보내주어야 한다

* 예외 응답으로 html 페이지를 보내면 웹 브라우저가 아닌 이상 할 수 있는 것이 별로 없다 


### api 예외처리

```java
// 특정 예외 발생 시 호출할 예외 페이지 설정
@Component
public class WebServerCustomizer implements
        WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    @Override
    public void customize(ConfigurableWebServerFactory factory) {

        ErrorPage errorPage404 = new ErrorPage(HttpStatus.NOT_FOUND, "/error-page/404");

        ErrorPage errorPage500 = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error-page/500");

        ErrorPage errorPageEx = new ErrorPage(RuntimeException.class, "/error-page/500");
                factory.addErrorPages(errorPage404, errorPage500, errorPageEx);
    }
}
```
```java
// 예외발생해서 에러페이지 내부 재요청 시 호출할 컨트롤러 
@Controller
public class ErrorPageController{
    
    // 클라이언트가 요청하는 HTTP Header의 Accept 의 값이 application/json 일 때 해당 메서드가 호출된다는 뜻
    @RequestMapping(value = "/error-page/500", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> errorPage500Api(HttpServletRequest request, HttpServletResponse response){

        log.info("API errorPage 500");

        Map<String, Object> result = new HashMap<>();
        Exception exception = (Exception) request.getAttribute("jakarta.servlet.error.exception");
        result.put("status", request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE));
        result.put("message", exception.getMessage());

        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        return new ResponseEntity<>(result, HttpStatus.valueOf(statusCode));

    }

    // HTTP Header의 Accept 의 값이 application/json이 아니라면 이 메서드가 호출됨
    @RequestMapping("/error-page/500")
    public String errorPage500(HttpServletRequest request, HttpServletResponse response) {
        log.info("errorPage 500");
        return "error-page/500";
    }
}
```
```java
// api 컨트롤러
@Controller
public class ApiExceptionController {

    @GetMapping("/api/members/{id}")
    public MemberDto getMember(@PathVariable("id") String id) {
        if (id.equals("ex")) {
            throw new RuntimeException("잘못된 사용자");
        }
        return new MemberDto(id, "hello " + id);
    }
    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String memberId;
        private String name;
    }
}
```

* 클라이언트에서 헤더값 application/json로 api/members/ex 요청 시 RuntimeException 발생

* WebServerCustomizer서 설정한대로 /error-page/500 경로로 재요청

* 요청 헤더값이 application/json 이니까 /error-page/500로 설정되있는 두개의 메서드 중 produces = MediaType.APPLICATION_JSON_VALUE로 설정되어있는 메서드 실행해 응답
