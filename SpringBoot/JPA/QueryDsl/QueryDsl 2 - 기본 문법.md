### QueryDsl 문법

* 결과 조회

    - fetch() : 리스트 조회, 데이터 없으면 빈 리스트 반환

    - fetchOne() : 단 건 조회
    
        - 결과가 없으면 : null

        - 결과가 둘 이상이면 : com.querydsl.core.NonUniqueResultException

    - fetchFirst() : limit(1).fetchOne()

    - fetchResults() : 페이징 정보 포함, total count 쿼리 추가 실행

    - fetchCount() : count 쿼리로 변경해서 count 수 조회

    ```java
    // List
    List<Member> fetch = queryFactory
            .selectFrom(member)
            .fetch();

    // 단 건
    Member findMember1 = queryFactory
            .selectFrom(member)
            .fetchOne();

    // 처음 한 건 조회
    Member findMember2 = queryFactory
            .selectFrom(member)
            .fetchFirst();

    /**
     * 페이징에서 사용
     * 2개의 쿼리가 실행된다 - count 쿼리, select 쿼리
     * QueryResults 는 엔티티의 리스트와 레코드 수를 동시에 다룰 수 있게 설계된 객체이다
     * fetchResults() @Deprecated 됨. 사용 x
    */ 
    QueryResults<Member> results = queryFactory
            .selectFrom(member)
            .fetchResults();

        // 실행되는 쿼리 1
        select
            count(m1_0.member_id) 
        from
            member m1_0

        // 실행되는 쿼리 2
        select
            m1_0.member_id,
            m1_0.age,
            m1_0.team_id,
            m1_0.username 
        from
            member m1_0

    // count 쿼리만 실행할때 사용
    // 성능이 중요할 때는 select 와 count 사용을 분리해야 한다
    long count = queryFactory
            .selectFrom(member)
            .fetchCount();
    ```

* 검색 조건 쿼리

    ```java
    // Q 타입 import static
    import static study.querydsl.entity.QMember.*;

    // 테스트 전 실행되는 메서드
    @BeforeEach
    public void before(){
        // JPAQueryFactory 먼저 생성됨
        queryFactory = new JPAQueryFactory(em);
        ...
    }

    /**
     * 검색 조건 쿼리
     * Q 타입 import static 해서 QMember.member 가 아닌 바로 member 사용 가능
     */
    @Test
    public void search(){
        Member findMember = queryFactory
                .selectFrom(member)  // select(member).From(member) 합친 것
                .where(member.username.eq("member1")  
                        .and(member.age.eq(10)))
                .fetchOne();

                // where 내부의 and를 빼고 아래처럼 작성해도 같은 sql이 생성됨
                //.where(
                //        member.username.eq("member1"),
                //        member.age.eq(10)
                //)

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    // 생성된 sql
    select
        m1_0.member_id,
        m1_0.age,
        m1_0.team_id,
        m1_0.username 
    from
        member m1_0 
    where
        m1_0.username=member1
        and m1_0.age=10
    ```
    ```java
    // Query Dsl 은 JPQL 이 제공하는 모든 검색 조건 제공
    member.username.eq("member1") // username = member1

    member.username.ne("member1") // username != member1

    member.username.eq("member1").not() // username != member1

    member.username.isNotNull() //이름이 is not null

    member.age.in(10, 20) // age in (10,20)

    member.age.notIn(10, 20) // age not in (10, 20)

    member.age.between(10,30) //between 10, 30

    member.age.goe(30) // age >= 30

    member.age.gt(30) // age > 30

    member.age.loe(30) // age <= 30

    member.age.lt(30) // age < 30

    member.username.like("member%") //like 검색

    member.username.contains("member") // like ‘%member%’ 검색

    member.username.startsWith("member") //like ‘member%’ 검색
    ```

* 정렬

    ```java
    /**
     * 회원 정렬 순서
     * 1. 회원 나이 내림차순(desc)
     * 2. 회원 이름 오름차순(asc)
     * 단 2에서 회원 이름이 없으면 마지막에 출력(null last)
     */
    @Test
    public void sort(){
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                .fetch();

        Member member5 = result.get(0);
        Member member6 = result.get(1);
        Member memberNull = result.get(2);
        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isNull();

    }

    // 생성된 sql
    select
        m1_0.member_id,
        m1_0.age,
        m1_0.team_id,
        m1_0.username 
    from
        member m1_0 
    where
        m1_0.age=100 
    order by
        m1_0.age desc,
        m1_0.username asc nulls last
    ```

