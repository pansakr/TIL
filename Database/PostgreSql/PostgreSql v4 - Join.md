### Join

* 여러 테이블을 연결해서 데이터를 가져오는 방법

* inner join, left join, right join, full outer join, cross join이 있다

#### Inner Join

* 두 테이블의 조인된 열에서 일치하는 값이 있는 두 테이블의 행을 반환한다

* 일치하지 않는 행은 출력하지 않는다

```
select * 
from 테이블1 join 테이블2          // join 키워드로 연결할 두 테이블을 입력한다
on 테이블1.컬럼3 = 테이블2.컬럼3    // on 키워드 뒤에 연결 조건 표현식을 지정한다
order by 테이블1.컬럼3;

select * 
from 테이블1 join 테이블2          
using(컬럼3)             // 사용하는 열 이름이 동일할 경우 using(조인 조건 컬럼)으로 간편하게 출력할 수 있고 이때는 조인 열 이름이 한번만 출력된다            
order by 테이블1.컬럼3
```

#### Left Join

* 왼쪽 테이블의 행을 모두 출력하고 왼쪽 테이블을 기준으로 오른쪽 테이블에서 일치하는 값을 가진 행을 연결해 출력한다

* 왼쪽 테이블과 일치하는 오른쪽 테이블의 열이 없다면 해당 오른쪽 행만 null로 출력한다

* 오른쪽 테이블에만 있는 값은 표시하지 않는다

```
SELECT *
FROM 테이블1 LEFT JOIN 테이블2          // left join 키워드를 사용한다
ON district_2020.id = district_2035.id // district_2020 (테이블 1), district_2035 (테이블 2)
ORDER BY district_2020.id;

// 결과 예시
id   테이블1                  id   테이블2
1    Oak Street School	      1    Oak Street School
2    Roosevelt High School    2	   Roosevelt High School
3    Dover Middle School      null null
4    Webutuck High School     4	   Webutuck High School

// 왼쪽 테이블은 모두 출력한다
// 왼쪽 테이블과 join 기준 열이 일치하는 오른쪽 테이블의 행이 출력된다
// 왼쪽 테이블과 join 기준 열이 일치하는 오른쪽 테이블의 행이 없다면 null로 연결되어 출력된다
// 오른쪽 테이블에만 있는 값은 출력되지 않는다

// Inner Join과 마찬가지로 열 이름이 같다면 on 대신 using()을 사용할 수 있다
```

#### Right Join

* 오른쪽 테이블의 행을 모두 출력하고 오른쪽 테이블을 기준으로 왼쪽 테이블과 일치하는 값을 가진 행을 연결해 출력한다

* 오른쪽 테이블과 일치하는 왼쪽 테이블의 열이 없다면 해당 왼쪽 행만 null로 출력한다

* 왼쪽 테이블에만 있는 값은 표시하지 않는다

```
SELECT *
FROM 테이블1 RIGHT JOIN 테이블2           // right join 키워드를 사용한다
ON district_2020.id = district_2035.id   // district_2020 (테이블 1), district_2035 (테이블 2)
ORDER BY district_2035.id;

// 결과 예시
id   테이블1                  id  테이블2
1    Oak Street School        1   Oak Street School
2    Roosevelt High School    2   Roosevelt High School
null null                     3   Morrison Elementary
null null                     4   Chase Magnet Academy
6    Webutuck High School     6   Webutuck High School

// Inner Join과 마찬가지로 열 이름이 같다면 on 대신 using()을 사용할 수 있다
```


#### Full Outer Join

* join테이블의 모든 행을 출력한다

* 한쪽에만 있는 행도 출력한다

