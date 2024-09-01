### count()

* 행의 개수나 숫자를 셀 때 사용한다.

```
// 테이블의 모든 행의 개수를 출력한다.
SELECT count(*)
FROM 테이블이름;

// 컬럼의 null이 아닌 행의 개수를 출력한다. 
SELECT count(컬럼이름)
FROM 테이블이름;

// 컬럼의 null이 아니고 중복을 제거한 행의 개수를 출력한다.
SELECT count(DISTINCT libname)
FROM pls_fy2018_libraries;
```

### max(), min()

* 열의 최대값, 최소값을 구할때 사용한다.

```
SELECT max(컬럼이름), min(컬럼이름)
FROM 테이블 이름;
```

### group by

<img src ="https://github.com/pansakr/TIL/assets/118809108/fa08ee1d-dff0-4067-a4fa-c90a4cfd43e5">

* group by 대상 컬럽을 기준으로 그룹을 나누고, 나눈 그룹별로 집계 연산을 수행 후 결과를 하나의 테이블에 모은다.

* 그렇기 때문에 group by 사용 시 select 의 모든 열은 집계 함수가 되거나 group by에 사용되어야 한다.

* 일반 컬럼 사용 시 그룹별로 나뉘었을때 그룹 기준 컬럼별 데이터가 여러개인데 최종 결과에는 그룹별로 하나의 데이터만 와야 하기 때문에 오류가 난다

* group by에 여러 컬럼을 지정할 시 첫번째 컬럼을 기준으로 그룹지어주고, 그룹 지어진 데이터를 그다음 컬럼으로 그룹 지어준다.

```
// 테이블1의 행
컬럼1   컬럼2
1       a
1       b  
1       a
2       q
2       j
3       po
3       po
3       po

// 여러 컬럼을 그룹 지어줄 때
SELECT 컬럼1, 컬럼2, count(*)
FROM 테이블1
GROUP BY 컬럼1, 컬럼2
ORDER BY 컬럼1;

// 결과
컬럼1   컬럼2   count(*)
1       a      2
1       b      1
2       q      1
2       j      1
3       po     3

// GROUP BY의 첫번째 옵션인 컬럼1의 데이터를 기준으로 두번째 옵션인 컬럼2를 그룹 지어준다.  
// 1에 해당하는 컬럼2의 값은 a, b, a가 있고 중복 데이터인 a는 그룹 지어줘서 1에 대한 컬럼2는 a, b가 된다.
// select에 count()를 사용했을 경우 GROUP BY 조건으로 그룹 지어진 행의 개수를 출력한다.
```

* JOIN과 WHERE 같이 사용하기
```
SELECT sum(t1.cou3) AS t1_cou,             // 순서3
       sum(t2.cou3) AS t2_cou,
       sum(t3.cou3) AS t3_cou
FROM tab1 t1                               // 순서1
       JOIN tab2 c2 ON t1.cou5 = t2.cou5   
       JOIN tab3 c3 ON t1.cou5 = t3.cou5     
WHERE t1.cou3 >= 0                         // 순서2
       AND t2.cou3 >= 0
       AND t3.cou3 >= 0;
```

* join으로 테이블을 연결하고 where문으로 필터링해준다. 

* 이후 필터링된 데이터들 중 t1, t2, t3 컬럼을 선택해서 sum으로 내부 데이터들을 모두 더해준 후 출력한다.


### having

* group by로 만들어진 그룹에 조건을 걸어 필터링한다.

* where로 먼저 필터링되고 group by로 그룹지은 데이터에 사용할 수 있다.
```
select 컬럼
from.. 테이블 join 테이블
where  조건식
group by 그룹지을 컬럼
having 그룹지은 데이터를 필터링할 조건식
order by 정렬
```

### rank(), dense_rank()

* 컬럼의 순위를 매겨준다.

```
SELECT
    컬럼1,
    컬럼2,
    rank() OVER (ORDER BY 컬럼2 DESC),
    dense_rank() OVER (ORDER BY 컬럼2 DESC)
FROM 테이블;

// rank, dense_rank 뒤에 over 키워드를 사용하고 ()안에 순위를 매길 컬럼을 적는다.
// 대상 컬럼을 오름차순 또는 내림차순으로 정렬하여 순위를 매긴다.
// rank()는 중복 값들을 동일 순위로 표시하고 중복 다음 값의 순위는 중복 개수만큼 떨어진 순위로 표시한다.
// dense_rank()는 중복 값들을 동일 순위로 표시하고 중복 다음 값의 순위는 중복 개수와 상관없이 순차적인 순위 값으로 표시한다.
```

### PARTITION BY

* 어떤 컬럼의 데이터 내에서 데이터 그룹별로 순위를 매길때 사용한다.

* rank(), dense_rank() 의 () 내부에 사용한다.

```
SELECT
    컬럼1,
    컬럼2,
    컬럼3,
    rank() OVER (PARTITION BY 컬럼1 ORDER BY 컬럼2 DESC)
FROM 테이블1

// 컬럼 1의 데이터를 같은 데이터들끼리 그룹짓고, 행들의 그룹마다 컬럼2의 데이터를 기준으로 역순으로 순위를 매긴다.

// 컬럼 2에 대한 하나의 순위가 생기는것이 아니라 그룹1의 순위, 2의 순위, 3의 순위.. 로 출력 결과에 여러 순위 그룹이 출력된다.
```
