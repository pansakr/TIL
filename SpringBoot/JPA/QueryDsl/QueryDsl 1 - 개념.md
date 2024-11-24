### Query Dsl

* jpql을 간단하게 작성할 수 있게 해주는 도구

    - entity와 매핑되는 QClass 객체를 사용해서 쿼리를 실행한다

    - JPAQueryFactory 를 사용해 쿼리에 사용할 객체를 생성한다

    - 문자열로 작성하는 jpql과 다르게 sql을 코드로 작성하기 때문에 IDE의 도움을 받을 수 있고 컴파일 단계에서 오류를 발견할 수 있다

* QueryDsl 코드가 JPQL 로 변환되고, 그 다음 SQL 로 변환되어 DB에서 실행된다

    ```Java
    // test
    import static study.querydsl.entity.QMember.*;

    @SpringBootTest
    @Transactional
    public class QuerydslBasicTest {

        @Autowired
        EntityManager em;

        /**
        * 동시성 문제 발생하지 않음
        */
        JPAQueryFactory queryFactory;

        // 테스트 전 실행됨
        @BeforeEach
        public void before(){
            queryFactory= new JPAQueryFactory(em);
            Team teamA = new Team("teamA");
            Team teamB = new Team("teamB");
            em.persist(teamA);
            em.persist(teamB);

            Member member1 = new Member("member1", 10, teamA);
            Member member2 = new Member("member2", 20, teamA);

            Member member3 = new Member("member3", 30, teamB);
            Member member4 = new Member("member4", 40, teamB);
            em.persist(member1);
            em.persist(member2);
            em.persist(member3);
            em.persist(member4);
        }

        // JPQL
        @Test
        public void startJPQL(){
            // member1 찾기
            String qlString = "select m from Member m where m.username = :username";
            Member findMember = em.createQuery(qlString, Member.class)
                    .setParameter("username", "member1")
                    .getSingleResult();

            assertThat(findMember.getUsername()).isEqualTo("member1");
        }

        // QueryDsl
        // Q 클래스 생성 -> 쿼리 작성 -> 쿼리 실행
        @Test
        public void startQueryDsl(){

            // Q 클래스 생성방법 1
            // QMember m = new QMember("m");

            // Q 클래스 생성방법 2. 권장 방법
            // QMember member = QMember.member;

            // Q 클래스 생성방법 3. 권장 방법
            // Q 타입 import static 해서 사용
            // QMember.member 로 호출하던 것을 member 만 사용해서 호출할 수 있다

            // 필드에 추가했으므로 생략. 필드에 없다면 필요함
            // JPAQueryFactory queryFactory = new JPAQueryFactory(em);

            // 쿼리 작성, 실행
            Member findMember = queryFactory
                    .select(member) // Q타입 member 는 import static 되었으므로 member 로 사용 가능
                    .from(member)
                    .where(member.username.eq("member1"))
                    .fetchOne();

            assertThat(findMember.getUsername()).isEqualTo("member1");
        }
    }
    ```

* QClass

    - QueryDsl로 작성하는 쿼리의 대상이 되는 클래스

        - 컴파일 시 프로젝트 내의 JPA 엔티티를 기반으로 생성된다

    - AnnotationProcessor 가 컴파일 시점에 @Entity를 기반으로 생성한 Static Class

    - AnnotationProcessor

        - Java 컴파일러가 컴파일 단계에서 어노테이션을 읽고, 그에 따라 추가 코드를 생성하거나 수정하는 작업