* 페이징

    ```java
    @Test
    public void paging1(){
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1) // 1번째 데이터부터 (첫 데이터는 0번째)
                .limit(2) // 2개
                .fetch();

        assertThat(result.size()).isEqualTo(2);
    }

    // 생성된 sql
    select
        m1_0.member_id,
        m1_0.age,
        m1_0.team_id,
        m1_0.username 
    from
        member m1_0 
    order by
        m1_0.username desc 
    offset
        1 rows 
    fetch
        first 2 rows only

    /**
     * 페이징 2
     * fetchResults() @Deprecated 됨. 사용 x
     */
    @Test
    public void paging2(){
        QueryResults<Member> queryResults = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1) // 1번째 데이터부터 (첫 데이터는 0번째)
                .limit(2) // 2개
                .fetchResults();

        assertThat(queryResults.getTotal()).isEqualTo(4);
        assertThat(queryResults.getLimit()).isEqualTo(2);
        assertThat(queryResults.getOffset()).isEqualTo(1);
        assertThat(queryResults.getResults().size()).isEqualTo(2);
    }
    ```

* 집합

    ```java
    /**
     * 집합 쿼리 1
     */
    @Test
    public void aggregation(){
        List<Tuple> result = queryFactory
                .select(
                        member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min()
                )
                .from(member)
                .fetch();

        Tuple tuple = result.get(0);
        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);
    }

        // 실행되는 sql
        select
            count(m1_0.member_id),
            sum(m1_0.age),
            avg(cast(m1_0.age as float(53))),
            max(m1_0.age),
            min(m1_0.age) 
        from
            member m1_0

    /**
     * 집합 쿼리 2
     * 팀의 이름과 각팀의 평균 연령 구하기
     */
    @Test
    public void group() throws Exception{

        List<Tuple> result = queryFactory
                .select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .fetch();

        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);

        assertThat(teamA.get(team.name)).isEqualTo("teamA");
        assertThat(teamA.get(member.age.avg())).isEqualTo(15);

        assertThat(teamB.get(team.name)).isEqualTo("teamB");
        assertThat(teamB.get(member.age.avg())).isEqualTo(35);
    }

    // 실행되는 sql
    select
        t1_0.name,
        avg(cast(m1_0.age as float(53))) 
    from
        member m1_0 
    join
        team t1_0 
            on t1_0.id=m1_0.team_id 
    group by
        t1_0.name
    ```

* 조인

    ```java
    /**
     * 조인
     * 팀 A에 소속된 모든 회원 조회
     */
    @Test
    public void join(){
        List<Member> result = queryFactory
                .selectFrom(member)
                .join(member.team, team) // leftJoin() 사용 시 left join 쿼리 실행됨. join()은 inner join 쿼리 실행
                .where(team.name.eq("teamA"))
                .fetch();

        // result 객체에서 username 필드의 값이 정확히 "member1"과 "member2"만 포함하고 있는지 테스트
        assertThat(result)
                .extracting("username")
                .containsExactly("member1", "member2");
    }

        // 실행되는 sql
        select
            m1_0.member_id,
            m1_0.age,
            m1_0.team_id,
            m1_0.username 
        from
            member m1_0 
        join
            team t1_0 
                on t1_0.id=m1_0.team_id 
        where
            t1_0.name=teamA

    /**
     * 세타 조인(연관관계가 없는 필드로 조인)
     * 회원의 이름이 팀 이름과 같은 회원 조회
     */
    @Test
    public void theta_join() throws Exception {

        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));

        List<Member> result = queryFactory
                .select(member)
                .from(member, team) // from 절에 세타 조인할 테이블 나열
                .where(member.username.eq(team.name))
                .fetch();

        assertThat(result)
                .extracting("username")
                .containsExactly("teamA", "teamB");
    }

        // 실행되는 sql
        select
            m1_0.member_id,
            m1_0.age,
            m1_0.team_id,
            m1_0.username 
        from
            member m1_0,
            team t1_0 
        where
            m1_0.username=t1_0.name
    ```

