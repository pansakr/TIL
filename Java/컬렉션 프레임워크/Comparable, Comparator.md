### Comparable, Comparator

* 객체를 비교할 수 있도록 만든다

	- 정수, 실수 같은 자바 기본형 타입은 부등호로 비교할 수 있지만 참조 타입은 그렇게 비교가 불가능하다

	- 그러나 Comparator<T> 나 Cmparable 인터페이스를 구현해서 객체를 비교할 수 있게 만들수 있다

```
Comparable 인터페이스는 compareTo(T o)를 구현해야 한다

Comparator 인터페이스는 compare(T o1, T o2)를 구현해야 한다
```

* Comparable은 자기 자신과 매개변수 객체를 비교하고, Comparator은 두 매개변수 객체를 비교한다

* Comparable 기본 사용 방법

```
// 자신과(A) Comparable의 제네릭 타입(Type) 을 비교한다
public class A implements Comparable<Type> { 
 
/*
  ...
  code
  ...
 */

	// 필수 구현 부분
	@Override
	public int compareTo(Type o) {
		/*
		 비교 구현
		 */
	}
}
```

```
// 참조 타입인 Student 클래스를 비교하기 위해 Comparable 구현
// 자기 자신과(Student) Comparable의 제네릭 타입인 Student를 비교한다
class Student implements Comparable<Student> {
 
	int age;			// 나이
	int classNumber;	// 학급
	
	Student(int age, int classNumber) {
		this.age = age;
		this.classNumber = classNumber;
	}
	
	@Override
	public int compareTo(Student o) {

        // 이렇게 해도 되지만 리턴 값이 int 표현 범위를 넘어가면 Overflow가 발생한다
        // return this.age - o.age; 

        // <, >, == 으로 대소비교를 해주는 것이 안전하며 일반적으로 권장되는 방식이다
        // 자기자신의 age가 o의 age보다 크다면 양수
		if(this.age > o.age) {
			return 1;
		}
		// 자기 자신의 age와 o의 age가 같다면 0
		else if(this.age == o.age) {
			return 0;
		}
		// 자기 자신의 age가 o의 age보다 작다면 음수
		else {
			return -1;
		}
	}
}

public static void main(String[] args)  {
 
	Student a = new Student(17, 2);	// 17살 2반
	Student b = new Student(18, 1);	// 18살 1반
		
	int isBig = a.compareTo(b);	// a자기자신과 b객체를 비교한다
}
```

* Comparator 기본 사용 방법

```
import java.util.Comparator;	// import 필요
public class ClassName implements Comparator<Type> { 
 
/*
  ...
  code
  ...
 */
 
	// 필수 구현 부분
	@Override
	public int compare(Type o1, Type o2) {
		/*
		 비교 구현
		 */
	}
}
```

```
// 비교할 객체 두개를 인자로 받아 서로를 비교한다
// 두 객체 비교에 있어 자기 자신은 영향이 가지 않는다
class Student implements Comparator<Student> {
 
	int age;			// 나이
	int classNumber;	// 학급
	
	Student(int age, int classNumber) {
		this.age = age;
		this.classNumber = classNumber;
	}
	
	@Override
	public int compare(Student o1, Student o2) {
    
		// o1의 학급이 o2의 학급보다 크다면 양수
		if(o1.classNumber > o2.classNumber) {
			return 1;
		}
		// o1의 학급이 o2의 학급과 같다면 0
		else if(o1.classNumber == o2.classNumber) {
			return 0;
		}
		// o1의 학급이 o2의 학급보다 작다면 음수
		else {
			return -1;
		}

        // 이렇게 할수도 있다
        // return o1.classNumber - o2.classNumber;
	}
}

public static void main(String[] args)  {
 
	Student a = new Student(17, 2);	// 17살 2반
	Student b = new Student(18, 1);	// 18살 1반
	Student c = new Student(15, 3);	// 15살 3반
			
	// a객체와는 상관 없이 b와 c객체를 비교한다
	int isBig = a.compare(b, c);
		
	if(isBig > 0) {
		System.out.println("b객체가 c객체보다 큽니다.");
	}
	else if(isBig == 0) {
		System.out.println("두 객체의 크기가 같습니다.");
	}
	else {
		System.out.println("b객체가 c객체보다 작습니다.");
	}
		
	}
```

* Comparator 활용

* 위에서 Comparator로 두 객체를 a, b, c 중 아무 객체를 사용해서 인자의 두 객체를 비교가 가능했다

* 위의 방법은 일관성이 떨어지기 때문에 익명 클래스를 활용해 비교하는 방법이 있다

```
public class Test {
	public static void main(String[] args) {
    
        Student a = new Student(17, 2);	// 17살 2반
	    Student b = new Student(18, 1);	// 18살 1반
		Student c = new Student(15, 3);	// 15살 3반
			
		// comp 익명객체를 통해 b와 c객체를 비교한다
		int isBig = comp.compare(b, c);
		
		if(isBig > 0) {
			System.out.println("b객체가 c객체보다 큽니다.");
		}
		else if(isBig == 0) {
			System.out.println("두 객체의 크기가 같습니다.");
		}
		else {
			System.out.println("b객체가 c객체보다 작습니다.");
		} 
	
	}
 
	// Comparator 인터페이스를 익명 객체로 구현한다
	public static Comparator<Student> comp2 = new Comparator<Student>() {
		@Override
		public int compare(Student o1, Student o2) {
			return o1.classNumber - o2.classNumber;
		}
	};
}
 
 
// 외부에서 익명 객체로 Comparator가 생성되기 때문에 클래스에서 Comparator을 구현 할 필요가 없어진다
class Student {
 
	int age;			// 나이
	int classNumber;	// 학급
	
	Student(int age, int classNumber) {
		this.age = age;
		this.classNumber = classNumber;
	}
 
}
```

