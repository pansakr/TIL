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

* 프로퍼티는 객체가 갖는 속성이고 이 속성의 실체(값)이 담기는 곳이 필드이다

```java
@Getter
@Setter
public class Member {
    private String name;
    private int age;
    private String phoneNumber;
}
```

* Member 클래스의 프로퍼티는 name, age, phoneNumber 이고, 이들의 실제 값이 담기는 곳이 필드(변수)가 된다

* 즉 Member 클래스는 이름, 나이, 폰번호의 속성(프로퍼티)을 가지고, 이들의 실제 값이 해당 필드에 담긴다


### POJO(Plain Old Java Object)

* 특정 기술에 종속되지 않는 순수한 자바 객체

* 일반적으로 getter, setter 같이 기본적인 기능만 가진 자바 객체를 뜻한다

* 특정 기술을 사용하기 위해 특정 프레임워크를 의존하게 되면 그것은 pojo라고 할 수 없다

* 특정 기술과 환경에 종속되어 의존하게 되면, 코드 가독성 뿐만아니라 유지보수, 확장성에도 어려움이 생긴다

* 스프링의 의존성 주입이나 어노테이션으로 객체를 주입받는 방법을 통해 특정 기술을 사용하면서 pojo를 유지할 수 있다

### 열거형(Enum)

* 상수 그룹을 나타내는 특수한 클래스

* 상수명만 있는 경우 사용법

```
public enum Color1 { 
    RED, BLUE, ORANGE 
}

Color1 inputColor = Color1.RED;
System.out.println(inputColor); //변수에 담아서 출력하는 방법

System.out.println(Color1.RED); //변수에 담지 않고 바로 출력하는 방법
```

* 상수명과 지정된 값이 있는 경우 사용법

```
public enum Flower {
    
    //상수와 그에 대응하는 값 지정. 상수는 대문자, 상수값은 소문자로 작성한다
    SUNFLOWER("sunflower"), ROSE("rose"); 

    private final String name; //상수값을 반환할 변수. 한번 초기화된 후 변경되지 않기 때문에 final을 붙인다

    private Flower(String flowerName){ //열거형은 기본적으로 생성자를 통해 값을 지정한다
        this.name = flowerName;
    }

    public String getName() { //private 변수를 가져올 get 메서드 생성
        return name;
    }
}

public static void main(String[] args) {
        Flower flower = Flower.ROSE;
        System.out.println("I have a " + flower.getName()); //get 매서드로 값 호출
    }
```
