### JPA 에서 지원하는 쿼리

* JPQL

    - SQL 과 문법이 유사하며 테이블이 아닌 엔티티 객체를 대상으로 검색하는 객체 지향 쿼리
    
        - SQL 은 데이터베이스 테이블을 대상으로 쿼리

    - JPQL 이 SQl 로 변환되어 실행됨
    
    ```java
    // JPQL. 엔티티 대상으로 쿼리
    List<Member> result = em.createQuery(
        "select m From Member m where m.username like '%kim&'", Member.class)
        .getResultList();

    tx.commit();

    // JPQL 이 엔티티 매핑 정보를 읽어 DB 에서 사용하는 SQL 을 생성
    // 실행된 sql
    select member.id as id.. from Member m where member.username like '%kim%'
    ```

* Criteria

    - 문자가 아닌 자바 코드로 JPQL 을 작성할 수 있음

    - 복잡하고 실용서이 없어서 거의 사용하지 않음

* QueryDSL

    - 문자가 아닌 자바 코드로 JPQL 을 작성할 수 있음

    - 컴파일 시점에 문법 오류를 찾을 수 있음

    - 동적쿼리 작성 편리함

    - 단순하고 쉬워서 실제 업무에서 주로 사용됨

* 네이티브 SQL

    - SQl 을 직접 사용할 수 있는 기능

    - JPQL 로 해결할 수 없는 특정 데이터베이스에 의존적인 기능 필요 시 사용

        - ex) 오라클 CONNECT BY, 특정 DB 만 사용하는 SQL 힌트
    
    ```java
    // 네이티브 SQL
    em.createQuery("select MEMBER_ID, city, street, zipcode, USERNAME From Member")
        .getResultList();

    tx.commit();
    ```

### JPQL(Java Persistence Query Language)

* 객체지향 쿼리 언어로 테이블을 대상으로 쿼리하는것이 아니라 엔티티 객체를 대상으로 쿼리한다

* SQL 을 추상화해서 특정 데이터베이스 SQL 에 의존하지 않는다

* JPQL 은 SQL 로 변환된다

