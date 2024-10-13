### 포인트컷 지시자

* @Pointcut("execution(* hello.aop.order..*(..))") 에서 execution 을 뜻한다

    - 포인트컷 표현식은 * hello.aop.order..*(..) 부분이다

    ```
    // 포인트컷 지시자의 종류

    execution : 메소드 실행 조인 포인트를 매칭한다. 스프링 AOP에서 가장 많이 사용하고, 기능도 복잡하다

    within : 특정 타입 내의 조인 포인트를 매칭한다
    args : 인자가 주어진 타입의 인스턴스인 조인 포인트

    this : 스프링 빈 객체(스프링 AOP 프록시)를 대상으로 하는 조인 포인트

    target : Target 객체(스프링 AOP 프록시가 가리키는 실제 대상)를 대상으로 하는 조인 포인트

    @target : 실행 객체의 클래스에 주어진 타입의 애노테이션이 있는 조인 포인트

    @within : 주어진 애노테이션이 있는 타입 내 조인 포인트

    @annotation : 메서드가 주어진 애노테이션을 가지고 있는 조인 포인트를 매칭

    @args : 전달된 실제 인수의 런타임 타입이 주어진 타입의 애노테이션을 갖는 조인 포인트

    bean : 스프링 전용 포인트컷 지시자, 빈의 이름으로 포인트컷을 지정한다.
    ```

