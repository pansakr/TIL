### 순수 JPA 와 QueryDsl

* 순수 JPA 와 QueryDsl 결합해서 사용

    ```java
    @Repository
    public class MemberJpaRepository {

        private final EntityManager em;
        private final JPAQueryFactory queryFactory;

        public MemberJpaRepository(EntityManager em) {
            this.em = em;
            this.queryFactory = new JPAQueryFactory(em);
        }

        public void save(Member member){
            em.persist(member);
        }

        public Optional<Member> findById(Long id){
            Member findMember = em.find(Member.class, id);
            return Optional.ofNullable(findMember);
        }

        /**
         * 순수 JPA findAll()
        */
        public List<Member> findAll(){
            return em.createQuery("select m from Member m", Member.class)
                    .getResultList();
        }

        /**
         * QueryDsl findAll()
        */
        public List<Member> findAll_QueryDsl(){
            return queryFactory
                    .selectFrom(member)
                    .fetch();
        }

        /**
         * 순수 JPA findByUsername()
        */
        public List<Member> findByUsername(String username){
            return em.createQuery("select m from Member m where m.username = :username", Member.class)
                    .setParameter("username", username)
                    .getResultList();
        }

        /**
         * QueryDsl findByUsername()
        */
        public List<Member> findByUsername_QueryDsl(String username){
            return queryFactory
                    .selectFrom(member)
                    .where(member.username.eq(username))
                    .fetch();
        }
    }
    ```

* JPAQueryFactory 의존성 주입하는 다른 방법

    ```java
    // JPAQueryFactory 빈으로 등록
    @Configuration
    public class JpaConfig {

        @Bean
        JPAQueryFactory jpaQueryFactory(EntityManager em){
            return new JPAQueryFactory(em);
        }
    }

    // @RequiredArgsConstructor 로 의존성 생성자 주입
    @Repository
    @RequiredArgsConstructor
    public class MemberJpaRepository {

        private final EntityManager em;
        private final JPAQueryFactory queryFactory;
    }
    ```

* 동적 쿼리 성능 최적화

    - Builder 사용

        ```java
        // 프로젝션에 사용할 DTO
        @Data
        public class MemberTeamDto {

            private Long memberId;
            private String username;
            private int age;
            private Long teamId;
            private String teamName;

            @QueryProjection
            public MemberTeamDto(Long memberId, String username, int age, Long teamId, String teamName) {
                this.memberId = memberId;
                this.username = username;
                this.age = age;
                this.teamId = teamId;
                this.teamName = teamName;
            }
        }

        // 검색 쿼리 최적화에 사용될 클래스
        @Data
        public class MemberSearchCondition {

            private String username;
            private String teamName;
            private Integer ageGoe;
            private Integer ageLoe;
        }

        // repository
        @Repository
        ... class{

            /**
             * 동적 쿼리 성능 최척화
            * - 빌더 사용
            */
            public List<MemberTeamDto> searchByBuilder(MemberSearchCondition condition){

                BooleanBuilder builder = new BooleanBuilder();

                if (hasText(condition.getUsername())) {
                    builder.and(member.username.eq(condition.getUsername()));
                }

                if (hasText(condition.getTeamName())){
                    builder.and(team.name.eq(condition.getTeamName()));
                }

                if (condition.getAgeGoe() != null) {
                    builder.and(member.age.goe(condition.getAgeGoe()));
                }

                if (condition.getAgeLoe() != null) {
                    builder.and(member.age.goe(condition.getAgeLoe()));
                }

                return queryFactory
                        .select(new QMemberTeamDto(
                                member.id.as("memberId"),
                                member.username,
                                member.age,
                                team.id.as("teamId"),
                                team.name.as("teamName")))
                        .from(member)
                        .leftJoin(member.team, team)
                        .where(builder)
                        .fetch();
            }
        }

        // test
        @Test
        public void searchTest(){
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

            MemberSearchCondition condition = new MemberSearchCondition();
            condition.setAgeGoe(35);
            condition.setAgeLoe(40);
            condition.setTeamName("teamB");

            List<MemberTeamDto> result = memberJpaRepository.searchByBuilder(condition);
            assertThat(result).extracting("username").containsExactly("member4");
        }

        // 실행되는 쿼리
        select
            m1_0.member_id,
            m1_0.username,
            m1_0.age,
            m1_0.team_id,
            t1_0.name 
        from
            member m1_0 
        left join
            team t1_0 
                on t1_0.id=m1_0.team_id 
        where
            t1_0.name=teamB 
            and m1_0.age>=35 
            and m1_0.age>=40
        ```

    - Where절 파라미터 사용

        ```java
        /**
         * 동적 쿼리 성능 최적화
         * - where 절 파라미터 사용
         * - where 절 파라미터는 재사용 가능
        */
        public List<MemberTeamDto> search(MemberSearchCondition condition) {
            return queryFactory
                    .select(new QMemberTeamDto(
                            member.id,
                            member.username,
                            member.age,
                            team.id,
                            team.name))
                    .from(member)
                    .leftJoin(member.team, team)
                    .where(usernameEq(condition.getUsername()),
                            teamNameEq(condition.getTeamName()),
                            ageGoe(condition.getAgeGoe()),
                            ageLoe(condition.getAgeLoe()))
                    .fetch();
        }

        // where 에 사용되는 메서드
        private BooleanExpression usernameEq(String username) {
            return isEmpty(username) ? null : member.username.eq(username);
        }
        private BooleanExpression teamNameEq(String teamName) {
            return isEmpty(teamName) ? null : team.name.eq(teamName);
        }
        private BooleanExpression ageGoe(Integer ageGoe) {
            return ageGoe == null ? null : member.age.goe(ageGoe);
        }
        private BooleanExpression ageLoe(Integer ageLoe) {
            return ageLoe == null ? null : member.age.loe(ageLoe);
        }
        ```

