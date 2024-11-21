### Spring Data Jpa

* JPA를 더욱 쉽게 사용할 수 있도록 Spring Framework에서 제공하는 데이터 접근 계층 라이브러리

* JPA 구현체(Hibernate 등)를 내부적으로 사용한다

* 기본적인 CRUD를 위한 메서드(save(), findById(), findAll() 등)를 자동으로 생성

* 쿼리 메서드 자동 생성

    - 메서드 이름 기반으로 쿼리를 생성(findBy, countBy, existsBy 등)
    
    - 복잡한 JPQL 대신 간단한 메서드 이름으로 조건부 검색 가능

* JPQL/Native Query 지원

    - 복잡한 조건이 필요할 경우 @Query 애너테이션을 사용하여 JPQL이나 네이티브 SQL 작성 가능

        - jpa만 사용했을 때는 em.createQuery("select ..") 로 JPQL을 작성해야 했음

* JPA -> Srping Data JPA 로 전환

    - Srping Data JPA 에서 리포지토리는 JpaRepository 인터페이스를 상속받는 인터페이스가 된다

        - 기존 JPA 는 리포지토리 역할을 하는 클래스를 만들고 save, find 등의 코드를 작성하고 사용
        
        - Srping Data JPA 는 JpaRepository 인터페이스를 상속받는 인터페이스를 만들고, 해당 인터페이스를 기존의 리포지토리 처럼 사용

            - JpaRepository 인터페이스는 save, findById 메서드 등이 작성된 CrudRepository 인터페이스와 그외 여러 인터페이스를 상속받는 구조로 이루어져 있음

            ```java
            // TeamRepository 인터페이스가 리포지토리로 사용됨
            // @Repository 가 없어도 있는 것처럼 인식
            // JpaRepositor<엔티티 타입, 해당 엔티티의 pk>
            public interface TeamRepository extends JpaRepository<Team, Long> {
            }

            // 서비스 계층
            @RequiredArgsConstructor
            public Class xxService{

                private final TeamRepository teamRepository

                // 인터페이스를 사용
                public Team xx(){
                    return teamRepository.find..();
                }

            }
            ```

        - 인터페이스의 구현체를 만들지 않았지만 사용할 수 있는 이유는 Srping Data Jpa 가 JpaRepository를 상속받은 인터페이스에 대해 자동으로 구현체를 생성해주기 때문

            - 개발자가 구현체를 직접 작성할 필요 없이, Spring이 생성한 구현체(프록시 객체) 가 주입됨

            - 인터페이스를 통해 의존성을 주입받고, 이 인터페이스의 메서드를 호출하면, 주입된 프록시 객체가 대신 동작

    ```java
    // Spring Data Jpa 적용 전
    @Repository
    public class TeamJpaRepository {

        @PersistenceContext
        private EntityManager em;

        public Team save(Team team){
            em.persist(team);
            return team;
        }

        public void delete(Team team){
            em.remove(team);
        }

        public List<Team> findAll(){
            return em.createQuery("select t from Team t", Team.class)
                    .getResultList();
        }

        public Optional<Team> findById(Long id){
            Team team = em.find(Team.class, id);
            return Optional.ofNullable(team);
        }

        public long count(){
            return em.createQuery("select count(t) from Team t", Long.class)
                    .getSingleResult();
        }
    }

    ```
    ```java
    // Spring Data Jpa 적용
    // CRUD 등의 기본적인 메서드를 지원하므로 매번 작성하지 않아도 됨
    public interface TeamRepository extends JpaRepository<Team, Long> {
    }
    ```

* 주요 메서드

    - T : 엔티티, ID : 엔티티의 식별자 타입, S : 엔티티와 그 자식 타입

    - save(S) : 새로운 엔티티는 저장하고 이미 있는 엔티티는 병합

    - delete(T) : 엔티티 하나를 삭제한다. 내부에서 EntityManager.remove() 호출

    - findById(ID) : 엔티티 하나를 조회한다. 내부에서 EntityManager.find() 호출

    - getOne(ID) : 엔티티를 프록시로 조회한다. 내부에서 EntityManager.getReference() 호출

    - findAll(…) : 모든 엔티티를 조회한다. 정렬(Sort)이나 페이징(Pageable) 조건을 파라미터로 제공할 수 있음

