### 스프링 컨테이너

* 스프링에서 자바 객체들을 관리하는 공간. 객체를 생성하고 의존관계를 연결해준다.

* 스프링 컨테이너가 관리하는 자바 객체를 빈(Bean)이라고 하는데, 스프링 컨테이너에서 이 빈의 생성부터 소멸까지 개발자 대신 관리해준다. 

* 스프링 컨테이너는 BeanFactory(인터페이스)와 ApplicationContext(인터페이스) 의 두가지 종류가 있는데 ApplicationContext가 BeanFactory의 기능을 상속하며 추가적인 기능을 제공하기 때문에 대부분 ApplicationContext를 사용한다.

* 스프링 컨테이너가 생성될때 @Configuration이 붙은 설정 클래스를 인자로 사용한다.

* 그리고 @Bean이 적용된 메서드를 모두 호출해 반환된 객체를 스프링 컨테이너에 등록한다. 이렇게 스프링 컨테이너에 등록된 객체를 스프링 빈이라 한다.

* 마지막으로 등록된 스프링 빈들의 의존 관계를 맺어준다

* 스프링 빈은 @Bean이 붙은 메서드의 명을 스프링 빈의 이름으로 사용한다.
```
// 스프링 컨테이너 생성 방법
// @Configuration이 적용된 설정 클래스(여기서는 000.class)를 인자로 지정해 스프링 컨테이너 생성
// 스프링 컨테이너는 파라미터로 넘어온 설정 클래스(구성 정보)를 활용해 스프링 빈을 등록한다
ApplicationContext applicationContext = new AnnotationConfigApplicationContext(000.class);

// 스프링 컨테이너에 등록된 스프링 빈(객체)를 찾는 방법
// 이름과 타입으로 찾을때. 이름은 @bean이 적용된 메서드의 이름이 기본값이다
applicationContext.getBean("@Bean의 메서드 이름", 반환타입);

// 타입으로만 찾을때
applicationContext.getBean(반환타입);

// 빈 이름은 메서드 이름을 사용하지만 직접 부여할 수도 있다
// 빈 이름은 절대 중복되면 안된다
@Bean(name="dddd")

// 같은 인터페이스를 상속한 A, B 클래스가 있다고 가정
// 빈에서 A, B를 리턴한다면 타입으로만 찾을 시 타입이 같아 어느것을 출력할지 몰라서 오류 발생
// 이름과 타입으로 찾아주면 된다
@Bean
리턴 A
@Bean
리턴 B
```

* 스프링 컨테이너에 객체를 스프링 빈으로 등록하고, 스프링 컨테이너에서 스프링 빈을 찾아서 사용한다


### 의존성 주입(Dependency Injection)

* 의존성이란 한 객체가 다른 객체를 사용할 때 의존성이 있다고 한다.

```
public class Store {

    private Pencil pencil;

}
```

* 위와 같이 Store 객체가 Pencil 객체를 사용하고 있는 경우 Store객체가 Pencil 객체에 의존성이 있다고 표현한다.

* 두 객체간의 관계를 맺어주는 것을 의존성 주입이라고 하며 생성자,필드,수정자 주입 등의 방법이 있는데 순환참조 에러 방지와 객체의 불변성 확보를 위해 주로 생성자 주입을 사용한다.

* 즉 스프링 컨테이너가 @Bean, @Component 어노테이션이 붙은 클래스들을 스캔해서 싱글톤 패턴으로 객체를 생성해서 가지고 있다가 메서드들에서 호출시 전달해준다. 

* 이때 여러 메서드들에서 같은 객체를 호출해도 싱글톤 패턴으로 객체를 생성했기 때문에 같은 객체가 전달된다.



### 싱글톤 패턴

* 클래스의 인스턴스가 1개만 생성되는 것을 보장하는 디자인 패턴

* 소프트웨어 디자인 패턴 중의 하나로 생성자가 여러 차례 호출되더라도 실제로 생성되는 객체는 하나이고, 최초 생성 이후에 호출된 생성자는 최초의 생성자가 생성한 객체를 리턴한다.

* 여러 사람의 요청마다 계속 객체를 생성하게 되면 메모리 낭비가 심하기에 싱글톤 패턴을 사용한다.
```java
public class SingletonService {

    // static 영역에 객체를 1개만 생성해둔다
    private static final SingletonService instance = new SingletonService();

    // public로 외부 접근을 허용해두고, 객체가 필요하면 이 static 메서드를 통해서만 조회하도록 허용한다.
    public static SingletonService getInstance(){
        return instance;
    }

    // 생성자를 private으로 선언해서 외부에서 new 키워드를 사용한 객체 생성을 막는다.
    // 생성자가 private이면 싱글톤이다
    private SingletonService(){}
    
    public void logic(){
        System.out.println("싱글톤 객체 로직 호출");
    }
}
```

