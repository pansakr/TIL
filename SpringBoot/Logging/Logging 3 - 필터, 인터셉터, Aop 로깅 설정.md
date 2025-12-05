### 필터, 인터셉터, AOP 로깅 설정

* 필터

    - traceId 생성

        - traceId : 하나의 요청 전체를 식별할 수 있는 고유 ID

        - 인터셉터나 AOP 에서 traceId 생성시 고유한 traceId 를 갖지 못한다

        - 예외 시 컨트롤러는 내부적으로 /error 재요청을 하는데, 이때 새로운 traceId 가 만들어지므로 예외를 추적하기 어려워진다

    - MDC
    
        - 로그에 자동으로 키-값 형태의 정보를 추가할 수 있는 저장소(맵)

        - ThreadLocal 기반 저장소

            - ThreadLocal 은 스레드가 살아있는 동안 값이 유지된다

            - 같은 스레드를 다시 재사용하면 이전 요청의 데이터가 그대로 남아 있음

            - 사용 후 값을 지워줘야 한다

            ```
            MDC.put(...)

            MDC.remove(...)
            ```
 
       - 예시

        ```
        MDC.put("traceId", "abcd-1234");

        log.info("회원가입 처리 시작");

        // 결과 로그
        [abcd1234] INFO  ... 회원가입 처리 시작
        ```

        - traceId = 하나의 요청 전체에 동일하게 붙는 “요청 고유 식별자(UUID)”

        ```java
        @Slf4j
        @Component
        public class TraceIdFilter extends OncePerRequestFilter {
    
            private static final String MDC_KEY_TRACE_ID = "traceId";
    
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain)
                    throws ServletException, IOException {
    
                // 이미 traceId가 헤더로 들어온 경우(예: 다른 서비스에서 전달) 우선 사용
                String incomingTraceId = request.getHeader("X-Trace-Id");
                String traceId = (incomingTraceId != null && !incomingTraceId.isBlank())
                        ? incomingTraceId
                        : UUID.randomUUID().toString().substring(0, 8);
    
                MDC.put(MDC_KEY_TRACE_ID, traceId);
    
                try {
                    // 필요하다면 응답 헤더로도 넣어줌
                    response.setHeader("X-Trace-Id", traceId);
    
                    filterChain.doFilter(request, response);
                } finally {
                    // 반드시 정리 (쓰레드 재사용 때문에)
                    MDC.remove(MDC_KEY_TRACE_ID);
                }
            }
        }
        ```

    - OncePerRequestFilter

        - 하나의 HTTP 요청당 딱 한번만 실행되는 스프링의 기본 필터

        - forward, include 등이 발생해도 한 번만 실행된다

    - "traceId"

        ```java
        private static final String MDC_KEY_TRACE_ID = "traceId";
        ```

        - logback 에 정의한 로그 패턴의 키 이름과 필터에서 정의한 상수의 값(tracdId) 이 일치해야함 

    - 빈 등록

        - 커스텀 필터 클래스를 스프링 빈에 등록해야 스프링이 필터 체인에 자동으로 추가한다

        - 빈으로 등록하지 않으면 필터 체인에 추가되지 않아서 실행 안됨

    - MDC.remove

        - MDC 는 ThreadLocal 기반 저장소이고, ThreadLocal 는 스레드가 살아있는 동안 유지된다

        - 같은 스레드를 재사용하면 이전 값이 남아있으므로 사용 후 반드시 삭제해야 한다

        - finally 블록 사용 추천

    
