### JPA 의 데이터 타입

* 엔티티 타입

    - @Entity 로 정의하는 객체

    - 데이터가 변해도 식별자로 지속 추적 가능

        - 엔티티 내의 어떤 데이터가 변경되어도 해당 데이터의 pk로 조회할 수 있다

* 값 타입

    - int, integer, String 처럼 단순히 값으로 사용하는 자바 기본 타입이나 객체

    - 식별자가 없고 값만 있으므로 변경시 추적 불가능

        - 숫자 100 을 200 으로 변경 시 이전 값을 추적할 수 없다

    - 기본값 타입

        - ex) String name, int age

        - 생명주기를 엔티티에 의존한다(회원을 삭제하면 나이, 이름 필드도 함께 삭제)

        - 값 타입은 공유하면 안된다

        ```java
        // 자바의 기본값 타입은 항상 값을 복사하기 때문에 공유되지 않는다
        int a = 10;
        int b = a;
        a = 20;

        // a = 20, b = 10
        // b 에 a 를 넣고 a를 20으로 바꿔도 b 의 값은 유지됨
        ```
        ```java
        // Integer 같은 래퍼 클래스나 String 같은 특수한 클래스
        Integer a = new Integer(10);

        // a 가 참조하는 주소값을 b 에 담는다
        Integer b = a;

        a 의 값을 20으로 변경하는 코드

        // a = 20, b = 20
        // b 도 a와 같은 주소를 참조하기 때문에 a 가 참조하는 대상의 값이 바뀌면 b의 값도 바뀐다 
        ```

    - 자바의 객체 참조 방식

        ```java
        Address a = new Address("age1");
        Address b = a;
        b.setAge("age2");
        ```
        
        - Address a = new Address("age1");

            - new Address("age1")는 Address 클래스의 새로운 인스턴스를 힙 메모리에 생성

            - 이때 "age1"은 생성자의 인자로 전달되어 Address 객체의 age 필드가 "age1" 으로 초기화
            
            - 스택 메모리의 참조 변수 a는 이 힙 메모리에 생성된 Address 객체의 주소를 가리키게 됨
            
        - Address b = a;
            
            - 여기서 b는 스택에 생성된 또 다른 참조 변수로, a가 가지고 있는 주소값(힙에 있는 Address 객체의 주소)을 그대로 복사해서 할당받음

            - 결과적으로 a와 b는 동일한 Address 객체를 가리키고 있습니다. 즉, 같은 객체를 공유하고 있는 상태

        - b.setAge("age2");

            - 이제 b를 통해 setAge() 메소드를 호출하면 b가 참조하는 객체의 age 필드 값이 "age2"로 변경

            - 중요한 점은, b와 a가 동일한 객체를 참조하고 있기 때문에, 하나의 객체에 변화가 생기면 둘 다 그 변화를 볼 수 있다

            - 따라서 a.getAge()를 호출해도 "age2"를 반환

    - 임베디드 타입(복합 값 타입)

        - 새로운 값 타입을 직접 정의 가능

            - 여러 엔티티에서 재사용 가능한 필드들을 별도의 타입으로 분리할 수 있다

            - 분리한 타입을 여러 엔티티에서 필드로 가질 수 있다

                - startDate, endDate 의 날짜 필드를 여러 엔티티에서 사용한다고 가정

                - 이들을 Period 라는 별도의 타입으로 분리하고, 엔티티들에서 Period 타입을 가지도록 할 수 있다

                - 별도의 타입으로 분리한 곳에 @Embeddable 를 표시하고, 이를 사용하는 곳에 @Embedded 를 표시한다

                - 기본 생성자 필수

        - 주로 기본 값 타입을(String, int 등) 모아서 만들어서 복합 값 타입이라고도 한다

        ```java
        // 임베디드 타입 사용 전
        // 여러 엔티티에서 재사용 가능한 기간, 주소 필드들이 있다
        @Entity
        public class Member{

            ...

            // 기간
            private LocalDateTime startDate;
            private LocalDateTime endDate;

            // 주소
            private String city;
            private String street;
            private String zipcode;
        }
        ```
        ```java
        // 별도의 타입으로 분리
        @Embeddable
        public class Period{
            private LocalDateTime startDate;
            private LocalDateTime endDatel
        }

        @Embeddable
        public class Address{
            private String city;
            private String street;
            private String zipcode;
        }
        ```
        ```java
        // 임베디드 타입 사용
        @Entity
        public class Member{

            ...

            @Embedded
            private Period period;

            @Embedded
            private Address address;
        }
        ```

        - 임베디드 타입은 엔티티의 값일 뿐이며, 임베디드 타입을 사용하기 전화 후에 매핑하는 테이블은 같다

        - 임베디드 타입은 공유하면 안된다
        ```java
        // Address 는 임베디드 타입
        @Embeddable
        public class Address{
            private String city;
            private String street;
            private String zipcode;
        }
        ```    
        ```java
        Address address = new Address("city", "street", "10000");

        // Address 타입의 값을 member, member2 에서 공유하고 있다
        Member member = new Member();
        member.set..
        member.setAddress(address);
        em.persist(member);

        Member member2 = new Member();
        member2.set..
        member2.setAddress(address);
        em.persist(member2);

        // member 객체 하나의 address 값을 변경했는데 member2 의 address 도 변경된다
        // member, member2 가 동일한 객체를(address) 참조하고 있기 때문에 발생하는 문제
        // member, member2 에 대한 update sql 생성됨
        member.getAdress().setCity("newCity");

        tx.commit();
        ```

        - 값 타입 복사로 해결

        ```java
        // Address 의 인스턴스를 2개 생성
        Address address = new Address("city", "street", "10000");
        Address copyAddress = new Address("city", "street", "10000");

        // member, member2 가 다른 Address 인스턴스를 사용
        Member member = new Member();
        member.set..
        member.setAddress(address);
        em.persist(member);

        Member member2 = new Member();
        member2.set..
        member2.setAddress(copyAddress);
        em.persist(member2);

        // member 에 대해서만 update sql 이 실행됨
        member.getAdress().setCity("newCity");

        tx.commit();
        ```

        - 그러나 실수로 같은 객체를 사용할 수도 있다

        - 불변 객체를 사용하면 값 타입을 공유하는 것을 원천적으로 막을 수 있다

            *불변 객체 : final 키워드를 사용해 여러 필드들을 만들고, 만든 필드들의 속성이 변하지 않게 설계한 객체

            - 생성자로면 값을 설정하고 수정자(Setter)를 만들지 않으면 된다

    - 컬렉션 값 타입

        - 값 타입을 하나 이상 저장할 때 사용

        - 실제 업무에서는 잘 사용하지 않음
