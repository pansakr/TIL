### 연관관계

* 객체는 참조, 테이블은 외래키로 연관관계를 맺는다

* jpa를 잘 사용하기 위해선 객체의 참조와 테이블의 외래키를 잘 매핑해야 한다

* 매핑에 필요한 방향, 다중성, 연관관계의 주인 의 3가지 핵심 키워드가 있다


### 방향

* 단방향, 양방향 연관관계가 있다

* 테이블은 조인을 사용해 조회할 수 있으므로 항상 양방향이다

* 객체는 한쪽만 참조하면 단방향, 서로를 참조하면 양방향이다

```
// A, B 클래스가 있고 A가 B를 참조할때, 즉 A클래스의 필드로 B가 있을때 단방향 관계이다

// 만약 B클래스도 A클래스를 참조한다면 이때는 A, B가 서로를 참조하니 양방향 관계이다
```


### 다중성

* 객체들간의 관계 또는 테이블간의 관계가 일대일, 일대다, 다대다 임을 나타낸다

* 일대일은 서로가 하나씩 가질 수 있는 관계이고 @OneToOne 으로 표시한다

* 일대다, 다대일은 한쪽이 다른쪽을 여러개 가지고 반대는 하나만 가질 수 있는 관계이고 @OneToMany, @ManyToOne으로 표시한다

* 다대다는 양쪽이 서로를 여러개 가질 수 있는 관계이고 @ManyToMany 로 표시한다

* 일대다, 다대다 연관관계는 거의 사용하지 않는다


### 객체와 테이블의 연관관계

* 회원과 팀이 있고 회원은 하나의 팀에만 소속될 수 있을때 둘은 다대일 관계다

* 객체로 만들었을때 회원 클래스가 팀 클래스를 필드로 가지고 있다고 가정한다.(설계에 따라 반대가 될 수 있다)

* 회원은 팀을 참조하니 회원.팀()으로 팀 조회가 가능하지만 팀은 회원을 참조하고 있지 않으니 불가능하고 이것을 단방향 연관관계라 한다

* 테이블로 만들었을때 회원 테이블이 팀 테이블을 외래키로 가지고 있다고 가정한다

* 외래키를 통해서 팀과 회원 조인, 회원과 팀 조인 둘다 가능하므로 이것은 양방향 관계이다

* 참조를 사용하는 객체의 연관관계는 단방향이고, 외래키를 사용하는 테이블은 양방향이다

* 하지만 팀이 회원을 참조하게 되면 객체도 양방향으로 만들 수 있다

* 객체의 양방향 연관관계를 정확히 설명하면 서로 다른 단방향 관계 2개의 집합이다


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
    
        <img src ="https://github.com/user-attachments/assets/c682d038-9d8d-4826-b6af-a1f48b8669a3">

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
            @JoinColumn(name="Team_ID")  // join 시 사용할 컬럼이라는 뜻. name 속성에 매핑할 테이블의 외래키 이름을 지정한다. 이 어노테이션은 생략할 수 있고 기본값은 '필드명_참조하는 테이블의 기본키 컬럼명' 이다
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
 
        <img src ="https://github.com/user-attachments/assets/5154b861-3c22-4d38-87d0-84e57148166a">

        - Member, Team 테이블은 양쪽 모두에서 외래키로 조인해서 서로를 조회할 수 있다
    
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
    
        // 멤버 엔티티와 일대다 관계. 서로 매핑(참조)을 했으니 양방향이다
        @OneToMany(mappedBy = "team") // mappedBy 속성이 붙은 쪽이 주인이 아니고 값으로 반대쪽 매핑의 필드 이름을 준다
        private List<Member> members = new ArrayList<>(); // 일대다 관계, 즉 팀 엔티티는 멤버 엔티티를 여러개 가질 수 있으니 리스트로 만든다
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
    
    - 테이블은 외래키 하나로 두 테이블의 연관관계를 관리하고 조인할 수 있으므로 항상 양방향 관계이다
    
    - 엔티티의 단방향 매핑은 하나의 참조만 사용하므로 이 참조로 외래키를 관리한다
    
    - 그런데 엔티티의 양방향 매핑은 서로를 참조하므로 외래키로 지정할 수 있는 포인트가 2곳이 된다
    
    - 테이블의 외래키는 1개인데 객체의 참조는 2개로 차이가 발생한다
    
    - 이 때문에 양방향 매핑시 2개의 참조 중 하나를 정해서 테이블의 외래키와 매핑해주어야 하는데 이것을 연관관계의 주인 이라 한다
    
    - 엔티티에 수정 사항이 반영되어 sql로 만들어질때 매핑 정보들을 분석해서 만들기 때문에 연관관계 매핑을 정확하게 해야한다
    
    - 반대쪽에서 객체 그래프를 탐색하기 위해 양방향 매핑을 사용한다