* execution

    - execution 문법 : execution(접근제어자? 반환타입 선언타입?메서드이름(파라미터) 예외?)

        - ? 는 생략 가능

        - .. 은 파라미터의 타입과 파라미터 수가 상관없다는 뜻

        - *은 아무 값이 들어와도 된다는 뜻

        - 접근제어자, 선언타입, 예외는 생략 가능하다

    - 가장 정확한 execution 문법을 사용한 포인트컷

    ```java
    // 커스텀 어노테이션 

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ClassAop {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MethodAop {
        String value();
    }
    ```
    ```java
    @ClassAop
    @Component
    public class MemberServiceImpl implements MemberService{

        @Override
        @MethodAop("test value")
        public String hello(String param) {
            return "ok";
        }

        public String internal(String param){
            return "ok";
        }
    }
    ```
    ```java
    // test
    @Slf4j
    public class ExecutionTest {

        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        Method helloMethod;

        // helloMethod 에 MemberServiceImpl.getMethod() 의 메서드 정보가 담겨있음
        // execution 표현식은 위의 메서드 정보를 매칭해 포인트컷 표현식을 적용한다
        @BeforeEach
        public void init() throws NoSuchMethodException {
            helloMethod = MemberServiceImpl.class.getMethod("hello", String.class);
        }

        // MemberServiceImpl.getMethod 의 정보 출력 
        @Test
        void printMethod(){
            log.info("helloMethod={}", helloMethod);
        }

        @Test
        void exactMatch(){

            // MemberServiceImpl.getMethod() 의 정보 : public java.lang.Stringhello.aop.member.MemberServiceImpl.hello(java.lang.String) 
            // 가장 정확한 포인트컷 표현식
            pointcut.setExpression("execution(public String hello.aop.member.MemberServiceImpl.hello(String))");

            // pointcut.matches -  Pointcut 표현식과 helloMethod 메소드, MemberServiceImpl 클래스의 매칭 여부를 true, false 로 반환
            // 메소드가 public 접근제어자인지, 타입이 String인지,
            // 메소드 이름이 hello이고, 매개변수가 String 타입 하나인지,
            // 해당 메소드가 클래스 MemberServiceImpl에 속해 있는지 를 비교한다
            Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
        }
    }
    ```
    ```
    // 정리

    1. 리플렉션으로 MemberServiceImpl.class.getMethod("hello", String.class) 의 정보를 가져온다

    2. getMethod() 의 정보는 public java.lang.Stringhello.aop.member.MemberServiceImpl.hello(java.lang.String) 이다

    3. execution 표현식은 위와 같은 메서드 정보를 매칭해 포인트컷 표현식을 사용한다

    4. pointcut.setExpression() 에 getMethod("hello", String.class) 와 매칭되는 포인트컷 표현식 적용

    5. pointcut.setExpression() 에 작성된 표현식이 가장 정확한 포인트컷 표현식 이다

    6. 적용된 표현식은 helloMethod, MemberServiceImpl.class 와 매칭된다

    7. 매칭 조건

    접근제어자?: public

    반환타입: String

    선언타입?: hello.aop.member.MemberServiceImpl

    메서드이름: hello

    파라미터: (String)
    
    예외?: 생략
    ```

    - 가장 많이 생략한 execution 문법을 사용한 포인트컷

    ```java
    // 테스트
    @Test
    void allMatch(){
        pointcut.setExpression("execution(* *(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    // 매칭 조건

    접근제어자?: 생략

    반환타입: *    // * 은 아무 값이나 들어와도 된다는 뜻

    선언타입?: 생략

    메서드이름: *

    파라미터: (..) // ..은 파라미터의 타입과 파라미터 수가 상관없다는 뜻

    예외?: 없음
    ```

    - 메서드 이름 매칭 관련 포인트컷

    ```java
    // hello() 메서드 이름 매칭
    @Test
    void nameMatch() {
        pointcut.setExpression("execution(* hello(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    // hel 로 시작하는 메서드 이름 매칭
    @Test
    void nameMatchStar1() {
        pointcut.setExpression("execution(* hel*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    // 중간에 el 이 들어가는 메서드 이름 매칭
    @Test
    void nameMatchStar2() {
        pointcut.setExpression("execution(* *el*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    // 작성한 클래스에 nono 라는 이름의 메서드는 없으므로 매칭 안됨
    @Test
    void nameMatchFalse() {
        pointcut.setExpression("execution(* nono(..))");
        assertThat(pointcut.matches(helloMethod,
                MemberServiceImpl.class)).isFalse();
    }
    ```

    - 패키지 매칭 관련 포인트컷

    ```java
    @Test
    void packageExactMatch1() {
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.hello(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }
    @Test
    void packageExactMatch2() {
        pointcut.setExpression("execution(* hello.aop.member.*.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    // 패키지 매칭 실패
    // . 은 정확하게 해당 위치의 패키지를 뜻한다
    // 즉 정확하게 hello.aop 위치의 패키지가 매칭된다
    // helloMethod, MemberServiceImpl.class 는 그 위치에 없으므로 매칭 실패
    @Test
    void packageExactMatchFalse() {
        pointcut.setExpression("execution(* hello.aop.*.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    // .. 은 해당 위치의 패키지와 그 하위 패키지를 포함한다
    // hello.aop.member 와 그 하위 패키지 매칭
    @Test
    void packageMatchSubPackage1() {
        pointcut.setExpression("execution(* hello.aop.member..*.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }
    @Test
    void packageMatchSubPackage2() {
        pointcut.setExpression("execution(* hello.aop..*.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }
    ```

    - 타입 매치

        - 포인트컷 표현식에 부모 타입을 지정하면 자식 타입도 매칭된다

    ```java
    // 표현식에 자식 타입 지정
    @Test
    void typeExactMatch() {
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    // 표현식에 부모 타입 지정
    // 부모 타입은 자식 타입을 포함하기 때문에 테스트는 성공한다
    @Test
    void typeMatchSuperType() {
        pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }
    ```

    - 타입 매치 - 부모 타입에 있는 메서드만 허용

    ```java
    // 부모 타입 MemberService - internal() 메서드 없음
    // 자식 타입 MemberServiceImpl - internal() 메서드 있음

    // 표현식에 자식 타입을(MemberServiceImpl) 지정
    // MemberServiceImpl 에는 internal() 메서드가 있으므로 매칭됨
    @Test
    void typeMatchInternal() throws NoSuchMethodException {
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.*(..))");
        Method internalMethod = MemberServiceImpl.class.getMethod("internal", String.class);
        assertThat(pointcut.matches(internalMethod, MemberServiceImpl.class)).isTrue();
    }

    // 표현식에 부모 타입을(MemberService) 지정
    // MemberService 에는 internal() 메서드가 없으므로 매칭되지 않음
    @Test
    void typeMatchNoSuperTypeMethodFalse() throws NoSuchMethodException {
        pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");
        Method internalMethod = MemberServiceImpl.class.getMethod("internal", String.class);
        assertThat(pointcut.matches(internalMethod, MemberServiceImpl.class)).isFalse();
    }
    ```

    - 파라미터 매치

    ```java

    // String 타입 파라미터를 가진 메서드 매칭
    @Test
    void argsMatch() {
        pointcut.setExpression("execution(* *(String))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    // 파라미터가 없는 메서드 매칭
    @Test
    void argsMatchNoArgs() {
        pointcut.setExpression("execution(* *())");
        assertThat(pointcut.matches(helloMethod,MemberServiceImpl.class)).isFalse();
    }

    // 정확히 하나의 파라미터 허용, 모든 타입 허용
    @Test
    void argsMatchStar() {
        pointcut.setExpression("execution(* *(*))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    // 숫자와 무관하게 모든 파라미터, 모든 타입 허용
    // 파라미터가 없어도 됨
    // (), (Xxx), (Xxx, Xxx)
    @Test
    void argsMatchAll() {
        pointcut.setExpression("execution(* *(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    // String 타입으로 시작, 숫자와 무관하게 모든 파라미터, 모든 타입 허용
    // (String), (String, Xxx), (String, Xxx, Xxx) 허용
    @Test
    void argsMatchComplex() {
        pointcut.setExpression("execution(* *(String, ..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }
    ```

