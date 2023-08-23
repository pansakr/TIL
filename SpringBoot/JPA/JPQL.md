### JPQL

* 엔티티 객체를 조회하는 객체지향 쿼리

* sql을 추상화해서 특정 데이터베이스에 의존하지 않는다.


#### select

```
select m from Member as m where m.username = 'Hello'

// select, from, as 와 같은 jpql 키워드는 대소문자를 구분하지 않는다.
// 엔티티와 엔티티의 속성인 Member, username은 대소문자를 구분한다.
// Member as m 처럼 별칭을 필수로 사용해야 한다. as는 생략 가능하다.
```

#### TypeQuery, Query

* 반환 타입을 지정할시 TypeQuery, 지정할 수 없으면 Query를 사용한다.

```
TypedQuery<Member> query =   
   em.createQuery("select m from Member m", Member.class); // Member.class로 타입을 지정했다.

// em.createQuery()의 두번째 파라미터에 반환 타입을 지정하면 TypedQuery를 반환한다.

Query query = 
   em.createQuery("select m.username, m.age from Member m");

// select절에서 여러 엔티티나 컬럼을 선택할 때는 Query객체를 사용한다.
```

#### new 객체 변환

* 조회 결과 entity를 dto로 변환하는 작업을 jpql에서 대신할 수 있다.

```
TypeQuery<UserDto> query =
    em.createQuery("select new jpabook.jpql.UserDto(m.username, m.age) // 생성자와 함께 명시했다.
    from Member m", UserDto.class);

List<UserDto> resultList = query.getResultLilst();

// select 절에 new키워드를 사용하면 반환받을 클래스를 지정할 수 있고 이 클래스의 생성자에 jpql 조회 결과를 넘겨줄 수 있다.
// 조회 결과를 UserDto에게 넘겨주고 UserDto가 반환되었다.
```

#### 페이징 api

* setFirstResult(int 시작위치), setMaxREsult(int 조회할 수량) 으로 간편하게 사용할 수 있다.

```
TypeQuery<Member> query =
    em.createQuery("select m from Member m order by m.username desc", Member.class);

query.setFistResult(10); // 인덱스는 0번부터 시작. 10번 인덱스부터 조회(데이터베이스의 11번째 행)
query.setMaxResult(20); // 조회할 데이터 수 
query.getResultList(); // 11 ~ 30번 까지 조회한다.
```

#### 조인

```
// 내부 조인
String teamName = "팀A"
String query = "select m from Member m inner join m.team t" + "where t.name = :teamName";

// 내부 조인시 inner는 생략할 수 있다.
// member엔티티의 외래키 컬럼과 member엔티티가 참조하는 team 엔티티의 기본키 컬럼을 기준으로 조인한다.
// 실제로는 쿼리를 변수에 담아 사용하진 않는다.

// 외부 조인
selesct m from Member m left join m.team t
```

#### 페치 조인

* sql 조인의 종류는 아니고 jpql에서 성능 최적화를 위해 제공하는 기능이다.

* 연관된 엔티티나 컬렉션을 같이 조회한다.

```
// jpql
select m from Member m join fetch m.team  // 페치 조인은 m.team 이후 별칭을 사용하지 않는다.

// 생성된 sql
select m.*, t.*
from Member m
join team t on m.team_id = t.id

// jpql에서 select m으로 회원 엔티티만 조회했는데 실행된 sql에는 연관된 팀 엔티티도 함께 조회되었다.
// 페치 조인 시 연관 엔티티를 함께 조회해서 가져오기 때문에 연관 엔티티를 실제 사용할 때 지연 로딩이 발생하지 않는다. 
```

### 경로 표현식

* 상태 필드 - 단순히 값을 저장하기 위한 필드

* 연관 필드 - 연관관계를 위한 필드

* 단일 값 연관 필드 - 대상이 엔티티

* 컬렉션 값 연관 필드 - 대상이 컬렉션

```
@Entity
public class Member{

    @Id
    private Long id;

    @Column
    private String username; // 상태 필드

    private Integer age; // 상태 필드

    @ManyToOne(...)
    private Team team; // 연관 필드(단일 값 연관 필드)

    @OneToMany(...)
    private List<Order> orders // 연관 필드(컬렉션 값 연관 필드)
}
```

```
// jpql에 상태 필드 사용
select m.username, m.age from Member m

// 실행되는 sql
select m.name, m.age from Member m

// jpql에 단일 값 연관 필드 사용
select o.member from Order o

// 실행되는 sql
select m.* from Orders o
join Member m on o.member_id = m.id
```

* jpql에 단일값 연관 필드 사용시 sql에서 내부 조인이 일어나는데 이것을 묵시적 내부 조인이라 한다.

*명시적 조인 - join을 직접 적어주는 것

*묵시적 조인 - 경로 표현식에 의해 조인이 일어나는것. 내부 조인만 할 수있다.
