### 인덱스

* 테이블의 검색 속도를 높여주는 객체, 자료구조

* 데이터를 찾기 위해 전체를 스캔하는 대신 인덱스를 바로 가기로 사용한다.

* 기본 키나 unique 제약조건이 포함된 열에 인덱스가 자동 생성된다.

* postgresql 에서만 사용되는 EXPLAIN ANALYZ 키워드로 데이터 검색 속도를 측정할 수 있다.

```
// 검색 속도 측정
EXPLAIN ANALYZE SELECT * FROM new_york_addresses
WHERE street = 'BROADWAY';

// 결과
Gather  (cost=1000.00..15196.68 rows=3229 width=46) (actual time=0.385..163.689 rows=3336 loops=1)
  Workers Planned: 2
  Workers Launched: 2
  ->  Parallel Seq Scan on new_york_addresses  (cost=0.00..13873.78 rows=1345 width=46) (actual time=0.006..33.653 rows=1112 loops=3) 
        Filter: (street = 'BROADWAY'::text)
        Rows Removed by Filter: 312346
Planning Time: 0.104 ms
Execution Time: 163.832 ms  // 검색에 걸린 시간 163.832 밀리초
```

* 컬럼에 인덱스를 추가할 수 있다.

```
// 인덱스 추가 명령어
CREATE INDEX 인덱스이름 ON 인덱스를 추가할 테이블 이름 (열 이름);

CREATE INDEX street_idx ON new_york_addresses (street);
```

* 인덱스 추가한 열을 검색할시 속도가 매우 빨라진다.

```
Bitmap Heap Scan on new_york_addresses  (cost=37.45..6491.70 rows=3229 width=46) (actual time=0.540..2.078 rows=3336 loops=1)
  Recheck Cond: (street = 'BROADWAY'::text)
  Heap Blocks: exact=2157
  ->  Bitmap Index Scan on street_idx  (cost=0.00..36.64 rows=3229 width=0) (actual time=0.324..0.324 rows=3336 loops=1) // Index Scan으로 인덱스를 사용해 데이터를 검색했음을 알 수 있다.
        Index Cond: (street = 'BROADWAY'::text)
Planning Time: 0.094 ms
Execution Time: 2.236 ms // 기존 163.832 ms에 비해 98% 이상 빨라졌다.
```

* 인덱스를 무분별하게 추가하면 db의 크기가 커지고 성능 부하가 걸리기 때문에 필요한 열에만 추가해야 한다.
