### jpa에서의 프록시

* 엔티티를 상속받아 만들어진 해당 엔티티의 참조(target 변수)를 가지고 있는 객체

* getReference() 로 특정 엔티티를 상속받은 프록시 객체를 생성할수 있고, 이 시점엔 겉모습만 같고 실제 데이터는 없다

* 만들어진 프록시 객체는 부모 엔티티와 같은 구조를 가지고 있으므로 구분하지 않고 사용하면 된다

```
// getReference()메서드 사용시 프록시 객체가 만들어진다
엔티티클래스 참조변수 = em.gerReference(엔티티클래스, 식별자)
```

* 프록시 객체의 get() 메서드로 데이터 조회시 jpa는 영속성 컨텍스트를 확인 후 프록시 객체에 해당하는 실제 엔티티가 없으면 db검색 후 엔티티를 생성 후 프록시 객체와 연결한다(초기화)

* 프록시 객체는 생성된 실제 엔티티 객체의 참조를 target 변수에 보관한다

* target으로 실제 엔티티에 접근해 조회 후 반환한다

* 프록시 객체의 초기화는 위 설명처럼 영속성 컨텍스트의 도움을 받기 때문에 준영속 상태라면 예외가 발생한다

* 프록시 객체는 식별자 값을 보관하므로 연관관계를 설정할 때 sql실행 횟수를 줄일 수 있다

* 프록시 객체는 주로 연관 엔티티의 지연 로딩에 사용한다


### 즉시 로딩과 지연 로딩

```
// A, B 클래스가 있고 A클래스에 B클래스를 참조하는 참조 변수 b 가 있다고 가정한다

A a = em.find(A.class, "a클래스의 @Id")
B b = a.getB();  // A클래스의 참조변수 b로 B클래스에 접근해 데이터를 읽어 반환

// find로 조회할때 A클래스와 A가 참조하는 B클래스도 모두 읽어오는 것이 즉시로딩이다
// find로 조회할때 A클래스만 읽어오고 B클래스를 실제 사용할 때 다시 DB를 조회해 읽어오는것이 지연로딩이다
```

* 즉시 로딩은 한번 조회할때 참조하는 테이블까지 조인해서 한꺼번에 가져온다

* 지연 로딩은 첫 조회시 참조 엔티티를 제외해 조회하고, 엔티티.get엔티티참조변수().get() 처럼 엔티티의 값을 실제 사용하는 시점에 데이터베이스에서 추가로 조회한다


### 즉시 로딩

* 연관된 엔티티를 즉시 조회하는 방법

  - 연관 엔티티에 해당하는 테이블을 조인해 한꺼번에 가져오기 때문에 프록시 객체가 사용되지 않는다

* @ManyToOne의 fetch 속성을 FetchType.EAGER로 지정한다

* jpa는 선택적 비식별 관계일땐 외부 조인, 필수 관계면 내부 조인을 사용해 조회한다

```
@JoinColumn(nullable = true) null 허용(기본값), 외부 조인 사용
@JoinColumn(nullable = false) null 허용하지 않음, 내부 조인 사용
```

* 내부 조인이 성능과 최적화에 유리하다


### 지연 로딩

* 엔티티가 참조하고 있는 객체를 실제로 조회할 때 DB를 조회해서 값을 가져오는 방법

  - em.find(Member.class, memberId)를 호출할 때, Member가 Team을 참조하고 있다면, JPA는 먼저 Member에 대한 SELECT 쿼리만 실행하고, Team 객체는 프록시로 가져온다
  
  - 즉, 실제로 Team 객체가 필요할 때까지 데이터베이스에서 조회되지 않으며, 이후에 member.getTeam() 호출로 Team에 접근하면 그때서야 Team 에 대한 SELECT 쿼리가 실행된다

* 실제 사용될 때까지 데이터 로딩을 미뤄서 지연 로딩이라 한다

* @ManyToOne의 fetch 속성을 FetchType.LAZE로 지정한다

