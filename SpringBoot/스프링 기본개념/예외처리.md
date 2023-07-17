### @ExceptionHandler

* @Controller, @RestController가 적용된 bean에서 발생하는 예외를 잡아서 하나의 메서드에서 처리해주는 기능

* @ExceptionHandler에 설정한 예외가 발생하면 handler가 실행된다.

* @Controller, @RestController가 아닌 @Service나 @Repository가 적힌 bean에서는 사용할 수 없다.

* @ExceptionHandler는 등록된 해당 Controller에서만 적용이 된다. 다른 컨트롤러의 예외는 잡을 수 없다.

* 다른 컨트롤러에서 작업하려면 같은 @ExceptionHandler를 적용해야 한다.

* 같은 코드를 중복해서 작성해야하는데 이를 해결하는 방법이 @ControllerAdvice를 사용하는 것이다.

```
@Controller
public class SampleController{ 

    //SampleController에서 RuntimeException이 발생하면 @ExceptionHandler이 적용된 메서드가 실행된다.
    @ExceptionHandler(value = RuntimeException.class) 
    public ResponseEntity<String> handle(){
        ..
    }
}
```

### @ControllerAdvice

* @Controller 어노테이션이 있는 모든 곳에서의 예외를 잡을수 있게 해준다.

* @ControllerAdvice 안에 있는 @ExceptionHandler는 모든 컨트롤러에서 발생하는 예외상황을 잡을 수 있다.

* @ControllerAdvice 의 속성 설정을 통하여 원하는 컨트롤러나 패키지만 선택할 수 있다.

* 따로 지정을 하지 않으면 모든 패키지에 있는 컨트롤러를 담당하게 된다.

```
@ControllerAdvice // 새로운 클래스를 만들어 사용한다.
public class PageControllerAdvice {

    @ExceptionHandler(DataAccessException.class) // @ControllerAdvice가 우선 예외를 잡고 예외 종류에 따라 해당 예외를 받고있는 @ExceptionHandler의 메서드가 실행된다. 
    public ResponseEntity<String> dataExceptionHandle() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<String> sqlExceptionHandle() {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
    }

}
```

### @RestControllerAdvice

* @ControllerAdvice와 동일한 역할을 하지만 객체를 반환할 수 있다는 의미를 가진다.

* @ControllerAdvice와 달리 응답의 body에 객체를 넣어 반환이 가능하다.

* @Controller과 @RestController에서 발생하는 예외를 모두 잡을 수 있다.


### ResponseEntityExceptionHandler 추상 클래스

* @ControllerAdvice를 사용한 클래스는 ResponseEntityExceptionHandler 추상 클래스를 상속 받는게 좋다.

* @ControllerAdvice를 사용한 클래스는 모든 예외를 받기 때문에 다양한 종류의 예외가 올 수 있다.

* 받은 예외 종류가 @ExceptionHandler에 정의되어있지 않다면 최상위 타입 예외인 Exception으로 처리가 된다.

* 그렇게 되면 정의되지 않은 다양한 예외가 서버 예외로 일괄적으로 처리될 수 있고, 해당 예외들은 무조건 500 상태코드를 리턴하게 된다.

* 그렇다고 모든 예외를 클래스에 정의하기에는 그 종류가 너무 많다.

* 이때 ResponseEntityExceptionHandler추상 클래스를 상속받게되면 해당 클래스에 다양한 예외가 정의되어 있기 때문에 받아줄 수 있다.

* 내가 @ExceptionHandler에 정의하지 않은 예외들도 ResponseEntityExceptionHandler추상 클래스에 정의되어있는 @ExceptionHandler로 처리가 가능해진다.

```
// 예외 발생할 컨트롤러
 @GetMapping("/users/{id}") 
    public User retrieveUser(@PathVariable int id){
        User user = service.findOne(id);

        if(user == null){ //id가 null이면 사용자 지정 예외 UserNotFountException발생
            throw new UserNotFountException(String.format("ID[%s] not found", id));
        }
        return user;
    }


@RestController
@ControllerAdvice // 모든 컨트롤러가 실행될때 @ControllerAdvice를 가지고 있는 클래스가 사전 실행된다
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class) // 하위타입 예외를 찾지 못했을때 실행
    public final ResponseEntity<Object> handleExceptions(Exception ex, WebRequest request){
        ExceptionResponse exceptionResponse =
                new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserNotFountException.class) //UserNotFountException예외 발생시 실행
    public final ResponseEntity<Object> handleUserNotFoundException(Exception ex, WebRequest request){
        ExceptionResponse exceptionResponse =
                new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));

        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse { // 예외 발생시 리턴해줄 객채
    private Date timestamp;
    private String message;
    private String details;
}

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFountException extends RuntimeException { // 사용자 지정 예외 클래스

    public UserNotFountException(String message) {
        super(message);
    }
}
```

* 컨트롤러에서 id값 없으면 사용자 지정 예외 UserNotFountException 발생

* @ControllerAdvice가 붙은 클래스인 CustomizedResponseEntityExceptionHandler클래스가 @Controller에서 예외 발생시 받아준다.

* 예외 종류에 따른 @ExceptionHandler를 실행시키는데 UserNotFountException가 발생했으니 

* @ExceptionHandler(UserNotFountException.class)가 붙은 메서드를 실행

* 메서드 내부에서 간단한 예외정보 필드가 있는 사용자 지정 클래스 ExceptionResponse를 생성

* 예외 메시지와 요청객체 정보를 받아 세팅후 적합한 상태 코드와 함께 리턴
