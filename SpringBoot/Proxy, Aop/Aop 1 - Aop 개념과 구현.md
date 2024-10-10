### 부가 기능과 핵심 기능

* 애플리케이션은 핵심 기능와 부가 기능으로 나눌 수 있다

* 핵심 기능

    - 해당 객체가 제공하는 고유의 기능

    - 주문 로직, 결제 로직 등

* 부가 기능

    - 핵심 기능을 보조하기 위해 제공되는 기능

    - 로그 추적, 트랜잭션 기능 등

* 부가 기능은 단독으로 사용되지 않고, 핵심 기능과 함께 사용된다

    - 로그 추적 기능은 어떤 핵심 기능이 호출되었는지 로그를 남기기 위해 사용한다

    - 이처럼 부가 기능은 핵심 기능을 보조하기 위해 존재한다

* 부가 기능은 여러 클래스에 걸쳐 함께 사용된다

    - 모든 애플리케이션 호출을 로깅해야 하는 경우, 이러한 부가 기능은 컨트롤러, 서비스, 리포지토리 모두에 적용된다

    - 이렇게 여러 곳에 동일하게 사용되는 부가 기능을 횡단 관심사 라고 한다 

    - 부가 기능을 100개의 클래스에 적용해야 한다면 100개 모두 동일한 코드를 작성해야 하고, 수정할때도 100개의 클래스 모두를 하나하나 수정해야 한다

    - 이런 문제를 해결하기 위해 AOP를 사용한다

### AOP(Aspect-Oriented Programming - 관점 지향 프로그래밍)

* 핵심 비즈니스 로직에 영향을 주지 않으면서, 그 외의 부가 기능을 분리해 관리하는 프로그래밍 방법

    - 에스펙트를 사용한 프로그래밍 방식이라고도 한다

    - 에스펙트(관점) : 부가 기능과 해당 부가 기능을 어디에 적용할지 정의한 것

* AOP의 대표적인 구현으로 AspectJ 프레임워크가 있고, 스프링은 AspectJ 의 일부 기능을 지원한다

    - AspectJ 는 횡단 관심사의 모듈화 를 지원한다

        - 오류검사 및 처리, 동기화, 성능 최적화, 모니터링 및 로깅

* AOP 의 적용 방식(분리된 부가 기능 로직을 실제 로직에 추가시키는 방법)

    - 컴파일 시점(컴파일 타임 위빙)

        - .java 파일을 .class 로 만드는 시점에 부가 기능 로직을 추가하는 방법

        - AspectJ가 제공하는 특별한 컴파일러를 사용해야 하며, 부가 기능 코드가 컴파일된 코드 주변에 실제로 붙어 버린다

        - 특별한 컴파일러도 필요하고 복잡하다 

    - 클래스 로딩 시점(로드 타임 위빙)

        - 자바를 실행하면 .class 파일이 jvm 내부의 클래스 로더에 보관되는데, 이때 중간에서 .class 파일을 조작해 jvm에 올리는 방법

        - .class 파일이 조작되는 시점에 에스펙트가 적용된다

        - 자바를 실행할 때 클래스 로더 조작기를 지정해야 하는데, 이 부분이 번거롭고 어렵다 

    - 런타임 시점(런타임 위빙. 프록시 사용)

        - 자바의 메인 메서드가 실행된 다음에 스프링 컨테이너와 프록시를 사용하는 방법

        - 이 방법을 주로 사용한다

* AOP 의 적용 위치

    - AspectJ를 사용해 컴파일 시점과 클래스 로딩 시점에 적용하는 AOP 는 메서드 뿐 아니라 생성자, 필드, static 에도 적용할 수 있다

        - 이렇게 AOP를 적용할 수 있는 지점을 조인 포인트(Join Point)라 한다

    - 스프링 AOP는 프록시 방식을 사용하므로 메서드 실행 지점에만 AOP를 적용할 수 잇다

        - 프록시를 사용하는 스프링 AOP의 조인 포인트는 메서드 실행으로 제한된다

        - 스프링에 AOP를 적용하려면 스프링 빈으로 등록되어야 한다