* 조인 - on절

    - 조인 대상 필터링

    ```java
    /**
     * 예) 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
     * JPQL: SELECT m, t FROM Member m LEFT JOIN m.team t on t.name = 'teamA'
     * SQL: SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.TEAM_ID=t.id and
     t.name='teamA'
     */
    @Test
    public void join_on_filtering() throws Exception {
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team).on(team.name.eq("teamA")) 
                .fetch();
        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }

        // from(member).leftJoin(member.team, team) 부분이 on 절로 변환된다
        // member m1_0 left join team t1_0 on t1_0.id = m1_0.team_id

        // 뒤쪽의 .on(team.name.eq("teamA")) 부분은 and 가 사용되어 앞의 on 과 연결된다
        // and t1_0.name=teamA
    }

    // 실행되는 sql
    select
        m1_0.member_id,
        m1_0.age,
        m1_0.team_id,
        m1_0.username,
        t1_0.id,
        t1_0.name 
    from
        member m1_0 
    left join
        team t1_0 
            on t1_0.id=m1_0.team_id 
            and t1_0.name=teamA
    ```

    - 연관관계 없는 엔티티 외부 조인

    ```java
    /**
     * 2. 연관관계 없는 엔티티 외부 조인
     * 예) 회원의 이름과 팀의 이름이 같은 대상 외부 조인
     * JPQL: SELECT m, t FROM Member m LEFT JOIN Team t on m.username = t.name
     * SQL: SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.username = t.name
     */
    @Test
    public void join_on_no_relation() throws Exception {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(team).on(member.username.eq(team.name))
                .fetch();
        for (Tuple tuple : result) {
            System.out.println("t=" + tuple);
        }

        // leftJoin()에 team 만 사용된 것은 연관관계 없는 조인이기 때문
        // t1_0.id=m1_0.team_id 와 같은 sql이 생성되지 않는다
    }

    // 실행되는 sql
    select
        m1_0.member_id,
        m1_0.age,
        m1_0.team_id,
        m1_0.username,
        t1_0.id,
        t1_0.name 
    from
        member m1_0 
    left join
        team t1_0 
            on m1_0.username=t1_0.name
    ```

* 페치 조인

    ```java
    @PersistenceUnit
    EntityManagerFactory emf;

    /**
     * 페치 조인 미적용
     */
    @Test
    public void fetchJoinNo() throws Exception {
        em.flush();
        em.clear();
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();
        boolean loaded =
                emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).as("페치 조인 미적용").isFalse();
    }

        // 실행되는 sql
        select
            m1_0.member_id,
            m1_0.age,
            m1_0.team_id,
            m1_0.username 
        from
            member m1_0 
        where
            m1_0.username=member1

    /**
     * 페치 조인 적용
     */
    @Test
    public void fetchJoinUse() throws Exception {
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.username.eq("member1"))
                .fetchOne();
        
        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).as("페치 조인 적용").isTrue();
    }

        // 실행되는 sql
        select
            m1_0.member_id,
            m1_0.age,
            t1_0.id,
            t1_0.name,
            m1_0.username 
        from
            member m1_0 
        join
            team t1_0 
                on t1_0.id=m1_0.team_id 
        where
            m1_0.username=member1
    ```

* 서브 쿼리

    - 서브 쿼리에 com.querydsl.jpa.JPAExpressions 사용

        - import static 으로 JPAExpressions 키워드 생략 가능

    - JPA 의 JPQL 은 from 절의 서브쿼리 (인라인 뷰) 를 지원하지 않는다

        - QueryDsl 도 지원하지 않는다

    - 서브 쿼리 eq 사용

    ```java
    /**
     * 나이가 가장 많은 회원 조회
     */
    @Test
    public void subQuery() throws Exception {

        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(
                        JPAExpressions
                                .select(memberSub.age.max())
                                .from(memberSub)
                ))
                .fetch();
                
        // JPAExpressions 를 import static 적용할 시
        // .where(member.age.eq(
        //         select(memberSub.age.max())
        //                 .from(memberSub)
        // ))

        assertThat(result).extracting("age").containsExactly(40);
    }

    // 실행되는 sql
    select
        m1_0.member_id,
        m1_0.age,
        m1_0.team_id,
        m1_0.username 
    from
        member m1_0 
    where
        m1_0.age=(
            select
                max(m2_0.age) 
            from
                member m2_0
        )
    ```

    - 서브 쿼리 goe 사용

    ```java
    /**
     * 나이가 평균 나이 이상인 회원
     */
    @Test
    public void subQueryGoe() throws Exception {

        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.goe(
                        JPAExpressions
                                .select(memberSub.age.avg())
                                .from(memberSub)
                ))
                .fetch();

        assertThat(result).extracting("age").containsExactly(30,40);
    }

    // 실행되는 sql
    select
        m1_0.member_id,
        m1_0.age,
        m1_0.team_id,
        m1_0.username 
    from
        member m1_0 
    where
        m1_0.age>=(
            select
                avg(cast(m2_0.age as float(53))) 
            from
                member m2_0
        )
    ```

    - 서브쿼리 여러 건 처리 in 사용

    ```java
    /**
     * 서브쿼리 여러 건 처리, in 사용
     */
    @Test
    public void subQueryIn() throws Exception {
        QMember memberSub = new QMember("memberSub");
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.in(
                        JPAExpressions
                                .select(memberSub.age)
                                .from(memberSub)
                                .where(memberSub.age.gt(10))
                ))
                .fetch();
        assertThat(result).extracting("age")
                .containsExactly(20, 30, 40);
    }

    // 실행되는 sql
        select
        m1_0.member_id,
        m1_0.age,
        m1_0.team_id,
        m1_0.username 
    from
        member m1_0 
    where
        m1_0.age in (select
                            m2_0.age 
                        from
                            member m2_0 
                        where
                            m2_0.age>10)
    ```

    - select 절에 subquery

    ```java
    @Test
    public void selectSubQuery(){
        QMember memberSub = new QMember("memberSub");

        List<Tuple> fetch = queryFactory
                .select(member.username,
                        JPAExpressions
                                .select(memberSub.age.avg())
                                .from(memberSub)
                ).from(member)
                .fetch();
        
        for (Tuple tuple : fetch) {
            System.out.println("username = " + tuple.get(member.username));
            System.out.println("age = " +
                    tuple.get(JPAExpressions.select(memberSub.age.avg())
                            .from(memberSub)));
        }
    }
    // 실행되는 sql
    select
        m1_0.username,
        (select
            avg(cast(m2_0.age as float(53))) 
        from
            member m2_0) 
    from
        member m1_0
    ```