```
SELECT *
FROM 테이블1 FULL OUTER JOIN 테이블2     // full outer join 키워드를 사용한다
ON district_2020.id = district_2035.id  // district_2020 (테이블 1), district_2035 (테이블 2)
ORDER BY district_2020.id;

// 결과 예시
id  테이블1                 id  테이블2
1   Oak Street School	    1	Oak Street School
2   Roosevelt High School   2	Roosevelt High School
5   Dover Middle School		
6   Webutuck High School    6	Webutuck High School
                            4	Chase Magnet Academy
                            3	Morrison Elementary

// 조인된 열을 먼저 출력한다
// 왼쪽 테이블에만 있는 행, 오른쪽 테이블에만 있는 행을 출력한다
// left / rigth join과 달리 한쪽에만 있는 행도 출력한다.
```

#### Cross Join

* 연결 대상 테이블의 모든 행 조합을 출력한다

```
SELECT *
FROM 테이블1 CROSS JOIN 테이블2                 // 일치하는 항목을 찾을 필요가 없기 때문에 on을 사용하지 않는다
ORDER BY district_2020.id, district_2035.id;   // district_2020 (테이블 1), district_2035 (테이블 2)
```

### is null

* 조인 결과에 null이 포함된 것만 출력할 수 있다

```
SELECT *
FROM 테이블1 LEFT JOIN 테이블2
ON 테이블1.id = 테이블2.id
WHERE 테이블2.id IS NULL;  // 테이블1, 테이블2 조인한 결과 중 null이 포함된 결과만 출력
```

### 테이블 별칭

```
SELECT 별칭1.id,              // 별칭으로 해당 테이블과 속한 컬럼들을 호출할 수 있다
       별칭1.school_2020,
       별칭2.school_2035
FROM 테이블 AS 별칭1 LEFT JOIN 테이블2 AS 별칭2   // 테이블 이름 뒤 as 별칭이름으로 별칭을 설정해준다
ON 별칭1.id = 별칭2.id
ORDER BY 별칭1.id;
```

### 여러 테이블 join

* 3개 이상의 테이블을 조인하는 방법

```
SELECT 별칭1.id,
       별칭1.school_2020,
       별칭2.enrollment,
       별칭3.grades
FROM 테이블1 AS 별칭1 JOIN 테이블2 AS 별칭2  // 테이블1과 테이블2를 id값이 일치하는 행만 연결해준다
    ON 별칭1.id = 별칭2.id
JOIN 테이블3 AS 별칭3                      // 테이블1과 테이블 3을 id값의 일치하는 행만 연결해준다
    ON 별칭1.id = 별칭3.id
ORDER BY 별칭1.id;
```

### union, union all

* union - 두개의 쿼리 결과를 중복을 제거해 합치고 출력해준다

* union all - 두개의 쿼리 결과를 중복을 포함해 합치고 출력해준다

* 쿼리 결과를 합쳐주기 때문에 두 쿼리 결과의 열 개수와 데이터 타입이 일치해야 한다.

```
SELECT * FROM 테이블1
UNION                  // 중복을 제거해 합쳐준다
SELECT * FROM 테이블2  // 두번째 쿼리의 행의 결과를 첫번째 쿼리의 행에 합쳐준다
ORDER BY id;

// 결과 예시
id  테이블1의 행   // 두번째 쿼리의 결과가 첫번째 쿼리로 합쳐지기 때문에 테이블1의 행이 표시된다
1   데이터1
2	데이터2
3	데이터3

SELECT * FROM 테이블1
UNION all              // 중복을 포함해 합쳐준다
SELECT * FROM 테이블2
```

### intersect

* 두 쿼리에 모두 존재하는 행만 중복을 제거해 출력한다
```
SELECT * FROM 테이블1
intersect             // 두 쿼리 결과에 모두 존재하는 행만 출력하고 중복을 제거한다
SELECT * FROM 테이블2  
ORDER BY id;
```

### except

* 첫번째 쿼리 결과에만 있고 두번째 쿼리 결과에는 없는 행을 출력한다
```
SELECT * FROM 테이블1
except                // 테이블 1에만 있는 결과를 출력한다
SELECT * FROM 테이블2  
ORDER BY id;
```