* 객체를 비교하기 위해 Comparable 또는 Comparator을 쓴다는 것은 곧 사용자가 정의한 기준을 토대로 비교를 하여 양수, 0, 음수 중 하나가 반환된다는 것이다


### Comparable, Comparator 와 정렬의 관계

* Java에서의 정렬은 특별한 정의가 되어있지 않는 한 '오름차순'을 기준으로 한다

* {1,3,2} 배열을 정렬한다면 먼저 1,3을 비교해 둘을 차이 값을 반환한다

* -2로 음수가 반환되고 이는 앞 요소가 뒤 요소보다 작다는 뜻이다

* 이는 두 요소가 오름차순이라는 의미이므로 음수가 나온다면 두 원소의 위치를 바꾸지 않는다

* 다음으로 3과 2를 비교하고 값은 1로 양수가 반환된다

* 이 경우 두 요소가 내림차순이라는 의미이므로 두 원소를 바꾼다

* 앞 요소가 뒤 요소보다 작다면 음수가 반환되어 위치를 바꾸지 않고 크다면 양수가 반환되어 위치를 바꾼다

* 즉 특수한 경우를 제외하고 정렬 알고리즘은 두 요소의 비교를 통해 두 원소를 교환할지 말지를 정한다

```
// Comparable로 정렬
public class Test {
	
	public static void main(String[] args) {
		
		MyInteger[] arr = new MyInteger[10];
		
		// 객체 배열 초기화 (랜덤 값으로) 
		for(int i = 0; i < 10; i++) {
			arr[i] = new MyInteger((int)(Math.random() * 100));
		}
 
		// 정렬 이전
		System.out.print("정렬 전 : ");
		for(int i = 0; i < 10; i++) {
			System.out.print(arr[i].value + " ");
		}
		System.out.println();
		
        // MyInteger는 참조 타입이기에 Comparable를 구현하지 않았다면 비교할 수 있는 기준이 없어 정렬이 불가능해 예외가 발생한다
		Arrays.sort(arr);
        
		// 정렬 이후
		System.out.print("정렬 후 : ");
		for(int i = 0; i < 10; i++) {
			System.out.print(arr[i].value + " ");
		}
		System.out.println();
	}
	
}
 
class MyInteger implements Comparable<MyInteger> {
	int value;
	
	public MyInteger(int value) {
		this.value = value;
	}
	
	@Override
	public int compareTo(MyInteger o) {
		return this.value - o.value;
	}
	
}
```

```
// Comparator로 정렬
public class CloseToInt implements Comparator<Integer> {
    
    int closeTo;

    public CloseToInt(int closeTo) {
        this.closeTo = closeTo;
    }

    // o1, o2 위치를 반대로 해서 오름차순의 반대인 내림차순으로 정렬된다
    @Override
    public int compare(Integer o1, Integer o2) {
        return o2 - o1;
    }
}

...main(){

    	Integer[] nums = {3, 8, 1, 7, 4, 9, 2, 6, 5};
        String[] strs = {
                "Fox", "Banana", "Elephant", "Car", "Apple", "Game", "Dice"
        };

		//  Arrays 클래스의 sort 메소드
        //  기본적으로 compareTo에 의거하여 정렬
        //  인자가 없는 생성자로 생성된 TreeSet, TreeMap도 마찬가지
        Arrays.sort(nums);
        Arrays.sort(strs);

        // nums 배열을 IntDescComp에 정의된 기준으로 정렬한다.
        // IntDescComp의 compare가 내림차순으로 정의했기에 nums 배열은 내림차순으로 정렬된다
        Arrays.sort(nums, new IntDescComp());

        // 익명 클래스로 간편하게 정렬 조건을 지정해 사용할 수 있다
        Arrays.sort(strs, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.length() - o2.length();
            }
        });
}
```

### 이터레이터

* 컬렉션 내부 요소들을 순회하는데 사용한다

* 순회하며 요소들을 제거할 수도 있다

* 컬렉션 요소들을 forEach로 제거하면 예외가 발생하기 때문에 이터레이터로 제거해야 한다

```
	Set<Integer> intHSet = new HashSet<>(
        Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)
    );

    // 이터레이터 반환 및 초기화
    // 기타 Collection 인터페이스를 구현한 클래스들에도 존재
    // Set의 제네릭에 Integer를 지정했기 때문에 Iterator도 Integer로만 지정할 수 있다
    Iterator<Integer> intItor = intHSet.iterator();

    // next : 자리를 옮기며 다음 요소 반환
    Integer int1 = intItor.next(); // 1
    Integer int2 = intItor.next(); // 2
    Integer int3 = intItor.next(); // 3

	// hasNext : 순회가 남았는지 여부 반환
    // 3까지 진행했으니 이후 순회 요소들이 남아 true 반환
    boolean hasNext = intItor.hasNext(); // true

    // 순회 초기화. 순회 진행도가 초기화된다
    intItor = intHSet.iterator();
```