* AspectJ를 사용하면 복잡하고 다양한 기술을 사용할 수 있지만 공부할 내용도 많고, 설정도 복잡해서 대부분 추가 설정 없이 사용할 수 있는 스프링 AOP를 사용한다

    - 실무에서는 스프링 AOP 기능만 사용해도 대부분의 문제를 해결할 수 있다

* AOP 용어

    - 조인 포인트(Join Point)

        - 어드바이스가 적용될 수 있는 위치

        - 추상적인 개념으로 AOP를 적용할 수 있는 모든 지점이다

        - 스프링 AOP는 프록시 방식을 사용하므로 조인 포인트는 항상 메서드 실행 지점으로 제한된다

    - 포인트컷(Point Cut)

        - 조인 포인트 중에서 어드바이스가 적용될 위치를 선별하는 기능

        - 주로 Aspect 표현식을 사용해서 지정

        - 스프링 AOP는 메서드 실행 지점만 포인트컷으로 선별 가능

            - 스프링 AOP는 조인 포인트가 메서드 실행 지점으로 제한되므로 여러 메서드 중 어느 메서드에 어드바이스를 적용할지 필터링하는 것만 가능하다

    - 타겟(Target)

        - 프록시 객체가 호출하는 실제 객체

    - 어드바이스(Advice)

        - 부가 기능

        - Around, Befoe, After 과 같은 다양한 종류의 어드바이스가 있다

    - 에스펙트(Aspect)

        - 어드바이스 + 포인트컷을 모듈화한 것

        - @Aspect 를 생각하면 된다

        - 여러 어드바이스와 포인트 컷이 함께 존재한다

    - 어드바이저(Advisor)

        - 하나의 어드바이저와 하나의 포인트 컷으로 구성된 것

        - 스프링 AOP에서만 사용되는 특별한 용어

    - 위빙(Weaving)

        - 포인트컷으로 결정한 타겟의 조인 포인트에 어드바이스를 적용하는 것

        - AOP 적용을 위해 에스펙트를 객체에 연결한 상태

            - 컴파일 타임, 로드 타임, 런타임

    - AOP 프록시

        - AOP 기능을 구현하기 위해 만든 프록시 객체

        - 스프링에서 AOP 프록시는 JDK 동적 프록시 또는 CGLIB 프록시이다

### 스프링 AOP 구현

```java
// 서비스
@Slf4j
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    public void orderItem(String itemId) {
        log.info("[orderService] 실행");
        orderRepository.save(itemId);
    }
}

// 리포지토리
@Slf4j
@Repository
public class OrderRepository {

    public String save(String itemId) {
        log.info("[orderRepository] 실행");
        //저장 로직
        if (itemId.equals("ex")) {
            throw new IllegalStateException("예외 발생!");
        }
        return "ok";
    }
}
```
```java
// @AsectJ 클래스
@Slf4j
// @Aspect 는 에스펙트라는 표식이지 컴포넌트 스캔이 아니기 때문에 AspectV1 를 AOP 로 사용하려면 빈으로 등록해야 한다
@Aspect   
public class AspectV1 {

    // @Around 의 값이 포인트컷이 된다
    // @Around 어노테이션의 메서드인 doLog 가 어드바이스가 된다
    @Around("execution(* hello.aop.order..*(..))") // hello.aop.order 패키지와 그 하위 패키지
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable{
        log.info("[log] {}", joinPoint.getSignature());
        return joinPoint.proceed();
    }

    // ProceedingJoinPoint 인터페이스의 proceed() 는 다음 어드바이스나 타켓을 호출한다
}
```
```java
// 테스트
@Slf4j
@SpringBootTest
@Import(AspectV1.class)
public class AopTest {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    // 해당 클래스가 프록시 객체인지 확인
    @Test
    void aopInfo(){
        log.info("isAopProxy, OrderService={}", AopUtils.isAopProxy(orderService));
        log.info("isAopProxy, OrderRepository={}", AopUtils.isAopProxy(orderRepository));
    }

    @Test
    void success(){
        orderService.orderItem("itemA");
    }

    // 예외 발생시 정상
    @Test
    void exception(){
        Assertions.assertThatThrownBy(() -> orderService.orderItem("ex"))
                .isInstanceOf(IllegalStateException.class);
    }
}
```
* AOP 를 구현하려면 org.springframework.boot:spring-boot-starter-aop 의존성을 추가해야 한다

    - 자동 프록시 생성기와 필요한 클래스 자동 빈 등록 등 AOP 구현에 필요한 기능들이 담겨있다

