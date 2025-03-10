### 트랜잭션 AOP

* @Transactional 을 사용하면 스프링이 AOP 를 사용해서 트랜잭션을 처리해준다

* @Transactional 은 메서드나 클래스에 붙일 수 있고, 클래스에 붙인다면 public 메서드가 AOP 적용 대상이 된다

* @Transactional 하나만 선언해서 편리하게 트랜잭션을 적용하는 것을 선언적 트랜잭션 관리라 하고, 실무에서는 대부분 이 방법을 사용한다


### @Transactional 어노테이션

* @Transactional 어노테이션은 메서드나 클래스 위에 붙일 수 있다. 클래스 위에 붙이면 모든 메서드에 적용된다

* 어노테이션 방식은 선언적 트랜잭션이라고 불리고, 선언 시 트랜잭션 기능이 적용된 프록시 객체가 생성된다

* @Transactional으로 생성된 프록시 객체는 @Transactional이 적용된 메소드가 호출될 경우, 트랜잭션 매니저를 사용해 트랜잭션을 시작하고, 정상 여부에 따라 Commit/Rollback 동작을 수행한다

* @Transactional이 적용된 메서드 실행시 내부의 로직들은 하나의 단위로 처리된다

  - 예를 들어 로직 실행 중 인터넷이 끊기는 것 처럼 이상 현상이 발생하면 기존 실행했던 작업들은 롤백 처리된다

* 트랜잭션을 성공적으로 마치면 결과는 항상 저장된다
