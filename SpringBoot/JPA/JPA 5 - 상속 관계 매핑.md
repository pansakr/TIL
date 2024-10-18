### 상속 관계 매핑

* DB는 슈퍼타입 서브타입 논리 모델을 3가지 물리 모델로 구현하는데, 이를 객체 구조로 구현하는 것

    - DB는 슈퍼타입 서브타입 논리 모델을 각각 테이블로 변환, 통합 테이블로 변환, 서브타입 테이블로 변환 의 3가지 물리 모델로 구현한다
 
    - DB의 3가지 물리 모델과 대응되는 상속 관계 매핑의 3가지 방법을 사용해 엔티티와 매핑할 수 있다 

* 상속 관계 매핑 방법에는 조인 전략, 단일 테이블 전략, 구현 클래스마다 테이블 전략이 있다

    - DB의 물리 모델과 대응하는 JPA의 상속 관계 매핑 전략
 
        - 각각 테이블로 변환 - 조인 전략
     
        - 통합 테이블로 변환 - 단일 테이블 전략
     
        - 서브타입 테이블로 변환 - 구현 클래스마다 테이블 전략 


#### 조인 전략

* 자식 테이블이 부모 테이블의 기본 키를 받아서 기본키 + 외래 키로 사용하는 전략

* 객체는 타입으로 구분할 수 있지만 테이블은 타입의 개념이 없기 때문에 타입을 구분할 컬럼을 추가해준다

<img src="조인 전략">

* Item 테이블에 pk, name, price, dtype 컬럼이 있고, Movie 자식 테이블 pk, director, actor 가 있다

    - 객체 구조로 바꾸면 item 객체에 id, price 필드가 있고, movie 객체에 director, actor 필드와 item 으로부터 상속받은 id, price 필드가 있다
 
    - movie를 예로 들면 테이블 구조에서는 pk, director, actor 3가지, 객체 구조에서는 pk, director, actor, price, id 5가지가 있다

```java
@Entity
@Ingeritance(strategy = IngeritanceType.JOINED) // 상속 매핑은 부모 클래스에 @Ingeritance를 사용해야 한다. 그리고 매핑 전략을 지정해야 하는데 여기서는 조인 전략을 사용하므로 .JOINED을 사용했다
@DiscriminatorColumn(name = "DTYPE") // 부모 클래스에 구분 컬럼을 생성. 기본값이 DTYPE이다
public abstract class Item{

    @Id @GeneratedValue
    @Column(name = "ITEM_ID")
    private Long id;

    private String name;
    private int price;

    ...
}

// 자식 엔티티는 부모 엔티티 Item 을 상속했으니 id, price 필드를 가지고 있다
@Entity
@DiscriminatorValue("A") // 부모 테이블의 구분 컬럼에 저장될 이름 지정. A로 저장됨. 기본값은 엔티티 이름(Album)
@PrimaryKeyJoinColumn(name = "BOOK_ID") // 자식 테이블의 기본 키 컬럼명을 변경하고 싶을때 사용한다. 기본값은 부모 테이블의 기본키 컬럼명이다
public class Album extends Item{

    private String artist;
}

@Entity
@DiscriminatorValue("M")
public class Movie extends Item{

    private String director;
    priate String actor;

}
```
```java
Movie movie = new Movie();
movie.setDirector("aaaa");
movie.setActor("bbbb");
movie.setName("ssss");
movie.setPrice(10000);

// 객체 구조의 movie는 위 4가지 필드를 모두 가지고 있지만, 테이블 구조의 movie 는 name, price 가 item 테이블에 있다
// 그래서 insert into item(name, price, id) 와 insert into Movice(actor, director, id) 의 2개의 insert sql이 실행된다
// movie 를 insert 했으므로 item 테이블의 dtype 은 m 이 된다 
em.persist(movie);

// DB 에 sql 반영 
em.flush();
// 영속성 컨텍스트 비우기
em.clear();

Movie findmovie = em.find(Movie.class, movie.getId())

// movie 객체는 4가지 필드를 모두 가지고 있지만, movie 테이블에 director, actor 2가지고 있고, 나머지 2개는 item 테이블에 있다
// 그래서 movie 테이블과 item 테이블을 내부 조인해서 가져오는 sql이 실행된다
// select movie.id .. from movie m join item i on m.id = i.id where movie.id = ?
tx.commit();
```

* 장점

    - 테이블 정규화

    -  외래 키 참조 무결성 제약조건 활용가능

    - 저장공간 효율화

* 단점

    - 조회시 조인을 많이 사용, 성능 저하

    - 조회 쿼리가 복잡함

    - 데이터 저장시 INSERT SQL 2번 호출

#### 단일 테이블 전략

* 자식 테이블에 사용된 컬럼들을 부모 테이블과 합쳐 하나의 테이블로 만들고 구분 컬럼으로 어떤 자식 데이터가 저장되었는지 구분하는 전략

