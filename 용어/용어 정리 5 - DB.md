### ERD(Entity-Relationship Diagram) 

* 개체들 간의 관계를 나타낸 표

### 로우, 레코드, 튜플 

* 테이블의 한 행에 저장된 데이터

### 컬럼, 필드 

* 테이블의 하나의 열을 가리킨다.

### pk(Primary Key, 기본키) 

* 테이블에서 각 행을 식별하기 위해 만들어진 null값과 중복이 허용되지 않는 유일한 키

* 다중 pk = 한 테이블에서 여러개의 컬럼을 pk로 지정한 경우. 지정한 여러개의 컬럼들의 조합이 중복이 아닐 경우 insert가 허용된다.예를 들어 ('1','a'), ('1','b') 는 앞의 1은 같지만 뒤의 a와 b가 다르기 때문에 가능하다

### fk(Foreign Key, 외래키) = 

### 스키마

* 하나의 데이터베이스 안에서 테이블, 뷰 등을 구분짓는 공간 (네임스페이스)

  - 네임스페이스
  
    - 이름을 구분하는 논리적 공간
   
    - 폴더처럼 생각해도 되지만, 폴더와 달리 데이터를 담는 공간은 아니고, 이름 구분만 해줌
 
* 데이터베이스의 하위, 테이블의 상위 영역이다

  - PostgreSQL은 모든 새 데이터베이스에 기본적으로 public이라는 스키마를 만들어 둔다

  - 테이블 생성 시 public 스키마에 생성됨

  - create table xxx(..) 명령어로 테이블 생성시 내부적으로는 create table public.xxx(...) 로 처리됨

### 엔티티

* 데이터베이스에서 데이터화하려는 사물, 개념의 정보 단위. 관계형 데이터베이스의 테이블 개념과 대응된다

### 메타데이터
  
* 다른 데이터를 설명해 주는 데이터. 인스타그램의 태그도 메타데이터의 일종

* 사용자는 메타데이터로 자기가 원하는 특정 데이터를 쉽게 찾아낼 수 있다


### 데이터 무결성

* 데이터의 정확성, 일관성, 유효성이 유지되는 것

* 데이터 무결성 제약조건으로 데이터 무결성을 보장


### 데이터 무결성 제약조건

* 데이터 무결성을 보장하기 위해 데이터의 저장, 삭제, 수정 등을 제약하기 위한 조건

* 개체 무결성 - 모든 테이블은 기본키를 가져야 한다

* 참조 무결성 - 참조 관계에 있는 두 테이블의 데이터가 항상 일관된 값을 갖도록 유지되어야 한다

* null 무결성 - 테이블의 특정 속성 값을 null이 될 수 없도록 제한했다면 해당 속성에 null이 있으면 안된다

* 도메인 무결성, 고유 무결성 등이 있다


### ANSI SQL

* DBMS마다 다른 SQL을 표준화 시킨 표준 SQL 문법


### 와일드카드

* 어떤 값을 대체하는데 사용되는 문자로 그 값이 될 수 있는 모든 것
