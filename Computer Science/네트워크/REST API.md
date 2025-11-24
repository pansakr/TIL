### REST

* 웹 시스템의 설계 방법과 웹에서 자원을 다루는 방식에 대한 규칙

  - 웹이라는 시스템을 어떻게 설계해야 유지보수/확장성이 좋아지는가에 대한 구조적 원칙

* REST 의 6가지 원칙

  - Clinet-Server 구조
 
    - 클라이언트와 서버를 분리해 각자의 역할을 확실히 구분
   
    - 클라이언트는 사용자 인증 등을 제공하고, 서버는 api 제공

    - 프론트 React/Vue, 서버 Spring Boot 같은 구조
   
  - Stateless (무상태)

    - 각 요청은 서버에게 필요한 모든 정보를 포함해야 하며, 서버는 클라이언트의 이전 요청에 대한 상태 정보를 저장하지 않는다

      - 세션, 로그인 정보, 클라이언트 상태 등을 서버에 저장하지 않는다
   
      - 요청에 토큰/자격 증명이 없으면 서버는 그 요청을 '로그인한 사용자' 라고 판단할 수 없으며, 인증을 위해서 JWT/OAuth 토큰 등이 필요함
        
    - HTTP 도 무상태성이며 REST 의 무상태성은 이 특성을 따라 서버에 상태를 저장하지 말라는 원칙
   
  - Cacheable (캐시 가능)
 
    - 클라이언트는 서버 응답을 캐싱할 수 있어야 한다
   
      - 서버는 매 응답마다 이 응답이 캐시 가능한지, 얼마동안 가능한지, 캐시해도 되는 조건은 무엇인지 헤더에 제공해야 한다
   
      - 클라이언트가 캐싱을 하게 되면 서버 부하 감소, 응답 속도 증가 등의 이점이 있다
     
  - Uniform interface (일관된 인터페이스)

    - URL 은 자원, HTTP 메서드는 자원에 대한 행동을 나타내야 한다
   
  - Layered System (계층화)
 
    - REST 서버는 보안, 로드 밸런싱 등을 추가해 여러 계층으로 구성될 수 있으며, 클라이언트는 서버의 구조와 관계 없이 동일하게 동작해야 하는 원칙
   
    - 클라이언트 입장에서는 클라이언트 서버 -> API 서버로 바로 연결되는것 처럼 보임
   
    - 실제로는 클라이언트 서버 -> 캐시 서버 -> 로드 밸런서 -> 인증 서버 -> 실제 API 서버 와 같이 여러 계층을 거친 뒤 도착
   
  - Code on Demand (코드 온디맨드) - 선택적 원칙
 
    - 필요한 경우 서버는 클라이언트에게 실행 가능을 전송할 수 있어야 한다
   
    - REST 서버는 일반적으로 정적 리소스를 전송하지만, 특정 요청 시 응답에 실행 코드를 포함할 수 있어야 한다

* HTTP URI을 통해 자원(Resource)을 명시하고, HTTP Method(POST, GET, PUT, DELETE)를 통해 해당 자원에 대한
CRUD Operation을 적용하는 것을 의미한다.

* 자원, 행위, 표현으로 구성되어 있다.

* 자원(resource) - 모든 자원은 고유의 url을 가지며 클라이언트는 이 url을 지정해 해당 자원에 대해 crud명령을 내릴 수 있다.

* 행위 - http method(GET,POST,PUT,DELETE)를 이용하여 자원을 조작하는것

* 표현 - 서버의 행위에 대한 응답(json, xml)

### REST API

* REST 설계 규칙에 따라 만든 API

* restapi와 비동기 요청은 다른것!

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