* 싱글톤 컨테이너 - 객체를 싱글톤 패턴으로 생성하고 관리하는 기능

* 스프링 컨테이너는 싱글톤 컨테이너 역할을 하기 때문에 등록된 빈들을 알아서 싱글톤으로 관리한다

* @Configuration없이 @Bean만 있다면 싱글톤을 보장하지 않는다.

* @Configuration은 별도 라이브러리를 활용해 @Bean이 붙은 메서드마다 스프링 빈이 존재하면 존재하는 빈을 반환하고, 없으면 생성해서 등록 후 반환하는 코드를 실행한다


### 싱글톤 패턴의 주의점

* 여러 클라이언트가 하나의 같은 객체 인스턴스를 공유하기 때문에 상태를 유지하게(stateful) 설계하면 안된다.

* 무상태로 설계해야 한다(stateless)

* 특정 클라이언트에 의존적인 필드가 있거나, 특정 클라이언트가 값을 변경할 수 있는 필드가 있으면 안된다

* 가급적 읽기만 가능해야 하고, 필드 대신 공유되지 않는 지역변수, 파라미터, ThreadLocal 등을 사용해야 한다.

```
// 상태를 유지하게 설계 시 문제점
// 주문 클래스
class order{

   // 상태를 유지하는 필드. 주문 금액을 저장하는 역할
   private Long price;

   // 주문시 호출되는 메서드
   public void order(String name, int price){
     this.price = price; // 주문한 상품의 금액을 필드에 저장한다
   }

   // 주문 금액 조회 시 호출되는 메서드
   public int getPrice(){
     return price;
   }

}
// 사용자 A, B가 있다고 가정
1. A가 1000원 상품을 주문 ->  price에 1000원 저장
2. A가 주문 금액 조회하기 전에 B가 20000 상품을 주문 -> price에 20000원 저장
3. A가 주문 금액 조회 -> B의 주문 금액인 20000원 응답  
```

### 컴포넌트 스캔

* @Configuration과 @Bean을 활용한 설정 정보 클래스 수동 작성 대신 자동으로 빈을 등록해주는 기능

* @Component가 붙은 클래스를 스캔해서 @Autowired와 같은 의존관계 설정 정보를 알려주는 어노테이션을 통해 스프링 빈으로 자동 등록 해준다

* 기존 수동 설정 클래스에서 의존관계 설정을 수작업으로 하던것 대신 의존관계 설정 정보를 알려주는 어노테이션을 사용해 자동으로 의존관계 설정을 해준다

```
//사용 방법. 빈으로 등록할 요소가 있는 패키지에 붙인다
@Component
class a{}

// 스캔 시작 위치 지정. 이 패키지를 포함해서 하위 패키지를 모두 탐색
// hello.com.bb 패키지부터 하위 패키지까지 스캔해서 @Component가 있는 클래스를 찾아 빈으로 등록
// 동일 선상에 있는 패키지는 스캔 범위에 포함되지 않는다
@Component(basePakages = "hello.com.bb")

// 지정한 클래스의 패키지를 탐색 시작 위로 지정
@Component(basePakageClasses = "hello.com.bb") 
```

* 권장 설정 방법은 프로젝트 최상단에 두는것이다. 경로가 spring.hello.com 라면 com 하위에 두는것이다

* 그런데 스프링 부트 프로젝트 생성시 자동으로 만들어지는 메인 메소드가 위치한 클래스에 붙은 @SpringBootApplication에는 @Component가 포함되어 있다



#### 컴포넌트 스캔 기본 대상

* @Component - 컴포넌트 스캔 기본

* @Controller - 스프링 MVC 컨트롤러에서 사용

* @Service - 비즈니스 로직에서 사용

* @Repository - 데이터 접근 계층에서 사용

* @Configuration - 설정 정보에서 사용
  
* @Controller, @Service, @Repository, @Configuration 모두 @Component를 포함하고 있다
  

### 제어의 역전 IOC(Inversion Of Control)

* 프레임워크 없이 개발할 때는 객체의 생성, 설정을 클라이언트 구현 객체가 모두 담당했다.

* 프레임워크를 사용하면 객체의 생성, 연결(주입)과 같은 제어 흐름을 프레임워크에 위임하고 기존 프로그램은 로직을 실행만 하게 된다

* 이와 같이 프로그램의 제어 흐름을 외부에 위임하는 설계 원칙을 제어의 역전이라고 한다. 
 
* 프레임워크는 제어의 역전 개념이 적용된 대표적인 기술이라고 할 수 있다.
