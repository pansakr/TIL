### Java SE(Standard Edition) 와 Java EE(Enterprise Edition - 현재 Jakarta EE)

* Java SE

    - 기본 Java 언어와 핵심 라이브러리 모음

        - 언어 문법 : class, interface, enum, generic ..

        - 기본 API : java.lang, java.util ..

        - JDBC, 쓰레드/동시성 기능을 위한 클래스 

    - Java 버전

        - 언어 문법, jvm, Java SE 의 API 버전을 말함

        - 즉 자바의 버전은 Java SE 가 몇 번째 버전인지 나타내는 것

    - Java SE 의 구현체는 JDK 이고, JDK 설치 시 Java SE 사용 가능

        - ex) OpenJDK, Oracle JDK, Amazon Corretto ..


* Java EE (현재는 Jakarta EE 로 이름 변경)

    - Java SE 에 웹/서버 실행에 필요한 기능을 추가로 정의한 표준(스펙)

        - Servlet / JSP: HTTP 요청/응답을 처리하는 웹 애플리케이션 표준

        - Filter: 요청/응답을 가로채서 전처리/후처리

        - JPA: ORM 표준 (Java 객체 ↔ DB 테이블 매핑)

    - Java SE JVM 위에서 실행되기 때문에 SE 버전의 영향을 받는다

        - EE 에서 정의된 클래스들이 SE 의 클래스들을 사용함

        - EE 구현체는 결국 JVM 위에서 실행되기 때문에 SE 버전에 따라 실행 가능 여부나 동작 방식이 달라짐

    - Java EE 는 표준(스펙) = 문서 이므로 실제로 실행하려면 구현체가 필요

        - 표준(스펙) : '어떤 기능을 어떻게 제공해야 한다' 를 정의한 문서  

        - 구현체 : 스펙대로 동작하도록 만든 코드

        - 예시

            - Servlet 스펙 구현 : Tomcat, Jetty

            - JPA 스펙 구현 : Hibernate 
            
            - Java EE 풀 스펙(모든 기능) 구현 : GlassFish, WildFly

    - 스프링 부트와 관계

        - 필터/서블릿 : Java EE 스펙으로 톰캣 같은 WAS가 구현해둠

        - 스프링 부트는 톰캣을 포함하고 있어 별도 설치 없이 실행 가능

        - 톰캣이 가진 Servlet/Filter 기능을 쉽게 연결해 쓸 수 있도록 해줌
        
            - Interceptor는 Servlet 스펙이 아니라, Spring MVC가 제공하는 별도의 기능