### 쿼리 메서드

* 메서드 이름으로 쿼리 생성

    - Spring Data Jpa 가 메소드 이름을 분석해서 쿼리를 실행하는 메서드를 만들어 주는 기능

    ```java
    // Spring Data Jpa 미사용
    // 이름과 나이를 기준으로 회원을 조회하는 메서드
    public List<Member> findByUsernameAndAgeGreaterThan(String username, int age){
        return em.createQuery("select m from Member m where m.username = :username and m.age > :age", Member.class)
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();
    }
    ```
    ```java
    // Srping Data Jpa 사용
    // 메서드 이름을 분석해서 그에 맞는 JPQL을 실행하는 메서드를 자동으로 구현해줌
    public interface MemberRepository extends JpaRepository<Member, Long> {
        List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
    }
    ```

* 문법을 정확히 지켜야 의도대로 실행된다

* 쿼리 메서드 필터 조건

    - https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation

* 쿼리 메서드 작성 방법

    - 조회: find…By ,read…By ,query…By get…By,
        
        - https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.querymethods.query-creation

        - ex:) findHelloBy 처럼 ...에 식별하기 위한 내용(설명)이 들어가도 된다

    - COUNT: count…By 반환타입 long

    - EXISTS: exists…By 반환타입 boolean

    - 삭제: delete…By, remove…By 반환타입 long

    - DISTINCT: findDistinct, findMemberDistinctBy

    - LIMIT: findFirst3, findFirst, findTop, findTop3

* 엔티티의 필드명이 변경되면 인터페이스에 정의한 쿼리 메서드의 이름도 함께 변경해야 한다

### NamedQuery

* 쿼리를 미리 정의하고 이름을 붙여서 관리하는 방식

* Named Query를 직접 등록해서 사용하는 일은 드물다

    - 순수 JPA로 NamedQuery 사용

    ```java
    // @NamedQuery 어노테이션으로 Named 쿼리 정의
    @Entity
    @NamedQuery(
        name="Member.findByUsername",
        query="select m from Member m where m.username = :username")
    public class Member {
        ...
    }
    ```
    ```java
    // Repository 에서 Named 쿼리 호출
    public class MemberRepository {

        public List<Member> findByUsername(String username) {
            ...
            List<Member> resultList =
                em.createNamedQuery("Member.findByUsername", Member.class)
                    .setParameter("username", username)
                    .getResultList();
        }
    }
    ```

    - 스프링 데이터 JPA로 NamedQuery 사용

    ```java
    // @NamedQuery 어노테이션으로 Named 쿼리 정의 하는것은 같음
    @Entity
    @NamedQuery(
        name="Member.findByUsername",
        query="select m from Member m where m.username = :username")
    public class Member {
        ...
    }
    ```
    ```java
    public interface MemberRepository extends JpaRepository<Member, Long>{
        
        // Member 엔티티에 정의된 네임드 쿼리 이름이 Member.findByUsername 인 것을 찾아 실행
        @Query(name = "Member.findByUsername")
        List<Member> findByUsername(@Param("username") String username);
    }
    ```

* @Query 로 리포지토리 메서드에 쿼리 정의

    - 네임드 쿼리 필요 시 이 방법을 주로 사용한다

    ```java
    public interface MemberRepository extends JpaRepository<Member, Long> {

        @Query("select m from Member m where m.username = :username and m.age = :age")
        List<Member> findUser(@Param("username") String username, @Param("age") int age);
    }

    // test
    @Test
    public void findUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }
    ```

* 조회 결과를 DTO 로 반환

    ```java
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    // test
    @Test
    public void findMemberDto() {

        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("s = " + dto);
        }
    }
    ```

