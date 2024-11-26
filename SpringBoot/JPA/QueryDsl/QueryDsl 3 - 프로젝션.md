### 프로젝션

* select 에 대상을 지정하는 것

    - 프로젝션 대상이 하나라면 타입을 명확하게 지정 가능

    - 정확히 말해서 쿼리에서 select 절에 명시된 반환 대상 을 뜻한다

        - select(member.username) 
        
            - 반환 대상이 member.username 단일 속성
            
            - 따라서 프로젝션 대상은 하나

        - select(member)
        
            - 반환 대상이 member 엔티티 전체
            
            - 엔티티는 여러 필드(username, age, team)를 가지지만, 프로젝션 관점에서 보면 하나의 엔티티를 반환하므로 프로젝션 대상은 하나다

* 단일 프로젝션 조회 결과 반환

    ```java
    @Test
    public void simpleProjection(){
        List<String> result = queryFactory
                .select(member.username)
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }
    ```

* 다중 프로젝션 조회 결과 반환

        - 프로젝션 대상이 둘 이상이면 Tuple이나 DTO 로 조회

        - Tuple 은 다중 프로젝션 결과를 조회할때 사용
    
    ```java
    @Test
    public void tupleProjection(){
        List<Tuple> result = queryFactory
                .select(member.username, member.age)
                .from(member)
                .fetch();
        
        for (Tuple tuple : result) {
            String username = tuple.get(member.username);
            Integer age = tuple.get(member.age);
            System.out.println("username=" + username);
            System.out.println("age=" + age);
        }
    }
    ```

* 조회 결과 DTO 로 반환

    - 순수 JPA 에서 DTO 로 반환

    ```java
    // DTO
    @Data
    public class MemberDto {
        
        private String username;
        private int age;
        
        public MemberDto(String username, int age){
            this.username = username;
            this.age = age;
        }
    }

    /**
     * 순수 JPA 에서 DTO 로 조회 에서 DTO 로 조회
     */
    @Test
    public void findDtoByJPQL(){
        List<MemberDto> result = em.createQuery("select new study.querydsl.dto.MemberDto(m.username, m.age) from Member m", MemberDto.class)
                .getResultList();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }
    ```

    - QueryDsl 에서 DTO 로 반환

        - 프로퍼티 접근, 필드 직접 접근, 생성자 사용, @QueryProjection 의 네가지 방법이 있다

        - 프로퍼티 접근

            ```java
            // MemberDto
            @Data
            @NoArgsConstructor
            public class MemberDto {

                private String username;
                private int age;

                public MemberDto(String username, int age){
                    this.username = username;
                    this.age = age;
                }
            }
            ```

            ```java
            /**
             * 프로퍼티 접근 방법을 사용해 DTO 로 조회
             * Projections.bean 에서 bean 은 getter, setter 를 뜻한다
            */
            @Test
            public void findDtoBySetter(){
                List<MemberDto> result = queryFactory
                        .select(Projections.bean(MemberDto.class,
                                member.username,
                                member.age))
                        .from(member)
                        .fetch();

                for (MemberDto memberDto : result) {
                    System.out.println("memberDto + " + memberDto);
                }
            }
            ```

        - 필드 접근

            ```java
            /**
             * 필드 접근 방법을 사용해 DTO 로 조회
             * Getter, Setter 없어도 됨
            */
            @Test
            public void findDtoByField(){
                List<MemberDto> result = queryFactory
                        .select(Projections.fields(MemberDto.class,
                                member.username,
                                member.age))
                        .from(member)
                        .fetch();

                for (MemberDto memberDto : result) {
                    System.out.println("memberDto + " + memberDto);
                }
            }
            ```

        - 프로퍼티, 필드 접근 사용 시 반환 타입에 쓰이는 Dto의 필드 이름과 쿼리 결과의 필드 이름이(엔티티의 필드 이름) 다를 때

            - 프로퍼티, 필드 접근 방법은 DTO 객체의 필드 이름과 쿼리 결과의 필드 이름을 기준으로 매핑한다

            - UserDto 클래스는 name과 age라는 필드를 가지므로, 쿼리 결과에서 name과 age라는 이름의 데이터를 찾아 매핑한다

            - 만약 쿼리 결과가 username = "member1" 인데 UserDto 의 필드는 name 이라면 이름이 맞이 않으므로 null 로 저장된다

            ```java
            // UserDto
            @Data
            @NoArgsConstructor
            public class UserDto {

                private String name;
                private int age;

                public UserDto(String name, int age) {
                    this.name = name;
                    this.age = age;
                }
            }
            ```

            ```java
            @Test
            public void findUserDto(){
                List<UserDto> result = queryFactory
                        .select(Projections.fields(UserDto.class,
                                member.username.as("name"),
                                member.age))
                        .from(member)
                        .fetch();

                for (UserDto userDto : result) {
                    System.out.println("userDto + " + userDto);
                }
            }

            // QueryDSL은 위 설정을 기반으로 SQL 쿼리를 생성

            // 생성된 SQl은 별칭을 사용해 조회 결과 필드 이름을 DTO와 맞춤

            // 조회된 데이터는 UserDto의 필드에 매핑
            ```

        - 서브쿼리에 별칭을 줄 경우

            ```java
            @Test
            public void findUserDto(){

                QMember memberSub = new QMember("memberSub");

                List<UserDto> result = queryFactory
                        .select(Projections.fields(UserDto.class,
                                member.username.as("name"),

                                ExpressionUtils.as(JPAExpressions
                                        .select(memberSub.age.max())
                                        .from(memberSub), "age") // 별칭 age
                        ))
                        .from(member)
                        .fetch();

                for (UserDto userDto : result) {
                    System.out.println("userDto + " + userDto);
                }
            }
            ```

        - 생성자 사용

            - 생성자 사용 방법은 조회 필드 이름과 DTO 필드 이름이 달라도 된다

            ```java
            /**
             * 생성자를 사용해 DTO 로 조회
            */
            @Test
            public void findDtoByConstructor(){
                List<MemberDto> result = queryFactory
                        .select(Projections.constructor(MemberDto.class,
                                member.username,
                                member.age))
                        .from(member)
                        .fetch();

                for (MemberDto memberDto : result) {
                    System.out.println("memberDto + " + memberDto);
                }
            }
            ```

        - @QueryProjection

            - DTO를 Q 타입으로 생성한 뒤, 해당 Q 타입을 프로젝션에 사용

            - 생성자 사용 방식과 다른 점은 컴파일 단계에서 오류를 확인할 수 잇다

                - 생성자 사용 시 실수로 매개변수를 더 사용해도 컴파일 단계에서 오류 검출 불가

                - @QueryProjection 사용 시 매개변수를 더 사용하면 컴파일 단계에서 오류 검출

            - 단점

                - Q타입 DTO 클래스가 추가로 생성된다

                - DTO 클래스에 @QueryProjection 을 적용함으로서 DTO 가 QueryDsl 에 의존성이 생긴다

                    - QueryDsl 제거 시 DTO 도 영향을 받게 된다

            ```java
            // 프로젝션에 사용할 DTO 의 생성자에 @QueryProjection 적용
            // 이후 컴파일 하면 Q 타입 DTO 가 생성된다
            @Data
            @NoArgsConstructor
            public class MemberDto {

                private String username;
                private int age;

                // 생성자에 @QueryProjection 적용
                @QueryProjection
                public MemberDto(String username, int age){
                    this.username = username;
                    this.age = age;
                }
            }
            ```

            ```java
            @Test
            public void findDtoByQueryProjection(){
                List<MemberDto> result = queryFactory

                        // 프로젝션으로 Q타입 DTO 생성자 사용
                        .select(new QMemberDto(member.username, member.age))
                        .from(member)
                        .fetch();

                for (MemberDto memberDto : result) {
                    System.out.println("memberDto = " + memberDto);
                }
            }
            ```

