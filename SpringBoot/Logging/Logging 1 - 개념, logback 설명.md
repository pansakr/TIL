### 로깅

* 시스템의 동작 상태와 이벤트 정보를 시간 순서대로 기록하는 작업

* 사용 목적

    - 디버깅 및 문제 해결

    - 시스템 모니터링

    - 보안 감사

        - 사용자 활동 및 시스템 접근 기록을 남기기 때문에 보안 침해 사고 발생 시 추적 및 증거로 사용될 수 있다

    - 데이터 분석

        - 수집된 로그 데이터를 분석해 사용자 행동 패턴을 이해하고 제품 개선에 활용할 수 있다

* 기본 개념

    - 로그 레벨

        - ERROR
        
            - 예외/장애, 요청 실패, 비즈니스 문제, 시스템 오류 등을 나타냄

        - WARN

            - 당장 서비스 운영에 큰 영향을 미치지 않지만 주의해야 할 부분을 나타냄
        
            - 또는 로직 상 유효성 확인이나 예상 가능한 문제로 인해 예외 처리가 필요한 경우 명시적으로 직접 사용

        - INFO

            - 시스템의 정상적인 동작을 나타냄

            - 운영에 참고할 만한 사항이나 중요한 비즈니스 프로세스가 완료되었을 때 명시적으로 직접 사용

        - DEBUG

            - SQL 쿼리 로깅 등 상세한 정보를 나타내며 개발 단계에서 사용됨

        - TRACE

            - DEBUG 보다 더 자세한 로깅을 나타내며 개발 단계에서 사용됨

            - 라이브러리/프레임워크 내부 동작을 살펴볼 때 사용

                - 특정 패키지/클래스만 잠깐 TRACE 로 살펴보고 문제 추적할 때 쓰는 용도

    - 운영은 INFO 이상, 개발/로컬은 DEBUG 또는 TRACE 까지 사용됨


