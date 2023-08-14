### Join

* 여러 테이블을 연결해서 데이터를 가져오는 방법

* inner join, left join, right join, full outer join, cross join이 있다.

#### Inner Join

* 두 테이블의 조인된 열에서 일치하는 값이 있는 두 테이블의 행을 반환한다.

* 일치하지 않는 행은 출력하지 않는다.

```
select * 
from 테이블1 join 테이블2          // join 키워드로 연결할 두 테이블을 입력한다.
on 테이블1.컬럼3 = 테이블2.컬럼3    // on 키워드 뒤에 연결 조건 표현식을 지정한다.
order by 테이블1.컬럼3;

select * 
from 테이블1 join 테이블2          
using(컬럼3)             // 사용하는 열 이름이 동일할 경우 using(조인 조건 컬럼)으로 간편하게 출력할 수 있고 이때는 조인 열 이름이 한번만 출력된다.              
order by 테이블1.컬럼3
```

#### Left Join

* 왼쪽 테이블의 행을 모두 출력하고 왼쪽 테이블을 기준으로 오른쪽 테이블에서 일치하는 값을 가진 행을 연결해 출력한다.

* 왼쪽 테이블과 일치하는 오른쪽 테이블의 열이 없다면 해당 오른쪽 행만 null로 출력한다.

* 오른쪽 테이블에만 있는 값은 표시하지 않는다.

```
SELECT *
FROM 테이블1 LEFT JOIN 테이블2          // left join 키워드를 사용한다.
ON district_2020.id = district_2035.id      
ORDER BY district_2020.id;

// 결과 예시
id   테이블1                 id  테이블2
1	 Oak Street School	    1 	 Oak Street School
2	 Roosevelt High School	2	 Roosevelt High School
3	 Dover Middle School	null null
4	 Webutuck High School	4	 Webutuck High School

// 왼쪽 테이블은 모두 출력한다.
// 왼쪽 테이블과 join 기준 열이 일치하는 오른쪽 테이블의 행이 출력된다.
// 왼쪽 테이블과 join 기준 열이 일치하는 오른쪽 테이블의 행이 없다면 null로 연결되어 출력된다.
// 오른쪽 테이블에만 있는 값은 출력되지 않는다.  

// Inner Join과 마찬가지로 열 이름이 같다면 on 대신 using()을 사용할 수 있다.
```

#### Right Join

* 오른쪽 테이블의 행을 모두 출력하고 오른쪽 테이블을 기준으로 왼쪽 테이블과 일치하는 값을 가진 행을 연결해 출력한다.

* 오른쪽 테이블과 일치하는 왼쪽 테이블의 열이 없다면 해당 왼쪽 행만 null로 출력한다.

* 왼쪽 테이블에만 있는 값은 표시하지 않는다.

```
SELECT *
FROM 테이블1 RIGHT JOIN 테이블2           // right join 키워드를 사용한다.
ON district_2020.id = district_2035.id
ORDER BY district_2035.id;

// 결과 예시
id   테이블1                  id  테이블2
1	 Oak Street School	      1	  Oak Street School
2	 Roosevelt High School	  2	  Roosevelt High School
null null		              3	  Morrison Elementary
null null		              4	  Chase Magnet Academy
6	 Webutuck High School	  6	  Webutuck High School

// Inner Join과 마찬가지로 열 이름이 같다면 on 대신 using()을 사용할 수 있다.
```


#### Full Outer Join

* join테이블의 모든 행을 출력한다.

* 한쪽에만 있는 행도 출력한다.

```
SELECT *
FROM 테이블1 FULL OUTER JOIN 테이블2     // full outer join 키워드를 사용한다.
ON district_2020.id = district_2035.id
ORDER BY district_2020.id;

// 결과 예시
id  테이블1                 id  테이블2
1	Oak Street School	    1	Oak Street School
2	Roosevelt High School	2	Roosevelt High School
5	Dover Middle School		
6	Webutuck High School	6	Webutuck High School
		                    4	Chase Magnet Academy
		                    3	Morrison Elementary

// 양쪽 테이블의 모든 행을 출력한다.
// 왼쪽 테이블의 행들과 일치하는 행을 먼저 출력하고 그다음 오른쪽 테이블에만 있는 행을 출력한다. 
// 일치하는 열이 없는 쪽은 null로 출력한다.
// left / rigth join과 달리 한쪽에만 있는 행도 출력한다.
```

#### Cross Join

* 연결 대상 테이블의 모든 행 조합을 출력한다.

```
SELECT *
FROM 테이블1 CROSS JOIN 테이블2                 // 일치하는 항목을 찾을 필요가 없기 때문에 on을 사용하지 않는다.
ORDER BY district_2020.id, district_2035.id;
```
