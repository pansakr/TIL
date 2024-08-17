### @ControllerAdvice

* 대상으로 지정한 여러 컨트롤러에서 예외 발생시 처리해주는 역할을 한다

* @ControllerAdvice 에 대상을 지정하지 않으면 모든 컨트롤러에 적용된다. (글로벌 적용)

* @RestControllerAdvice 는 @ControllerAdvice 에서 @ResponseBody 만 더해져 있다

* @Controller , @RestController 의 차이와 같다.

```java
// @RestController 가 적용된 클래스 대상
@ControllerAdvice(annotations = RestController.class)
public class ExampleAdvice1 {}

// 해당 경로의 패키지와 하위의 컨트롤러 대상
@ControllerAdvice("org.example.controllers")
public class ExampleAdvice2 {}

// 지정한 클래스 대상
@ControllerAdvice(assignableTypes = {ControllerInterface.class, AbstractController.class})
public class ExampleAdvice3 {}
```
