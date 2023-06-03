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
    
    //상수와 그에 대응하는 값 지정. 상수는 대문자, 상수값은 소문자로 작성한다.
    SUNFLOWER("sunflower"), ROSE("rose"); 

    private final String name; //상수값을 반환할 변수. 한번 초기화된 후 변경되지 않기 때문에 final을 붙인다.

    private Flower(String flowerName){ //열거형은 기본적으로 생성자를 통해 값을 지정한다.
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
