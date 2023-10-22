### application.yaml 파일 설정

* 개발(dev), 테스트(stg), 운영(prod) 별로 사용하는 db, 로깅등 여러 설정들이 달라진다.

* 스프링 부트에서는 .yml 파일로 프로젝트별 설정 값을 나눌 수 있다.

* application.yaml - default 프로젝트 설정 값

  application-local.yaml - 로컬 환경에서의 프로젝트 설정 값

* application-dev.yaml - 개발 환경에서의 프로젝트 설정 값

* application-stg.yaml - 테스트 환경에서의 프로젝트 설정 값

* application-prod.yaml - 운영 환경에서의 프로젝트 설정 값

* 실행하는 profile에 따라서 yaml에 default로 설정해둔 값이 오버라이드 됨


### profile 설정

* 운영 환경별로 설정들이 달라지므로 이를 위해 설정 파일들을 운영 환경별로 분리해 작성한다.

* 각각 환경별로 분리된 파일들은 application.yaml 파일을 오버라이딩하여 사용한다.

* 따라서 공통 설정은 application.yaml에 작성하고 환경별 달라지는 설정들을 오버라이드하면 된다.

* 실행 시 실행아이콘 왼쪽의 xxxApplication -> edit configurations -> vm Option에 
-Dspring.profiles.active="프로파일 이름" 을 설정하면 해당 프로파일의 yml설정을 읽어 실행한다.

* yml에 server.servlet.context-path 옵션은 루트 경로를 의미한 것으로 보통은 /로 하지만 
실행 환경을 명확하게 확인할 수 있게 최상위 경로로 해당 실행환경 이름을 지정할 수 있다.

* 인텔리제이 유료 버전은 edit -> Active Profile 란에 프로파일 이름을 적으면 된다.


### 환경별 yml 설정

* application.yml(공통 설정), application-local.yml, application-stg.yml 로 구성

* application.yml

```yml
// 어떤 환경으로 실행하던 같이 실행되는 파일
spring:
  profiles:
    active: local  // 기본으로 실행할 설정 파일 지정

// 공통 설정
server:
  port: 8080
```

* application-local.yml

```yml
spring:
  config:
    activate:
      on-profile: local  // 설정 파일 이름 지정

// 개별 설정. 이 환경으로 실행할때만 적용된다.
...
```

* application-stg.yml

```yml
spring:
  config:
    activate:
      on-profile: stg  // 설정 파일 이름 지정

// 개별 설정. 공통 설정과 같은 옵션이 있다면 오버라이드 되어 실행된다.
server:
  port: 8081
```

### LogLevel

* .yaml에 로그 범위부터 패키지 범위까지 설정할 수 있다.

*  TRACE  <  DEBUG  <  INFO  <  WARN  <  ERROR 의 순서를 가진다.

* 패키지별로 로그 레벨을 설정할 경우 상위 패키지의 로그를 먼저 설정하고 하위 패키지의 로그를 설정하는 방식으로 한다. 

```
logging:
  level:
   root: info // info는 default 옵션이다.
              // info 이상의 로그를 출력한다. 즉 TRACE, DEBUG 레벨의 로그는 출력하지 않는다.
    
```
