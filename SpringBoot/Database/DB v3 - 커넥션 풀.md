### 커넥션 풀

* 커넥션을 미리 생성해두고 필요할 때 사용하는 방법

    - 커넥션 : DB 서버와 클라이언트가 통신할 수 있도록 TCP 연결을 한 상태

* DB 연결마다 커넥션을 매번 새로 만드는 것은 시간도 많이 걸리고 리소스도 사용하기 때문에 실무에선 항상 커넥션 풀을 사용한다

```
// 커넥션 획득 과정
1. 애플리케이션 로직은 DB 드라이버를 통해 커넥션을 조회한다

2. DB 드라이버는 DB와 TCP/IP 커넥션을 연결한다. 이 과정에서 3 way handshake 같은 TCP/IP 연결을 위한 네트워크 동작이 발생한다

3. DB 드라이버는 TCP/IP 커넥션이 연결되면 ID, PW와 기타 부가정보를 DB에 전달한다

4. DB는 ID, PW를 통해 내부 인증을 완료하고, 내부에 DB 세션을 생성한다

5. DB는 커넥션 생성이 완료되었다는 응답을 보낸다

6. DB 드라이버는 커넥션 객체를 생성해서 클라이언트에 반환한다
```

* 커넥션 풀(풀 - 보관소) 설명

```
1. 애플리케이션을 시작하는 시점에 커넥션 풀은 필요한 만큼 커넥션을 미리 확보해서 풀에 보관한다. 기본값은 10개이다

2. 커넥션 풀에 들어있는 커넥션은 tcp/ip로 db와 커넥션이 연결되어 있는 상태기 때문에 즉시 sql을 db에 전달할 수 있다

3. 애플리케이션 로직에서 이제는 db드라이버를 통해 새로운 커넥션을 획득하지 않고, 커넥션 풀에 이미 생성되어 있는 커넥션을 객체 참조로 가져다 쓴다

3. 이때 커넥션 풀은 자신이 가지고 있는 커넥션 중 하나를 반환한다

4. 애플리케이션 로직은 받은 커넥션을 사용해 sql을 db에 전달하고 결과를 받아 처리한다

5. db작업 종료 후 커넥션을 종료하는것이 아니라, 다음에 사용할 수 있도록 해당 커넥션을 커넥션 풀에 반환한다

6. 적절한 커넥션 풀 숫자는 서비스 마다 다르므로 성능 테스트를 통해 정해야 하고, 커넥션 풀은 서버당 최대 커넥션 수를 제한할 수 있다

7. 직접 구현보다는 커넥션 풀 오픈소스를 사용하는데 주로 HikariCP 를 사용한다

8. 스프링 부트는 기본 커넥션 풀로 HikariCP 를 제공한다
```

### DataSource

* 커넥션을 획득하는 방법을 추상화한 인터페이스

```java
// DataSource 핵심 기능인 커넥션 조회
public interface DataSource {
Connection getConnection() throws SQLException;
}
```

* HikariCP를 사용하다가 다른 커넥션 풀 오픈소스로 바꾸면 코드도 함께 변경해야 하는 문제를 해결하기 위해 자바는 DataSource 인터페이스를 제공한다

* 대부분의 커넥션 풀은 DataSource 인터페이스를 구현해두었기에 커넥션 풀의 코드를 직접 의존하는것이 아니라 DataSource 인터페이스에만 의존하도록 로직을 작성하면 된다

#### DriverMangerDataSource

* DriverManger도 DataSoruce를 통해 사용할 수 있도록 스프링이 제공하는 DataSource를 구현한 클래스

* 원래 DriverManger 는 DataSource를 사용하지 않기 때문에 커넥션 풀로 변경시 관련 코드를 다 고쳐야 했는데 그 문제를 해결한 것이다

```java
// 테스트
@Slf4j
public class ConnectionTest {

    // DriverManager 사용
    @Test
    void driverManager() throws SQLException {
        Connection con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Connection con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        log.info("connection={}, class={}", con1, con1.getClass());
        log.info("connection={}, class={}", con2, con2.getClass());
    }

    // DriverManager + DataSource 사용 (커넥션 풀 x)
    @Test
    void dataSourceDriverManager() throws SQLException {

        // DriverManagerDataSource - 항상 새로운 커넥션을 획득
        // DriverManagerDataSource 첫 생성때만 파라미터를 전달한다
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        userDataSource(dataSource);
    }

    private void userDataSource(DataSource dataSource) throws SQLException {

        // 커넥션 획득 시 getConnection()만 호출
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();
        log.info("connection={}, class={}", con1, con1.getClass());
        log.info("connection={}, class={}", con2, con2.getClass());
    }
}
```

#### 커넥션 풀 - HikariCp

```java
// 테스트
@Test
void dataSourceConnectionPool() throws SQLException, InterruptedException {

    // 커넥션 풀링
    HikariDataSource dataSource = new HikariDataSource();
    dataSource.setJdbcUrl(URL);
    dataSource.setUsername(USERNAME);
    dataSource.setPassword(PASSWORD);

    // 최대 커넥션 개수
    // HikariCp는 최대 커넥션 개수 만큼 커넥션을 생성한다
    dataSource.setMaximumPoolSize(10);

    // 풀의 이름
    dataSource.setPoolName("MyPool");

    useDataSource(dataSource);
}

private void useDataSource(DataSource dataSource) throws SQLException {

    Connection con1 = dataSource.getConnection();
    Connection con2 = dataSource.getConnection();
    log.info("connection={}, class={}", con1, con1.getClass());
    log.info("connection={}, class={}", con2, con2.getClass());
}
```

* 커넥션을 생성해 풀에 채우는 것은 별도의 쓰레드가 한다

* 커넥션 풀에 커넥션을 채우는 것은 오래 걸리는 작업이기 때문에 애플리케이션을 실행할 때 커넥션 풀을 채울 때 까지 대기하면 실행 시간이 늦어진다

* 따라서 별도의 쓰레드를 사용해서 커넥션 풀을 채워야 애플리케이션 실행 시간에 영향을 주지 않는다

* 커넥션 풀 활용

```java
@Slf4j
public class MemberRepositoryV1 {

    // DataSource 의존관계 주입
    private final DataSource dataSource;

    // DataSource 의존관계 주입
    public MemberRepositoryV1(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values (?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
            return member;
        }catch (SQLException e){
            log.error("db error", e);
            throw e;
        }finally{
            close(con, pstmt, null);
        }
    }

    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();
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


    // JdbcUtils 을 사용하면 커넥션을 좀 더 편리하게 닫을 수 있다
    // 커넥션 풀 사용시 닫지 않고 커넥션 풀에 반환한다
    private void close(Connection con, Statement stmt, ResultSet rs){

        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(con);
    }

    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("get connection={}, class={}", con, con.getClass());
        return con;
    }
}
```
```java
// 테스트
@Slf4j
class MemberRepositoryV1Test {

    MemberRepositoryV1 repository;

    // @BeforeEach - 테스트 전 실행
    @BeforeEach
    void beforeEach() throws Exception {

        // 기본 DriverManager - 항상 새로운 커넥션 획득
        // DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

        // 커넥션 풀 사용 (HirariCp) - 동일한 커넥션 재사용
        // 테스트는 순서대로 실행되기 때문에 사용하고 돌려주는것을 반복해 동일한 커넥션만 사용하는 것이다
        // 웹 애플리케이션에서 동시에 여러 요청이 들어오면 여러 쓰레드에서 커넥션 풀의 커넥션을 다양하게 가져간다 
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        repository = new MemberRepositoryV1(dataSource);
    }

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
