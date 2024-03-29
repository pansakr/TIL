### ORM(Object Relational Mapping)

-   객체와 관계형 데이터베이스를 매핑해주는 기술.
-   자바는 객체지향 패러다임이고, 관계형 데이터베이스는 데이터를 정규화해서 보관하는 것을 목표로 한다.
-   ORM은 이러한 객체와 관계형 데이터베이스의 패러다임의 불일치를 개발자 대신 해결해준다.
-   DBMS벤더마다 다른 sql에 대한 종속성을 줄일수 있다.
-   상속받거나 다른 객체를 참조하고 있는 객체를 db에 저장할때 테이블에 맞게 sql을 작성해야 했던 것을 대신 해준다.
-   객체를 orm프레임워크에 저장하면 orm이 적절한 sql을 생성해 실행해준다.
-   즉 객체나 테이블은 서로를 신경쓸 필요 없이 각자의 목적에 맞게 모델링하면 orm이 자동으로 매핑해준다.

\*벤더(vendor) = 제품 판매인 또는 판매업체.

\*매핑(mapping) = 어떤 데이터와 다른 데이터를 짝짓거나 연결해 저장하는 과정

### 패러다임 불일치

* 객체와 관계형 데이터베이스는 목적이 다르므로 데이터를 저장하는 방법이 다르게 설계되어 있다.

* 객체 구조를 테이블 구조에 저장하려면 패러다임의 불일치를 해결해 줘야하는데 그것이 개발자의 몫이었다.

* 상속받은 객체를 테이블에 저장하려면 부모객체, 자식객체 따로 insert를 해줘야 한다.

* 또 객체는 다른 객체를 자신의 필드로 참조해서 연관 객체를 조회하는데 테이블은 외래키를 사용해서 연관 테이블을 조인해 조회한다.

* 객체는 참조 필드에 참조대상 객체정보가 들어있고, 테이블로 옮겨 저장하면 참조 필드가 외래키가 되는데 테이블의 외래 키는 객체와 달리 다른 테이블의 컬럼 하나만 참조할 수 있다.

* 반대로 객체를 테이블에 맞추면 참조를 통해 객체를 찾을 수 없게 되어서 객체지향의 특징을 잃게 된다.

* 객체지향의 특징을 가지면서 테이블에 저장하려면 참조를 외래키로 변환해야하고, 조회시 외래키를 참조로 변환해서 가져와야 한다.

* 이 과정이 패러다임 불일치를 해결하기 위해 소모하는 비용이다.

*패러다임 - 이론적인 틀이나 체계

### JPA(Java Persistence API)

-   자바에서 제공하는 ORM기술에 대한 API 표준 명세이다. 
-   즉 JPA는 ORM 기술의 인터페이스를 모아둔 것이고, JPA를 사용하려면 이를 구현한 ORM 표준 프레임워크를 선택해야한다.
-   대표적인 오픈소스 ORM 프레임워크가 Hibernate 이다.

### Hibernate

* JPA라는 인터페이스의 구현체이다.

### Spring Data JPA

* Spring에서 제공하는 라이브러리로 JPA를 쉽게 사용할 수 있게 도와준다.

* Spring Data JPA 라이브러리를 통해 Repository 인터페이스를 사용할 수 있고, Repository 인터페이스를 구현해서 JPA를 쉽게 사용할 수 있다. 

### JPA 사용이유

* dbms 벤더마다 다른 sql을 알아서 생성해주기에 벤더 독립성을 가진다.

* jpa가 sql을 자동 생성해주기때문에 생산성이 향상된다.

* 객체와 db의 패러다임 불일치를 개발자 대신 해결해준다.

* jpa 이전에는 데이터의 변경사항을 테이블 구조에 맞는 sql을 일일히 작성해서 반영해야 했는데 jpa 이후로는 데이터의 수정사항을 엔티티라는 객체에 반영해주면 jpa가 자동으로 객체 구조로 저장된 데이터를 테이블 구조에 맞는 sql로 바꿔서 db에 반영해주기 때문에 매우 편리하다.


### 컨텍스트(Context)

-   어떤 루틴이 실행될 때 변수값들과 이전에 실행된 함수들의 내부상태.
-   동일한 루틴이 여러번 실행되더라도 컨텍스트에 따라 다른 결과가 나올 수 있는 것이다.

### 빌드(build)

* 빌드 = xxx.java -> xxx.class 로 바꾸는 과정

### 인터페이스(InterFace)

* 휴대폰, 컴퓨터 등의 2개 이상의 장치나 소프트웨어 사이에서 정보나 신호를 주고받을 때 그 사이를 연결하는 매개체