* 객체 구조는 하나의 부모 클래스를 여러 자식 클래스가 상속받는 구조로 조인 전략과 같고 Ingeritance 옵션만 변경해주면 된다

    - 자식 엔티티 하나를 저장할때 다른 자식 엔티티의 필드는 사용되지 않아 null이 입력되므로 자식 엔티티와 매핑한 컬럼은 모두 null을 허용해야 한다
 
    - 하나의 테이블에서 자식 테이블의 컬럼을 구분해야 해서 구분 컬럼이 필수다

<img src="단일 테이블 전략">

```java
@Entity
@Ingeritance(strategy = IngeritanceType.SINGEL_TABLE) // 단일 테이블 전략 SINGEL_TABLE 사용
@DiscriminatorColumn(name = "DTYPE") // 단일 테이블을 사용하므로 구분컬럼 필수
public abstract class Item{

    @Id @GeneratedValue
    @Column(name = "ITEM_ID")
    private Long id;

    private String name;
    private int price;

    ...
}

@Entity
@DiscriminatorValue("A") // 단일 테이블을 사용하므로 구분컬럼이 필수이다.
public class Album extends Item{...}

@Entity
@DiscriminatorValue("M")
public class Movie extends Item{...}
```
```java
// movie 객체 생성 후 값 추가

// 객체 구조에선 movie 가 있지만, 테이블 구조에선 item 테이블이 movie 객체의 필드를 컬럼으로 모두 가지고 있는 형태이다
// 그래서 item 테이블로 insert sql 이 생성된다 insert into item(...)
// item 테이블에는 movie 관련 컬럼 이외에도 album, book 관련 컬럼이 있는데 이들은 null 값으로 insert 된다  
em.persist(movie);

// DB 에 sql 반영 후 영속성 컨텍스트 비우기

// movie 객체를 지정했지만, 테이블 구조에서는 item 테이블에 movie 가 포함되어 있는 형태라서 item 테이블로 select sql이 생성된다
// select movie.id, movie.name .. from item where movi.id = ? and movie.dtype = 'm'
Movie findmovie = em.find(Movie.class, movie.getId())
```

* 장점

    - 조인이 필요 없어서 조회 성능이 빠르고 조회 쿼리가 단순하다
 
- 단점

    - 자식 엔티티가 매핑한 컬럼은 null을 허용해야 한다
 
    - 단일 테이블에 모두 저장하므로 테이블이 커질 수 있고, 그 정도가 커지면 성능이 오히려 느려질 수 있다


#### 구현 클래스마다 테이블 전략

* 각각의 자식 테이블이 부모 컬럼을 모두 포함하는 전략 

    - 자식 테이블이 부모 컬럼을 포함하기 때문에 부모 테이블은 만들지 않는다

    - 테이블 자체가 다르기 때문에 구분 컬럼을 사용하지 않는다

* 객체 구조는 조인 전략과 같고, Ingeritance 옵션만 변경해주면 된다

* 이 전략은 잘 사용하지 않는다

<img src = "구현 테이블마다 테이블 전략">

```
@Entity
@Ingeritance(strategy = IngeritanceType.TABLE_PER_CLASS) // 구현 클래스마다 테이블 전략 TABLE_PER_CLASS 사용
public abstract class Item{

    @Id @GeneratedValue
    @Column(name = "ITEM_ID")
    private Long id;

    private String name;
    private int price;

    ...
}

@Entity
public class Album extends Item{...}

@Entity
public class Movie extends Item{...}
```

* 장점

    - 서브 타입을 명확하게 구분해서 처리할 때 효과적

    - not null 제약조건 사용 가능

* 단점

    - 여러 자식 테이블을 함께 조회할 때 성능이 느림(UNION SQL 필요)
 
        - 세 테이블이 같은 id를 사용하기 때문에 세 테이블을 합쳐서 조회해야 함

    - 자식 테이블을 통합해서 쿼리하기 어려움

### @MappedSuperclass

* 부모 클래스를 테이블과 매핑하지 않고 자식 클래스에게 정보만 제공할때 사용한다

* 부모 클래스에 객체들이 주로 사용하는 공통 매핑 정보를 정의하고, 자식 엔티티는 상속을 통해 매핑 정보를 물려받는다

* 부모 클래스는 공통 매핑 정보만 제공하는 용도로 테이블과 매핑하지 않는다. @MappedSuperclass로 표시한다

* 자식 엔티티들은 하나의 부모 클래스에게 상속받았지만 공통 필드 때문에 상속받은것일뿐, 연관관계는 없다. 따라서 db에서 외래키로 참조하는 관계가 아니다

```
@Entity
@AttributeOverride(name = "id", column = @Column(name = "MEMBER_ID")) // 상속받은 매핑 정보를 재정의한다
public class Member extends BaseEntity{...}

// 부모에게 상속받은 id속성의 컬럼명을 MEMBER_ID로 재정의했다.

@Entity
@AttributeOverrides({  // 여러개를 재정의할 때 사용
    @AttributeOverride(name = "id", column = @Column(name = "MEMBER_ID"))
    @AttributeOverride(name = "name", column = @Column(name = "MEMBER_NAME"))
    }) 
public class Member extends BaseEntity{...}
```
