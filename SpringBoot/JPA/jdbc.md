### jdbc

* 자바 애플리케이션의 데이터를 db에 저장 및 업데이트하거나 db에 저장된 데이터를 java에서 사용할 수 있게 하는 자바 api

* Connection, Statement, ResultSet의 3가지 인터페이스를 제공한다.

* jdbc를 사용하기 위해선 jdbc드라이버를 먼저 로딩한 후 db와 연결하게 된다.

*jdbc 드라이버 - 데이터베이스와 통신을 담당하는 인터페이스. dbms마다 다른 jdbc드라이버를 구현해 제공한다.


### jdbc 사용 순서

* 드라이버 로딩 -> Connection 객체 생성 -> Statement 객체 생성 -> Query실행 -> ResultSet으로 실행된 쿼리 결과 조회 -> Result, Statement, Connection 객체 Close

* DriverManager를 통해 사용하고자 하는 jdbc 드라이버를 로딩한다.

* 정상적으로 로딩되면 DriverManager로 db와 연결되는 세션인 Connetion 객체를 생성한다.

* Statement 객체에 실행할 SQL 쿼리문을 입력한다.

* 생성한 Statement를 이용해 SQL을 실행한다.

* ResultSet 객체로 실행된 SQL에 대한 결과를 받는다.

* Result, Statement, Connection 순서대로 Close한다.


### 커넥션 풀

* 커넥션 객체를 미리 생성해 보관하고 필요할때 꺼내서 쓸 수 있도록 관리해 주는것

* jdbc 과정 중 커넥션 객체를 생성하는 작업은 비용이 많이 들기 때문에 커넥션 객체를 어느정도 미리 생성 해둔다.

* 이후 연결객체가 필요할때 커넥션 풀에서 보관하고 있던 커넥션 객체를 받아서 연결하고 종료시 다시 반납한다.

* 커넥션 생성, 종료에 필요한 복잡한 과정을 모두 생략하고 필요할때 만들어진 연결 객체를 썼다가 반납만 하면 되기 때문에 서버의 부하가 많이 줄어든다.   
