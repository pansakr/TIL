### 자바의 모니터

* 객체의 동기화 및 상태를 관리하기 위한 추상적인 개념 

### 자바의 락

* 모니터의 구현 중 하나로, 동기화 메커니즘을 제공하는 실제 도구

* 락은 객체 단위로 적용되기 때문에 하나의 클래스 내부에 동기화 메서드가 둘 이상 있으면 동시에 실행되지 못한다

* 한 스레드가 객체의 synchronized 메서드를 실행 중이라면, 동일한 객체의 다른 synchronized 메서드를 호출하려는 다른 스레드는 락이 반납될 때까지 대기해야 한다

```
// 동작 흐름. 하나의 동기화 메서드를 A, B 스레드가 실행하려는 상황
1. A 스레드: 동기화 메서드를 호출 → 객체의 락 획득 시도 → 락을 성공적으로 획득하면 메서드 실행 시작
2. B 스레드: 동기화 메서드를 호출 → 객체의 락 획득 시도 → 락이 없는 경우 대기
3. A 스레드: 메서드 실행 완료 → 객체의 락을 해제
4. B 스레드: 대기에서 벗어나 락을 획득하고 메서드 실행
```

### Object 의 스레드 관련 메서드

* wait() - 현재 스레드를 대기 상태로 만들고 락을 반납한다

* notifyAll() - 대기 상태에 있는 모든 스레드를 깨운다

  - 깨어난 스레드들은 다시 락을 얻기 위해 경쟁하고, 락을 얻은 스레드가 실행을 재개한다
 
  - 여러 스레드가 같은 객체 락을 기다리고 있을 때 사용하며, 그 중 어떤 스레드가 실행되어야 하는지 확신할 수 없을 때 사용한다
 
* notify() - 대기열의 스레드 중 하나만 깨운다

  - 깨운 스레드가 어떤 이유로 실행 불가능한 상태면 무한 대기 상태가 된다
 
  - 잘 사용하지 않음

```java
public class PhoneBooth {

    synchronized public void phoneCall (SoldierRun soldier) {
        System.out.println("☎️ %s 전화 사용중...".formatted(soldier.title));

        try { Thread.sleep(500);
        } catch (InterruptedException e) {}

        System.out.println("👍 %s 전화 사용 완료".formatted(soldier.title));

        // 아래의 코드가 없다면 하나의 스레드가 무한으로 실행함
        notifyAll(); // 대기 상태의 모든 스레드를 깨움
        try {
          wait();  // 현재 스레드를 대기 상태로 만든다
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
```
```java
// 개별 스레드로 사용할 클래스
public class SoldierRun implements Runnable{

    String title;
    PhoneBooth phoneBooth;

    public SoldierRun(String title, PhoneBooth phoneBooth) {
        this.title = title;
        this.phoneBooth = phoneBooth;
    }
    @Override
    public void run() {
        while (true) {
            phoneBooth.phoneCall(this);
        }
    }
}
```
```java
// main
public static void main(String[] args) {

  PhoneBooth phoneBooth = new PhoneBooth();

  Arrays.stream("김병장,이상병,박일병,최이병".split(","))
          .forEach(s -> new Thread(
              new SoldierRun(s, phoneBooth)
          ).start());
    }
```

* 생산자와 소비자 예제

```java
// takeout(), fill() 메서드는 동시에 실행되지 못한다
// 하나의 메서드가 실행중이라면 다른 스레드들은 대기해야 한다 
public class CoffeeMachine {

    final int CUP_MAX = 10;
    int cups = CUP_MAX;

    synchronized public void takeout (CustomerRun customer) {
        if (cups < 1) {
            System.out.printf(
                    "[%d] 😭 %s 커피 없음%n", cups, customer.name
            );
        } else {
            try { Thread.sleep(1000);
            } catch (InterruptedException e) {}

            System.out.printf(
                    "[%d] ☕️ %s 테이크아웃%n", cups, customer.name
            );
            cups--;
        }

        notifyAll();
        try { wait();
        } catch (InterruptedException e) {}
    }

    synchronized public void fill () {
        if (cups > 3) {
            System.out.printf(
                    "[%d] 👌 재고 여유 있음...%n", cups
            );
        } else {
            try { Thread.sleep(1000);
            } catch (InterruptedException e) {}

            System.out.printf(
                    "[%d] ✅ 커피 채워넣음%n", cups
            );
            cups = CUP_MAX;
        }

        notifyAll();
        try { wait(); // 커피를 채우고 나감
        } catch (InterruptedException e) {}
    }
}
```
```java
// 고객 - 소비자
public class CustomerRun implements Runnable{
    String name;
    CoffeeMachine coffeeMachine;

    public CustomerRun(String name, CoffeeMachine coffeeMachine) {
        this.name = name;
        this.coffeeMachine = coffeeMachine;
    }

    @Override
    public void run() {
        while (true) {
            coffeeMachine.takeout(this);
        }
    }
}
```
```java
// 카페 매니저 - 생산자
public class ManagerRun implements Runnable{
    CoffeeMachine coffeeMachine;
    public ManagerRun(CoffeeMachine coffeeMachine) {
        this.coffeeMachine = coffeeMachine;
    }

    @Override
    public void run() {
        while (true) {
            coffeeMachine.fill();
        }
    }
}
```
```java
// main
public class Main {

    public static void main(String[] args) {

        CoffeeMachine coffeeMachine = new CoffeeMachine();

        Arrays.stream("철수,영희,돌준,병미,핫훈,짱은,밥태".split(","))
                .forEach(s -> new Thread(
                        new CustomerRun(s, coffeeMachine)
                ).start()); // 고객 - 소비자 스레드 7개 실행

        // 카페 매니저 - 생산자 스레드 1개 실행
        new Thread(new ManagerRun(coffeeMachine)).start();
    }
}
```