* JPQL 문법

    <img src ="https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Spring/JPA/JPQL%20%EB%AC%B8%EB%B2%95.jpg" alt="JPQL 문법">

    - select m from Member as m where m.age > 18

        - 엔티티와 속성은 대소문자를 구분한다 (Member, age)

        - JPQL 키워드는 대소문자 구분하지 않는다 (SELECT, FROM, where)

        - 엔티티 이름을 사용해야 한다. 테이블 이름이 아님 (테이블이 Member, 엔티티가 M 이라면 M 을 사용)

        - 별칭은(m) 필수이고 as는 생략 가능

        - COUNT, SUM, AVG, MAX, MIN, GROUP BY, HAVING, ORDER BY 모두 사용 가능하다

    - 반환 타입 지정 TypeQuery, Query

        - TypeQuery 는 반환 타입이 명확할 때 사용

            ```java
            // 반환 타입이 Member 타입으로 명확하다
            TypedQuery<Member> query = em.createQuery("SELECT m FROM Member m", Member.class);
            ```

        - Query 는 반환 타입이 명확하지 않을 때 사용

            ```java
            // m.username 은 String, a.ge 는 int 타입으로 반환 타입이 여러개이다
            Query query = em.createQuery("SELECT m.username, m.age from Member m");

            // m.username 하나만 반환하므로 반환 타입이 String 으로 명확하다
            TypeQuery query = em.createQuery("SELECT m.username from Member m", String.class); 
            ```

    - 결과 조회

        - 결과가 하나 이상일 때 리스트 반환

            ```java
            TypeQuery<Member> query = em.createQuery(...);
            List<Member> resultList = query.getResultList();

            // 결과가 없으면 빈 리스트 반환
            ```

        - 결과가 정확히 하나일 때 단일 객체 반환

            ```java
            Query result = em.createQuery(...);
            Member result = query.getSigleResult();

            // 결과가 없으면 NoResultException 발생
            // 결과가 둘 이상이면 NonUniqueResultException 발생
            ```

    - 파라미터 바인딩

        - 이름 기준

            ```java
            Member member = new Member();
            member.setusername("member1");
            member.setAge(10);
            em.persist();

            TypeQuery<Member> query = em.createQuery(
                    "SELECT m FROM Member m where m.username=:username", Member.class);
            
            // JPQL 의 username 에 usernameParam 값을 바인딩
            query.setParameter("username", usernameParam);

            // 메서드 체이닝으로 편리하게 사용 가능
            em.createQuery(
                    "SELECT m FROM Member m where m.username=:username", Member.class)
                    .setParameter("username", usernameParam);
                    .getSingleResult();
            ```

        - 위치 기준

            - 권장하지 않는 방법

            ```java
            TypeQuery<Member> query = em.createQuery(
                    "SELECT m FROM Member m where m.username=?1", Member.class);
            
            // JPQL 의 1번에 usernameParam 값을 바인딩
            query.setParameter(1 , usernameParam);
            ```

    - 프로젝션

        - SELECT 절에 조회할 대상을 지정하는 것

        - 엔티티, 임베디드 타입, 스칼라 타입(숫자, 문자 등 기본 데이터 타입) 이 대상이 된다

        ```java
        SELECT m FROM Member m -> 엔티티 프로젝션

        // member 테이블과 team 테이블의 join 이 일어난다
        // 명시적으로 알아볼 수 있게 m.team 대신 join 키워드를 사용하는 것이 좋다
        // SELECT t FROM Member m join m.team t
        SELECT m.team FROM Member m -> 엔티티 프로젝션

        SELECT m.address FROM Member m -> 임베디드 타입 프로젝션

        SELECT m.username, m.age FROM Member m -> 스칼라 타입 프로젝션

        DISTINCT로 중복 제거
        ```

        - 프로젝션의 여러 값 조회

            ```java
            // Query 타입으로 조회
            Query query = em.createQuery("SELECT m.username, m.age FROM Member m")
                            .getResultList();

            // Object[] 타입으로 조회
            List<Object[]> resultList = em.createQuery("SELECT m.username, m.age FROM Member m")
                                            .getResultList();
                            
            Object[] result = resultList.get(0);
            log.info("usernmae", result[0]);
            log.info("age", result[1]);

            // new 명령어로 조회 - 권장 방법
            // 단순 값읏 DTO 로 바로 조회
            // 패키지 ㅁㅇ을 포함한 전체 클래스 명 입력
            // DTO 클래스에 순서와 타입이 일치하는 생성자 필요
            List<Object[]> resultList = em.createQuery(
                "SELECT new jpql.MemberDTO(m.username, m.age) FROM Member m", MemberDTO.class)
                .getResultList();

            MemberDTO memberDTO = result.get(0);
            ```
    - 페이징

        - JPA는 페이징을 두가지 API로 추상화 한다

        - 조회 시작 위치(0 부터 시작) : setFirstResult(int startPosition)

        - 조회할 데이터 수 : setMaxResults(int maxResult);

            ```java
            List<Member> resultList = em.createQuery(
                "select m from Member m order by m.name desc", Member.class)
                .setFirstResult(1)
                .setMaxResults(10)
                .getResultList();
            ```
    - 조인

        - 내부 조인 ([INNER] JOIN (INNER 생략 가능))

            ```java
            String qeury = "select m from Member m join m.team t";
            List<Member> result = em.createQuery(query, Member.class).getResultList();
            ```

        - 외부 조인 (LEFT [OUTER] JOIN (OUTER 생략 가능))

            ```java
            String qeury = "select m from Member m left join m.team t";
            ```

        - 세타 조인 (CROSS 조인)

            ```java
            String qeury = "select m from Member m, Team t where m.username = t.name";
            ```

        - ON 절

            - 조인 대상 필터링

            ```java
            // JPQL
            String qeury = "SELECT m, t FROM Member m LEFT JOIN m.team t on t.name = 'A'";

            // 실행되는 SQL
            SELECT m.*, t.* 
            FROM Member m 
            LEFT JOIN Team t 
            ON m.TEAM_ID=t.id and t.name='A'
            ```

            - 연관관계 없는 엔티티 외부 조인
            
            ```java
            // 회원의 이름과 팀의 이름이 같은 대상 외부 조인

            // JPQL
            String query = "SELECT m, t FROM Member m LEFT JOIN Team t on m.username = t.name";

            // 실행되는 SQL
            SELECT m.*, t.* 
            FROM Member m 
            LEFT JOIN Team t 
            ON m.username = t.name
            ```
        - 서브 쿼리

            - 서브 쿼리 지원 함수

                - ALL (subquery) : 모두 만족하면 참

                - ANY, SOME (subquery): 조건을 하나라도 만족하면 참

                - IN (subquery): 서브쿼리의 결과 중 하나라도 같은 것이 있으면 참

            ```java
            // 팀A 소속인 회원
            select m from Member m
            where exists (select t from m.team t where t.name = '팀A')
            
            // 전체 상품 각각의 재고보다 주문량이 많은 주문들
            select o from Order o
            where o.orderAmount > ALL (select p.stockAmount from Product p)
            
            // 어떤 팀이든 팀에 소속된 회원
            select m from Member m
            where m.team = ANY (select t from Team t)
            ```

        - JPQL 기본 함수

            - CONCAT

            - SUBSTRING

            - TRIM
            
            - LOWER, UPPER

            - LENGTH

            - LOCATE

            - ABS, SQRT, MOD

            - SIZE, INDEX(JPA 용도)
