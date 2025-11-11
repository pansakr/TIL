### 자바빈(javabean)

* 데이터를 표현하는 것을 목적으로 하는 자바 클래스

* 자바빈은 규정되어있는 규칙에 맞게 작성해야 한다

```
// 자바빈 규약

멤버변수의 접근제어자는 private로 선언되어야 한다

멤버변수마다 별도의 get/set 메서드가 존재해야 하고 접근제어자는 public이어야 한다

기본 생성자가 존재해야 한다

기본 패키지가 아닌 특정 패키지에 존재해야 한다
```

* dto 라고 생각하면 됨


### 필드와 프로퍼티

* 프로퍼티는 객체가 갖는 속성이고 이 속성의 값이 담기는 곳이 필드이다

```java
@Getter
@Setter
public class Member {
    private String name;
    private int age;
    private String phoneNumber;
}
```

* Member 클래스의 프로퍼티는 name, age, phoneNumber 이고, 이들의 값을 담는 변수가 필드이다

* 즉 Member 클래스는 name, age, phoneNumber 속성(프로퍼티)을 가지고, 이들의 값이 해당 name, age, phoneNumber 필드에 저장된다


### POJO(Plain Old Java Object)

* 특정 기술에 종속되지 않는 순수한 자바 객체

* 일반적으로 getter, setter 같이 기본적인 기능만 가진 자바 객체를 뜻한다

* 특정 기술을 사용하기 위해 특정 프레임워크를 의존하게 되면 그것은 pojo라고 할 수 없다

* 특정 기술과 환경에 종속되어 의존하게 되면, 코드 가독성 뿐만아니라 유지보수, 확장성에도 어려움이 생긴다

* 스프링의 의존성 주입이나 어노테이션으로 객체를 주입받는 방법을 통해 특정 기술을 사용하면서 pojo를 유지할 수 있다

### 열거형(Enum)

* 상수 그룹을 나타내는 특수한 클래스

* 동작 방식

    - enum 은 내부적으로 정의된 상수마다 하나의 객체를 생성하는 방식으로 동작함

     ```java
    public enum OrderStatus {

        COMPLETED("완료"),
        CANCELED("취소");
    
        private final String label;
    
        OrderStatus(String label) {
            this.label = label;
        }
    }

    // 변환 예시(컴파일 시 내부 구조)
    // 위 코드가 정확히 이렇게 변환되는 것은 아니지만, 비슷한 방식으로 처리됨 
    public final class OrderStatus extends Enum<OrderStatus> {

        public static final OrderStatus COMPLETED = new OrderStatus("COMPLETED", 0, "완료");
        public static final OrderStatus CANCELED = new OrderStatus("CANCELED", 1, "취소");
    
        private final String label;
    
        private OrderStatus(String name, int ordinal, String label) {
            super(name, ordinal);
            this.label = label;
        }
    }
    ```

    - 상수에 괄호로 값을 지정하면 그 값을 담을 필드와 해당 값을 받는 생성자가 반드시 필요함

        - enum 은 정의된 상수명과 괄호 안의 값을 생성자의 인자로 사용해 상수 객체를 하나씩 생성한다
        
        - 따라서 지정한 값에 대응하는 타입을 매개변수로 받는 생성자와 해당 타입을 받을 필드가 반드시 필요하다

        - 즉 enum 작성된 상수와 괄호 안의 값은 "이 상수를 만들 때 이런 값으로 생성하겠다" 는 선언으로, 실제로 그 값이 저장되는 시점은 enum 클래스 로딩 시 생성자 호출로 생성될 때 이다

    - 상수에 괄호가 없으면 자바가 자동으로 enum 클래스의 기본 생성자 Enum(String name, int ordinal) 를 호출하므로 별도의 생성자를 정의할 필요가 없다

        ```java
        public final class OrderStatus extends Enum<OrderStatus> {
        
            public static final OrderStatus COMPLETED = new OrderStatus("COMPLETED", 0);
            ...

            private OrderStatus(String name, int ordinal) {
                super(name, ordinal);
            }
        }
        ```

    - 괄호 안의 값을 사용하려면 get 메서드가 반드시 필요하다
 
        - enum 의 name() 메서드는 상수의 이름(식별자) 을 반환하기 때문
     
        - 괄호 안의 값은 생성자에 전달되는 필드값으로 name() 메서드는 이 값을 전혀 사용하지 않는다
     
        - 대신 생성자에 의해 필드에 전달되었으므로 해당 필드를 사용하게 위해 get 메서드가 필요하다  

* 상수명만 있는 경우 사용법

    ```java
    public enum Color1 { 
        RED, BLUE, ORANGE 
    }
    
    Color1 inputColor = Color1.RED;
    System.out.println(inputColor); // 변수에 담아서 출력하는 방법
    
    System.out.println(Color1.RED); // 변수에 담지 않고 바로 출력하는 방법
    ```

* 상수명과 괄호가 있는 경우 사용법

    - 생성자, 필드 반드시 필요함
 
    - 지정된 값을 사용하려면 get 메서드 필요함

    ```java
    public enum Flower {
        
        // 상수와 그에 대응하는 값 지정. 상수는 대문자, 상수값은 소문자로 작성한다
        SUNFLOWER("sunflower"), ROSE("rose"); 
    
        private final String name; // 상수값을 반환할 변수. 한번 초기화된 후 변경되지 않기 때문에 final을 붙인다
    
        private Flower(String flowerName){ // 열거형은 기본적으로 생성자를 통해 값을 지정한다
            this.name = flowerName;
        }
    
        public String getName() { // private 변수를 가져올 get 메서드 생성
            return name;
        }
    }
    
    public static void main(String[] args) {
            Flower flower = Flower.ROSE;
            System.out.println("I have a " + flower.getName()); // get 매서드로 값 호출
        }
    ```
    
    - 값을 사용하려면 get 메서드가 필요한 이유
 
        - enum 의 name() 메서드는 상수의 이름(식별자)를 반환하기 때문
     
        - 괄호 안의 값은 생성자에 전달된 필드값으로 name() 메서드는 이 값을 전혀 사용하지 않는다
     
        - 괄호 안의 값은 생성자에 의해 필드에 전달되었으므로 사용하려면 get 메서드가 필요하다     