* 스프링에서 로깅을 사용하는 방법

    - SLF4J + Logback 조합으로 로깅 사용

    - SLF4J

        - 여러 로깅 라이브러리를 통합하여 인터페이스로 제공하는 추상화 라이브러리

        - 이를 통해 애플리케이션 코드가 특정 로깅 라이브러리에 종속되지 않도록 할 수 있다

        - 스프링 부트 스타터 의존성에 로깅이 포함되어 있으며, 이 안에 SLF4J 와 Logback 이 함께 들어있다

    - Logback

        - SLF4J 의 구현체

        - 대부분의 실무에서 이 구현체를 사용
        
        - SLF4J 의 인터페이스를(API) 통해 Logback 을 호출하는 방식으로 사용한다

    - System.out.println() 과 비교햇을때 장점

        - 부가 정보 제공

            - 스레드 정보, 클래스 이름 등 부가 정보를 볼 수 있음

        - 출력 형식을 자유롭게 지정 가능

        - 로그 레벨에 따라 남기고 싶은 로그를 별도로 지정할 수 있다

        - 콘솔, 파일, DB, 원격 로그 서버 등 여러 위치에 로그를 남길 수 있다

    - 기본 설정

        - 로깅 라이브러리는 기본 스타터 의존성에 포함되어 있으므로 별도의 설정이 필요 없음

        ```java
        // @SLF4J 미사용 시
        @RestController
        public class HelloController {

            private static final Logger log = LoggerFactory.getLogger(HelloController.class)

            @GetMapping("/hello")
            public String hello() {
                log.info("hello");
                return "hello";
            }
        }

        // @SLF4J 사용 시
        @SLF4J
        @RestController
        public class HelloController {

            @GetMapping("/hello")
            public String hello() {
                log.info("hello");
                return "hello";
            }
        }
        ```
    * 로깅 사용 기준 정하기

        - 어디서 어떤 레벨로 로깅할지 규칙을 정해야 한다

        - 컨트롤러 (예시)

            ```java
            @PostMapping("/users")
            public UserResponse signUp(@RequestBody UserRequest request) {
                log.info("[UserSignUp] start email={}", request.getEmail());
                UserResponse response = userService.signUp(request);
                log.info("[UserSignUp] success userId={}", response.getId());
                return response;
            }
            ```

            - INFO : 요청 시작/종료에 로깅하며 핵심 요약 기록

            - DEBUG : 요청 바디, 세부 파라미터 (개발 환경)

        - 서비스

            ```java
            @Transactional
            public UserResponse signUp(UserRequest request) {

                if (userRepository.existsByEmail(request.getEmail())) {
                    log.warn("[UserSignUp] duplicated email={}", request.getEmail());
                    throw new UserException("EMAIL_DUPLICATED");
                }

                User user = userRepository.save(UserMapper.toEntity(request));
                log.debug("[UserSignUp] new user saved id={}", user.getId());

                return UserMapper.toResponse(user);
            }
            ```

            - INFO : 비즈니스 흐름의 중요한 이벤트

            - DEBUG : 디버그가 필요할 만한 내부 상태

        - 예외 처리

            ```java
            @Slf4j
            @RestControllerAdvice
            public class GlobalExceptionHandler {

                @ExceptionHandler(UserException.class)
                public ResponseEntity<ErrorResponse> handleUserException(UserException e) {
                    log.warn("[UserException] code={}, message={}", e.getCode(), e.getMessage());
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body(new ErrorResponse(e.getCode(), e.getMessage()));
                }

                @ExceptionHandler(Exception.class)
                public ResponseEntity<ErrorResponse> handleException(Exception e) {
                    log.error("[UnexpectedException]", e); // 스택트레이스 출력
                    return ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new ErrorResponse("INTERNAL_SERVER_ERROR", "잠시 후 다시 시도해주세요."));
                }
            }
            ```

            - WARN : 예상 가능한 예외 (클라이언트 예외 등)

            - ERROR : 예상하지 못한 예외
        
    * 설정으로 로그 레벨/출력 제어

        ```yaml
        // 개발 환경
        logging:
        level:
            root: INFO # 외부 라이브러리 INFO
            com.example.subscribe: DEBUG  # 내 패키지는 DEBUG

        ```
        ```yaml
        // 운영 환경
        logging:
        level:
            root: INFO
            org.hibernate.SQL: WARN
        file:
            name: /var/log/subscribe/app.log
        ```

    * logback 설정

        - 롤링 규칙 : 로그 파일을 효율적으로 관리하기 위한 규칙

            - 로그 파일을 일정 기간 또는 특정 용량만큼 보관해 로그 파일의 크기가 무한대로 커지는 것을 방지하기 위한 방법

        - 간단한 설정은 application.properties, 세부 설정은 logback-spring.xml 에 한다

        - logback-spring.xml 설정

            - 설정할 옵션

                - 콘솔 출력 : 개발할 때 IDE 콘솔 확인용

                - 파일 저장 : 운영 서버에서 분석할수 있도록

                - 롤링 : 로그 파일이 너무 커지지 않게 날짜/용랑 기준으로 나누고, 오래된 건 삭제 및 압축

                - ERROR 로그 분리 : 장애 분석할 때 보기 쉽도록

            - 공통 설정 : 경로/패턴

                ```xml
                <property name="LOG_HOME" value="./logs"/>

                <property name="LOG_PATTERN" value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [trace=%X{traceId}] %logger{36} - %msg%n"/>
                ```

                - <property> 태그
                
                    - 설정 파일내에서 재사용할 수 있는 변수 정의 

                    - name
                    
                        - 해당 property 를 식별할 수 있는 고유 이름

                        - name 옵션에 지정된 이름을 다른 설정에서 ${변수이름} 으로 사용할 수 있다

                    - value
                    
                        - 변수에 저장할 값 설정 (경로, 패턴 문자열, 숫자 등)

                - ./logs : 로그가 저장될 경로 설정 (상대경로)
                    
                    - 현재 실행 위치 기준으로 logs 폴더

                    - /myApp 에서 프로젝트 실행 시 /myApp/logs 폴더에 생성

                - /var/log/myAppLogs (절대경로)

                    - 우분투 기준

                    - 환경별 다른 설정 적용할 수 있음 ?

                - %d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [trace=%X{traceId}] %logger{36} - %msg%n

                    - 로그 한 줄의 모양을 정의

                    - %d{...} → 날짜/시간

                    - %thread → 로그를 찍은 스레드 이름 (웹 요청이면 http-nio-8080-exec-1)

                    - %-5level → 로그 레벨 (INFO, WARN, ERROR…)

                    - %X{traceId} → MDC에 넣어둔 traceId

                    - %logger{36} → 로그 찍은 클래스 이름 (길이 36자로 제한)

                    - %msg → log.info("...") 에서 쓴 메시지

                    - %n → 줄바꿈

                    ```
                    // 예시
                    2025-12-01 18:10:32.123 [http-nio-8080-exec-1] INFO  [trace=ab12cd34] c.e.s.user.UserService - [UserSignUp] success userId=1
                    ```

            - Appender

                - 로깅 프레임워크에서 로그 메시지가 어디에 어떤 형식으로 출력될지 결정하는 구성 요소

                    - 애플리케이션이 생성한 로그 메시지는 appender 이 지정하는 목적지로 출력됨

                - 여러 종류의 Appender 가 존재함

            - ConsoleAppender

                - 로그 메시지를 화면에(콘솔 또는 터미널) 출력

                - 별도 설정 없다면 기본적으로 활성화 되어 있으며, 개발 중인 환경에 실시간으로 로그를 보여줌

                - 경로 : ch.qos.logback.core.ConsoleAppender

                ```
                <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
                    <encoder>
                        <pattern>${LOG_PATTERN}</pattern>
                    </encoder>
                </appender>
                ```

                - name

                    - 해당 appender 를 식별하기 위한 고유 이름

                - class

                    - 실제로 로그를 처리하고 출력하는 구체적인 자바 구현체 설정

                    - Logback 프레임워크에 로그 출력 시 어떤 종류의 클래스를 사용할 것인지 지정해주는 것

                    - 콘솔에 로그를 찍을 거니 ConsoleAppender 지정

                - <encoder> 태그

                    - 로그 메시지의 형식과 인코딩을 설정

                    - <pattern>${LOG_PATTERN}</pattern>

                        - 위에서 만든 LOG_PATTERN 사용

            - FileAppender + RollingFileAppender

                - FileAppender

                    - 로그를 지정한 파일에 기록

                    - 경로 : ch.qos.logback.core.FileAppender

                - RollingFileAppender

                    - 로그 파일이 너무 커지는 것을 방지하기 위해, 파일 크기/날짜별로 새 로그파일을 생성하고 관리

                    - RollingFileAppender 이 FileAppender 기능을 포함하며 추가적인 기능을 제공

                    - 운영 환경에서 주로 사용

                    - 경로 : ch.qos.logback.core.rolling.RollingFileAppender

                ```xml
                <appender name="APP_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
                    <file>${LOG_HOME}/app.log</file>

                    <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
                        <fileNamePattern>${LOG_HOME}/app.%d{yyyy-MM-dd}.%i.log.gz</fileNamePattern>
                        <maxFileSize>100MB</maxFileSize>
                        <maxHistory>30</maxHistory>
                        <totalSizeCap>10GB</totalSizeCap>
                    </rollingPolicy>

                    <encoder>
                        <pattern>${LOG_PATTERN}</pattern>
                    </encoder>
                </appender>
                ```

                - <file>

                    - 현재 시점에 로그를 기록할 파일의 이름과 경로 지정

                - rollingPolicy

                    - 로그 파일 교체 규직 정의

                    - SizeAndTimeBasedRollingPolicy : 날짜 + 크기 기준으로 롤링 설정하는 클래스

                - fileNamePattern

                    - 롤링된 과거 로그 파일들 이름 패턴

                    - ${LOG_HOME}/app.%d{yyyy-MM-dd}.%i.log.gz

                        - logs/app.2025-12-01.0.log.gz

                        - logs/app.2025-12-01.0.log.gz

                    - .gz : gzip 압축까지 해줌

                - maxFileSize

                    - 하루치의 로그에서 개별 로그 파일의 최대 용량

                    - 100MB 를 넘으면 ...0.log.gz -> ...1.log.gz 식으로 늘어남

                - maxHistory

                    - 로그 보관 일수 설정

                    - 30 : 최근 30일 까지 저장, 그 이전 파일은 삭제

                - totalSizeCap

                    - 전체 저장할 로그 용량의 상한

                    - 10GB 넘어가면 가장 오래된 파일부터 삭제

                - 요약

                    - app.log 에 계속 쓰다가, 특정 시점(날짜바뀜/용량초과)에 app.날짜.번호.log.gz 로 옮기고 다시 새 로그를 app.log에 쓰는 구조

                    - 예시 1

                        - 서버 시작 : logs/app.log 에 파일 생성

                        - 로그가 계속 app.log 에 쌓임

                        - 하루동안 설정된 용량 제한을(100mb) 넘지 않았다면 app.log 하나만 있음

                        - 용량을 넘는 순간 기존 app.log 를 app.2025-12-01.0.log.gz 로 이름 바꾸고 압축

                        - 새로운 app.log 파일을 다시 만들어서 로그를 계속 씀

                        - 또 용량이 넘었다면 app.2025-12-01.1.log.gz 로 이름 바꾸고 압축 후 새로운 app.log 파일 생성

                    - 예시 2

                        - 서버 시작 : logs/app.log 에 파일 생성

                        - 로그가 app.log 에 쌓이다가 날짜가 바뀜

                        - 기존의 app.log 를 app.2025-12-01.(번호).log.gz 로 바꾸고(번호 : 마지막 번호)로 이름을 바꾸고 옮김

                        - 바뀐 날자용 app.log 생성

                        - 바뀐 날짜부터의 로그는 새 app.log 에 쌓임  

            - ERROR 만 따로 모으는 Appender

                ```xml
                <appender name="ERROR_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
                    <file>${LOG_HOME}/error.log</file>

                    <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
                        <fileNamePattern>${LOG_HOME}/error.%d{yyyy-MM-dd}.%i.log.gz</fileNamePattern>
                        <maxFileSize>50MB</maxFileSize>
                        <maxHistory>60</maxHistory>
                        <totalSizeCap>5GB</totalSizeCap>
                    </rollingPolicy>

                    <encoder>
                        <pattern>${LOG_PATTERN}</pattern>
                    </encoder>

                    <filter class="ch.qos.logback.classic.filter.LevelFilter">
                        <level>ERROR</level>
                        <onMatch>ACCEPT</onMatch>
                        <onMismatch>DENY</onMismatch>
                    </filter>
                </appender>
                ```

                - filter

                    - 특정 조건을 만족하는 로그 메시지만 선택적으로 처리하거나, 특정 메시지를 제외할 때 사용

                    - LevelFilter : 특정 레벨만 받아들이는 필터

                - level
                
                    - 필터 조건 설정

                    - ERROR : 로그의 레벨이 ERROR 인지 확인

                - onMatch
                
                    - 필터의 조건이 일치했을 때 어떻게 처리할지 설정

                    - ACCEPT : 로그 이벤트를 수락해 해당 Appender 로 출력 (나머지 필터 검사 건너뜀)

                    - DENY : 로그 이벤트를 거부하고 출력하지 않음 (나머지 필터 검사 건너뜀)

                    - NEUTRAL : 다음 필터에게 판단을 넘기거나, 끝이라면 Appender 의 기본 설정에 따라 처리

                - onMismatch

                    - 필터의 조건이 일치하지 않았을 때 어떻게 처리할지 설정

                    - onMatch 와 옵션 같음

            - AsyncAppender

                - 로그를 비동기적으로 처리해 성능을 향상

                    - 일반적으로 로깅은 메인 스레드에서 동기적으로 이루어진다

                    - 로그 기록 작업이 지연되면 사용자의 요청을 처리하는 메인 작업(비즈니스 로직)도 함께 자연됨

                - 원리

                    - 메인 스레드는 로그 메시지를 AsyncAppender 내부의 큐에 넣고 바로 사용자의 요청 처리로 넘어감
                    
                    - 로깅은 별도의 스레드를 사용해 작업

                - 비동기 로깅 설정을 하면 로그 순서가 순서대로 찍히지 않을 수 있으므로 중요한 로그는 동기로 설정하는 것이 좋다

                    - 예외 로그 같은 경우 문제를 순서대로 파악해야 하기 때문에 동기로 설정해야함

                - 경로 : ch.qos.logback.classic.AsyncAppender

                ```xml
                <appender name="ASYNC_APP" class="ch.qos.logback.classic.AsyncAppender">
                    <appender-ref ref="APP_FILE"/>
                    <queueSize>2048</queueSize>
                    <discardingThreshold>0</discardingThreshold>
                </appender>

                <appender name="ASYNC_ERROR" class="ch.qos.logback.classic.AsyncAppender">
                    <appender-ref ref="ERROR_FILE"/>
                    <queueSize>512</queueSize>
                    <discardingThreshold>0</discardingThreshold>
                </appender>
                ```

                - queueSize

                    - 큐에 쌓을 수 있는 로그 메시지 개수

                - discardingThreshold 

                    - 큐가 꽉 찼을때 로그 버리는 기준

                    - 0 이면 버리지 말고 최대한 다 쌓으려는 설정

            - logger / root 설정

                - 어떤 Appender 를 사용할 지 지정

                - 이 설정이 없다면 Appender 태그에 정의한 로그 규칙이 적용되지 않아 로그가 찍히지 않음

                ```xml
                <logger name="com.example.subscribe" level="DEBUG" additivity="false">
                    <appender-ref ref="CONSOLE"/>
                    <appender-ref ref="ASYNC_APP"/>
                    <appender-ref ref="ASYNC_ERROR"/>
                </logger>

                <root level="INFO">
                    <appender-ref ref="CONSOLE"/>
                    <appender-ref ref="ASYNC_APP"/>
                    <appender-ref ref="ASYNC_ERROR"/>
                </root>
                ```

                - <root> 태그

                    - 모든 패키지/클래스의 기본 로거 규칙 정의

                    - level 옵션 : 기본 로깅 레벨 설정

                - <appender-ref> 태그

                    - 이미 정의된 Appender 를 연결

                    - ref 옵션 : 연결하려는 Appender 의 고유 이름 지정

                - <logger> 태그

                    - 특졍 영역에 대해 기본 로깅 설정(root)과 다른 로깅 규칙을 적용할 때 사용

                    - name

                        - 적용할 범위 지정

                        - 패키지 경로 입력

                    - level : 로깅 레벨 설정

                    - additivity

                        - 상위 로거 상속 여부

                        - 기본값 : true

                        - 자신의 logger 에서 찍은 로그가 연결된 상위 로거로 흘러가 중복으로 출력될 수 있음

                        - 보통 중복 출력 방지를 위해 패키지 단위 logger 에 additivity = false 사용
