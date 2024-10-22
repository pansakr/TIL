### 경로 표현식

* .으로 객체 그래프를 탐색하는 것

```java
select m.username -> 상태 필드
from member m
join m.team t   -> 단일 값 연관 필드
join m.orders o -> 컬렉션 값 연관 필드
where t.name = "팀A"
```

* 명시적 조인과 묵시적 조인

    - 명시적 조인
    
        - join 키워드 직접 사용

        ```java
        select m from Member m join m.team t
        ```
    
    - 묵시적 조인

        - 경로 표현식에 의해 묵시적으로 SQL 조인이 발생하는 것 (내부 조인만 가능)

        ```java
        // 이 JPQL은 member 와 team 을 join 하는 sql로 바뀌어 실행된다
        select m.team from Member m
        ```

* 상태 필드

    - 단순히 상태 값을 저장하기 위한 필드

        - m.username

    - .으로 더 이상 탐색할 수 없다

* 연관 필드

    - 연관관계를 위한 필드

    - 단일 값 연관 필드

        - 대상이 엔티티 ex) m.team

        - 묵시적 내부 조인이 발생한다

         - 마지막이 객체로 끝났기 때문에 .으로 더 탐색할 수 있다

        ```java
        String qeury = "select m.team From Member m";

        // member 와 team 테이블을 join 해서 가져온다
        // 실행되는 sql
        select team.xx .. from member m
        join team t
        on m.id = t.id
        ```

    - 컬렉션 값 연관 필드

        - 대상이 컬렉션 ex) m.orders

        - 묵시적 내부 조인 발생, 더이상 탐색 불가능

        ```java
        @Entity
        public class Team{

            private List<Member> members;

            ...
        }

        // members 는 컬렉션이기 때문에 내부를 더이상 탐색하지 못한다
        String query = "select t.members From Team t"

        // FROM 절에서 명시적 조인으로 별칭을 얻어서 탐색할 수 있다
        String query = "select m.username From Team t join t.members m"
        ```

* 실제 업무에서는 명시적 조인을 사용해야 한다

    - 묵시적 조인은 예상하지 못한 join 쿼리가 추가로 생성되기 때문에 관리하기 어렵다

* 패치 조인(Fetch Join)

    - 연관 엔티티나 컬렉션은 SQL 한 번에 조회하는 기능

    - N + 1 해결 방안으로 사용된다

        - N + 1

            - 최초 쿼리를 실행한 쿼리에 더해서 추가로 쿼리가 실행되는 것

            ```java
            // N + 1 발생 코드

            Team team1 = new Team();
            Team team2 = new Team();
            team1.set()...
            team2.set()...

            // Member 엔티티는 팀 엔티티를 참조
            Member member1 = new Member();
            Member member2 = new Member();
            Member member3 = new Member();

            member1.setTeam(team1);
            member2.setteam(team1);
            member3.setTeam(team2);
            ...
            em.persist(member1);
            em.persist(member2);
            em.persist(member3);

            String query = "select m From Member m";

            // Member 결과 여러개 조회됨
            // Member 가 참조하는 Team은 프록시 객체로 조회됨
            List<Member> result = em.createQuery(query, member.class).getResultList();

            for(Member member : result){
                // member.getTeam() 으로 team 객체 사용
                // member.getTeam() 사용 시점에 Team 프록시 객체의 값을 조회하기 위해 영속성 컨텍스트에 요청
                // 첫 조회시 영속성 컨텍스트에 Team 값이 없으니 DB에 조회 후 영속성 컨텍스트에 저장 후 값을 반환
                // 두번째 조회시 영속성 컨텍스트에 Team 값이 있으니 DB 조회하지 않고 값을 가져올 수 있음
                // team 을 조회하는 sql 생성됨   
                System.out.println("member = " + member.getTeam());
            }

            // 생성된 sql
            // member1, member2 는 같은 팀을 가지고 있다
            // member1 의 team을 조회한 결과는 영속성 컨텍스트에 남아 있고, member2 조회 시 같은 팀이므로 DB를 조회하지 않고 영속성 컨텍스트의 값을 가져온다
            // member3는 다른 team을 가지고 있으므로 영속성 컨텍스트에 결과가 없기 때문에 DB를 조회해서 가져온다
            select m from member m
            select team.* from team where team.id = xx // member1 의 팀을 조회하기 위해 생성된 sql
            select team.* from team where team.id = xx // member3 의 팀을 조회하기 위해 생성된 sql

            // select m From Member m 하나를 실행했는데 총 3개의 sql이 실행되었다
            // 만약 회원이 100명이고, 각각의 팀을 가지고 있다면 100번의 추가 sql이 실행된다
            // 1 + 100 이 되는 셈
            ```

        - 패치 조인

            - 초기 조회 시점에 연관된 엔티티의 데이터를 함께 가져와서 DB의 추가 조회를 막는 방법

            - 지연로딩 보다 fetch join 이 우선됨

                - 엔티티에 지연 로딩으로 설정해도 fetch join 사용 시 fetch join 으로 실행됨

            ```java
            // 위 코드에 이 부분만 변경
            // 연관 관계의 데이터를 함께 조인해서 가져옴
            String query = "select m From Member m join fetch m.team"

            // member 와 연관관계인 team 의 데이터까지 한꺼번에 조인해서 가져온다
            // 이후에 member.team() 을 호출해도 이미 데이터를 가져왔기 때문에 추가로 DB를 조회하지 않는다 
            ```
        - 일반 조인과 패치 조인의 차이

            - 패치 조인을 사용할 때만 연관 엔티티도 함께 조회 (즉시 로딩)

            - 일반 조인 실행 시 연관된 엔티티를 함께 조회하지 않음

            ```java
            // 일반 join
            // member 와 team 을 조인해서 team 의 데이터를 조회
            String query = "select m From member m join m.team t"

            // 패치 join
            // member 와 team 을 조인해서 member 와 team 모두 조회
            String query = "select m From Member m join fetch m.team"
            ```

    - 패치 조인 주의점

        - 패치 조인 대상에는 별칭을 줄 수 없다

        - 둘 이상의 컬렉션은 패치 조인 할 수 없다

        - 컬렉션을 패치 조인하면 페이징 API 를 사용할 수 없다

            - 일대일, 다대일 같은 단일 값 연관 필드들은 패치 조인해도 페이징 가능

        - 엔티티의 글로벌 로딩 전략보다 우선함

            - 글로벌 로딩 전략은 모두 지연 로딩으로 최적화가 필요한 부분에 패치 조인을 적용해야 한다