* @Aspect 가 적용된 클래스를 만들고, 그 내부에 @Around 로 포인트컷과 @Around 가 적용된 메서드에 어드바이스를 만든다

* 작성한 @Aspect 클래스를 스프링 빈으로 등록한다

    - @Component, @Bean, @Import 중에서 아무거나 사용해도 된다

* 애플리케이션 실행 시 먼저 스프링 빈들이 생성되고, @Aspect 빈을 어드바이저로 만들어서 생성된 빈들이 어드바이저의 포인트컷에 해당되면 프록시 객체를 생성해 빈으로 등록, 해당되지 않으면 원래 객체로 등록한다 


* 포인트컷 분리

    - @Around 에 포인트컷 표현식을 직접 작성할 수도 있지만, @Pointcut 을 사용해 별도로 분리할 수도 있다

    ```java
    // @Aspect 클래스
    @Slf4j
    @Aspect
    public class AspectV2 {

        // pointcut signature
        // hello.aop.order 패키지와 하위 패키지
        // 포인트컷 시그니처 : allOrder()
        @Pointcut("execution(* hello.aop.order..*(..))")
        private void allOrder(){} 
        
        // @Pointcut 메서드를 @Around 에 적용
        @Around("allOrder()")
        public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable{
            log.info("[log] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }

        // @Pointcut 이 적용된 메서드는 여러 곳에 적용할 수 있다
        @Around("allOrder()")
        public Object ...()
    }
    ```

    - @Pointcut 에 포인트컷 표현식을 사용하며 메서드 이름과 파라미터를 합쳐서 포인트컷 시그니쳐라 한다

    - 메서드의 반환 타입은 void 여야 하고, 코드 내용은 비워둔다

    - @Around 에 포인트컷을 직접 지정해도 되지만, 위 예시처럼 포인트컷 시그니처를 사용해도 된다

    - 만약 다른 에스펙트 클래스에서 allOrder() 시그니처를 참고하려면 public 를 사용해야 한다

* 어드바이스 추가

    - 어드바이스를 추가해 한 클래스에 어드바이스가 2개 실행되게 할 수 있다

    ```java
    // @Aspect 클래스
    @Slf4j
    @Aspect
    public class AspectV3 {

        // pointcut signature
        // hello.aop.order 패키지와 하위 패키지
        @Pointcut("execution(* hello.aop.order..*(..))")
        private void allOrder(){}

        // 클래스 이름 패턴이 *Service
        @Pointcut("execution(* *..*Service.*(..))")
        private void allService(){}

        // hello.aop.order 패키지와 하위 패키지
        @Around("allOrder()")
        public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable{
            log.info("[log] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }

        // hello.aop.order 패키지와 하위 패키지 이면서 클래스 이름 패턴이 *Service
        @Around("allOrder() && allService()")
        public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable{

            try{
                log.info("[트랜잭션 시작] {}", joinPoint.getSignature());
                Object result = joinPoint.proceed();
                log.info("[트랜잭션 커밋] {}", joinPoint.getSignature());
                return result;
            }catch (Exception e){
                log.info("[트랜잭션 롤백] {}", joinPoint.getSignature());
                throw e;
            }finally{
                log.info("[리소스 릴리즈] {}", joinPoint.getSignature());
            }
        }
    }
    ```

    - 서비스 클래스는 @Around 의 포인트컷 2개 모두 해당되므로 2가지의 어드바이스가 적용된다

    - 리포지토리 클래스는 하나의 포인트컷만 해당하므로 1가지의 어드바이스만 적용된다

