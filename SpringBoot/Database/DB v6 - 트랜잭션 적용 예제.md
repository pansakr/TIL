### 트랜잭션 적용 예제

```java
// service
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        // 트랜잭션을 시작하려면 커넥션이 필요하다
        Connection con = dataSource.getConnection();

        try{
            // 수동 커밋 설정. 트랜잭션 시작
            // 트랜잭션을 시작하려면 자동 커밋 모드를 꺼야한다
            con.setAutoCommit(false);

            // 비즈니스 로직
            bizLogic(con, fromId, toId, money);

            con.commit(); // 성공시 커밋
        }catch (Exception e){
            con.rollback(); // 실패시 롤백
            throw new IllegalStateException(e);
        }finally {
            // 리소스 반환
            release(con);
        }


    }

    private void bizLogic(Connection con, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);

        memberRepository.update(con, fromId, fromMember.getMoney() - money);
        validation(toMember);

        memberRepository.update(con, toId, toMember.getMoney() + money);
    }

    private static void release(Connection con) {
        if(con != null){
            try{
                // 오토 커밋으로 다시 변환해 커넥션 풀에 반환
                // 이걸 안하면 다음에 풀에서 가져올땐 수동 커밋 모드로 가져와진다 (기본값 자동커밋)
                con.setAutoCommit(true);

                // 커넥션 풀 사용시 풀에 반납, 아니라면 커넥션 종료
                con.close();
            }catch (Exception e){
                log.info("error", e);
            }
        }
    }

    private static void validation(Member toMember) {
        if(toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
```
```java
// repository
    public Member findById(Connection con, String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            // connection 을 여기서 새로 연결하지 않는다
            // 여기서 커넥션을 새로 연결하면 db 세션도 새로 만들어진다
            // 파라미터로 받은 커넥션을 사용해야 db 세션이 유지되기 때문
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
            // connection은 여기서 닫지 않는다. 닫으면 db 세션이 종료되기 때문
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
//            JdbcUtils.closeConnection(con);
        }
    }

        public void update(Connection con, String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        PreparedStatement pstmt = null;

        try{
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        }catch (SQLException e){
            log.error("db error", e);
            throw e;
        }finally{
            // connection은 여기서 닫지 않는다. 닫으면 db 세션이 종료되기 때문
            JdbcUtils.closeStatement(pstmt);
//            JdbcUtils.closeConnection(con);;
        }

    }

    // 나머진 동일
```
```java
// test
    @Test
    @DisplayName("이체중 예외 발생")
    void accountTransferEx() throws SQLException {

        ... 나머진 동일

        // 이체 중 예외발생으로 롤백해서 원래 금액으로 돌아옴
        assertThat(findMemberA.getMoney()).isEqualTo(10000);
        assertThat(findMemberB.getMoney()).isEqualTo(10000);
    }
```

### 이 방법의 문제점

* jdbc 구현 기술은 repository 계층에서만 사용되야 하지만 트랜잭션 적용 때문에 강제로 서비스 계층에서 사용된다. 

* 트랜잭션을 유지하기 위해 커넥션을 파라미터로 넘겨야 해서 똑같은 기능도 트랜잭션용과 아닌 것 2가지를 만들어야 한다

* repository 계층의 jdbc 구현 기술 예외가 서비스 계층으로 전파된다

* jdbc 반복 문제. try, catch, finally 