### 양방향 연관관계의 주인

* 테이블은 외래 키 하나로 두 테이블이 연관관계를 맺는데, 객체 양방향 관계는 A->B, B->A 처럼 참조가 2군데이다

* 둘 중 테이블의 외래 키를 관리할 곳을 지정해야 한다

    - 연관관계의 주인: 외래 키를 관리하는 참조
      
    - 주인의 반대편: 외래 키에 영향을 주지 않음, 단순 조회만 가능

* 주인쪽이 테이블의 외래키와 매핑되어 등록, 수정, 삭제를 할 수 있고 아닌 쪽은 읽기만 할 수 있다

* 주인이 아닌 쪽에 mappedBy 속성을 사용하고 값으로 주인 엔티티 매핑의 필드 이름을 준다

* 연관관계의 주인을 선택할 때는 테이블 구조를 보고 외래키를 가지고 있는 테이블로 정해야 한다

```
// A, B 테이블이 있고 A가 B를 참조하고 있다면 A에 외래키가 있다는 뜻이다

// A, B 엔티티가 있고 각각 A, B 테이블과 매핑하고 양방향 매핑 관계라서 서로를 참조하고 있는 상태다

// 위의 객체와 테이블을 매핑할때 객체의 연관관계의 주인을 선택해야 하는데 A 엔티티로 줘야 한다

// B엔티티로 주게되면 매핑된 B테이블은 자신의 필드에 외래키가 없지만 외래키를 관리해야 하는 상황이 벌어진다

// A엔티티로 주면 테이블 구조상 A테이블은 B테이블을 참조하고 있어서 외래키를 가지고 있었기 때문에 설계상 자연스러운 상태가 된다
```

### 여러 다중성에서의 외래키 

* 관계형 데이터베이스의 특성상 일대다, 다디일의 관계는 항상 다쪽에 외래키가 있다

* 그래서 엔티티의 다대일, 일대다 관계에서도 항상 다쪽이 외래키를 가지기 때문에 @ManyToOne 에는 mappedBy속성이 없다

* 양방향 관계에서도 연관관계의 주인은 항상 다쪽이다

### 양방향 연관관계 주의점

* 연관관계의 주인과 주인이 아닌 쪽 모두에 값을 설정해야 한다

    - mappedBy 속성이 붙은 필드를 조회 시 DB에서 조회해서 가져오면 문제없다
 
    - 그런데 mappedBy 적용된 필드에 값 세팅을 안한 상태가 영속성 컨텍스트에 적용이 되었을때 mappedBy 적용된 필드를 조회하면 값이 없다
 
        - 영속성 컨텍스트에 저장된 엔티티가 없다면 DB를 조회해 값을 가져오는데, 영속성 컨텍스트에 엔티티가 있다면 해당 값을 가져오기 때문에 발생하는 문제
     
    - 연관관계 편의 메서드를 사용해 해결할 수 있다
 
        - 연관관계 편의 메서드는 주인, 주인이 아닌 객체 둘 중 하나에만 있어야 한다 
 
    ```java
    @Entity
    public class Member{
    
        ...
        
        @ManyToOne // 팀 엔티티와 다대일 관계
        @JoinColumn(name="Team_ID") 
        private Team team;

        // 연관관계의 주인 객체에 값을 설정할 때 주인이 아닌 객체에도 자동으로 값을 세팅하도록 설정
        // 일반적인 set 메서드와 다르므로 메서드 이름을 바꾸는 것도 좋다
        public void setTeam(Team team){
            this.team = team;
            team.getMembers().add(this);
        }
    }
    ```

