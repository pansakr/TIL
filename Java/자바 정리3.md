

### 캡슐화(Encapsulation)

* 외부에서 내부의 내용을 숨겨 데이터를 보호하고 다른 객체의 접근을 제한하는 것

* 멤버 변수들을 private로 설정하고 값을 넣고 가져오는 getter, setter 메서드로 접근한다.


### LomBok

* 해당하는 어노테이션을 사용하면 getter, setter, toString, 생성자를 자동 생성해준다.

#### @Getter

#### @Setter

#### @ToString 

* Object 클래스의 toString() 메소드를 자동 오버라이딩해준다.

#### @RequiredArgsConstructor 

* final이나 @NonNull 이 붙은 멤버 변수를 매개변수로 받는 생성자 생성
 
#### @Data 

* @Getter, @Setter, @ToString, @RequiredArgsConstructor, @EqualsAndHashCode 합쳐진 어노테이션

#### @Builder

* 클래스 내에 매개 변수의 개수에 따른 여러개의 생성자를 만들 수 있는데

* 모든 멤버 변수를 넣은 생성자들 만들고 @Bulider 어노테이션을 붙이면 

* 객체 생성시 빌더 패턴으로 생성자를 사용할 수 있다.
```
 Class Person2
   private int no;
   private String name;
   private String phone;

 @Bulider
 public Person2(int no, String name, String phone){
   this.no = no;
   this.name = name;
   this.phone = phone;
 }
   
 
 class sample01{

   public static void main(String[] args){

     //Persion2 per1 = new Persion2(1);
     Person2 per1 = Person.builder().no(1).bulid();

     //Persion2 per1 = new Persion2(2, "Jin");
     Person2 per2 = Person.builder().no(2).name("Jin")bulid();

     //Persion2 per1 = new Persion2(3, "Kain", "010-1111-2222");
     Person2 per1 = Person.builder().no(3).name.("Kain").phone("010-1111-2222").bulider();
   } 
 }
 ```
