### API 설계 시 주의점

* 엔티티에 프레젠테이션 계층을 위한 로직이(검증 등) 들어가면 안된다

    - 엔티티 사용하는 여러 API 가 만들어지는데, 한 엔티티에 각각의 API가 요구사항을(검증 등) 담을 순 없다

    - 엔티티가 변경되면 API 스펙이 변한다

        - 엔티티와 매핑된 DB의 테이블이 변경되어 요청/응답 데이터가 변경

        - API 스펙은 아니지만 엔티티의 필드가 변경되면 기존의 필드명으로 호출하던 코드들을 모두 수정해야 한다


* API 요청 스펙에 맞춰 별도의 DTO를 만들어야 한다

    - 하나의 엔티티를 A, B 의 두 API가 사용할때 A는 name 필드가 null 이어도 되고, B는 null 이면 안될 수 있다

    - 이때는 API 마다 별도의 DTO 를 만들어서 내부 필드에 검증을 다르게 적용해야 한다

    - 즉 API 별로 다른 요구사항을 별도의 DTO 로 해결할 수 있다


* 응답 데이터에 List를 사용할때는 객체로 한번 감싸야 한다

    - List를 바로 응답하면 확장성이 떨어진다

    - List를 다른 객체로 한번 감싸서 반환해야 한다

    ```java
    // list 바로 응답
    public List<Member> membersV1{
        return memberService.findMembers(); 
    }

    // 응답 데이터
    [
        {
            "name": "member1"
        },
        {
            "name": "member2"
        }
    ]
    ```
    ```java
    public Result memberV2(){
        List<Member> findMembers = memberService.findMembers();

        // list 데이터인 findMembers 를 Result 객체에 담아서 반환
        return new Result(collect);
    }

    class Result<T>{
        private T data;
    }

    // 응답 데이터. 확장 가능
    {
        "data": [
            {
                "name": "member1"
            },
            {
                "name": "member2"
            }
        ]
    }

    // 확장한 응답 데이터
    // 필요 시 count 같은 항목을 추가할 수 있다
    {
        "count" : 2,
        "data": [
            {
                "name": "member1"
            },
            {
                "name": "member2"
            }
        ]
    }
    ```

### 엔티티 양방향 연관관계의 무한 참조 문제

* 응답에 양방향 연관관계의 엔티티 사용 시 무한 참조 발생

    - Order가 Member를 참조하고, Member 도 Order를 참조할 때 Order 호출 시 무한 참조 발생

        - Order 가 호출되어 참조하고 있는 Member 를 호출하고, Member 는 다시 내부의 Order 를 호출해 무한 참조 발생

    - 양방향 관계에서는 둘 중 하나에 @JsonIgnore 를 적용해야 무한 참조를 방지할 수 있다

    ```java
    @Entity
    @Getter @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED) // protected 기본 생성자 생성
    public class Order {

        @Id @GeneratedValue
        @Column(name = "order_id")
        private Long id;

        // 연관 관계 필드
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "member_id") // 외래키 이름 지정
        private Member member;
        }
    ```
    ```java
    @Entity
    @Getter @Setter
    public class Member {

        @Id @GeneratedValue
        @Column(name = "member_id")
        private Long id;

        @NotEmpty
        private String name;

        @Embedded
        private Address address;

        /**
        * mappedBy 값에 연관관계의 주인 엔티티(ORDER) 에서 지정한 자신의 필드 이름 지정
        * @JsonIgnore - Order 와 Member 는 양방향 연관관계로 둘중 하나에 @JsonIgnore 사용해야 한다
        * 그러지 않으면 무한 참조 발생
        */
        @JsonIgnore
        @OneToMany(mappedBy = "member")
        private List<Order> orders = new ArrayList<>();
    }
    ```

* 그러나 위 방법을 적용하고 응답에 엔티티 사용시 오류 발생

    - Order 의 Member 는 지연 로딩으로 설정되어 있고, 지연 로딩은 연관 필드는 실제 사용하기 전까지 DB에서 값을 검색해오지 않는다
    
    - 연관 필드인 Member 를 사용하지 않았으니 member 필드에는 member 객체 대신 프록시 객체가 있다

    - 이 프록시 객체를 응답 데이터로 표현하지 못해 오류가 발생한다

    - hibernate6Module 라이브러리를 사용해 해결 가능

        ```java
        // build.gradle

        // 지연 로딩 시 연관관계의 프록시 객체를 출력 가능하게 해줌(null로 출력하거나 설정에 따라 연관관계 데이터를 검색해서 가져옴)
        // 스프링 부트 2.xx 버전은 hibernate5, 3.xx 는 hibernate6 사용
        // hibernate5 는 javax.persistence 패키지를 사용하는데, Spring Boot 3.x 이상부터는 jakarta.persistence를 사용해서 NoClassDefFoundError 발생
        implementation 'com.fasterxml.jackson.datatype:jackson-datatype-hibernate6'
        ```
        ```java
        // Bean 으로 등록
        @Bean
        Hibernate5Module hibernate5Module(){
            return new Hibernate5Module();
        }
        ```

* 위 문제점들은 엔티티를 응답에 바로 사용할 경우 발생하는 문제로, 엔티티 대신 DTO 를 사용하면 발생하지 않는다

    - DTO 사용 시 엔티티를 명시적으로 호출하지 않으므로 순환 참조 문제가 발생하지 않는다

### distinct

* sql에서 distinct 는 행의 내용들이 모두 같아야 중복을 제거할 수 있다

    - jpa에서 distinct 는 두 가지 기능이 있는데, db에 distinct 키워드를 사용하게 한다
    
    - 그리고 조회 결과를 엔티티로 변환할 때 id 값이 같으면 중복 행으로 간주하고 제거한다