```java
// Member 엔티티가 Team 엔티티를 참조하고 있다
@Entity
public class Member{

    ...
    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 설정
    @JoinColumn(name = "TEAM_ID)
    private Team team;
}

Team team = new Team();
team.setName("teamA");
em.persist(team);

Member member1 = new member();
member1.setUseranme("member1");
member1.setTeam(team);
em.persist(team);

// db에 sql 반영
em.flush();
// 영속성 컨텍스트 비움
em.clear();

// member 조회
// member 에 대한 sql만 생성됨
Member m = em.find(Member.class, member1.getId());

// member 가 참조하는 team 객체는 Team 프록시 객체로 조회됨
System.out.println("m = " + m.getTeam().getClass());

// 현재 Team 프록시 객체는 비어있는 상태로, Team 객체의 값을 가지고 오기 위해 DB를 조회해 가져온다
// 영속성 컨텍스트 확인 -> 있으면 값을 가져오는데 없으니 DB조회 후 영속성 컨텍스트에 조회한 값을 저장 -> 저장한 값을 가져옴 
m.getTeam().getName();

// 영속성 컨텍스트가 비워지거나 종료되기 전에 다시 조회하면 Team 프록시 객체의 값이 있으니 DB를 조회하지 않고 가져온다 
```

### 즉시 로딩 문제점

* 예상하지 못한 SQL이 발생한다

  - JPQL 에서 N + 1 문제가 발생함
 
  *N + 1 : 처음 1개의 쿼리를 호출했는데 그것 때문에 N 개의 추가 쿼리가 실행됨

  ```java
  // 즉시 로딩으로 설정된 경우
  // member 엔티티는 team 엔티티를 참조하고 있음

  Team team1 = new Team();
  Team team2 = new Team();
  team1.set...
  team2.set...
  
  Member member1 = new Member();
  Member member2 = new Member();

  member1.set...
  member1.setTeam(team1);
  member2.set...
  member2.setTeam(team2);

  em.flush();
  em.clear();

  // member 에 대한 select sql 이 1개 생성됨
  // member1, member2 이 조회되고 이들은 각각 team1, team2 객체를 가지고 있다
  // 즉시 로딩은 연관 객체가지 한꺼번에 가져오기 때문에 team1, team2 에 대한 select sql 2번이 추가로 실행된다
  // member 하나만 조회했는데, 총 3번의 sql 이 실행되었다  
  List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList(); // 조건 없이 모든 Member 엔티티를 조회하는 쿼리

  tx.commit();
  ```

### 즉시로딩, 지연로딩 중 어떤것을 사용해야할까?

* jpa의 기본 fetch 전략은 연관 엔티티가 하나면 즉시로딩, 컬렉션이면 지연 로딩을 사용한다

```
@ManyToOne, @OneToOne - 즉시 로딩

@OneTomany, @ManyToMany - 지연 로딩
```

* 연관 컬렉션에 데이터가 수만개일때 즉시 로딩이면 수만개의 데이터가 함께 로딩되기 때문에 컬렉션은 지연 로딩으로 설정하는것이 좋다

* 추천 방법은 일단 모든 연관관계에 지연 로딩을 사용해 완성하고 실제 사용하는 상황을 보고 필요한 곳만 즉시 로딩으로 바꿔준다

### 영속성 전이

* 특정 엔티티를 저장/삭제 할때 연관된 엔티티도 저장/삭제 하는 기능

  - 관련된 엔티티를 함께 저장/삭제 하는 기능일 뿐, 연관관계를 매핑하는 것과 아무 상관 없다

* CASCADE 옵션으로 JPA의 영속성 전이 옵션을 사용할 수 있다

* CASCADE 미사용

```java
// 테이블 구조
Parent - Child는 일대다, 다대일 양방향 관계

@Entity
public class parent{

    @Id, @GeneretedValue
    private Long id;

    @OneToMany(mappedBy = "parent")
    private List<Child> children = new ArrayList<Child>();
    ...
}

@Entity
public class Child{

    @Id @GenereatedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;
    ...
}
```
```java
Child child1 = new Child();
Child child2 = new Child();

Parent parent = new Parent();
parent.addChild(child1);
parent.addChild(child2);

em.persist(parent);
em.persist(child1);
em.persist(child2);

// parent, child1, child2 와 관련된 총 3번의 sql이 실행된다
tx.commit();
```

* CASCADE 사용

```java
@Entity
public class parent{

    ...

    @OneToMany(mappedBy = "parent", cascade = Cascade.Type.PERSIST) // CASCADE 저장 옵션 사용
    private List<Child> children = new ArrayList<Child>();

    ...
}
```
```java

Child child1 = new Child();
Child child2 = new Child();

Parent parent = new Parent();
parent.addChild(child1);
parent.addChild(child2);

em.persist(parent);

// 한번의 persist() 실행으로 관련 엔티티들이 insert 된다
// sql이 3번 실행되는건 같다
tx.commit()l
}
```