### 동적 쿼리

* BooleanBuilder 를 사용한 동적 쿼리

    - BooleanBuilder 는 동적 쿼리에 사용되는 객체이다

        - .and() 를 사용해 sql 에 사용될 조건을 추가할 수 있다

    ```java
    @Test
    public void dynamicQuery_BooleanBuilder(){
        String usernameParam = "member1";
        Integer ageParam = 10;

        List<Member> result = searchMember1(usernameParam, ageParam);
        assertThat(result.size()).isEqualTo(1);
    }

    private List<Member> searchMember1(String usernameParam, Integer ageParam) {

        // 초기값을 지정할 수도 있다
        // BooleanBuilder builder = new BooleanBuilder(member.username.eq(usernameParam));

        BooleanBuilder builder = new BooleanBuilder();

        if (usernameParam != null) {

            // usernameParam != null 이라면 builder 변수에 괄호 안의 and 조건 추가
            builder.and(member.username.eq(usernameParam));
        }
        if (ageParam != null) {
            builder.and(member.age.eq(ageParam));
        }

        return queryFactory
                .selectFrom(member)
                .where(builder) // builder 를 여러개 만들어 서로 조합 가능. builder.and()
                .fetch();
    }

    // 실행되는 sql
    // age가 null 이라면 and m1_0.age=10 sql 이 생성되지 않는다
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

* Where 다중 파라미터를 사용한 동적 쿼리

    - where 조건에 null 값은 무시된다

    - 메서드를 다른 쿼리에서 재활용 할 수 있다

    ```java
    @Test
    public void dynamicQuery_WhereParam(){
        String usernameParam = "member1";
        Integer ageParam = 10;

        List<Member> result = searchMember2(usernameParam, ageParam);
        assertThat(result.size()).isEqualTo(1);
    }

    private List<Member> searchMember2(String usernameParam, Integer ageParam) {
            return queryFactory
                    .selectFrom(member)
                    .where(usernameEq(usernameParam), ageEq(ageParam)) // where 조건에 null이 오면 해당 조건은 무시된다
                    .fetch();
        }

    private Predicate usernameEq(String usernameParam) {
        return usernameParam != null ? member.username.eq(usernameParam) : null;
    }

    private Predicate ageEq(Integer ageParam) {
        return ageParam != null ? member.age.eq(ageParam) : null;
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
            and m1_0.age=10

        // usernameParam 이 null일 때
        // where 조건에서 제외된다
        select
            m1_0.member_id,
            m1_0.age,
            m1_0.team_id,
            m1_0.username 
        from
            member m1_0 
        where
            m1_0.age=10

    // where 다중 파라미터에 사용되는 메서드 조합
    @Test
    public void dynamicQuery_WhereParam(){
        String usernameParam = "member1";
        Integer ageParam = 10;

        List<Member> result = searchMember2(usernameParam, ageParam);
        assertThat(result.size()).isEqualTo(1);
    }

    private List<Member> searchMember2(String usernameParam, Integer ageParam) {

        return queryFactory
                .selectFrom(member)
                .where(allEq(usernameParam, ageParam)) // 마찬가지로 where 조건에 null이 오면 해당 조건은 무시된다
                .fetch();
        }

    // 조합 대상이 된 메서드는 반환 타입에 Predicate 대신 BooleanExpression 사용
    // BooleanExpression 이 Predicate 를 상속받는 구조
    private BooleanExpression usernameEq(String usernameParam) {
        return usernameParam != null ? member.username.eq(usernameParam) : null;
    }

    private BooleanExpression ageEq(Integer ageParam) {
        return ageParam != null ? member.age.eq(ageParam) : null;
    }

    // 위의 두 메서드를 조합
    // 두 메서드의 결과 중 null이 있다면 해당 조건은 where에서 무시됨
    private BooleanExpression allEq(String usernameCond, Integer ageCond) {
        return usernameEq(usernameCond).and(ageEq(ageCond));
    }
    ```

* 수정, 삭제 벌크 연산

    - 쿼리 한번으로 대량의 데이터를 수정할 때 사용

    - 주의점

        - update 직접 실행 시 DB의 상태와 영속성 컨텍스트의 상태가 달라진다

            - 변경 감지의 경우 엔티티를 수정하면 변경점이 영속성 컨텍스트에 저장되고, 변경 내역에 대한 sql이 DB에 반영되어 영속성 컨텍스트와 DB의 상태가 같다

            - 그러나 update 직접 실행 시 영속성 컨텍스트를 무시하고 DB에 바로 반영하기 때문에 DB 에 반영된 내역이 영속성 컨텍스트에 저장되지 못한 상태이다

            - 이 상태에서 select 시 DB에서 값을 가져오지만 영속성 컨텍스트 값과 중복되게 되고, 이 경우는 DB 조회 값을 버리고 영속성 컨텍스트의 값을 선택하게 된다

            - 잘못된 값이 조회된다

    - 해결 방법

        - 벌크 연산 후 항상 영속성 컨텍스트를 비우면 된다

        - 비운 다음 select 시 DB 조회 후 그 값을 영속성 컨텍스트에 저장한 다음 반환하므로 영속성 컨텍스와 DB 가 일치하는 값을 가지게 되고, 올바른 값도 조회된다 

    - 수정 벌크 연산

        ```java
        @Test
        public void bulkUpdate(){
            long count = queryFactory
                    .update(member)
                    .set(member.username, "비회원")
                    .where(member.age.lt(28)) // lt : 미만
                    .execute(); // update 사용 시 execute() 호출

            em.flush(); // 영속성 컨텍스트의 변경점 강제 반영 
            em.clear(); // 영속성 컨텍스트 비우기
        }

        // 실행되는 sql
        update
            member 
        set
            username=비회원
        where
            age<28
        ```

    - 삭제 벌크 연산

        ```java
        @Test
        public void bulkDelete(){
            long count = queryFactory
                    .delete(member)
                    .where(member.age.gt(18))
                    .execute();
        }

        // 실행되는 sql
        delete 
        from
            member 
        where
            age>18
        ```

    - 기존 숫자에 1 더하기

        ```java
        @Test
        public void bulkAdd(){
            long count = queryFactory
                    .update(member)
                    .set(member.age, member.age.add(1)) // 곱하기: multiply(x)
                    .execute();
        }

        // 실행되는 sql
        update
            member 
        set
            age=(age+cast(1 as integer))
        ```
