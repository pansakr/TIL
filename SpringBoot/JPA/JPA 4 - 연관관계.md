### 연관관계

* 객체는 참조, 테이블은 외래키로 연관관계를 맺는다.

* jpa를 잘 사용하기 위해선 객체의 참조와 테이블의 외래키를 잘 매핑해야 한다.

* 매핑에 필요한 방향, 다중성, 연관관계의 주인 의 3가지 핵심 키워드가 있다.


### 방향

* 단방향, 양방향 연관관계가 있다.

* 테이블은 조인을 사용해 조회할 수 있으므로 항상 양방향이다.

* 객체는 한쪽만 참조하면 단방향, 서로를 참조하면 양방향이다.

```
// A, B 클래스가 있고 A가 B를 참조할때, 즉 A클래스의 필드로 B가 있을때 단방향 관계이다.

// 만약 B클래스도 A클래스를 참조한다면 이때는 A, B가 서로를 참조하니 양방향 관계이다.
```


### 다중성

* 객체들간의 관계 또는 테이블간의 관계가 일대일, 일대다, 다대다 임을 나타낸다.

* 일대일은 서로가 하나씩 가질 수 있는 관계이고 @OneToOne 으로 표시한다.

* 일대다, 다대일은 한쪽이 다른쪽을 여러개 가지고 반대는 하나만 가질 수 있는 관계이고 @OneToMany, @ManyToOne으로 표시한다.

* 다대다는 양쪽이 서로를 여러개 가질 수 있는 관계이고 @ManyToMany 로 표시한다.


### 객체와 테이블의 연관관계

* 회원과 팀이 있고 회원은 하나의 팀에만 소속될 수 있을때 둘은 다대일 관계다.

* 객체로 만들었을때 회원 클래스가 팀 클래스를 필드로 가지고 있다고 가정한다.(설계에 따라 반대가 될 수 있다.)

* 회원은 팀을 참조하니 회원.팀()으로 팀 조회가 가능하지만 팀은 회원을 참조하고 있지 않으니 불가능하고 이것을 단방향 연관관계라 한다.

* 테이블로 만들었을때 회원 테이블이 팀 테이블을 외래키로 가지고 있다고 가정한다.

* 외래키를 통해서 팀과 회원 조인, 회원과 팀 조인 둘다 가능하므로 이것은 양방향 관계이다.

* 참조를 사용하는 객체의 연관관계는 단방향이고, 외래키를 사용하는 테이블은 양방향이다.

* 하지만 팀이 회원을 참조하게 되면 객체도 양방향으로 만들 수 있다.

* 객체의 양방향 연관관계를 정확히 설명하면 서로 다른 단방향 관계 2개의 집합이다.


### 객체와 테이블의 모델링

* 객체를 테이블에 맞추어 모델링

    - 객체의 참조 대신 필드를 테이블의 외래 키의 형태로 사용

    - 객체는 참조를 사용해 연관 객체를 탐색할 수 있는데, 객체 구조를 테이블에 맞춰버리면 그렇게 할 수 없다

    ```java
    // Team의 pk를 가지고 있음
    @Entity
    public class Member {
    
        @Id @GeneratedValue
        private Long id;
    
        @Column(name = "USERNAME")
        private String name;

        // 이 필드가 테이블의 외래 키 역할을 한다
        // 실제로 테이블의 외래 키로 매핑되서 외래키 제약 조건을 생성한다는 뜻이 아니다
        // 객체를 테이블에 맞췄을 때 테이블의 외래 키가 객체의 필드 형태로 된다는 것
        @Column(name = "TEAM_ID")
        private Long teamId;
        …
    }
    
    @Entity
    public class Team {
    
        @Id @GeneratedValue
        private Long id;
    
        private String name;
        …
    }
    ```
    ```java
    // Team 을 조회하기 위해 여러 과정을 거쳐야 한다
    
    // Member 조회
    Member findMember = em.find(Member.class, member.getId());

    // 조회된 Member 로 teamId 조회
    Long findTeamId = findMember.getTeamId();

    // teamId 로 team 조회
    Team findTeam = em.find(Team.class, findTeamId);
    ```

