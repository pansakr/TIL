### 트랜잭션 aop

* @Transactional 을 사용하면 스프링이 AOP 를 사용해서 트랜잭션을 처리해준다

* @Transactional 은 메서드나 클래스에 붙일 수 있고, 클래스에 붙인다면 public 메서드가 AOP 적용 대상이 된다

* @Transactional 하나만 선언해서 편리하게 트랜잭션을 적용하는 것을 선언적 트랜잭션 관리라 하고, 실무에서는 대부분 이 방법을 사용한다