* within

    - 특정 클래스나 패키지 내의 메소드에 대해 포인트컷을 정의

    - 타입으로 매칭하고 (메서드 이름 X) 매칭된 타입의 모든 메서드는 자동 매칭됨

    - 실제로는 거의 사용하지 않는다

    ```java
    public class WithinTest {

        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        Method helloMethod;

        @BeforeEach
        public void init() throws NoSuchMethodException {
            helloMethod = MemberServiceImpl.class.getMethod("hello", String.class);
        }

        // hello.aop.member.MemberServiceImp 로 매칭
        // MemberServiceImp 의 메서드는 모두 자동 매칭됨
        @Test
        void withinExact() {
            pointcut.setExpression("within(hello.aop.member.MemberServiceImpl)");
            assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
        }

        @Test
        void withinStar() {
            pointcut.setExpression("within(hello.aop.member.*Service*)");
            assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
        }

        @Test
        void withinSubPackage() {
            pointcut.setExpression("within(hello.aop..*)");
            assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
        }

        // within 은 정확하게 타입이 맞아야 한다
        // MemberService 인터페이스에 hello() 가 있지만 표현식에 MemberService 를 지정하면 hello() 메서드와 매칭되지 않는다
        // 표현식에 구현체인 MemberServiceImpl 을 지정해야 매칭된다
        @Test
        @DisplayName("타켓의 타입에만 직접 적용, 인터페이스를 선정하면 안된다.")
        void withinSuperTypeFalse() {
            pointcut.setExpression("within(hello.aop.member.MemberService)");
            assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
        }
        
        // execution 은 인터페이스를 지정해도 자식 타입의 메서드와 매칭이 된다
        @Test
        @DisplayName("execution은 타입 기반, 인터페이스를 선정 가능.")
        void executionSuperTypeTrue() {
            pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");
            assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
        }
    }
    ```

* args

    - 메소드의 파라미터 타입에 따라 포인트컷을 정의

    - args 단독으로는 잘 사용하지 않는다

    ```java
    public class ArgsTest {

        Method helloMethod;

        @BeforeEach
        public void init() throws NoSuchMethodException {
            helloMethod = MemberServiceImpl.class.getMethod("hello", String.class);
        }

        // 인자로 오는 타입을 표현식으로 지정
        private AspectJExpressionPointcut pointcut(String expression) {
            AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
            pointcut.setExpression(expression);
            return pointcut;
        }

        // pointcut() 메서드의 인자로 오는 타입을 표현식으로 지정한다
        @Test
        void args() {

            // String 타입의 매개변수를 가진 메서드가 있으면 매칭
            // MemberServiceImpl 에는 hello(String s) 의 String 타입의 매개변수를 가진 메서드가 있으므로 매칭됨
            assertThat(pointcut("args(String)")
                    .matches(helloMethod, MemberServiceImpl.class)).isTrue();

            // Object 타입의 매개변수를 가진 메서드가 있으면 매칭
            // MemberServiceImpl 의 hello(String s) 메서드의 String 은 Object 의 하위 타입이므로 매칭됨
            assertThat(pointcut("args(Object)")
                    .matches(helloMethod, MemberServiceImpl.class)).isTrue();

            // 매개변수가 없는 메서드 매칭
            assertThat(pointcut("args()")
                    .matches(helloMethod, MemberServiceImpl.class)).isFalse();

            // 숫자와 무관하게 모든 파라미터, 모든 타입 허용
            // 파라미터가 없어도 됨
            assertThat(pointcut("args(..)")
                    .matches(helloMethod, MemberServiceImpl.class)).isTrue();

            // 정확히 하나의 파라미터 허용, 모든 타입 허용
            assertThat(pointcut("args(*)")
                    .matches(helloMethod, MemberServiceImpl.class)).isTrue();

            // String 타입으로 시작, 숫자와 무관하게 모든 파라미터, 모든 타입 허용
            assertThat(pointcut("args(String,..)")
                    .matches(helloMethod, MemberServiceImpl.class)).isTrue();
        }

        /**
         * execution(* *(java.io.Serializable)): 메서드의 시그니처로 판단 (정적)
        * args(java.io.Serializable): 런타임에 전달된 인수로 판단 (동적)
        */
        @Test
        void argsVsExecution() {
            //Args
            assertThat(pointcut("args(String)")
                    .matches(helloMethod, MemberServiceImpl.class)).isTrue();
            assertThat(pointcut("args(java.io.Serializable)")
                    .matches(helloMethod, MemberServiceImpl.class)).isTrue();
            assertThat(pointcut("args(Object)")
                    .matches(helloMethod, MemberServiceImpl.class)).isTrue();
            //Execution
            assertThat(pointcut("execution(* *(String))")
                    .matches(helloMethod, MemberServiceImpl.class)).isTrue();
            assertThat(pointcut("execution(* *(java.io.Serializable))") //매칭 실패
                    .matches(helloMethod, MemberServiceImpl.class)).isFalse();
            assertThat(pointcut("execution(* *(Object))") //매칭 실패
                    .matches(helloMethod, MemberServiceImpl.class)).isFalse();
        }
    }
    ``` 