* 파라미터 바인딩

    ```java
    select m from Member m where m.username = ?0 // 위치 기반
    select m from Member m where m.username = :name // 이름 기반

    // 대부분 이름 기반 사용

    @Query("select m from Member m where m.username = :name")
    Member findMembers(@Param("name") String username);
    ```

    - 컬렉션 파라미터 바인딩

    ```java

    // Collection 타입으로 in절 지원
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    // test
    @Test
    public void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("aaa", "BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    // 실행되는 쿼리
    // 컬렉션의 데이터가 sql in에 사용됨
    select
        m1_0.member_id,
        m1_0.age,
        m1_0.team_id,
        m1_0.username 
    from
        member m1_0 
    where
        m1_0.username in ('aaa','BBB')
    ```

* 반환 타입

    ```java
    List<Member> findByUsername(String name); // 컬렉션
    Member findByUsername(String name); // 단건
    Optional<Member> findByUsername(String name); // 단건 Optional
    ```

    - 컬렉션

        - 반환 타입이 컬렉션일 땐 조회 결과가 없거나 한개나 여러개라도 정상
    
        - 결과 없음 : 빈 컬렉션 반환

        - 결과 하나 또는 여러개 : 한개 또는 여러개의 데이터가 담긴 컬렉션 반환
    
    - 단건 조회

        - 반환 타입이 단일 타입일 땐 결과가 없으면 null, 2개 이상이면 예외 발생

        - 결과 한개 : 정상 반환
    
        - 결과 없음 : null 반환

            - 순수 jpa에선 NoResultException 예외가 발생하지만 DataJpa에선 해당 예외를 잡아 null 로 반환
    
        - 결과가 2건 이상 : javax.persistence.NonUniqueResultException 예외 발생

        - 단건 조회 시 결과가 있을 수도 있고, 없을 수도 있을 땐 Optional 사용

### 페이징과 정렬

* 순수 JPA로 구현

    ```java
    /**
     * 검색 조건: 나이가 10살
     * 정렬 조건: 이름으로 내림차순
     * 페이징 조건: 첫 번째 페이지, 페이지당 보여줄 데이터는 3건
    */

    public List<Member> findByPage(int age, int offset, int limit) {
        return em.createQuery("select m from Member m where m.age = :age order by m.username desc")
            .setParameter("age", age)
            .setFirstResult(offset)
            .setMaxResults(limit)
            .getResultList();
    }

    public long totalCount(int age) {
    return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
        .setParameter("age", age)
        .getSingleResult();
    }

    // test
    @Test
    public void paging(){

        // given
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 10));
        memberJpaRepository.save(new Member("member3", 10));
        memberJpaRepository.save(new Member("member4", 10));
        memberJpaRepository.save(new Member("member5", 10));

        int age = 10;
        int offset = 0;
        int limit = 3;

        // when
        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);

        // member, totalCount 변수를 페이지 계산 공식에 적용...
        // totalPage = totalCount / size ...
        // 마지막 페이지 ...
        // 최초 페이지 ..

        // then
        assertThat(members.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(5);
    }
    ```