### 스프링 데이터 Jpa 와 QueryDsl

* 스프링 데이터 Jpa 와 QueryDsl 결합해서 사용

    - QueryDsl을 사용하는 사용자 정의 리포지토리 작성

        ```java
        // 스프링 데이터 Jpa의 기능을 제공하는 JpaRepository를 상속하는 기본 리포지토리
        public interface MemberRepository extends JpaRepository<Member, Long> {
            List<Member> findByUsername(String username);
        }

        // QueryDsl 을 사용하기 위한 커스텀 인터페이스
        public interface MemberRepositoryCustom {
            List<MemberTeamDto> search(MemberSearchCondition condition);
        }

        // 커스텀 인터페이스 구현 클래스
        // 클래스 이름 규칙을 지켜야 한다
        public class MemberRepositoryImpl implements MemberRepositoryCustom{

            private final JPAQueryFactory queryFactory;

            public MemberRepositoryImpl(EntityManager em) {
                this.queryFactory = new JPAQueryFactory(em);
            }

            @Override
            public List<MemberTeamDto> search(MemberSearchCondition condition) {
                return queryFactory
                        .select(new QMemberTeamDto(
                                member.id,
                                member.username,
                                member.age,
                                team.id,
                                team.name))
                        .from(member)
                        .leftJoin(member.team, team)
                        .where(usernameEq(condition.getUsername()),
                                teamNameEq(condition.getTeamName()),
                                ageGoe(condition.getAgeGoe()),
                                ageLoe(condition.getAgeLoe()))
                        .fetch();
            }
            private BooleanExpression usernameEq(String username) {
                return isEmpty(username) ? null : member.username.eq(username);
            }
            private BooleanExpression teamNameEq(String teamName) {
                return isEmpty(teamName) ? null : team.name.eq(teamName);
            }
            private BooleanExpression ageGoe(Integer ageGoe) {
                return ageGoe == null ? null : member.age.goe(ageGoe);
            }
            private BooleanExpression ageLoe(Integer ageLoe) {
                return ageLoe == null ? null : member.age.loe(ageLoe);
            }

        }

        // 만들어 두었던 기본 리포지토리에 커스텀 리포지토리를 추가로 상속
        public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
            List<Member> findByUsername(String username);
        }

        // test
        @Test
        public void searchTest(){
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

            MemberSearchCondition condition = new MemberSearchCondition();
            condition.setAgeGoe(35);
            condition.setAgeLoe(40);
            condition.setTeamName("teamB");

            List<MemberTeamDto> result = memberRepository.search(condition);
            assertThat(result).extracting("username").containsExactly("member4");
        }
        ```

    - QueryDsl 관련 코드를 꼭 커스텀 인터페이스를 만들어 상속받는 방식으로 작성하지 않아도 된다

        - QueryDsl 리포지토리 클래스를 바로 만들어서 사용해도 된다

        ```java
        @Repository
        public class MemberQueryRepository{
            private final JPAQueryFactory queryFactory;

            public MemberRepositoryImpl(EntityManager em) {
                this.queryFactory = new JPAQueryFactory(em);
            }

            .. queryDsl 코드
        }
        ```

