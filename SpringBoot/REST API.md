### rest

* HTTP URI을 통해 자원(Resource)을 명시하고, HTTP Method(POST, GET, PUT, DELETE)를 통해 해당 자원에 대한
CRUD Operation을 적용하는 것을 의미한다.

* 자원, 행위, 표현으로 구성되어 있다.

* 자원(resource) - 모든 자원은 고유의 url을 가지며 클라이언트는 이 url을 지정해 해당 자원에 대해 crud명령을 내릴 수 있다.

* 행위 - http method(GET,POST,PUT,DELETE)를 이용하여 자원을 조작하는것

* 표현 - 서버의 행위에 대한 응답(json, xml)

### rest api

* rest 기반으로 서비스 api를 구현한 것

### rest api설계 규칙

* url에서 자원은 명사, 소문자, 복수 명사를 사용해야 한다.
* ex) GET/Member/1 -> GET/members/1
* 자원에 대한 행위는 http method로 표현한다.
* url에 http method가 들어가면 안된다.
* ex) GET/members/delete/1 -> DELETE/members/1
* url에 행위에 대한 동사 표현이 들어가면 안된다. crud 기능을 나타내는 것은 url에 사용하지 않는다.
* ex) GET/members/show/1 -> GET/members/1
* ex) GET/members/insert/2 -> POST/members/2
* 경로 부분 중 변하는 부분은 유일한 값으로 대체한다.
* ex) student를 생성하는 route: POST /students 
* ex) id=12인 student를 삭제하는 route: DELETE /students/12


### @pathVariable

* 스프링에서 url로 넘어온 자원을 파라미터로 받을때 사용한다.

```
// 서버로부터 요청 url이 members/15 로 왔을때

@postMapping("/members/{id}") //15가 {id} 에 맵핑된다.
public 반환타입 메서드이름(@pathVariable(value = "id") int id)
```

