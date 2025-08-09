### breake문
``
break;
``
* 브레이크 만나는 순간 반복문 전체 탈출

### contiune문
```
countiune;
```
* 이후 실행문을 실행하지 않고 다음 반복으로 넘어간다
```
 for(int i=1; i<5; i++){
            if(i==3) continue;     // 3이 되면 컨티뉴에 의해 이후 출력문을 실행하지 않고 다음 반복으로 넘어간다
            System.out.println(i);
        }
```
* 컨티뉴 만나는 순간 이후 실행문 실행하지 않고 다음 반복으로 넘어감


###향상된 for문
```
 for(자료형 변수명 : 배열명){
   실행문
 }
```
```
 String[] numbers = {"one", "two", "three"}

 for(String number : numbers){
            System.out.println(number);
        }
```
* 오른쪽에 배열을 배치하고, 왼쪽에는 오른쪽에 배치한 배열의 타입과 변수명을 적어준다

* 배열에 저장되있는 값들을 하나씩 왼쪽의 변수에 담을때마다 반복문을 1번씩 실행하고, 배열에 값이 더이상 없으면 반복문을 종료한다


### while 문
```
 초기식
 while(조건식){
    실행문
 }
```
* 조건식이 true일 경우 무한 반복, false이면 종료
```
 int i = 0; // 초기식

 while(i <= 10){
    System.out.println(i); // 실행문
    i++; // 증감식
 }
```
* 초기식은 필요하면 만들고, 증감식은 실행문 작성 영역에 작성한다

### do ~while문
```
 do{
 실행문 
 } while(조건식)
```
* do의 실행문을 먼저 실행하고 while의 조건식이 false면 종료, true면 do의 실행문을 다시 실행한다

### switch ~ case문
```
 switch(변수 or 연산식){
   case 비교값1 : 실행문1
                      break;
   case 비교값2 : 실행문2
                      break;
   default : 실행문4
 }
```
* swith문에 변수나 연산식이 들어오면 그 값과 case의 비교값을 비교해서 같으면 실행문이 실행한다

* 다만 실행문을 실행하고 break 문을 만날때까지 다른 case의 실행문도 실행하기 때문에 case의 끝에는 break를 사용해줘야 한다

* swith문의 조건과 일치하는 비교값이 없으면 default의 실행문을 실행한다
```
 int a = 5
 
 switch (a){
 
    case 10: System.out.println(10); 
               break;
    case 9: System.out.println(9);
               break;
    case 8: System.out.println(8);
               break;
    default : System.out.println("해당없음");
 }
``` 