* Spring Data Jpa 로 구현

    - 페이징과 정렬 기능을 구현해 놓음

    - 이전에는 페이징을 위한 로직을 따로 작성해야 했다

    ```java
    org.springframework.data.domain.Sort : 정렬 기능

    org.springframework.data.domain.Pageable : 페이징 기능 (내부에 Sort 포함)

    // 특별한 반환 타입
    org.springframework.data.domain.Page : 추가 count 쿼리 결과를 포함하는 페이징

    org.springframework.data.domain.Slice : 추가 count 쿼리 없이 다음 페이지만 확인 가능(내부적으로 limit + 1조회)

    List (자바 컬렉션): 추가 count 쿼리 없이 결과만 반환

    // 사용 예시
    Page<Member> findByUsername(String name, Pageable pageable); //count 쿼리 사용

    Slice<Member> findByUsername(String name, Pageable pageable); //count 쿼리 사용 안함

    List<Member> findByUsername(String name, Pageable pageable); //count 쿼리 사용 안함

    List<Member> findByUsername(String name, Sort sort);
    ```
    ```java
    // repository
    public interface MemberRepository extends JpaRepository<Member, Long> {
        Page<Member> findByAge(int age, Pageable pageable);

        // Slice<Member> findByAge(int age, Pageable pageable);
    }

    // test
    @Test
    public void paging(){

        // given
        
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;

        // when
        
        /**
         * Pageable 의 구현체 PageRequest 사용
            * 첫 번째 파라미터에 현재 페이지, 두 번째 파라미터에 조회할 데이터 수
            * sort 는 제외 가능
        */ 
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // Pageable 은 인터페이스 이므로 구현체인 PageRequest 를 인자로 전달한다
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        // 페이징을 유지하며 dto 로 변환
        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        // Slice 는 추가로 limit + 1 으로 조회
        // Slice<Member> page = memberRepository.findByAge(age, pageRequest);

        // then
        
        List<Member> content = page.getContent(); //조회된 데이터
        long totalElements = page.getTotalElements();

        for (Member member : content) {
            System.out.println("member = " + member);
        }
        System.out.println("totalElements = " + totalElements);

        assertThat(content.size()).isEqualTo(3); //조회된 데이터 수
        assertThat(page.getTotalElements()).isEqualTo(5); //전체 데이터 수
        assertThat(page.getNumber()).isEqualTo(0); //페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2); //전체 페이지 번호
        assertThat(page.isFirst()).isTrue(); //첫번째 항목인가?
        assertThat(page.hasNext()).isTrue(); //다음 페이지가 있는가?
    }
    ```
    ```java
    // Page 인터페이스
    public interface Page<T> extends Slice<T> {

        int getTotalPages(); //전체 페이지 수

        long getTotalElements(); //전체 데이터 수

        <U> Page<U> map(Function<? super T, ? extends U> converter); //변환기
    }
    ```
    ```java
    // Slice 인터페이스
    public interface Slice<T> extends Streamable<T> {

        int getNumber(); //현재 페이지
        int getSize(); //페이지 크기
        int getNumberOfElements(); //현재 페이지에 나올 데이터 수
        List<T> getContent(); //조회된 데이터
        boolean hasContent(); //조회된 데이터 존재 여부
        Sort getSort(); //정렬 정보
        boolean isFirst(); //현재 페이지가 첫 페이지 인지 여부
        boolean isLast(); //현재 페이지가 마지막 페이지 인지 여부
        boolean hasNext(); //다음 페이지 여부
        boolean hasPrevious(); //이전 페이지 여부
        Pageable getPageable(); //페이지 요청 정보
        Pageable nextPageable(); //다음 페이지 객체
        Pageable previousPageable();//이전 페이지 객체
        <U> Slice<U> map(Function<? super T, ? extends U> converter); //변환기
    }
    ```

### 벌크성 수정 쿼리

* 더티 체킹으로 하나씩 수정하는것이 아닌 일괄적으로 수정하는 쿼리

* 순수 JPA 로 구현

    ```java
    /**
     * 벌크 수정 쿼리
    */
    public int bulkAgePlus(int age){
        return em.createQuery(
                "update Member m set m.age = m.age + 1" +
                " where m.age >= :age")
                .setParameter("age", age)
                .executeUpdate();
    }
    ```
    ```java
    /**
     * 벌크 수정 쿼리 테스트
    */
    @Test
    public void bulkUpdate() throws Exception {
        
        // given
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 19));
        memberJpaRepository.save(new Member("member3", 20));
        memberJpaRepository.save(new Member("member4", 21));
        memberJpaRepository.save(new Member("member5", 40));
        
        // when
        int resultCount = memberJpaRepository.bulkAgePlus(20);
        
        // then
        assertThat(resultCount).isEqualTo(3);
    }
    ```

