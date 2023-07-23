### Iterator

* ArrayList, HastSet과 같은 컬렉션을 순회,반복하는데 사용하는 객체. 반복자라고 한다.

* 인덱스가 없는 자료구조의 경우 안의 데이터를 순회하기 위해 Iterator, for문을 사용한다.

* 모든 컬렉션 프레임워크에 공통으로 사용 가능

```
//list 생성
 ArrayList<String> cars = new ArrayList<>();
        cars.add("벤츠");
        cars.add("람보르기니");
        cars.add("롤스로이스");
        cars.add("페라리");

        // iterator 획득
        Iterator<String> iterator = cars.iterator();

        // while문을 사용한 경우
        while(iterator.hasNext())
        {
            String str = iterator.next();
            System.out.println(str);
        }

        // for문을 사용한 경우
        for (String car : cars)
        {
            System.out.println("cars : " + car);
        }

// >> 벤츠
// >> 람보르기니
// >> 롤스로이스
// >> 페라리
```
