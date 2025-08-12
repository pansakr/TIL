### SOILD

#### SRP(Single responsibility principle)

* 한 클래스는 하나의 책임만 가져야 한다

* 변경이 있을 때 파급 효과가 적으면 단일 책임 원칙을 잘 다른 것


#### OCP 개방-폐쇄 원칙(Open/Closed Principle)

* 소프트웨어 요소는 확장에는 열려 있으나 변경에는 닫혀 있어야 한다

* 다형성을 활용해 새로운 클래스를 만들어서 새로운 기능을 구현한다

* 기존 코드 변경 x -> 새로운 클래스를 만들어 새로운 기능 구현


#### LSP 리스코프 치환 원칙(Liskov sub)

* 다형성에서 하위 클래스는 인터페이스 규약을 모두 지켜야 한다


#### ISP 인터페이스 분리 원칙(Interface segregation principle)

* 특정 클라이언트를 위한 인터페이스 여러 개가 범용 인터페이스 하나보다 낫다

* 인터페이스를 목적에 따라 분리하면 해당 부분을 다른 기능으로 대체하기 쉬워진다


#### DIP 의존관계 역전 원칙(Dependency inversion principle)

* 구현 클래스에 의존하지 말고 인터페이스에 의존해야 한다


### 어떻게 ocp, dip를 지킬 수 있는가?

```java

interface BBB{}
class bbb implements BBB{}
class bbb2 implements BBB{}

// aaa 클래스에서 BBB인터페이스를 구현한 bbb클래스를 사용 == 의존한다.
public class aaa implements AAA{

    // 다형성을 활용해 DIP를 지키려고 했다
    // 그러나 BBB 인터페이스, bbb 구현체 모두를 의존하기에 DIP를 지키지 못했다
    // private final BBB b = new bbb(); 

    // 인터페이스만 선언해 DIP를 지켰다.
    // 그러나 구현체가 없어 사용할 수 없다
    private final BBB bb;
}
```

* 위 상황에서 DIP를 지키려면 객체 생성, 연결에 관한 로직을 분리해 처리해야 한다


```java

interface BBB{}
class bbb implements BBB{} 
class bbb2 implements BBB{}

public class aaa implements AAA{

    private final BBB bb;

    // 생성자 추가
    public aaa(BBB b){
        this.bb = b;
    }
}

// 객체를 대신 생성하고 연결해주는 설정 클래스
public class AppConfig {

    // 구현체 변경 가능 
    public BBB config1(){
        return new aaa(new bbb());
        // return new aaa(new bbb2());
    }
}
```

* AppConfig가 객체 생성, 연결을 대신해 준다

* aaa 클래스 입장에서는 의존관계를 외부에서 주입해주는것 같다고 해서 의존관계 주입 또는 의존성 주입이라 한다

* aaa 클래스는 생성자를 통해 어떤 구현객체가 들어올지(주입될지) 모른다

* aaa 클래스의 생성자를 통해 어떤 구현객체를 주입할지는 AppConfig에서 결정하고 aaa는 실행에만 집중하면 된다

* 기능을 변경하고 싶다면 부품을 갈아끼우듯이 AppConfig에서 다른 구현체로 변경해주기만 하면 된다