* 엔티티 삭제 시에도 영속성 전이를 사용할 수 있다

```
@Entity
public class parent{

    ...

    @OneToMany(mappedBy = "parent", cascade = Cascade.Type.REMOVE) // CASCADE 삭제 옵션 사용
    private List<Child> children = new ArrayList<Child>();

    ...
}


// CASCADE 미사용시
Parent panret = em.find(...) 
Child child1 = em.find(...) 
Child child2 = em.find(...) 

em.remove(panret)  // 조회 후 삭제를 3번 해야 한다
em.remove(child1)
em.remove(child2)


// CASCADE 사용시
Parent panret = em.find(...)  // 부모 타입만 조회 후 삭제하면 연관 엔티티도 삭제된다
em.remove(parent)
```

* CASCADE의 종류

```
ALL       // 모두 적용
PERSIST   // 영속
MERGE     // 병합
REMOVE    // 삭제
REFRESH   // REFRESH
DETACH    // 준영속화
```

* 특정 엔티티를 한 곳에서만 사용할때(단일 소유자) 사용한다

  - child 엔티티를 parent 엔티티에서만 사용한다면 사용해도 된다
 
  - 다른 엔티티들에서 child 엔티티를 사용한다면 데이터 정합성에 문제가 생겨 사용하면 안된다 

### 고아 객체

* 부모 엔티티와 연관관계가 끊어진 엔티티를 자동으로 삭제하는 기능

* JPA의 고아 객체 제거 기능은 부모 엔티티의 컬렉션에서 자식 엔티티의 참조를 제거하면 자식 엔티티가 자동으로 삭제되도록 한다

```java
@Entity
public class parent{

    ...

    @OneToMany(mappedBy = "parent", orphanRemoval = true) // 고아객체 자동 삭제 옵션 사용
    private List<Child> children = new ArrayList<Child>();
}
```
```java
Child child1 = new Child();
Child child2 = new Child();

Parent parent = new Parent();
parent.addChild(child1);
parent.addChild(child2);

em.persist(parent);

em.plush();
em.clear();

Parent = findParent = em.find(Parent.class, parent.getId());

// 자식 엔티티를 컬렉션에서 제거
// 컬렉션에서 제거된 자식 엔티티에 대한 delte sql 생성됨
findParent.getChildren().remove(0);  

// DELETE FROM CHILD FROM WHERE ID = xxx 쿼리 실행됨
tx.commit();
```
* 영속성 전이와 마찬가지로 참조하는 곳이 하나일 때만 사용해야 한다

  - 특정 엔티티가 개인 소유일 때 사용

* @OneToOne, @OneToMany 만 가능

###  Cascade.Type.REMOVE 와 orphanRemoval = true 의 차이점

* A, B 엔티티는 일대다, 다대일 양방향 매핑이고, 연관관계 주인은 B 이고, A는 B를 List 로 참조하고 있다고 가정 

  - Cascade.Type.REMOVE : B 엔티티가 삭제될 때 A 엔티티도 삭제된다 (DB 에서 delete query 실행)

  - orphanRemoval = true : B 엔티티의 컬렉션에서 A 엔티티가 제거될 때 A 엔티티도 삭제된다 (DB 에서 delete query 실행)

```
@Entity
public class parent{

    @OneToMany(mappedBy = "parent", orphanRemoval = true) // 컬렉션에서 제거 시 DB 에 delte sql 실행
    private List<Child> children = new ArrayList<Child>();

    ...
}

Parent = findParent = em.find(Parent.class, parent.getId());

// 컬렉션에서 연관관계 엔티티 삭제 시 연관관계 엔티티와 매핑된 테이블의 실제 데이터 삭제 
// Cascade.Type.REMOVE 는 엔티티를 삭제 시 실제 데이터를 삭제하고, 컬렉션에서 삭제해도 실제 데이터를 삭제하지 않는다
findParent.getChildren().remove(0);

// Cascade.Type.REMOVE 는 엔티티 삭제 시 해당 엔티티와 연관관계의 엔티티와 매핑된 테이블의 실제 데이터 삭제
em.remove(findParent())
```

* 두 옵션은 참조하고 있는 엔티티가 한곳일 때만 사용해야 한다

* 두 옵션으로 삭제된 자식 엔티티를 다른 곳에서 참조하고 있었다면 문제가 발생한다