* 객체 지향 모델링

    - 객체의 참조와 테이블의 외래 키를 매핑
 
    - 객체의 참조로 연관 객체를 찾을 수 있다

    - 객체와 테이블 단방향 연관관계 매핑
    
        <img src ="">

        - 테이블의 연관관계를 객체 지향 모델링에 맞춘 것이 위의 그림에서 객체 연관관계이다
     
        - JPA는 객체 연관관계가 작성된 엔티티 클래스의 정보를 사용해 관련된 테이블에 접근하기 때문에 둘을 매핑해줘야 한다
     
            - 매핑해주지 않으면 jpa는 엔티티의 Team team 이 테이블의 TEAM_ID(FK) 인줄 모르기 때문에 관련된 테이블을 찾을 수 없다고 오류가 발생한다
         
            - 즉 엔티티의 참조와(Team team) 테이블의 외래 키(TEAM_ID(FK)) 가 연관관계가 있다고 정의해 줘야 한다   

        ```java
        @Entity
        public class Member{
        
            ...
        
            // 단방향 연관관계 매핑
            // 다중성 정보를 적는다. 여기서는 다대일 관계라는 @ManyToOne을 사용했다
            // Member 입장에서 Member 가 다수고, Team이 하나라는 것
            @ManyToOne  
            @JoinColumn(name="Team_ID")  // join 시 사용할 컬럼이라는 뜻. name 속성에 매핑할 테이블의 외래키 이름을 지정한다. 이 어노테이션은 생략할 수 있고 기본값은 '필드명_참조하는 테이블의 기본키 컬럼명' 이다.
            private Team team;
        
        }
        
        @Entity
        public class Team{
        
            @Id
            @Column(name="Team_ID")
            private String id;
        }
        ```
        ```java
        // Member 에서 Team 을 바로 조회할 수 있다

        Team team = new Team();
        team.setname("TeamA");
        em.persist(team);

        Member member = new Member();
        member.setUsername("member1");

        // member에 team 그대로 저장
        member.setTeam(team);

        // DB에 insert 할 때 jpa 가 team의 pk값을 꺼내서 외래키에 저장 
        em.persist(member);

        // Member 조회
        Member findMember = em.find(Member.class, member.getId());
    
        // 조회된 Member 로 team 조회
        Team findTeam = findMember.getTeam();
        ```

    - 객체와 테이블의 양방향 연관관계 매핑
 
        <img src ="양방향 매핑">

        - Member, Team 테이블은 양쪽 모두에서 외래키로 조인해서 서로를 조회할 수 있다
     
        - 
    
    ```java
    @Entity
    public class Member{
    
        @Id
        @Column(name = "Member_Id)
        private String id;
    
        private String username;
    
        @ManyToOne // 팀 엔티티와 다대일 관계
        @JoinColumn(name="Team_ID") 
        private Team team;
    
    }
    
    @Entity
    public class Team{
    
        @Id
        @Column(name="Team_ID")
        private String id;
    
        priate String name;
    
        // 멤버 엔티티와 일대다 관계. 서로 매핑(참조)을 했으니 양방향이다.
        @OneToMany(mappedBy = "team") // mappedBy 속성이 붙은 쪽이 주인이 아니고 값으로 반대쪽 매핑의 필드 이름을 준다.
        private List<Member> members = new ArrayList<>(); // 일대다 관계, 즉 팀 엔티티는 멤버 엔티티를 여러개 가질 수 있으니 리스트로 만든다.
    }

    // team 에서 member 호출
    
    Team team = new Team();
    team.setname("TeamA");
    em.persist(team);

    Member member = new Member();
    member.setUsername("member1");
    member.setTeam(team);
    em.persist(member);

    // Member 조회
    Member findMember = em.find(Member.class, member.getId());

    List<Member> members = findMember.getTeam().getMembers();


    // 연관관계 설정 및 조회 과정
    1. member.setTeam(team)을 통해 member와 team의 관계가 설정됨
    
    2. em.persist(member)로 member가 데이터베이스에 저장되면서, team과의 외래 키 관계가 관리됨
    
    3. findMember.getTeam()을 호출하면, JPA는 team 엔티티를 프록시로 가져오고, 이후 findMember.getTeam().getMembers()를 호출할 때 실제 데이터베이스에서 team_id에 해당하는 members 리스트를 조회해 초기화함
    
    4. 따라서, findMember.getTeam().getMembers()를 호출하는 순간에 List<Member>가 조회되어 반환
    
    5. members 리스트는 실제로 데이터베이스에 저장된 값에 따라 동적으로 채워지게 된다
    ```
    
    - 테이블은 외래키 하나로 두 테이블의 연관관계를 관리하고 조인할 수 있으므로 항상 양방향 관계이다. 
    
    - 엔티티의 단방향 매핑은 하나의 참조만 사용하므로 이 참조로 외래키를 관리한다.
    
    - 그런데 엔티티의 양방향 매핑은 서로를 참조하므로 외래키로 지정할 수 있는 포인트가 2곳이 된다.
    
    - 테이블의 외래키는 1개인데 객체의 참조는 2개로 차이가 발생한다.
    
    - 이 때문에 양방향 매핑시 2개의 참조 중 하나를 정해서 테이블의 외래키와 매핑해주어야 하는데 이것을 연관관계의 주인 이라 한다.
    
    - 엔티티에 수정 사항이 반영되어 sql로 만들어질때 매핑 정보들을 분석해서 만들기 때문에 연관관계 매핑을 정확하게 해야한다.
    
    - 반대쪽에서 객체 그래프를 탐색하기 위해 양방향 매핑을 사용한다.


### 양방향 연관관계의 주인

* 두 연관관계 중 하나를 주인으로 정해야 한다.

* 주인쪽이 테이블의 외래키와 매핑되어 등록, 수정, 삭제를 할 수 있고 아닌 쪽은 읽기만 할 수 있다.

* 주인이 아닌 쪽에 mappedBy 속성을 사용하고 값으로 주인 엔티티 매핑의 필드 이름을 준다.

* 연관관계의 주인을 선택할 때는 테이블 구조를 보고 외래키를 가지고 있는 테이블로 정해야 한다.

```
// A, B 테이블이 있고 A가 B를 참조하고 있다면 A에 외래키가 있다는 뜻이다.

// A, B 엔티티가 있고 각각 A, B 테이블과 매핑하고 양방향 매핑 관계라서 서로를 참조하고 있는 상태다.

// 위의 객체와 테이블을 매핑할때 객체의 연관관계의 주인을 선택해야 하는데 A 엔티티로 줘야 한다. 

// B엔티티로 주게되면 매핑된 B테이블은 자신의 필드에 외래키가 없지만 외래키를 관리해야 하는 상황이 벌어진다.

// A엔티티로 주면 테이블 구조상 A테이블은 B테이블을 참조하고 있어서 외래키를 가지고 있었기 때문에 설계상 자연스러운 상태가 된다.  
```

### 여러 다중성에서의 외래키 

* 관계형 데이터베이스의 특성상 일대다, 다디앨의 관계는 항상 다쪽에 외래키가 있다.

* 그래서 엔티티의 다대일, 일대다 관계에서도 항상 다쪽이 외래키를 가지기 때문에 @ManyToOne 에는 mappedBy속성이 없다.

* 양방향 관계에서도 연관관계의 주인은 항상 다쪽이다.
