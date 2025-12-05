### logback-spring.xml 설정

```xml
<!-- logback-spring.xml-->
<?xml version="1.0" encoding="UTF-8"?>
<configuration scan="true">

    <!-- 공통 property -->

    <!-- SpringProperty: application.yml 의 값을 logback 에서 읽어옴 -->
    <springProperty scope="context" name="LOG_HOME" source="logging.file.path" defaultValue="./logs"/>

    <property name="LOG_HOME" value="/var/log/subscribe"/>

    <property name="LOG_PATTERN"
              value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [trace=%X{traceId}] %logger{36} - %msg%n"/>

    <!-- 콘솔 출력 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
        </encoder>
    </appender>

    <!-- 로그 저장 규칙 -->
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

    <!-- ERROR 전용 로그 파일 -->
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

    <!-- 비동기 로깅 설정 -->
    <appender name="ASYNC_APP" class="ch.qos.logback.classic.AsyncAppender">
        <appender-ref ref="APP_FILE"/>
        <queueSize>2048</queueSize>
        <discardingThreshold>0</discardingThreshold>
    </appender>

    <logger name="com.example.subscribe" level="INFO" additivity="false">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="ASYNC_APP"/>
        <appender-ref ref="ERROR_FILE"/>
    </logger>

    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="ASYNC_APP"/>
        <appender-ref ref="ERROR_FILE"/>
    </root>

</configuration>
```

```yml
# application-local.yml
logging:
  file:
    path: ./logs
```

```yml
# application-prod.yml
logging:
  file:
    path: /var/log/demo
```

* yml 에 운영 환경별 path 값을 넣지 않고 logback-spring.xml 에 운영 환경별 설정도 가능

    - 중복되는 설정이 많아져서 비추천

        - LOG_HOME 변수를 환경별로 다르게 설정해야 하는데, yml 값을 받아오지 않는다면 환경별로 하나씩 설정해줘야 한다

        - 설정한 LOG_HOME 값을 사용하는 appender 도 환경별로 똑같이 설정해줘야 한다
        
        - LOG_HOME 값을 제외하면 동일한 appender 가 환경별로 생기기 때문에 중복이 생기고, 설정이 길어진다 

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <configuration scan="true">

        <!-- 공통: 로그 패턴 & 콘솔 -->
        <property name="LOG_PATTERN"
                value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [trace=%X{traceId}] %logger{36} - %msg%n"/>

        <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>${LOG_PATTERN}</pattern>
            </encoder>
        </appender>

        <!-- local -->
        <springProfile name="local">

            <!-- local: ./logs -->
            <property name="LOG_HOME" value="./logs"/>

            <!-- 파일 appender들: LOG_HOME=./logs 기준으로 설정 -->
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

            <appender name="ASYNC_APP" class="ch.qos.logback.classic.AsyncAppender">
                <appender-ref ref="APP_FILE"/>
                <queueSize>2048</queueSize>
                <discardingThreshold>0</discardingThreshold>
            </appender>

            <logger name="com.example.subscribe" level="DEBUG" additivity="false">
                <appender-ref ref="CONSOLE"/>
                <appender-ref ref="ASYNC_APP"/>
                <appender-ref ref="ERROR_FILE"/>
            </logger>

            <root level="INFO">
                <appender-ref ref="CONSOLE"/>
                <appender-ref ref="ASYNC_APP"/>
                <appender-ref ref="ERROR_FILE"/>
            </root>
        </springProfile>

        <!-- prod -->
        <springProfile name="prod">

            <!-- prod: /var/log/subscribe -->
            <property name="LOG_HOME" value="/var/log/subscribe"/>

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

            <appender name="ASYNC_APP" class="ch.qos.logback.classic.AsyncAppender">
                <appender-ref ref="APP_FILE"/>
                <queueSize>2048</queueSize>
                <discardingThreshold>0</discardingThreshold>
            </appender>

            <logger name="com.example.subscribe" level="INFO" additivity="false">
                <appender-ref ref="CONSOLE"/>
                <appender-ref ref="ASYNC_APP"/>
                <appender-ref ref="ERROR_FILE"/>
            </logger>

            <root level="INFO">
                <appender-ref ref="CONSOLE"/>
                <appender-ref ref="ASYNC_APP"/>
                <appender-ref ref="ERROR_FILE"/>
            </root>
        </springProfile>

    </configuration>
    ```
