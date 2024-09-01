### sql 실행 순서

* FROM, JOIN -> WHERE -> GROUP BY -> HAVING -> SELECT -> ORDER BY 순서로 실행된다.

* FROM, JOIN - 조회할 테이블을 지정하고 JOIN으로 가상 테이블로 결합한다.

* WHERE - 조건에 맞는 데이터를 필터링한다.

* GROUP BY - 선택한 컬럼을 기준으로 그룹핑 한다.

* HAVING - 그룹핑 후에 조건을 줘서 필터링한다.

* SELECT - 위 조건들을 거쳐 필터링 된 데이터에서 출력할 열을 선택한다.

* ORDER BY - 출력할 열들의 행을 어떻게 정렬한다.

* LIMIT - 몇개의 결과 행을 보여줄지 선택한다.