* 엔티티 직접 사용

    - JPQL 에서 엔티티 직접 지정 시 SQL 에서 해당 엔티티의 기본 키 값을 사용한다

    ```java
    // JPQL
    select count(m.id) from Member m  // 엔티티의 아이디를 사용
    select count(m) from Member m     // 엔티티를 직접 사용

    // 실행되는 SQL
    // JPQL 둘 다 아래의 SQL 실행
    select count(m.id) as cnt from Member m

    // 엔티티를 파라미터로 전달
    String jpql = "select m from Member m where m = :member";
    String jpql2 = "select m from Member m where m.id = :memberId";

    em.createQuery(jpql).setParameter("member", member).getResultList();
    em.createQuery(jpql2).setParameter("memberId", memberId).getResultList();

    // 실행되는 SQL
    // JPQL 둘 다 아래의 SQL 실행
    select m.* from Member m where m.id = ?

    // 외래키 값 사용
    String jpql = "select m from Member m where m.team = :team";
    xxx.setParameter("team", team);

    String jpql = "select m from Member m where m.teamId = :teamId";
    xxx.setParameter("teamId", teamId);

    // 둘다 해당 SQL로 변환되어 실행
    select m.* from Member m where m.team_id = ?
    ```

* Named 쿼리

    - 미리 정의해서 이름을 부여해두고 사용하는 JPQL

    - 어노테이션, XML 에 정의할 수 있고, 정적 쿼리만 가능

        - XML 이 우선권을 가진다

    - 애플리케이션 로딩 시점에 초기화 후 재사용

    - 애플리케이션 로딩 시점에 쿼리를 검증

        - 애플리케이션 실행 시점에 네임드 쿼리의 문법 오류가 검출된다

    ```java
    @Entity
    @NamedQuery(
        name = "Member.findByUsername",
        query="select m from Member m where m.username = :username")
    public class Member {
    ...
    }
    List<Member> resultList =
            em.createNamedQuery("Member.findByUsername", Member.class)
            .setParameter("username", "회원1")
            .getResultList();
    ```

* 벌크 연산

    - 주로 여러 건의 Update, Delete 에 사용

    - jpa 변경 감지 기능으로 여러 건의 update를 실행하려면 너무 많은 update sql 이 실행된다

        - 모든 상품의 가격을 10% 인상

        - 모든 상품 조회 -> 상품 엔티티의 가격을 10% 증가시킴

        - 트랜잭션 커밋 시 변경감지가 동작

        - 변경된 데이터가 100건이라면 100번의 update sql 실행

    - 벌크 연산을 사용하면 쿼리 한번으로 여러 테이블의 로우를 변경할 수 있다

        ```java
        String sql = "update Member m set m.age = 20";

        int resultCount = em.createQuery(sql).executeupdate();
        ```

        - executeUpdate()의 결과로 영향받은 엔티티 수 반환

        - update, delete 지원

    - 벌크 연산은 영속성 컨텍스트를 무시하고 데이터베이스에 직접 쿼리

        - 벌크 연산 수행 후 영속성 컨텍스트가 초기화 해야 한다

            - 벌크 연산은 영속성 컨텍스트에 저장되지 않고 DB에 바로 반영된다

            - 그래서 영속성 컨텍스트에는 벌크 연산으로 업데이트된 데이터가 없는 상태이다

            - 이 상태에서 벌크 연산으로 업데이트한 값을 호출하면 업데이트 되지 않은 영속성 컨텍스트의 값을 가져오기 때문에 원하는 데이터가 나오지 않는다

            - 영속성 컨텍스트를 초기화 시켜주고 호출하면 DB의 값을 읽어오기 때문에 원하는 값을 얻을 수 있다

            ```java
            Member member = new Member();
            member.setUsername("회원1");
            member.setAge(0);

            // 영속성 컨텍스트에 저장하고 DB에 반영
            // 현재 영속성 컨텍스트의 member의 age 값 0
            em.persist();

            // 영속성 컨텍스트에 저장하지 않고 DB에 바로 반영
            int resultCount = 
                em.createQuery("update Member m set m.age = 20").executeUpdate();

            // 영속성 컨텍스트의 값을 가져오기 때문에 age 값은 0이다
            Member findMember = em.find(Member.class, member1.getId());

            // 영속성 컨텍스트 초기화
            em.clear();

            // 영속성 컨텍스트가 초기화 되었기 때문에 DB 조회 후 결과를 영속성 컨텍스트에 저장하고 그 값을 가져온다 
            // 벌크 연산으로 업데이트된 20 이 조회됨
            Member findMember = em.find(Member.class, member1.getId());
            ```    
