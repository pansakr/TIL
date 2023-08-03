### 자바 코드 실행원리, main메서드

* 자바 실행전 static 키워드를 가지고 있는 모든 메서드, 필드들을 jvm의 메서드 영역에 로드한다.

* 자바 실행시 jvm은 이름이 main인 메소드를 찾아 내부에 기술된 내용들을 순차적으로 실행한다.

* 이때 main이 static이 아니면 jvm의 메서드 영역에서 찾을수 없기 때문에 반드시 main은 static이어야 한다.

* main 내부 실행이 끝나면 프로그램이 종료된다.


### main 메소드 실행시 Runtime Data Area 영역 변화

```
class Abc{ // static 영역
    static int a1 = 11;
    static char b1 = 'B';
}

class Water{
    
    int w = 50; // heap 영역

    // heap, stack 영역
    public void drink(){  // 호출이 되면 중괄호 내부가 실행된다. 이때 stack 공간이 메모리에 만들어진다.
        System.out.println(w + "ml 물을 마신다");  // 1.1
    }
}

public class Def{

    static int num = 150; // static 영역

    public static void main(String[] args){  // static 영역
        Water water = new Water();    // 1
        water.drink()                 // 2
        System.out.println(Abc.a1);   // 3
        System.out.println(Abc.b1);   // 4
        System.out.println(Def.num);  // 5
    }
}
```

* 자바 실행시 main()메서드 실행전 클래스 로더가 jvm method 영역에 static 키워드가 붙은 Abc클래스의 a1,b1과 Def클래스의 num 그리고 main() 메서드를 로드한다.

* 로드된 Abc 내부에 a1 = 11, b1 = 'B'가 있고, Def내부에는 num = 150 이 있다.

* jvm이 method 영역의 main메소드를 찾아 실행한다.

* 이때 main queue 가 생성되고 이 공간에는 main() 내부의 실행코드가 queue자료구조 순서로 쌓인다. 위의 코드에서 1 ~ 5 번 순서니까 5, 4, 3, 2, 1 (1번이 먼저 5번이 마지막) 로 쌓인다.

* 그리고 먼저 들어온 1번부터 차레대로 실행한다.

* main()메서드를 실행했으니 stack 영역에 main 스택프레임이 생성된다.

* 1번 실행 - main 스택 영역에 water 참조변수를 생성하고 heap 영역에 Water 클래스를 생성한다.

* 스택 영역의 water 참조변수에 heap 영역의 Water 클래스의 주소 정보를 저장한다.

* 스택 영역의 참조변수의 크기는 항상 4Btye다.

* 2번 실행 - drink() 메서드를 실행한다. drink()메서드의 Queue가 생성되고 내부의 실행코드가 순서대로 쌓인다. 

* 반드시 메서드를 호출 해야 내부를 오픈할 수 있다.

* 위의 코드에선 System.out.println(w + "ml 물을 마신다"); 1.1번 하나이다.

* 1.1번이 실행되고 메서드가 실행됬으니 Queue 영역에 drink() 스택프레임이 생성된다.

* 이 코드에선 없지만 리턴값이나 지역변수가 있다면 스택프레임에 저장되고 스택프레임 삭제시 같이 삭제된다.

* 내부의 실행코드가 완료되었으니 drink() Queue와 스택프레임이 삭제된다. 

* 3 ~ 5번 실행 -method 영역에 생성된 Abc클래스를 참조해 Abc.a1, Abc.b1 을 실행하고 Def를 참조해 Def.num을 실행한다.

* main Queue를 전부 실행했으니 stack 영역의 main 메소드가 pop(제거)되고 프로그램이 종료된다.