* 양방향 매핑시 무한 루프

    - toString, lombok, json 생성 라이브러리 사용 시 무한 루프 발생
 
    ```java
    class Parent {
        private Long id;
        private List<Child> children;
    
    @Override
    public String toString() {
        return "Parent{" +
                "id=" + id +
                ", children=" + children +  // 이 부분에서 각 Child의 toString() 호출
                '}';                        .. children 은 children.toString() 이 생략되어 있는 것이다
        }
    }
    
    class Child {
        private Long id;
        private Parent parent;
        
        @Override
        public String toString() {
            return "Child{" +
                    "id=" + id +
                    ", parent=" + parent +  // 이 부분에서 Parent의 toString() 호출
                    '}';
        }
    }
    ```
    - Parent의 toString()이 호출되면, children 리스트의 각 Child에 대해 toString()을 호출
      
    - Child의 toString()이 호출되면 다시 parent의 toString()을 호출
      
    - 이렇게 서로 계속해서 toString()을 호출하며, 순환 참조가 일어나기 때문에 무한 루프가 발생

    - lombok 사용 시 toString 을 빼고 사용하면 된다

* 단방향 매핑으로 설계를 완료하는게 제일 좋고, 꼭 필요한 경우만 양방향 매핑을 해야 한다

    - 단방향 매핑만으로 이미 연관관계 매핑은 완료된 것이고, 양방퍙 매핑은 반대 방향에서 조회하는 기능이 추가된 것 뿐이다

    - 양방향 매핑 추가시 설계가 더 복잡해진다

### 여러 연관관계 매핑

* 일대일

    - 회원이 하나의 사물함을 가지고, 사물함도 하나의 회원만 가지는 관계
 
    - 서로 일대일 관계이다
 
    - 테이블 구조에서 회원이 외래키를 가지고 있을때, 이와 매핑된 객제 구조에서 회원 엔티티가 외래키를 가지는 것을 주 테이블에 외래 키, 사물함 엔티티가 외래키를 가지는 경우를 대상 테이블에 외래키를 가지는 구조 라고 한다
 
    - 주 테이블에 외래 키 단방향, 양방향
 
        - 다대일 연관관계의 매핑 방법과 비슷하다
     
        - 양방향 매핑 시 다대일 양방향 매핑 처럼 외래 키가 있는 곳이 연관관계의 주인
     
        - 주인이 아닌 쪽에 똑같이 mappedBy를 적용하고, 일대일 관계이므로 리스트 대신 객체 타입을 직접 정의해주면 된다
        ```java
        테이블 구조는 member 테이블이 locker 테이블의 pk를 외래키로 가지고 있다
        
        // member 엔티티 (연관관계 주인 엔티티)
        @OneToOne
        @JoinColumn(name = "LOCKER_ID")
        private Locker locker;

        // lock 엔티티
        // 다대일과 달리 하나를 가지고 있으므로 list<member>가 아니라 객체 타입을 바로 작성한다
        @OneToOne(mappedBy = "locker")
        private Member member
        ```
    - 대상 테이블에 외래 키 단방향
 
        - member 테이블이 locker 테이블의 pk를 외래키로 가지고 있는 구조를 객체로 옮겼을때, locker 엔티티가 외래키를 가지는 구조
     
        - jpa에서 지원하지 않는다
     
    - 대상 테이블에 외래 키 양방향
 
        - 주 테이블에 외래 키 양방향 매핑과 비슷하다
     
        - 단지 연관관계의 주인을 locker로 변경하고, member는 읽기 전용으로 만들면 된다
 
* 다대일

    - 회원은 하나의 팀을 가지고, 팀은 여러 회원을 가지는 관계

    - 다대일 단방향

        - 위 관계에서 회원 입장에서 팀을 바라본 관계. 다(회원) 대 일(팀) 이다
     
        - 위 관계를 객체로 만들었을때 회원 객체에서만 팀 객체를 참조할 수 있다면 다대일 단방향이다   

        - 가장 많이 사용하는 연관관계
     
    - 다대일 양방향
 
        - 위와 같은 다대일 관계에서 팀 객체에서도 회원 객체를 참조할 수 있게 만든 것
     
        - 회원 객체에서 팀 객체를, 팀 객체에서 회원 객체를 참조할 수 있다
     
        - 이때 연관관계의 주인은 DB에서 외래키가 있는 곳으로 하며, DB는 항상 다쪽이 외래키를 가지고 있기 대문에 다대일 양방향은 다 쪽이 외래키, 즉 연관관계의 주인이 된다
     
