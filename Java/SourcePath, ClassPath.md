### 소스패스

* .java 파일을 컴파일할 때 컴파일러가 참조할 경로

* 소스패스는 /src 처럼 소스 파일의 최상위 디렉토리만 가리키면 컴파일러가 해당 디렉토리 내의 소스파일을 찾는다 (컴파일은 안함)

* src/../xx.java 컴파일할 파일의 경로를 지정하면 해당 파일을 컴파일한다

```
// MyClass.java 하나만 컴파일
javac -sourcepath src -d out src/com/example/MyClass.java

javac - 자바 컴파일러
-sourcepath src : src 디렉토리를 소스패스로 지정
-d out : 컴파일된 클래스 파일을 out/ 디렉토리에 저장
```
```
// src/com/example 하위의 .java 파일들 모두 컴파일
javac -sourcepath src -d out src/com/example/*.java
```

* IDE 사용 시 소스패스를 자동으로 설정하고 모든 .java 파일을 컴파일하도록 설정한다

### 클래스패스

* JVM이 자바 프로그램을 실행할 때 필요한 클래스 파일들을 찾는 경로

* 프로그램 실행 시 JVM은 클래스패스에서 클래스 파일을 찾아 메모리에 로드

```
// 패키지 경로
// Main 은 HelperClass, UtilityClass 를 사용한다
/out
    /com
        /example
            MainClass.class
            HelperClass.class
    /org
        /utils
            UtilityClass.class

// 클래스 패스 명령어
java -cp out com.example.MainClass

-cp out : 클래스패스를 out 디렉토리로 설정
```

*  JVM은 com.example.MainClass를 실행하며, 필요한 다른 클래스 (HelperClass, UtilityClass 등)를 out 디렉토리에서 자동으로 찾는다

* 클래스 패스도 IDE에서 자동으로 설정해준다