* Spring Data Jpa 로 구현

    ```java
    public interface MemberRepository extends JpaRepository<Member, Long> {

        // @Modifying 이 있어야 내부적으로 executeUpdate() 를 실행
        // 없다면 getResultList() 나 getSingleResult() 가 호출되어 업데이트 되지 않음
        @Modifying
        @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
        int bulkAgePlus(@Param("age") int age);
    }
    ```
    ```java
    /**
     * 벌크 수정 쿼리 테스트
    */
    @Test
    public void bulkUpdate() throws Exception {

        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        
        // 더티체킹과 달리 영속성 컨텍스트를 거치지 않고 바로 DB에 update sql 을 전달
        // 따라서 영속성 컨텍스트에는 해당 업데이트가 적용되지 않음 
        // int resultCount = memberRepository.bulkAgePlus(20);
        
        // findListByUsername() 호출 시 영속성 컨텍스트에 유저 데이터가 있으므로 영속성 컨텍스를 조회 (업데이트 되지 않은 데이터)
        // List<Member> result = memberRepository.findListByUsername("member5");
        // Member member5 = result.get(0);
        
        // 40 출력 
        // System.out.println("member5 = " + member5);

        int resultCount = memberRepository.bulkAgePlus(20);
    
        // 영속성 컨텍스트이 변경사항 DB에 반영
        em.flush();

        // 영속성 컨텍스트 초기화
        // @Modifying(clearAutomatically = true) 옵션 사용 시 자동으로 초기화 하므로 생략 가능. 기본값 false
        em.clear();

        // 영속성 컨텍스트에 데이터가 없으므로 DB 조회 후 반환하고 영속성 컨텍스트에 저장
        List<Member> result = memberRepository.findListByUsername("member5");
        Member member5 = result.get(0);

        // 41 조회됨
        System.out.println("member5 = " + member5);

        //then
        assertThat(resultCount).isEqualTo(3);
    }
    ```

### @EntityGraph

* 연관된 엔티티들을 SQL 한번에 조회하는 방법

* 사실상 페치 조인의 간편 버전

* Spring Data Jpa 로 조회

    ```java
    // repository
    public interface MemberRepository extends JpaRepository<Member, Long> {

        @Query("select m from Member m left join fetch m.team")
        List<Member> findMemberFetchJoin();
    }

    // test
    @Test
    public void findMemberLazy() throws Exception {

        // given
        // member1 -> teamA
        // member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 20, teamB));

        em.flush();
        em.clear();

        // when
        // 지연 로딩. N + 1 발생
        // List<Member> members = memberRepository.findAll();

        // 연관 테이블까지 한꺼번에 조회
        // N + 1 발생하지 않음
        List<Member> members = memberRepository.findMemberFetchJoin();

        //then
        for (Member member : members) {
            member.getTeam().getName();
        }
    }
    ```

* Spring Data Jpa 의 @EntityGraph 로 조회

    ```java
    // repository
    public interface MemberRepository extends JpaRepository<Member, Long> {

        @Override
        @EntityGraph(attributePaths = {"team"})
        List<Member> findAll();
    }

    // test
    @Test
    public void findMemberLazy() throws Exception {

        // given
        // member1 -> teamA
        // member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 20, teamB));

        em.flush();
        em.clear();

        @EntityGraph 사용
        List<Member> members = memberRepository.findAll();

        //then
        for (Member member : members) {
            member.getTeam().getName();
        }
    }
    ```

### JPA Hint

* JPA 쿼리 힌트(SQL 힌트가 아니라 JPA 구현체에게 제공하는 힌트)

* 잘 사용되지 않음

```java
// repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);
}

// test
@Test
public void queryHint() throws Exception {

    //given
    memberRepository.save(new Member("member1", 10));
    em.flush();
    em.clear();

    //when
    Member member = memberRepository.findReadOnlyByUsername("member1");

    /**
     * set..() 호출 시 더티 체킹 실행
     * - jpa는 더티 체킹을 위해 원본과 비교 데이터 2 종류를 가지고 있어야 한다
     * - 그리고 둘을 비교해 update sql을 생성한다
     * - 최적화가 잘 되있더라도 구조상 성능이 필요한 구간이다
     * 만약 더티 체킹을 하지 않고 set() 만 호출하고 싶다면 @QueryHints 로 readOnly 옵션을 주어 해결할 수 있다
     * - set..() 호출해도 힌트에 readOnly 가 명시되어 있으므로 더티체킹을 하지 않는다
    */
    member.setUsername("member2");
    em.flush(); // 더티체킹 (Update Query 실행 X)
}
```

### Lock

```java
public interface MemberRepository extends JpaRepository<Member, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findByUsername(String name);
}
```
