### REST

* 웹 시스템의 설계 방법과 웹에서 자원을 다루는 방식에 대한 규칙

  - 웹이라는 시스템을 어떻게 설계해야 유지보수/확장성이 좋아지는가에 대한 구조적 원칙

* REST 의 6가지 원칙

  - Clinet-Server 구조
 
    - 클라이언트와 서버를 분리해 독립적으로 개발이 가능해야 한다
   
      - 분리되면 테스트, 배포, 유지보수가 좋아짐 
   
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

    - 클라이언트와 서버가 통신할 때 API 는 동일한 형식/규칙을 사용해야 한다
   
    - 자세한 방법은 REST API 디자인 가이드에서 설명
   
  - Layered System (계층화)
 
    - REST 서버는 보안, 로드 밸런싱 등을 추가해 여러 계층으로 구성될 수 있으며, 클라이언트는 서버의 구조와 관계 없이 동일하게 동작해야 한다
   
    - 클라이언트 입장에서는 클라이언트 서버 -> API 서버로 바로 연결되는것 처럼 보임
   
    - 실제로는 클라이언트 서버 -> 캐시 서버 -> 로드 밸런서 -> 인증 서버 -> 실제 API 서버 와 같이 여러 계층을 거친 뒤 도착
   
  - Code on Demand (코드 온디맨드) - 선택적 원칙
 
    - 필요한 경우 서버는 클라이언트에게 실행 가능을 전송할 수 있어야 한다
   
    - REST 서버는 일반적으로 정적 리소스를 전송하지만, 특정 요청 시 응답에 실행 코드를 포함할 수 있어야 한다
   
    - 거의 사용하지 않음

### REST API

* REST 설계 규칙에 따라 만든 API

* REST API 디자인 가이드 (Uniform interface 구성 요소)

  - 자원 식별(URI)

    - URI 는 정보의 자원을 표현해야 한다
 
    - 자원은 명사, 소문자를 사용해야 하고 URL 끝에 /를 사용하지 않는다
 
    ```
    /members/1
    /items/10
    ```

    - 잘못된 예시
    ```
    /getMember 
    ```

    - 행위에 대한 표현이 들어가면 안됨
   
  - 자원에 대한 행위는 HTTP Method(GET, POST, PUT, DELETE)로 표현한다
 
    ```
    GET          조회

    POST         생성

    PUT          전체 수정

    PATCH        부분 수정

    DELETE       삭제
    ```

    ```
    GET    /members      회원 목록 조회 (복수형)
    POST   /members      회원 생성
    GET    /members/1    회원 상세 조회 (단수형)
    PATCH  /members/1    회원 일부 수정
    DELETE /members/1    회원 삭제
    ```

  - 자기 서술적 메시지 (Self-descriptive messages)
 
    - 요청과 응답만 보면 어떤 의미인지 알 수 있어야 한다
   
    ```
    Content-Type: application/json
    Authorization: Bearer abcdef...
    ```

    - 모든 정보는 요청/응답에 다 들어 있어야 한다
   
    - 어떤 타입인지, 어떤 방법으로 인증하는지, 어떤 캐시 정책인지, 어떤 에러인지 등
   
  - HATEOAS
 
    - 응답 안에 다음에 할 수 있는 행동의 링크를 포함
   
    ```json
    {
      "orderId": 10,
      "status": "PAID",
      "links": [
        { "rel": "cancel", "href": "/orders/10/cancel", "method": "POST" },
        { "rel": "receipt", "href": "/orders/10/receipt", "method": "GET" }
      ]
    }
    ```

    - 거의 사용하지 않음

### @pathVariable

* 스프링에서 url로 넘어온 자원을 파라미터로 받을때 사용한다.

```
// 서버로부터 요청 url이 members/15 로 왔을때

@postMapping("/members/{id}") //15가 {id} 에 맵핑된다.
public 반환타입 메서드이름(@pathVariable(value = "id") int id)
```