* case

    - 단순한 조건

    ```java
    @Test
    public void basicCase(){
        List<String> result = queryFactory
                .select(member.age
                        .when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타"))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }
    
    // 실행되는 sql
    select
        case 
            when m1_0.age=10 
                then cast(열살 as varchar) 
            when m1_0.age=20 
                then cast(스무살 as varchar) 
            else '기타' 
        end 
    from
        member m1_0
    ```

    - 복잡한 조건

    ```java
    @Test
    public void complexCase(){
        List<String> result = queryFactory
                .select(new CaseBuilder()
                        .when(member.age.between(0, 20)).then("0~20살")
                        .when(member.age.between(21, 30)).then("21~30살")
                        .otherwise("기타"))
                .from(member)
                .fetch();
    }

    // 실행되는 sql
    select
        case 
            when (m1_0.age between 0 and 20) 
                then cast(0~20살 as varchar) 
            when (m1_0.age between 21 and 30) 
                then cast(21~30살 as varchar) 
            else '기타' 
        end 
    from
        member m1_0
    ```

    - orderBy에서 Case 문 함께 사용

    ```java
    @Test
    public void rankCase(){

        NumberExpression<Integer> rankPath = new CaseBuilder()
                .when(member.age.between(0, 20)).then(2)
                .when(member.age.between(21, 30)).then(1)
                .otherwise(3);

        List<Tuple> result = queryFactory
                .select(member.username, member.age, rankPath)
                .from(member)
                .orderBy(rankPath.desc())
                .fetch();
        
        for (Tuple tuple : result) {
            String username = tuple.get(member.username);
            Integer age = tuple.get(member.age);
            Integer rank = tuple.get(rankPath);
            System.out.println("username = " + username + " age = " + age + " rank = " +
                    rank);
        }
    }

    // 실행되는 sql
    select
        m1_0.username,
        m1_0.age,
        case 
            when (m1_0.age between 0 and 20) 
                then cast(2 as integer) 
            when (m1_0.age between 21 and 30) 
                then cast(1 as integer) 
            else 3 
        end 
    from
        member m1_0 
    order by
        case 
            when (m1_0.age between 0 and 20) 
                then 2 
            when (m1_0.age between 21 and 30) 
                then 1
            else 3 
        end desc

    // 결과
    username = member4 age = 40 rank = 3
    username = member1 age = 10 rank = 2
    username = member2 age = 20 rank = 2
    username = member3 age = 30 rank = 1
    ```

* 상수, 문자 더하기

    - 상수 더하기

    ```java
    // 상수 더하기. username, A 형태
    @Test
    public void constant(){
        List<Tuple> result = queryFactory
                .select(member.username, Expressions.constant("A"))
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }
    // 실행되는 sql
    select
        m1_0.username 
    from
        member m1_0

    // 결과
    // sql 에는 변경사항이 없어 보이지만 결과에 A 가 더해 출력됨
    tuple = [member1, A]
    tuple = [member2, A]
    tuple = [member3, A]
    tuple = [member4, A]
    ```

    - 문자 더하기

    ```java
    /**
     * 문자 더하기. username_age 형태
     * 숫자는 concat() 대상이 아니기 때문에 stringValue() 를 사용해 문자로 변환
     */
    @Test
    public void concat(){
        List<String> result = queryFactory
                .select(member.username.concat("_").concat(member.age.stringValue()))
                .from(member)
                .where(member.username.eq("member1"))
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    // 실행되는 sql
    select
        ((m1_0.username||'_')||cast(m1_0.age as varchar)) 
    from
        member m1_0 
    where
        m1_0.username=member1

    // 결과
    s = member1_10
    ```