* 페이징 + QueryDsl

    * 전체 카운트를 조회하는 단순한 방법

        ```java
        @Override
        public Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable) {
            QueryResults<MemberTeamDto> results = queryFactory
                    .select(new QMemberTeamDto(
                            member.id,
                            member.username,
                            member.age,
                            team.id,
                            team.name))
                    .from(member)
                    .leftJoin(member.team, team)
                    .where(usernameEq(condition.getUsername()),
                            teamNameEq(condition.getTeamName()),
                            ageGoe(condition.getAgeGoe()),
                            ageLoe(condition.getAgeLoe()))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetchResults();

            List<MemberTeamDto> content = results.getResults();
            long total = results.getTotal();

            return new PageImpl<>(content, pageable, total);
        }

        private BooleanExpression usernameEq(String username) {
            return isEmpty(username) ? null : member.username.eq(username);
        }
        private BooleanExpression teamNameEq(String teamName) {
            return isEmpty(teamName) ? null : team.name.eq(teamName);
        }
        private BooleanExpression ageGoe(Integer ageGoe) {
            return ageGoe == null ? null : member.age.goe(ageGoe);
        }
        private BooleanExpression ageLoe(Integer ageLoe) {
            return ageLoe == null ? null : member.age.loe(ageLoe);
        }

        // test
        @Test
        public void searchPageSimpleTest(){
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

            MemberSearchCondition condition = new MemberSearchCondition();
            PageRequest pageRequest = PageRequest.of(0, 3);

            Page<MemberTeamDto> result = memberRepository.searchPageSimple(condition, pageRequest);

            assertThat(result.getSize()).isEqualTo(3);
            assertThat(result.getContent()).extracting("username").containsExactly("member1", "member2", "member3");
        }
        ```

    - 데이터 내용과 전체 카운트를 별도로 조회하는 방법

        - count 쿼리를 별도로 작성해 최적화

        ```java
        @Override
        public Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable) {
            List<MemberTeamDto> content = queryFactory
                    .select(new QMemberTeamDto(
                            member.id,
                            member.username,
                            member.age,
                            team.id,
                            team.name))
                    .from(member)
                    .leftJoin(member.team, team)
                    .where(usernameEq(condition.getUsername()),
                            teamNameEq(condition.getTeamName()),
                            ageGoe(condition.getAgeGoe()),
                            ageLoe(condition.getAgeLoe()))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            long total = queryFactory
                    .select(member)
                    .from(member)
                    .leftJoin(member.team, team)
                    .where(usernameEq(condition.getUsername()),
                            teamNameEq(condition.getTeamName()),
                            ageGoe(condition.getAgeGoe()),
                            ageLoe(condition.getAgeLoe()))
                    .fetchCount();

            return new PageImpl<>(content, pageable, total);
        }
        ```

        - count 쿼리 최적화

        ```java
        JPAQuery<Member> countQuery = queryFactory
            .select(member)
            .from(member)
            .leftJoin(member.team, team)
            .where(usernameEq(condition.getUsername()),
                    teamNameEq(condition.getTeamName()),
                    ageGoe(condition.getAgeGoe()),
                    ageLoe(condition.getAgeLoe()));

        // return new PageImpl<>(content, pageable, total);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
        ```
