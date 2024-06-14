### 서브쿼리

* 하나의 SQL 문장 내부에 SQL을 사용하는 것 

* () 내부의 쿼리 결과를 외부 쿼리에서 사용하는것

* SELECT, FROM, WHERE, HAVING 에서 사용할 수 있으며 사용 위치에 따라 명칭이 다르다

* SELECT - 스칼라 서브쿼리

* FROM - 인라인 뷰

* WHERE - 일반 서브쿼리, 스칼라 서브쿼리

* HAVING - 일반 서브쿼리

```
// where 조건에 서브쿼리 사용
SELECT 컬럼1,
       컬럼2,
       컬럼3
FROM 테이블1
WHERE 컬럼3 >= (
    SELECT percentile_cont(.9) WITHIN GROUP (ORDER BY 컬럼3)
    FROM 테이블1
    )

// 컬럼 3과 서브쿼리의 결과를 비교한다.
```

* 서브쿼리의 결과를 select에서 사용

```
SELECT round(테이블 별칭.average, 0) as average,
       테이블 별칭.median,
       round(테이블 별칭.average - 테이블 별칭.median, 0) AS median_average_diff
FROM (
     SELECT avg(컬럼1) AS average,  // 컬럼1의 평균
            percentile_cont(.5)    // 컬럼2의 중앙값
                WITHIN GROUP (ORDER BY 컬럼1)::numeric AS median
     FROM 테이블1
     )
AS 테이블 별칭;  // 서브쿼리의 결과에 별칭을 붙여 SELECT에서 해당 별칭으로 결과 컬럼을 호출할 수 있다.
```

* 서브쿼리 결과를 조인하기

```
SELECT 테이블 별칭2.컬럼2-1 AS st,  // 서브쿼리 조인 결과 테이블에서 컬럼 추출
       테이블 별칭2.pop_est_2018,
       테이블 별칭1.establishment_count,
       round((est.establishment_count/census.pop_est_2018::numeric) * 1000, 1)
           AS estabs_per_thousand
FROM
    (                     // 서브쿼리1
         SELECT 컬럼1-1,                           
                sum(컬럼1-2) AS establishment_count
         FROM 테이블1
         GROUP BY 컬럼1-1
    )
    AS 테이블 별칭1
JOIN         
    (                     // 서브쿼리2
        SELECT 컬럼2-1,
               sum(컬럼2-2) AS pop_est_2018
        FROM 테이블2
        GROUP BY 컬럼2-1
    )
    AS 테이블 별칭2
ON 테이블 별칭1.컬럼1-1 = 테이블 별칭2.컬럼2-1  // 서브쿼리 1과 2의 결과를 조인
```

* SELECT에 서브쿼리를 사용해서 열 생성하기

* 서브쿼리는 단일 행만 생성한다.
