### API 예외처리

* 웹 브라우저에 HTML 화면을 제공할때는 오류가 발생하면 스프링 부트의 기본값으로 적용된 BasicErrorController 를 사용해 오류 화면을 보여주는게 편하다

* 하지만 API는 시스템마다 다른 응답 데이터를 출력해야 해서 BasicErrorController 나 HandlerExceptionResolver 로 다루기 어렵다

* @ExceptionHandler 로 api 예외처리를 편하게 할 수 있다


### @ExceptionHandler

* api 예외처리 어노테이션

* ExceptionHandlerExceptionResolver 가 @ExceptionHandler 이다

* @ExceptionHandler 가 작성된 컨트롤러의 예외만 처리할 수 있다

```
// 예외 흐름
컨트롤러(예외 발생) -> ExceptionResolver 실행 -> 우선순위가 높은 ExceptionHandlerExceptionResolver 실행 -> 발생한 예외를 처리할 @ExceptionHandler 가 있는지 확인 -> 응답
```

```java
// 사용방법
1. 예외를 처리할 컨트롤러 클래스에 @ExceptionHandler 를 붙인 예외처리 메서드를 만들고 어노테이션 옵션으로는 처리하고 싶은 예외를 지정
2. 해당 컨트롤러에서 지정한 예외가 발생하면 만들어둔 메서드가 호출된다
3. 이때 지정한 예외와 그 예외의 자식 클래스는 모두 잡을 수 있다

ex) 부모 예외인 AException과 해당 예외를 상속한 BException 클래스가 있다고 가정

..class{

    @ExceptionHandler(AException.class)
    public ...aError(AException e){}

    @ExceptionHandler(BException.class)
    public ...bError(BException e){}

    // 부모 예외인 A 예외가 발생하면 aError()가 호출 대상이 된다
    // 자식 예외인 B 예외가 발생하면 aError(), bError() 모두 호출 대상이 되고, 자세한 것이 우선권을 가져 bError()가 호출된다
}
```

* 자세한 사용 방법

```java
// 에러 응답용 객체
@Data
@AllArgsConstructor
public class ErrorResult {
    private String code;
    private String message;
}
```
```java
@Slf4j
@RestController
public class ApiExceptionV2Controller {

    // 첫번째 방법
    // @ResponseStatus 로 응답의 에러코드를 지정할 수 있다
    // @ResponseStatus 미사용시 에러코드는 200이 되고, 예외가 발생했는데 정상 응답 코드가 나오면 안되니 변경해 주는것이다 
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResult illegalExHandler(IllegalArgumentException e){
        log.error("[exceptionHandler] ex", e);

        // @RestController 로 인해 ErrorResult 객체가 json으로 변환된다
        return new ErrorResult("BAD", e.getMessage());
    }

    // 두번째 방법
    // @ExceptionHandler 에 예외를 지정하지 않고, 메서드의 매개변수에 잡을 예외를 지정한다
    // 그리고 @ResponseStatus 대신 ResponseEntity 로 에러 코드, 에러 메시지를 응답한다
    @ExceptionHandler
    public ResponseEntity<ErrorResult> userExHandler(UserException e){
        log.error("[exceptionHandler] ex", e );
        ErrorResult errorResult = new ErrorResult("USER-EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    // 세번째 방법
    // 상위 예외를 지정해 자식 예외들이 처리되지 않았을 경우 처리해준다 
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult exHandler(Exception e){
        log.error("[exceptionHandler] ex", e);
        return new ErrorResult("EX", "내부 오류");
    }

    @GetMapping("/api2/members/{id}")
    public MemberDto getMember(@PathVariable("id") String id) {

        if (id.equals("ex")) {
            throw new RuntimeException("잘못된 사용자");
        }

        if (id.equals("bad")) {
            throw new IllegalArgumentException("잘못된 입력 값");
        }
        if (id.equals("user-ex")){
            throw new UserException("사용자 오류");
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