* 포인트컷 참조

    - 포인트컷을 공용으로 사용하기 위해 별도의 외부 클래스에 모아놔도 된다

        - 외부에서 호출할 때는 포인트컷의 접근 제어자를 public 로 해야 한다
    
    ```java
    // 포인트컷 클래스
    public class Pointcuts {

        @Pointcut("execution(* hello.aop.order..*(..))")
        public void allOrder(){}

        // 클래스 이름 패턴이 *Service
        @Pointcut("execution(* *..*Service.*(..))")
        public void allService(){}

        @Pointcut("allOrder() && allService()")
        public void orderAndService(){}
    }

    // @Aspect 클래스에서 포인트컷 클래스의 메서드 참조
    @Slf4j
    @Aspect
    public class AspectV4Pointcut {

        // 패키지 경로 전부를 작성해줘야 한다
        @Around("hello.aop.order.aop.Pointcuts.allOrder()")
        public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable{
            log.info("[log] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }

        @Around("hello.aop.order.aop.Pointcuts.orderAndService()")
        public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable{

            try{
                log.info("[트랜잭션 시작] {}", joinPoint.getSignature());
                Object result = joinPoint.proceed();
                log.info("[트랜잭션 커밋] {}", joinPoint.getSignature());
                return result;
            }catch (Exception e){
                log.info("[트랜잭션 롤백] {}", joinPoint.getSignature());
                throw e;
            }finally{
                log.info("[리소스 릴리즈] {}", joinPoint.getSignature());
            }
        }
    }
    ```

* 어드바이스 순서

    - 어드바이스는 기본적으로 순서를 보장하지 않는다

    - 순서를 지정하려면 @Order(1), @Order(2) 어노테이션을 적용해야 하는데 이것은 어드바이스 단위가 아니라 클래스 단위로 적용할 수 있다

    - 그래서 하나의 에스펙트에 여러 어드바이스가 있으면 순서를 보장 받을 수 없기 때문에 에스펙트를 별도의 클래스로 분리해야 한다

    ```java
    @Slf4j
    public class AspectV5Order {

        // 내부 클래스를 만들어 @Aspect 적용
        // 내부 클래스를 사용하지 않고 별도의 클래스로 만들어도 된다
        @Aspect
        @Order(2) // 어드바이스 실행 순서
        public static class LogAspect{
            @Around("hello.aop.order.aop.Pointcuts.allOrder()")
            public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable{
                log.info("[log] {}", joinPoint.getSignature());
                return joinPoint.proceed();
            }
        }

        @Aspect
        @Order(1)
        public static class TxAspect{
            @Around("hello.aop.order.aop.Pointcuts.orderAndService()")
            public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable{

                try{
                    log.info("[트랜잭션 시작] {}", joinPoint.getSignature());
                    Object result = joinPoint.proceed();
                    log.info("[트랜잭션 커밋] {}", joinPoint.getSignature());
                    return result;
                }catch (Exception e){
                    log.info("[트랜잭션 롤백] {}", joinPoint.getSignature());
                    throw e;
                }finally{
                    log.info("[리소스 릴리즈] {}", joinPoint.getSignature());
                }
            }
        }
    }
    ```

