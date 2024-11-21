### 사용자 정의 리포지토리 구현

* Srping Data Jpa 의 리포지토리는 인터페이스만 정의하면 구현체는 자동 생성해준다

* 여러 이유로 인터페이스의 메서드를 직접 구현하고 싶을 때 사용하는 방법

    - JPA 직접 사용

    - MyBatis, 스프링 JDBC Template 사용

    - DB 커넥션 직접 사용

    - QueryDSL 사용

```java
// 사용자 정의 리포지토리 인터페이스 생성
public interface MemberRepositoryCustom {

    List<Member> findMemberCustom();
}

// 사용자 정의 리포지토리 인터페이스를 구현하는 클래스
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final EntityManager em;
    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }
}

// 기존 Srping Data Jpa 리포지토리가 사용자 정의 리포지토리 인터페이스 상속
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {}

// 기존 리포지토리가 JpaRepository, 사용지 지정 리포지토리를 모두 상속하고 있다
// 따라서 MemberRepository 로 둘다 사용 가능하다
// JpaRepository 사용 시 이전과 같이 Data Jpa 가 자동으로 구현해준 메서드를 사용하게 된다
// MemberRepositoryCustom 사용 시 직접 구현한 MemberRepositoryImpl 의 메서드를 사용하게 된다
```

* 주의점

    * 사용자 정의 리포지토리 구현 클래스의 이름은 아래 규칙 중 하나를 따라야 한다

        - JpaRepository 를 상속받는 인터페이스 이름 + Impl 

            ```java
            MemberRepository : JpaRepository 를 상속받는 인터페이스

            MemberRepositoryCustom : 사용자 정의 리포지토리 인터페이스

            // MemberRepository + Impl
            MemberRepositoryImpl : 사용자 정의 리포지토리 인터페이스 구현 클래스
            ```
    
        - 사용자 정의 인터페이스 명 + Impl

            ```java
            MemberRepositoryCustom : 사용자 정의 리포지토리 인터페이스

            // MemberRepositoryCustom + Impl
            MemberRepositoryCustomImpl : 사용자 정의 리포지토리 인터페이스 구현 클래스
            ```
    
    * 항상 사용자 정의 리포지토리로 만들지 않아도 된다

        - 임의의 리포지토리를 만들어 사용해도 된다

        - MemberRepository 처럼 분리된 개별 리포지토리를 생성해도 된다

        - 핵심 비즈니스 로직이 담긴 리포지토리, 화면에 맞춘 최적화 쿼리를 사용하는 리포지토리 둘로 분리하는 편이 좋다  

### Auditing

* 엔티티를 생성, 변경할 때 변경한 사람과 시간을 자동 추적하는 기능

    - 등록일, 수정일, 등록자, 수정자

* 순수 JPA 사용

    - 주요 어노테이션

        - @MappedSuperclass
        
            - 부모 클래스는 테이블과 매핑하지 않고, 상속받는 자식 클래스에게 부모 클래스가 가지는 칼럼만 매핑정보로 제공하는 방법

        - @PrePersist : 엔티티가 영속화 되기 전에 호출

            - 영속화 : 엔티티 객체가 DB에 반영되는 것

        - @PreUpdate : 엔티티가 업데이트 되기 전에 호출

        - 사용 가능한 모든 콜백 어노테이션

            - @PrePersist: persist 호출 직전
            
            - @PostPersist: persist 호출 직후
            
            - @PreUpdate: 업데이트 전
            
            - @PostUpdate: 업데이트 후
            
            - @PreRemove: 삭제 전
            
            - @PostRemove: 삭제 후
            
            - @PostLoad: 엔티티 로드 후

    ```java
    // main 클래스 설정
    @SpringBootApplication 
    @EnableJpaAuditing // 추가
    public class DataJpaApplication {

        public static void main(String[] args) {
            SpringApplication.run(DataJpaApplication.class, args);
        }
    }

    // entity
    @MappedSuperclass
    public class JpaBaseEntity {

        @Column(updatable = false) // 변경감지를 통해 해당 필드에 대한 updata 쿼리 생성을 막음
        private LocalDateTime createdDate;
        private LocalDateTime updatedDate;

        @PrePersist
        public void perPersist(){
            LocalDateTime now = LocalDateTime.now();
            createdDate = now;
            updatedDate = now;
        }

        @PreUpdate
        public void preUpdate(){
            updatedDate = LocalDateTime.now();
        }
    }

    // 사용할 엔티티 객체로 상속
    public class Member extends JpaBaseEntity {}

    // test
    @Test
    public void jpaEventBaseEntity() throws Exception {

        //given
        Member member = new Member("member1");
        memberRepository.save(member); //@PrePersist 실행됨
        Thread.sleep(100);
        member.setUsername("member2");
        em.flush(); //@PreUpdate 실행됨
        em.clear();

        //when
        Member findMember = memberRepository.findById(member.getId()).get();

        //then
        System.out.println("findMember.createdDate = " + findMember.getCreatedDate());
        System.out.println("findMember.updatedDate = " + findMember.getUpdatedDate());
    }
    ```

