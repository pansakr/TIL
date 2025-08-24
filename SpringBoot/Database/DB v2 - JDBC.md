### jdbc 로 데이터베이스 조회


#### 연결

```java
// db 접속에 필요한 기본 정보 설정
public abstract class ConnectionConst {

    public static final String URL = "jdbc:h2:tcp://localhost/~/test";
    public static final String USERNAME = "sa";
    public static final String PASSWORD = "";
}

```
```java
// 데이터베이스 연결
@Slf4j
public class DBConnectionUtil {

    public static Connection getConnection(){
        try {
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            log.info("get connection={}, class={}", connection, connection.getClass());
            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
```
```java
// 테스트
@Slf4j
public class DBConnectionUtilTest {

    @Test
    void connection(){
        Connection connection = DBConnectionUtil.getConnection();
        assertThat(connection).isNotNull();
    }
}
```

* 로직에서 커넥션이 필요하면 DriverManager.getConnection() 호출

* Connection 을 가져와야 하는데 이는 인터페이스라서 구현체가 필요함(jdbc를 구현한 구현체의 커넥션)

* DriverManager 는 라이브러리에 등록된 드라이버 목록을(jdbc 구현체) 자동으로 인식한다

* 그리고 드라이버들에게 순서대로 접속에 필요한 정보를 넘겨서 커넥션을 획득할 수 있는지 확인한다

* URL: 예) jdbc:h2:tcp://localhost/~/test, 이름, 비밀번호 등 접속에 필요한 추가 정보

* 각각의 드라이버는 URL 정보를 체크해서 본인이 처리할 수 있는 요청인지 확인한다

* 예를 들어서 URL이 jdbc:h2 로 시작하면 이것은 h2 데이터베이스에 접근하기 위한 규칙이다. 따라서 H2 드라이버는 본인이 처리할 수 있으므로 실제 데이터베이스에 연결해서 커넥션을 획득하고 이 커넥션을 클라이언트에 반환한다

* 반면 URL이 jdbc:h2 로 시작했는데 MySQL 드라이버가 먼저 실행되면 이 경우 본인이 처리할 수 없다는 결과를 반환하게 되고, 다음 드라이버에게 순서가 넘어간다

* 이렇게 찾은 커넥션 구현체가 클라이언트에 반환된다

#### 등록

```
// db에 회원 테이블 생성
create table member (
member_id varchar(10),
money integer not null default 0,
primary key (member_id)
);
```

```java
// db의 회원 테이블에 데이터를 저장하고 조회할때 사용
@Data
public class Member {

    private String memberId;
    private int money;

    public Member(){}

    public Member(String memberId, int money){
        this.memberId = memberId;
        this.money = money;
    }
}
```
```java
@Slf4j
public class MemberRepositoryV0 {

    public Member save(Member member) throws SQLException {

        // db에서 사용할 sql
        String sql = "insert into member(member_id, money) values (?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try{
            // 커넥션 획득
            con = getConnection();

            // 데이터베이스에 전달할 SQL과 파라미터로 전달할 데이터들을 준비
            pstmt = con.prepareStatement(sql);

            // SQL의 첫번째 `?` 에 값을 지정한다. 문자이므로 setString 을 사용
            pstmt.setString(1, member.getMemberId());

            // SQL의 두번째 `?` 에 값을 지정한다. `Int` 형 숫자이므로 setInt 를 지정
            pstmt.setInt(2, member.getMoney());


            // Statement 를 통해 준비된 SQL을 커넥션을 통해 실제 데이터베이스에 전달
            // executeUpdate() 은 int 를 반환하는데 영향받은 DB row 수를 반환한다. 
            // 여기서는 하나의 row를 등록했으므로 1을 반환한다
            pstmt.executeUpdate();

            return member;
        }catch (SQLException e){
            log.error("db error", e);
            throw e;
        }finally{

            // 리소스 반환. 리소스 정리는 예외가 발생하든 하지 않든 항상 수행되어야 한다
            // 반환되지 않는다면 커넥션이 끊어지지 않고 계속 유지되는 문제가 발생한다
            // 호출을 보장하기 위해 finally에 작성
            // try{}에서 close() 호출 시 executeUpdate() 같은 윗줄의 코드에서 예외가 발생하면 바로 catch로 넘어가 close가 호출이 되지 않는다
            close(con, pstmt, null);
        }


    }

    private void close(Connection con, Statement stmt, ResultSet rs){

        if(rs != null){
            try {
                rs.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

        if(stmt != null){
            try {
                stmt.close();
            } catch (SQLException e) {
                log.info("error", e);
            }

        }

        if(con != null){
            try {
                con.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }
    }

    // 이전에 만들어준 연결용 클래스 활용
    private Connection getConnection(){
        return DBConnectionUtil.getConnection();
    }
}
```
```java
// 테스트
class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();

    @Test
    void crud() throws SQLException {
        Member member = new Member("memberV0", 10000);
        repository.save(member);
    }
}
```

#### 조회

```java
    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            // 데이터 조회만 할때는 executeQuery() 사용
            // rs 에 select 쿼리의 결과가 순서대로 들어간다
            // ex) select member_id, money 라고 지정하면 member_id , money 라는 이름으로 rs 에 데이터가 저장된다
            rs = pstmt.executeQuery();

            // rs 는 내부에 있는 커서를 이동해서 다음 데이터를 조회한다
            // rs.next() 호출시 커서가 다음으로 이동하며 최초의 커서는 데이터를 가리키고 있지 않으므로 최초 한번은 호출해야 데이터를 조회할 수 있다
            // rs.next()가 true면 커서 이동 결과 데이터가 있다는 것이고 false 면 없다는 뜻이다 
            if(rs.next()){
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            }else {
                throw new NoSuchElementException("member not found memberId=" + memberId);
            }

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
    }
```
```java
// 테스트
@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();

    @Test
    void crud() throws SQLException {

        // save
        Member member = new Member("memberV0", 10000);
        repository.save(member);

        //findById
        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember={}", findMember);
        assertThat(findMember).isEqualTo(member);
    }
}
```

#### 수정, 삭제

```java
    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        }catch (SQLException e){
            log.error("db error", e);
            throw e;
        }finally{
            close(con, pstmt, null);
        }
    }

    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }
```
```java
// 테스트
@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();

    @Test
    void crud() throws SQLException {

        // save
        Member member = new Member("memberV0", 10000);
        repository.save(member);

        //findById
        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember={}", findMember);
        assertThat(findMember).isEqualTo(member);

        //update: money: 10000 -> 20000
        repository.update(member.getMemberId(), 20000);
        Member updatedMember = repository.findById(member.getMemberId());
        assertThat(updatedMember.getMoney()).isEqualTo(20000);

        //delete
        repository.delete(member.getMemberId());
        assertThatThrownBy(() -> repository.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);
    }
}
```