* 인터셉터

    - 요청 단위별로 호출됨

    - 호출된 컨트롤러의 이름, 호출된 URL, HTTP 메서드 같은 것은 필터에서 알 수 없으므로 인터셉터에서 로깅을 해야 함

    ```java
    @Slf4j
    @Component
    public class LoggingInterceptor implements HandlerInterceptor {

        private static final String START_TIME = "requestStartTime";

        // preHandle : 요청이 컨트롤러로 들어가기 직전에 호출
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

            long startTime = System.currentTimeMillis();    // 현재 시간 ms 단위로 기록
            request.setAttribute(START_TIME, startTime);    // 나중에 사용하기 위해 요청 객체에 저장

            // 필터에서 저장한 traceID 값
            // logback 에서 로그 패턴에 traceID 포함하도록 설정했으므로 굳어 넣지 않아도 됨 
            // String traceId = MDC.get("traceId");    
            String method = request.getMethod();    // HTTP 메서드 정보
            String uri = request.getRequestURI();   // /api/users 같은 URI

            String handlerInfo = getHandlerInfo(handler);   // 실행되는 컨트롤러/메서드 정보

            // 요청 시작 로그
            log.info("[Request] {} {} handler={}", method, uri, handlerInfo);

            return true;    // true : 컨트롤러로 계속 진행
        }

        // afterCompletion : 요청이 끝난 뒤 한번 호출
        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                    Object handler, Exception ex) {

            Long startTime = (Long) request.getAttribute(START_TIME);

            // startTime 을 못 읽으면 -1 로 표시
            long duration = (startTime != null) ? System.currentTimeMillis() - startTime : -1;

            int status = response.getStatus();      // HTTP 응답 코드 다시 읽기
            String uri = request.getRequestURI();   // 어떤 경로의 응답인지 다시 표시

            if (ex != null) {   // 예외 발생한 경우
                log.error("[Response] {} status={} duration={}ms EXCEPTION={}",
                        uri, status, duration, ex.toString());
            } else {
                log.info("[Response] {} status={} duration={}ms",
                        uri, status, duration);
            }
        }

        private String getHandlerInfo(Object handler) {

            // 대부분의 경우 handler 는 HandlerMethod 타입
            if (handler instanceof HandlerMethod handlerMethod) {
                String className = handlerMethod.getBeanType().getSimpleName();
                String methodName = handlerMethod.getMethod().getName();
                return className + "#" + methodName;
            }
            return handler.toString();
        }
    }
    ```
    ```java
    // 인터셉터 등록
    @Configuration
    @RequiredArgsConstructor
    public class WebMvcConfig implements WebMvcConfigurer {

        private final LoggingInterceptor loggingInterceptor;

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(loggingInterceptor)
                    .order(1)
                    .addPathPatterns("/**") // 모든 요청 URL 에 적용
                    .excludePathPatterns(   // 그중 인터셉터에서 제외할 URL 패턴
                            "/css/**",
                            "/js/**",
                            "/images/**",
                            "/favicon.ico",
                            "/error"
                    );
        }
    }
    ```

* AOP

    - 메서드 호출 단위

    - 메서드 실행 시간 측정, 특정 메서드만 로깅 같은 사항들은 요청과 관계 없는 부가 로직이고, 적용 위치를 고를 수 있으므로 AOP 가 적합

    ```java
    @Component
    @Aspect
    @Slf4j
    public class LoggingAop {

        @Pointcut("@within(org.springframework.stereotype.Controller) || " +
                "@within(org.springframework.web.bind.annotation.RestController)")
        public void controllerLayer(){}

        @Pointcut("@within(org.springframework.stereotype.Service)")
        public void serviceLayer(){}

        @Pointcut("@within(org.springframework.stereotype.Repository)")
        public void repositoryLayer(){}

        @Around("controllerLayer() || serviceLayer() || repositoryLayer()")
        public Object logging(ProceedingJoinPoint joinPoint) throws Throwable{

            String className = joinPoint.getSignature().getDeclaringType().getName();
            String methodName = joinPoint.getSignature().getName();
            Object[] args = joinPoint.getArgs();
            long startTime = System.currentTimeMillis();

            log.info("[Request] {}#{} args={}", className, methodName, args);

            try{
                Object result = joinPoint.proceed();
                long duration = System.currentTimeMillis() - startTime;

                log.info("[Response] {}#{} result={} duration={}", className, methodName,
                        result != null ? result.getClass().getName() : "null", duration);

                return result;

            }catch(Throwable ex){

                log.warn("{}#{} ex={}", className, methodName, ex.toString());

                throw ex;
            }

        }
    }
    ```