* 일대다

    - 일대다 단방향
 
        - 회원은 하나의 팀을 가지고, 팀은 여러 회원을 가질 때 팀 입장에서 회원을 바라본 관계. 일(팀) 대 다(회원) 이다
     
        - 테이블의 일대다 관계는 항상 다 쪽에 외래키가 있는데, jpa에서 일대다 설정 시 일 쪽이 외래키를 가진다
        
            - 위 관계를 객체로 표현하면, 일 쪽이 연관관계의 주인(외래키)을 가지게 된다. 따라서, 객체인 팀 쪽이 외래키를 가지게 되고, 테이블에서는 회원 쪽이 외래키를 가진 구조가 된다
     
            - 이대로 매핑할 시 회원 테이블의 외래키를 팀 객체에서 관리하게 되는 특이한 구조가 된다
         
                - 엔티티가 관리하는 외래 키가(팀 객체에서 회원 외래키 관리) 다른 테이블에(회원 테이블에서 팀 외래키 관리) 있게 되고, 연관관계 관리를 위해 추가로 UPDATE SQL 실행해야 한다
             
                <img src="https://github.com/user-attachments/assets/a43927d3-802c-4658-a52e-12a8873a6de7">
                
                ```java
                // 일(팀) 대 다(회원) 관계
                // 엔티티는 팀 객체가 회원 객체를 참조
                // 테이블은 회원 테이블이 팀 외래키를 소유
                
                // 회원 생성
                // DB의 member 테이블에 insert 하는 쿼리 생성
                Member member = new Member();
                member.serUsername("memberA");
                em.persist(member);

                // 팀 생성
                // DB의 team 테이블에 insert 하는 쿼리 생성
                Team team = new Team();
                team.setName("teamA");

                // 팀이 회원을 참조하고 있으므로 팀에 회원 추가
                // DB의 team 테이블에 member 가 없고, member 테이블이 team 외래키를 가지고 있다
                // member 테이블에 기존의 member를 업데이트하는 쿼리 생성
                team.getMembers().add(member);
         
                em.persist(team);
         
                // 객체와 테이블이 가지고 있는 외래키의 위치가 달라서 데이터 추가 시 update sql 이 추가로 생성되었다
              
                // 객체와 테이블이 같은 위치에 외래키를 가지고 있었다면 데이터 추가 시 update sql 은 생성되지 않는다
                ```
                - 사실 일대다 연관관계에서 다 쪽에 주인을 주면 해결이 되는 문제지만 그러면 다대일을 사용하는거나 마찬가지이므로 그냥 다대일을 사용하는 것이 좋다
                
                    - 일대다 관계에서 '다' 쪽에 연관관계의 주인을 두는 것은 다대일 관계와 동일하게 동작하므로, 굳이 일대다에서 '다' 쪽에 주인을 두어 복잡하게 만들기보다는, 직관적으로 다대일 관계로 통일하는 것이 더 좋다

                - 그리고 위 상황에서 member, team 엔티티 persist() 시 member, team 의 insert sql 에 더해 member 의 update sql 이 추가로 생성되었는데 이것 자체로 문제가 된다

                    - 예시는 간단해서 그렇지 여러 테이블이 복잡하게 얽혀 있는 실제 업무에서는 위와 같은 상황이 발생하면 건드리지 않은 member의 update sql이 왜 생성되었는지 파악하기 어렵다

                    - 이런 것 하나하나가 분석하기 어렵게 만들기 위와 같은 방법은 때문에 피해야 한다

    - 일대다 양방향
 
        - 이런 매핑은 공식적으로 존재하지 않는다
     
        - 다대일 양방향을 사용하는것이 좋다

* 다대다

    - 회원은 여러 상품을 가질 수 있고, 상품도 여러 회원을 가질 수 있는 관계

    - 관계형 데이터베이스는 정규화된 테이블 2개로 다대다 관계를 표현할 수 없다
   
        - 회원_상품 이라는 연결 테이블을 추가해, 회원이 회원_상품 테이블에 일대다, 회원_상품이 상품 테이블에 다대일 하는 구조로 풀어낸다
     
    - 위 관계를 객체로 옮겼을때 @ManyToMany 를 사용해 연결 테이블을 생략하고 회원과 상품 객체만으로 다대다 연관관계를 만들 수 있다
 
        - 하지만 그렇게 하면 회원 객체를 조회했을때 회원 객체가 상품 객체를 참조하기 때문에 원치 않는 상품 객체도 조회되게 된다
     
        - 그리고 실제 업무에서는 연결 테이블에 데이터가 있기 때문에 위 구조라면 중간 테이블 역할을 하는 객체가 없어서 조회하지 못하게 된다
     
    - 실제로 @ManyToMany 는 사용하지 않고, 연결 테이블을 활용해 다대다 관계를 풀어낸다
