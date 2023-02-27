```
@Column(nullable = false)
```
* null값이 넘어와도 정상적으로 수행되다가 db쪽으로 쿼리가 도착하면 nullable = false 옵션에 의해 예외가 발생한다.

```
@NotNull
```
* DB로 넘어가기 전, null값이 넘어오는 순간 예외가 발생한다.

* 즉 @NotNull 어노테이션이 더 빠르게 예외를 감지하기 때문에 더 안전하다.