* 어드바이스 종류

    ```
    @Around : 메서드 호출 전후에 수행, 가장 강력한 어드바이스, 조인 포인트 실행 여부 선택, 반환 값 변환, 예외
    변환 등이 가능

    @Before : 조인 포인트 실행 이전에 실행

    @AfterReturning : 조인 포인트가 정상 완료후 
    
    @AfterThrowing : 메서드가 예외를 던지는 경우 실행

    @After : 조인 포인트가 정상 또는 예외에 관계없이 실행(finally)
    ```

    - @Around 가 가장 강력한 기능이며 모든 기능을 가지고 있다

    - 나머지들은 @Around 의 기능을 조각내서 부분적으로 적용할 때 사용한다

    ```java
    @Slf4j
    @Aspect
    public class AspectV6Advice {

        <!-- @Around("hello.aop.order.aop.Pointcuts.orderAndService()")
        public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable{

            try{
                // @Before
                log.info("[트랜잭션 시작] {}", joinPoint.getSignature());
                Object result = joinPoint.proceed();

                // @AfterReturning
                log.info("[트랜잭션 커밋] {}", joinPoint.getSignature());
                return result;
            }catch (Exception e){

                // @AfterThrowing
                log.info("[트랜잭션 롤백] {}", joinPoint.getSignature());
                throw e;
            }finally{

                // @After
                log.info("[리소스 릴리즈] {}", joinPoint.getSignature());
            }
        } -->

        // 조인 포인트 실행하는 부분인 joinPoint.proceed() 는 자동으로 실행된다
        @Before("hello.aop.order.aop.Pointcuts.orderAndService()")
        public void doBefore(JoinPoint joinPoint){
            log.info("[before {}]", joinPoint.getSignature());
        }

        // 조인 포인트가 실행되고 난 후 returning 값 출력
        // returning 값과 매개변수의 이름이 일치해야 한다
        // 현재 returning 타입으로 Object 가 지정되었다
        @AfterReturning(value = "hello.aop.order.aop.Pointcuts.orderAndService()", returning = "result")
        public void doReturn(JoinPoint joinPoint, Object result){
            log.info("[return] {} return={}", joinPoint.getSignature(), result);
        }

        // 예외가 발생할 경우 실행
        // throwing 값과 매개변수의 이름이 일치해야 한다
        @AfterThrowing(value = "hello.aop.order.aop.Pointcuts.orderAndService()", throwing = "ex")
        public void doThrowing(JoinPoint joinPoint, Exception ex){
            log.info("[ex] {} message={}", ex);
        }

        // 조인 포인트가 정상 실행 또는 예외가 발생 상관없이 실행 
        @After(value = "hello.aop.order.aop.Pointcuts.orderAndService()")
        public void doAfter(JoinPoint joinPoint){
            log.info("[after] {}", joinPoint.getSignature());
        }
    }
    ```

    - @Before

        - 조인 포인트가(타겟) 실행되기 전 실행

        - 메서드 종료시 자동으로 다음 타켓이 호출된다

            - joinPoint.proceed() 를 사용하지 않아도 자동으로 다음 타겟이 호출된다

            - 예외 발생 시 다읔 코드가 호출되지는 않는다

    - @AfterReturing

        - 메서드 실행이 정상적으로 반환될 때 실행

        - returning 속성에 사용된 이름은 어드바이스 메서드의 매개변수 이름과 일치해야 한다

        - returning 에 지정된 타입의 값을 반환하는 메서드만 대상으로 실행한다

            - @AfterReturing 이 적용된 메서드가 String 을 리턴할때, returning 이 String 이거나 Object 라면 값을 받을 수 있으므로 실행된다

            - 하지만 returning 이 Integer 이라면 String 은 Integer 에 들어갈 수 없으므로 @AfterReturing 이 적용된 어드바이스 자체가 실행되지 않는다

        - @Around 와 다르게 반환되는 객체를 변경할 수 없다

            - 반환 객체를 조작할 순 있다

    - @AfterThrowing

        - 메서드 실행이 예외를 던져서 종료될 때 실행

        - throwing 속성에 사용된 이름은 어드바이스 메서드의 매개변수 이름과 일치해야 한다

        - throwing 에 지정된 타입과 맞는 예외를 대상으로 실행한다

            - 부모 타입 예외를 지정하면 모든 자식 타입 예외는 인정되어 실행된다

    - @After

        - 메서드 실행이 종료되면 실행 (finally 와 비슷함)

        - 정상 및 예외 반환 조건을 모두 처리

        - 보통 리소스를 해제하는 데 사용한다

    - @Around

        - 메서드 실행(타겟) 전후에 작업을 수행한다

        - 위의 모든 기능을 포함한다

        - @Before 와 달리 joinPoint.proceed() 로 타겟을 반드시 호출해줘야 한다

    - 어드바이스들의 실행 순서

        - @Around -> @Before -> @After -> @AfterReturning -> @AfterThrowing

            - 호출 순서는 위 순서대로지만 리턴 순서는 반대로 적용된다

            - @Aspect 안에 동일한 종류의 어드바이스가 2개 있으면 순서가 보장되지 않는다

            - 그럴 경우 @Aspect 를 분리해 @Order 를 적용하면 된다

    - @Around 로 모든 기능을 사용할 수 있는데 다른 어드바이스들이 있는 이유

        - @Around 는 모든 기능을 사용 가능하기에 실수할 가능성이 있다

        - @Before, @After 같은 어드바이스는 기능은 적지만 코드가 단순해서 실수할 가능성이 낮다

            - 그리고 코드를 작성한 의도가 명확하게 드러난다
            
            - @Before 어노테이션을 보는 순간 대략적으로 타겟 실행 전에 한정에서 어떤 일을 하는 코드인지 알 수 있다
