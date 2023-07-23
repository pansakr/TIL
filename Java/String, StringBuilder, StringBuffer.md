### new String("문자열"), String a = "문자열" 차이점

* 리터럴로 생성한 객체는 JVM의 Heap 공간안의 String Pool 영역에 값이 생성되고 생성된 값은 불변이다.

* 리터럴로 String 객체 생성시 String Pool 영역에 해당 리터럴 값이 있는지 확인 후, 없으면 생성하고 
그 주소를 Stack에서 참조한다.

* 이후 리터럴로 다른 String 객체 생성시 String Pool 영역에 해당 리터럴 값이 있는지 확인 후, 있으면
해당 리터럴의 주소를 참조하고 없으면 생성후 그 주소를 참조한다.

```
String a = "java"  // String Pool 영역에 "java" 생성후 그 주소를 Stack영역의 참조변수 a가 참조
String b = "java"  // String Pool "java"가 있으니 이 주소를 Stack영역의 참조변수 b가 참조
                   // a,b 는 같은 주소값을 가진다.
```

* new String("문자열") 로 String 객체 생성 시 JVM의 heap 영역에 값이 생성되고(불변) Stack영역의 참조변수가 생성된 값을 참조한다.

* String Pool 영역이나 heap영역에 같은 값이 있더라도 새로운 객체를 생성하기 때문에 참조하는 주소값이
다르다.

```
String a = new String("java"); //heap 영역에 "java" 생성후 Stack영역의 참조변수 a가 그 값을 참조
String b = new String("java"); //heap 영역에 "java" 새로 생성후 Stack영역의 참조변수 b가 그 값을 참조
                               //매번 객체를 새로 생성했기 때문에 참조하는 주소값이 다르다.
```

### String, StringBuilder, StringBuffer

* String은 불변속성, StringBuilder, StringBuffer은 가변 속성이다.

* 문자열 연산을 할 경우 String은 불변이기에 새로운 메모리영역을 생성후 가리키게 되고 기존의 영역은 
Garbage로 남아있다가 GC(Garbage Collection)에 의해 사라진다.

```
String str = "Java";
str = str + "Python"; // str이 참조하고 있는 값이 JavaPython으로 바뀐게 아니라
                      // JavaPython 값을 새로 생성후 이 주소를 str이 참조하고 기존 Java가 저장된 메모리 영역은 GC에 의해 사라지게 된다.
```

* 그렇기에 문자열 추가, 수정, 삭제 등 연산이 빈번하게 발생하는 알고리즘에 String 클래스를 사용하면 heap 영역에 많은 가비지가 생성되어 힙메모리 부족으로 성능에 부정적인 영향을 끼치게 된다.

* 이를 해결하기 위해 가변성을 가지는 StringBuilder, StringBuffer 클래스가 있다.

* 이들은 동일 객체 내에서 문자열을 변경하는것이 가능하다.

```
StringBuffer sb = new StringBuffer("Java"); // heap메모리에 "java"값 생성후 stack메모리의 참조변수 sb가 참조
sb.append("Python); // sb가 참조하고 있는 heap메모리 객체에 저장된 "Java" 가 "JavaPython"으로 변경 
```

### StringBuilder, StringBuffer

* StringBuffer는 동기화를 지원해서 멀티쓰레드 환경에서 안전하다.

* StringBuilder는 동기화를 지원하지 않기 때문에 멀티쓰레드 환경에서 사용하는 것은 적합하지 않다. 

* 하지만 동기화를 고려하지 않는 많큼 단일쓰레드에서의 성능은 StringBuffer보다 뛰어나다.
