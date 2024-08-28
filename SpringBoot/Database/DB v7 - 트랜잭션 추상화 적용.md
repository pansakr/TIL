### 트랜잭션 추상화

* 트랜잭션을 다루는 방법을 추상화한 인터페이스

```java
// 예시
public interface TxManager {
begin();
commit();
rollback();
}
```

* 데이터 접근 기술마다 트랜잭션을 사용법이 다르기 때문에 다른 기술로 교체한다면 많은 부분을 수정해야 한다

* 이 문제는 서비스 계층이 트랜잭션 추상화 인터페이스를 의존하게 하고, 사용하는 기술에 맞는 해당 인터페이스의 구현체를 di로 주입하면 된다

* 스프링은 트랜잭션 추상화 인터페이스를 만들어 두었고, 해당 인터페이스를 구현한 데이터 접근 기술에 따른 구현체도 만들어 두어서 가져다 사용하기만 하면 된다

```
// 스프링 트랜잭션 추상화 인터페이스
PlatformTransactionManager 

            ↓     

// 해당 인터페이스를 구현한 여러 데이터 접근 기술들의 구현체들
DataSource TransactionManager, Jpa TransactionManager ...
```

### 트랜잭션 동기화

* 트랜잭션을 유지하려면 같은 커넥션을 유지해야 한다

* 같은 커넥션을 동기화하기 위해 이전에는 파라미터로 커넥션을 전달했다

* 스프링에서 제공하는 트랜잭션 매니저를 사용하면 커넥션을 동기화할(유지) 수 있다

```
1. 서비스 계층에서 트랜잭션 시작을 위해 트랜잭션 매니저를 호출한다

2. 호출된 트랜잭션 매니저는 데이터 소스를 통해 커넥션을 만들고 수동 커밋 모드로 변경해 트랜잭션을 시작한다. 그리고 이것을 트랜잭션 동기화 매니저에 보관한다

3. 트랜잭션 동기화 매니저는 쓰레드 로컬에 커넥션을 보관해서 멀티 쓰레드 환경에 안전하게 커넥션을 보관할 수 있다

4. 트랜잭션 매니저는 생성된 트랜잭션의 정보를 서비스 계층에 반환한다

5. 서비스 계층에서 비즈니스 로직을 수행하면서 리포지토리의 메서드들을 호출한다

6. 리포지토리 계층의 메서드들은 트랜잭션이 시작된 커넥션이 필요한데, 트랜잭션 동기화 매니저에 보관된 커넥션을 꺼내서 사용한다. 이 과정을 통해 같은 커넥션을 사용하고 트랜잭션도 유지된다

7. 비즈니스 로직 성공, 실패에 따라 커밋 또는 롤백 한다. 트랜잭션은 커밋이나 롤백하면 종료된다

8. 트랜잭션이 종료되면 트랜잭션 매니저는 트랜잭션 동기화 매니저에 보관된 커넥션을 통해 트랜잭션을 종료하고 커넥션도 닫는다. 따로 리소스를 반환하지 않아도 된다

// 8번 자세히
8.1 트랜잭션을 종료하려면 동기화된 커넥션이 필요한데, 트랜잭션 동기화 매니저를 통해 동기화된 커넥션을 획득한다

8.2 획득한 커넥션을 통해 데이터베이스에 트랜잭션을 커밋하거나 롤백한다

8.3 리소스를 정리한다
```

```java
/**
 * service
 * transactionManager 로 트랜잭션 시작
*/ 
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_1 {

    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        // 트랜잭션 시작
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try{
            // 비즈니스 로직
            bizLogic(fromId, toId, money);
            transactionManager.commit(status); // 성공시 커밋
        }catch (Exception e){
            transactionManager.rollback(status); // 실패시 롤백
            throw new IllegalStateException(e);
        }
    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);

        memberRepository.update(toId, toMember.getMoney() + money);
    }

    private static void validation(Member toMember) {
        if(toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
```
```java
/**
 * repository
 * 매개변수로 커넥션을 받는 메서드 제거
 * 커넥션을 DataSourceUtils.getConnection() 로 가져옴
*/
@Slf4j
public class MemberRepositoryV3 {

    private final DataSource dataSource;

    public MemberRepositoryV3(DataSource dataSource) {
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

    private void close(Connection con, Statement stmt, ResultSet rs){

        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);

        // 트랜잭션 동기화를 사용하려면 DataSourceUtils 를 사용해야 한다
        DataSourceUtils.releaseConnection(con, dataSource);
    }

    private Connection getConnection() throws SQLException {

        // 트랜잭션 동기화를 사용하려면 DataSourceUtils 를 사용해야 한다
        Connection con = DataSourceUtils.getConnection(dataSource);
        log.info("get connection={}, class={}", con, con.getClass());
        return con;
    }
}
```
```java
// test
/**
 * 트랜잭션  - 트랜잭션 매니저
 */
class MemberServiceV3_1Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    private MemberRepositoryV3 memberRepository;
    private MemberServiceV3_1 memberService;

    @BeforeEach
    void before(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        memberRepository = new MemberRepositoryV3(dataSource);

        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);

        memberService = new MemberServiceV3_1(transactionManager, memberRepository);
    }

    @AfterEach
    void after() throws SQLException {
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {
        // given - 준비(세팅)
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        // when - 실행
        memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);

        // then - 결과
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberB.getMemberId());

        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(12000);
    }

    @Test
    @DisplayName("이체중 예외 발생")
    void accountTransferEx() throws SQLException {
        // given - 준비(세팅)
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberEx = new Member(MEMBER_EX, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberEx);

        // when - 실행
        assertThatThrownBy(() -> memberService.accountTransfer(memberA.getMemberId(), memberEx.getMemberId(), 2000))
                .isInstanceOf(IllegalStateException.class);

        // then - 결과
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberEx.getMemberId());

        assertThat(findMemberA.getMoney()).isEqualTo(10000);
        assertThat(findMemberB.getMoney()).isEqualTo(10000);
    }
}
```
