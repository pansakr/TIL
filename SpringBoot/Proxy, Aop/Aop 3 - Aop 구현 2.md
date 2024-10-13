### AOP 구현 2

* Aop 설명과 함께 구현된 예제는 Aop 1 - Aop 개념과 구현에 있음

```java
// 어노테이션 클래스

// @Trace 어노테이션 클래스 
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Trace {
}

// @Retry 어노테이션 클래스
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Retry {
    int value() default 3;
}
```
```java
// Aspect 클래스

// @Trace Aspect 클래스
@Slf4j
@Aspect
public class TraceAspect {

    // @Trace 어노테이션이 사용된 메서드면 어드바이스 실행 
    @Before("@annotation(hello.aop.exam.annotation.Trace)") // @Trace 어노테이션의 경로 모두 작성
    public void doTrace(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        log.info("[trace] {} args={}", joinPoint.getSignature(), args);
    }
}

// @Retry Aspect 클래스
@Slf4j
@Aspect
public class RetryAspect {

    // 매개변수에 @Retry 어노테이션 타입을 명시하고 해당 변수를 @annotation 옵션에 사용
    @Around("@annotation(retry)") 
    public Object doRetry(ProceedingJoinPoint joinPoint, Retry retry) throws Throwable {

        log.info("[retry] {} retry={}", joinPoint.getSignature(), retry);

        int maxRetry = retry.value();
        Exception exceptionHolder = null;
        for(int retryCount = 1; retryCount <= maxRetry; retryCount++){
            try{
                log.info("[retry] try count={}/{}", retryCount, maxRetry);
                return joinPoint.proceed();
            }catch (Exception e){
                exceptionHolder = e;
            }
        }
        throw exceptionHolder;
    }
}
```
```java
// 서비스
@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;

    @Trace
    public void request(String itemId){
        examRepository.save(itemId);
    }
}

// 리포지토리
@Repository
public class ExamRepository {

    private static int seq = 0;

    /**
     * 5번에 1번 실패하는 요청
     */
    @Trace
    @Retry(4)
    public String save(String itemId){
        seq++;
        if(seq % 5 == 0){
            throw new IllegalStateException("예외 발생");
        }
        return "ok";
    }
}
```
```java
// 테스트
@Slf4j
@Import({TraceAspect.class, RetryAspect.class})
@SpringBootTest
class ExamServiceTest {

    @Autowired
    ExamService examService;

    @Test
    void test() {
        for (int i = 0; i < 5; i++){
            log.info("client request i={}", i);
            examService.request("data" + i);
        }
    }
}
```
