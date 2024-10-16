### 객체와 테이블 매핑

* JPA 설정 파일 옵션

    - 애플리케이션 실행 시점에 데이터베이스 스키마 자동 생성 옵션

        - jpa 설정 파일의 'spring.jpa.hibernate.ddl-auto=값' 옵션을 사용해 데이터베이스 DDL을 자동으로 작성하여 테이블을 생성하거나 수정할 수 있다

        - 운영 환경은 none을 사용하며 create, create-drop,update를 사용하면 안된다

            - create

                - 기존 테이블 삭제 후 생성

                - 애플리케이션 시작 시 기존 테이블 삭제 후 생성

            - create-drop

                - 테이블 생성 후 종료시점에 삭제

                - 보통 테스트 할때 사용하는 옵션

            - update

                - 변경분만 DB에 반영

                - 삭제는 반영 안됨

            - validate

                - 엔티티와 테이블이 정상 매핑되었는지 확인

            - none

                - DDL 자동 작성 기능 사용하지 않음

    - 데이터베이스 방언 설정

        - 'org.hibernate.dialect.방언' 옵션으로 어떤 DB 벤더의 sql이 자동 생성되게 할지 정할 수 있다

        - org.hibernate.dialect.Oracle10gDialect 옵션은 오라클 문법의 sql이 생성된다


* @Entity

    - @Entity 가 붙은 클래스를 JPA 가 관리해주며, 엔티티라 한다

    - JPA를 사용해 테이블과 매핑할 클래스에는 필수로 사용해야 한다

    - 기본 생성자가 필수이고, final 클래스, enum, 인터페이스, 내부 클래스에는 사용하지 못한다

* @Table

    - 엔티티와 매핑할 테이블 지정

    - @Table 의 name 속성

        - 매핑할 테이블 이름 지정

        - 기본값은 엔티티 클래스의 이름이며, 대부분 기본값으로 사용한다

* @Column

    - 엔티티의 필드와 매핑할 컬럼 지정 

    - @Column 의 name 속성

        - 필드와 매핑할 테이블의 컬럼 이름

        - 기본값은 객체의 필드 값

        ```java
        // 필드 이름은 username, 테이블의 컬럼 이름으로는 name 으로 저장
        @Column("name")
        private String username;
        ```
    
    - nullable = true/false 속성

        - @Column 이 적용된 필드와 매핑된 DB 컬럼에 NOT NULL 제약조건을 걸거나 걸지 않는다

        - DB 만 NOT NULL 제약조건이 걸리고, 애플리케이션에서는 null 할당을 방지하지 않는다

            - 애플리케이션에서 null 값을 해당 필드에 할당하려 할때 예외가 발생하지 않고, DB에 실제로 저장하려 할때 NOT NULL 제약조건에 의해 예외가 발생한다

            - 필드 값에 null 검사를 하려면 @NotNull 같은 Bean Validation 을 사용해야 한다

        - ddl-auto create 또는 update 옵션이 아니면 실제 DB에 영향을 미치지 않으므로 필드와 매핑된 컬럼의 제약 조건을 확인하는 가독성을 높이는 용도로 사용한다

    - length

        - 매핑된 컬럼의 길이 제한

        - ddl-auto create 또는 update 옵션이 아니면 실제 DB에 영향을 미치지 않으므로 필드와 매핑된 컬럼의 제약 조건을 확인하는 가독성을 높이는 용도로 사용한다


* @Enum

    - 자바 Enum 타입을 DB의 컬럼과 매핑할때 사용

    - 속성

        - EnumType.ORDINAL, EnumType.String 의 두가지 속성이 있다

            - EnumType.ORDINAL : enum 순서를 숫자 타입으로 DB 에 저장. 기본값
            
            ```java
            // EnumType.ORDINAL 로 설정

            public enum RoleType{
                GUEST, USER, ADMIN
            }

            // Entity 클래스
            class .. {

                ..

                @Enum(EnumType.ORDINAL)
                public Enum RoleType
            }

            // main 메서드
            .. main{
                
                ..

                member.setRoleType(GUEST)

                em.persist(member)
            }

            // DB 테이블
            RoleType
            0

            // EnumType.ORDINAL 은 순서가 숫자로 저장된다
            // GUEST가 첫 번째이니 0, USER 은 두번째니 1, ADMIN 은 세번째니 2 로 저장된다
            ```

            - EnumType.String : enum 이름을 DB 에 저장

        - 무조건 EnumType.String 을 사용한다

* @Temporal

    - 날짜 타입을 매핑할 때 사용

    - 속성

        - TemporalType.DATE, TIME, TIMESTAMP 의 세 가지 속성이 있다

        - 사용하는 날짜 타입에 맞춰 사용한다

            - TemporalType.DATE : 날짜, DB의 date 타입과 매핑 (ex 2024-10-16)

            - TemporalType.TIME : 시간, DB의 time 타입과 매핑 (ex 10:10:11)

            - TemporalType.TIMESTAMP : 날짜와 시간, DB의 timestamp 타입과 매핑 (ex 2024-10-16 10:10:11)

        - 보통 날짜, 시간 모두 사용하므로 TIMESTAMP 를 사용한다

    - LocalDate, LocalDateTime 타입을 사용한다면 생략 가능하기 때문에 잘 사용하지 않는다

        - LocalDate - date 타입과 매핑, LocalDateTime - timestamp 타입과 매핑

* @LOB

    - DB의 BLOB, CLOB 타입과 매핑

    - 매핑하는 필드 타입이 문자면 CLOB, 나머지는 BLOB 로 매핑된다

* @Transient

    - DB의 컬럼과 매핑되지 않음

    - 이 어노테이션이 적용된 필드는 DB의 컬럼과 매핑되지 않는다

        - DB와 관계없는 필드가 되고, 주로 메모리에서만 임시로 어떤 값을 보관하고 싶을 때 사용한다

* @Id

    - DB의 기본 키와 매핑

    - 기본 키를 직접 할당할 시 @Id 만 사용

    - 기본 키를 자동 생성할 시 @GeneratedValue 함께 사용

    ```java
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public String name;
    ```
    - @GeneratedValue 옵션

        - IDENTITY

            - 기본 키 생성을 데이터베이스에 위임
            
            - mysql, PostgreSQL 에서 사용

            - IDENTITY 전략 사용 시 persist() 를 실행하면 즉시 INSERT SQL이 실행되어 DB에서 식별자를 조회한다

                - JPA는 쓰기 지연 저장소에 모아뒀던 SQL 들을 트랜잭션 커밋 시점에 DB에 반영한다

                - IDENTITY 사용 시 persist() 실행 시점에 INSERT SQL 이 실행되므로 SQL을 모아서 보내지 못한다

        - SEQUENCE

            - 데이터베이스에 시퀀스 오브젝트 사용
            
            - ORACLE, PostgreSQL 에서 사용
            
            ```java
            @Entity
            @SequenceGenerator(
                name = “MEMBER_SEQ_GENERATOR",
                sequenceName = “MEMBER_SEQ", //매핑할 데이터베이스 시퀀스 이름
                initialValue = 1, allocationSize = 1)
            public class Member {
            @Id
            @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEMBER_SEQ_GENERATOR")
            private Long id;
            ```

        - TABLE

            - 키 생성 전용 테이블을 하나 만들어서 데이터베이스 시퀀스를 흉내내는 전략
            
            - 모든 DB에서 사용

            - 성능이 떨어진다

        - AUTO

            - DB 방언에 따라 자동 지정. 기본값

            - IDENTITY, SEQUENCE, TABLE 세 가지 중 사용하고 있는 DB에 맞게 자동 선택됨
