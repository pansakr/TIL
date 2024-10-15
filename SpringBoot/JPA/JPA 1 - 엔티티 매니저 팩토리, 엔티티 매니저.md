### 엔티티 매니저 팩토리

* jpa를 사용하기위한 기반 객체와 커넥션 풀, 엔티티 매니저를 생성해주는 클래스

* 서버 실행 시점에 하나만 생성하고 이후 애플리케이션 전체에서 공유된다

```
// 엔티티 매니저 팩토리 생성
// persistence.xml에서(xml 대신 yml 도 가능) createEntityManagerFactory() 의 인자로 오는 문자열을 가진 persistence-unit을 찾아 엔티티 매니저 팩토리를 생성한다. 
EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");

// persistence.xml 설정파일 예시
<persistence-unit name="jpabook"> // persistence-unit 의 name 값이(jpabook) createEntityManagerFactory() 의 인자로 와야 한다
        <properties>
            <!-- 필수 속성 --
            <property name="jakarta.persistence.jdbc.driver" value="org.h2.Driver"/>
            ...
            <!-- 옵션 -->
            <property name="hibernate.show_sql" value="true"/>
            ...
        </properties>
    </persistence-unit>
```

* 이때 jpa를 동작 시키기 위한 기반 객체와 구현체에 따라서 커넥션 풀도 생성하므로 엔티티 매니저의 생성 비용이 크기 때문에 애플리케이션 전체에 딱 한번만 생성 후 공유해서 사용한다.


### 엔티티 매니저

```
EntityManager em = emf.createEntityManager()
```

* 엔티티 매니저 팩토리에서 엔티티 매니저를 생성하며, 엔티티를 데이터베이스에 crud하는 기능을 제공한다

* 사용이 끝나면 엔티티 매니저와 엔티티 매니저 팩토리를 종료해야 한다

* 요청이 올때마다 새로 실행되고 버리고, 쓰레드간에 공유되지 않는다

```
em.close // 엔티티 매니저 종료

emf.close // 엔티티 매니저 팩토리 종료
```

### 엔티티 매니저를 사용해 DB와 통신

* 엔티티 매니저 팩토리 생성 -> 엔티티 매니저 팩토리로 엔티티 매니저 생성 -> 엔티티 매니저가 트랜잭션을 획득해 DB 통신

```java
// 엔티티 매니저 팩토리 생성
// 엔티티 매니저 팩토리는 하나만 생성해서(서버 실행 시점) 애플리케이션 전체에서 공유됨
EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");

// 엔티티 매니저 생성
// 엔티티 매니저는 요청이 올때마다 새로 실행되고 버린다
// 쓰레드간에 공유되지 않는다
EntityManager em = emf.createEntityManager(); 

// 트랜잭션 획득
// JPA의 모든 데이터 변경은 트랜잭션 안에서 실행된다
EntityTransaction tx = em.getTransaction() 

try {

            tx.begin(); //트랜잭션 시작
            logic(em);  //비즈니스 로직

            // 트랜잭션 커밋
            // 트랜잭션 커밋 시점에 바뀐 점이 있다면 감지해서 자동으로 DB에 업데이트 쿼리 실행
            tx.commit();

        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback(); // 예외 발생시 트랜잭션 롤백
        } finally {
            em.close(); // 엔티티 매니저 종료
        }

        emf.close(); // 엔티티 매니저 팩토리 종료
    
public static void logic(EntityManager em) {

        String id = "id1";
        Member member = new Member();
        member.setId(id);
        member.setUsername("지한");
        member.setAge(2);

        //등록
        em.persist(member);

        //수정
        member.setAge(20);

        //한 건 조회
        Member findMember = em.find(Member.class, id);
        System.out.println("findMember=" + findMember.getUsername() + ", age=" + findMember.getAge());

        //목록 조회
        List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();
        System.out.println("members.size=" + members.size());

        //삭제
        //em.remove(member);

    }
```

* jpa는 항상 트랜잭션 안에서 데이터를 변경한다

* 트랜잭션 없이 데이터를 변경하면 예외가 발생한다

* 엔티티 매니저의 persist()로 저장할 엔티티를 넘겨주면 jpa는 전달받은 엔티티의 매핑 정보를 분석해 sql을 만들어 db에 전달한다

* setAge()로 엔티티의 값을 변경하면 jpa는 엔티티의 변경사항을 추적해서 update sql을 만들어 db에 전달한다

* 엔티티 매니저의 remove()로 삭제할 엔티티를 넘겨주면 jpa는 delete sql을 생성해 db에 전달한다.

* find(엔티티, id)는 db의 기본키와 매핑한 엔티티 클래스의 @Id 값을 사용해 전달받은 엔티티를 조회한다.
