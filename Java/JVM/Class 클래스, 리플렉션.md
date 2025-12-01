### 자바의 Class 클래스 (클래스 이름이 Class)

* 클래스의 정보를 담는 설계도 객체

    - 하나의 클래스에 대한 모든 정보를 담고 있다

    - 경로 : java.lang.Class

    - Class(java.lang.Class) 객체에 담기는 정보

        ```java
        // 처음 참조될 때 User 의 정보가 담긴 Class 객체가(Class<User>) Heap 에 생성됨
        public class User{
            private String name;

            public void hello(){}
        }
        ```

        - 필드/메서드/생성자 목록

        - 접근 제어자/패키지 정보

        - 부모 클래스 및 구현한 인터페이스

        - 어노테이션, static 변수 정보

* 생성되는 시점

    - 클래스가 처음 참조되어 JVM 에 로드될 때 Class(java.lang.Class) 객체가 자동으로 생성된다

        - 생성자는 private 로 new 로 만들 수 없게 되어 있다

        - 클래스의 메타데이터는 JVM 이 정확한 시점에 로딩하고 관리해야 하기 때문에 오직 JVM 만 생성할 수 있다

        - Class(java.lang.Class) 객체는 클래스마다 단 하나만 존재함

    - new 로 객체를 생성할 때

        ```java
        User user = new User();
        ```

        - User 클래스가 처음 사용되는 경우

            - User.class 파일을 찾음

            - 클래스 로더가 User.class 정보를 메서드 영역에 로드

            - User 의 정보가(필드 구조, 생성자 정보 등) 담긴 Class<User> 객체가 Heap 에 생성 (java.lang.Class 타입 객체)

            - JVM 이 Class<User> 정보를 참조해 Heap 에 User 인스턴스(객체) 생성

            - User 객체 생성되기 전 반드시 Class 객체가 먼저 자동으로 만들어짐
        
        - User 클래스가 이전에 이미 로드된 경우

            - Class 객체는 다시 생성되지 않고 기존 것을 재사용함

    - static 메서드/변수의 첫 접근시

        ```java
        Math.random()   // Math 클래스의 static 메서드
        ```

        - Math 클래스 첫 접근시 JVM 에 로드

        - Class<java.lang.Math> 객체가(java.lang.Class 타입) Heap 에 생성

        - static 필드 초기화 후 random() 실행

    - 프로그래머가 Class.forName("패키지명.클래스명") 호출 시

        ```java
        Class<?> clazz = Class.forName("com.example.User");
        ```

        - User.class 파일이 JVM 로드

        - Class<User> 객체가(java.lang.Class 타입) Heap 에 생성

        - 클래스 초기화 블록 실행
    

### 리플렉션

* 런타임에 클래스 정보를(필드, 메서드, 생성자, 어노테이션) 읽고 조작할 수 있는 기술

    - Class(java.lang.Class) 객체를 기반으로 동작

    - 컴파일 시점이 아닌 실행 중에 클래스 구조를 읽고 수정할 수 있음

* 어떤 클래스를 사용할지 실행 시점에만 알 수 있는 경우 사용됨

* 스프링, JPA, AOP 같은 프레임워크는 리플렉션을 사용한다

    - 스프링은 컴파일 시점에 어떤 .class 파일이 있는지 알 수 없다

        - 컴파일은 JDK 가 하고, 스프링은 컴파일 과정에 참여하지 않기 때문에 개발자가 만든 클래스 목록을 미리 알 수 없다

        - 애플리케이션이 시작되면 스프링은 스스로 class 파일들을 찾아서 어떤 클래스가 있고, 해당 클래스가 무슨 역할인지 직접 알아내야 한다

        - 이 동작이 리플렉션이며, 스프링/JPA/AOP 는 리플렉션 없이는 동작이 불가능하다

    - 스프링 DI(의존성 주입)

        - 실행 시 .class 파일이 뭐가 있는지 모르기 때문에 실행 시점에 JVM 으로부터 classpath 를 기반으로 직접 .class 파일을 찾는다

            - classpath : .class 파일들이 존재하는 디렉토리 목록

        - 스프링이 설정에서 컴포넌트 스캔 경로를 보고 지정된 범위의 패키지를 스캔한다 

        - 해당 디렉토리들 내부의 .class 파일 목록을 가져온다

        - .class 파일을 하나씩 열어보고 메타데이터를 읽는다

            - 여기서 리플렉션이 사용됨

            - 이 .class 파일에 @Component, @Service, @Controller 가 붙었는가?

            - 어떤 생성자/필드/메서드가 있는가?

            - 어떤 어노테이션이 있는가?

            ```java
            // 코드 예시
            MetadataReader reader = metadataReaderFactory.getMetadataReader(className);
            AnnotationMetadata metadata = reader.getAnnotationMetadata();
            ```

        - 빈으로 등록할 클래스를 결정하고 의존성을 주입한다

            - UserService.class 에 @Service 가 붙어있고 생성자에 어떤 파라미터가 필요한지 확인

            - 파라미터 타입을 먼저 빈으로 생성 후 UserService.class 의 생성자의 매개변수로 사용해 객체 생성

            - UserService.class 빈으로 등록


* 동작 원리

    - JVM 로딩 시 생성된 Class(java.lang.Class) 객체의 구조 정보를 읽어 탐색 및 조작


- 리플렉션의 기능

    - 클래스 동적 로드(Class.forName)

    - 객체 생성(newInstance, Constructor)

    - private 필드/메서드 접근

    - 메서드 동적 호출(Method.invoke)

* 단점

    - 성능 저하 (invoke 는 직접 호출보다 느림)

    - private 접근 가능하므로 캡슐화 파괴 위험

* 리플렉션을 사용하는 프레임워크

    - 스프링 DI/AOP

        - @Autowired, Bean 생성, AOP 프록시, @Controller 매핑 등

    - JPA

        - 엔티티 필드 분석, DB 매핑, 프록시 생성, Lazy 로딩 등

    - Jackson 직렬화/역직렬화

        - JSON <-> 객체 변환 시 필드/생성자 탐색

    - JUnit 테스트 러너

        - @Test 붙은 메서드 실행

    - 프록시 기반 라이브러리(Mockito 등)

        - private 필드 주입 및 프록시 생성
