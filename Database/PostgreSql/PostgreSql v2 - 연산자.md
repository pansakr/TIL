### 연산자 기호

* +, -, *, /, %  - 더하기, 빼기, 곱하기, 나눈 값의 몫, 나눈 값의 나머지

* ^ 지수화 - 앞 숫자를 뒤 숫자만큼 제곱할 수 있다. 3^4는 3의 4제곱

* |/, sqrt(n) 제곱근 - 기호 뒤 숫자의 제곱근 |/ 10 = 10의 제곱근 3.1622...

* ||/ 세제곱근 - 기호 뒤 숫자의 세제곱근

* factorial(n), n! 팩토리얼 - 1부터 n까지 정수의 곱

*제곱근 - 제곱해서 a가 되는 실수를 a의 제곱근이라 한다. 9의 제곱근은 3, -3이다.

*세제곱근 - 세제곱해서 a가 되는 실수

*팩토리얼 - 1부터 어떤 양의 정수 n까지의 정수를 모두 곱한 것. n!로 나타낸다.

```
SELECT 3 ^ 4;         // 지수  3의 4제곱
SELECT |/ 10;         // 제곱근  10의 제곱근
SELECT sqrt(10);      // 제곱근  ''
SELECT ||/ 10;        // 세제곱근  10의 세제곱근
SELECT factorial(4);  // 팩토리얼  
SELECT 4 !;           // 팩토리얼
```

* 연산의 순서는 지수와 근 -> 곱하기, 나누기 -> 더하기 빼기 이고 후순위의 연산을 먼저 실행하려면 ()안에 작성해줘야 한다.


### round()

* round(식, 표시할 소수점 이하 자리수(표시할 자리수 이하의 수들은 반올림한다.))

```
18 / 153 = 0.11764705....

// 정수로 나누면 몫이 0으로 나오니 둘 중 하나를 실수로 바꿔준다.
round(18::numeric / 153, 2); // 18 / 153 의 결과를 소수점 2자리까지 반올림한다.
결과 = 0.12
```

### sum(), avg()

* 행의 합, 평균 구하는 함수

```
SELECT sum(컬럼이름) AS 별칭1,
       round(avg(컬럼이름), 0) AS 별칭2
FROM 테이블이름;
```

### 중앙 값을 나타내는 함수

* 어떤 그룹의 평균이 아닌 중앙값을 나타낸다.

* 평균을 구할 때 차이가 큰 값이 하나 있으면 평균을 왜곡하기 때문에 이때는 중앙 값으로 평균을 찾는다.

* percentile_cont(백분위수) WITHIN GROUP(ORDER BY 컬럼) - 집합 내 숫자의 중앙값을 출력한다. 숫자 그룹이 짝수일 경우 짝수 사이의 소수점을 출력한다.

* percentile_disc(백분위수) - 집합 내 숫자의 중앙값을 출력한다. 앞에서부터 50% 범위에 속하는 부분 중 마지막 값을 출력한다.

```
numbers 컬럼 - 1,2,3,4,5,6

SELECT
    percentile_cont(.5) // .5로 중앙값인 50번째 백분위를 입력한다.
    WITHIN GROUP (ORDER BY numbers), // 1 ~ 6 중앙인 3, 4 의 중앙값 3.5가 출력된다.
    percentile_disc(.5)
    WITHIN GROUP (ORDER BY numbers) // 1 ~ 6 에서 앞에서부터 50%인 1,2,3의 마지막 값 3을 출력한다.
FROM percentile_test;

      percentile_cont  percentile_disc
결과 -            3.5                3
```

### 배열과 unnset()

* ARRAY[백분위수 여러개] - ARRAY[] 함수에 여러개를 집어넣어 한번에 여러 백분위 값을 구할 수 있다.

* unnset() - 배열을 행으로 바꾼다.

*백분위 - 특정 집단의 점수 분포에서 상대적 위치를 나타내는 점수,방법

```
SELECT percentile_cont(ARRAY[.25,.5,.75])  // .25, .5, .75 의 백분위를 한꺼번에 검색한다.
       WITHIN GROUP (ORDER BY pop_est_2019) AS quartiles
FROM us_counties_pop_est_2019;

결과 - {10902.5,25726.0,68072.75}


SELECT unnest(   // 배열을 행으로 바꿔준다.
            percentile_cont(ARRAY[.25,.5,.75])
            WITHIN GROUP (ORDER BY pop_est_2019)
            ) AS quartiles
FROM us_counties_pop_est_2019;

결과 - 10902.5
       25726
       68072.75
```