* Spring Data Jpa 사용

    - 설정 방법

        - 스프링 부트 설정 클래스에 @EnableJpaAuditing 적용

        - 엔티티에 @EntityListeners(AuditingEntityListener.class) 적용

    - 주요 어노테이션

        - @CreatedDate
        
        - @LastModifiedDate
        
        - @CreatedBy
        
        - @LastModifiedBy
    
    ```java
    // 메인 클래스 설정 방법은 순수 JPA 사용 방법과 같음

    // entity
    @EntityListeners(AuditingEntityListener.class)
    @MappedSuperclass
    @Getter
    public class BaseEntity {

        @CreatedDate
        @Column(updatable = false)
        private LocalDateTime createdDate;

        @LastModifiedDate
        private LocalDateTime laseModifiedDate;
    }

    // 사용할 엔티티 객체로 상속
    public class Member extends BaseEntity {}
    ```
    ```java
    // 메인 클래스 추가 설정
    @SpringBootApplication
    @EnableJpaAuditing
    public class DataJpaApplication {

        public static void main(String[] args) {
            SpringApplication.run(DataJpaApplication.class, args);
        }

        @Bean
        public AuditorAware<String> auditorProvider(){

            // 실제 업무에서는 시큐리티 같은 곳에서 데이터를 가져옴
            return () -> Optional.of(UUID.randomUUID().toString());
        }
    }

    // 등록자, 수정자 추가
    @EntityListeners(AuditingEntityListener.class)
    @MappedSuperclass
    @Getter
    public class BaseEntity {

        @CreatedDate
        @Column(updatable = false)
        private LocalDateTime createdDate;

        @LastModifiedDate
        private LocalDateTime laseModifiedDate;

        @CreatedBy
        @Column(updatable = false)
        private String createdBy;

        @LastModifiedDate
        private String lastModifiedBy;
    }

    // 사용할 엔티티 객체로 상속

    // text
    @Test
    public void jpaEventBaseEntity() throws Exception {
        //given
        Member member = new Member("member1");
        memberRepository.save(member); //@PrePersist
        Thread.sleep(100);
        member.setUsername("member2");
        em.flush(); //@PreUpdate
        em.clear();

        //when
        Member findMember = memberRepository.findById(member.getId()).get();

        //then
        System.out.println("findMember.createdDate = " + findMember.getCreatedDate());
        System.out.println("findMember.updatedDate = " + findMember.getLaseModifiedDate());
        System.out.println("findMember.createdBy = " + findMember.getCreatedBy());
        System.out.println("findMember.updatedBy = " + findMember.getLastModifiedBy());
    }

    // 등록시간, 수정시간이 필요하지만, 등록자, 수정자는 필요 없을 수도 있다
    // 그래서 등록일 수정일, 등록자 수정자 로 Base 타입을 분리하고, 원하는 타입을 선택해서 상속한다
    ```
