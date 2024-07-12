### Http 스펙

#### Http Request
```
// 첫째줄 - 요청라인(http 메서드)
POST /create-developer HTTP/1.1
// 둘째줄부터 줄바꿈 이전까지 - 헤더
Content-Type: application/json
Accept: application/json

// 줄바꿈 이후 - 요청 바디 
{
  "developerLevel": "JUNIOR",
  "developerSkillType": "FULL_STACK",
  "experienceYears": 2,
  "memberId": "sunny.flower",
  "name": "sun",
  "age": 36
}
```
#### Http Response 
```
// 첫째줄 - 응답 상태코드
HTTP/1.1 200 OK
// 둘째줄부터 줄바꿈 전까지 - 헤더
Content-Type: application/json
Transfer-Encoding: chunked
Date: Sat, 17 Jul 2021 15:33:34 GMT
Keep-Alive: timeout=60
Connection: keep-alive

// 줄바꿈 이후 - 응답 바디
{
  "developerLevel": "JUNIOR",
  "developerSkillType": "FULL_STACK",
  "experienceYears": 2,
  "memberId": "sunny.flo1wer",
  "name": "sun",
  "age": 36
}
```

#### content-type 

* http 메시지 바디의 데이터 형식을 지정
* body에 담긴 데이터가 어떤 타입의 데이터인지 설명

#### http header, body
* http header에는 (요청/응답)에 대한 요구사항이
* http body에는 그 내용이 적혀있고,
* Response header 에는 웹서버가 웹브라우저에 응답하는 메시지가 들어있고, 
* Reponse body에 데이터 값이 들어가있다.

### 클라이언트에서 서버로 요청 데이터를 전달하는 방법

#### GET - 쿼리 파라미터

* 메시지 바디 없이 url의 쿼리 파라미터에 데이터를 포함해서 전달

* 쿼리 파라미터 - url뒤에 ?를 붙이고 '파라미터 = 값' 형식으로 넘길 데이터를 붙이고, 여러개를 넘길 경우 파라미터 뒤에 &를 붙여 구분하는 방법

* ex) /url?age=20&phone=01011112222

* 검색, 필터, 페이징 등에서 사용


#### POST - HTML Form

* HTML form의 헤더 타입 - content-type: application/x-www-form-urlencoded

* 메시지 바디에 쿼리 파라미터 형식으로 전달

* 회원가입, 주문 등에서 사용


#### Http message body에 데이터를 직접 담아서 요청

* HTTP API에서 주로 사용. JSON, XML, TEXT

* 주로 JSON 형식을 사용

* HTTP METHOD - POST, PUT, PATCH
  

### 스프링에서 http Request의 데이터를 받는 방법

#### @PathVariable

* url에 포함되어 오는 가변 데이터를 사용하기 위한 어노테이션

```
Path Parameter = (value = "/user/{type}/id/{id}")
메소드이름 (@PathVariable(name = "type") String type){}

// value에 적힌 uri의 중괄호를 @PathVariable 를 사용해서 자바 변수로 받는다.
``````
* uri에 반드시 데이터가 있어야 경로가 완성되기 때문에 선택적 데이터인 경우(데이터가 없을수도 있는) 사용하지 않는다.


#### @ModelAttribute

* http 파라미터를 특정 java 객체에 바인딩한다.

* 바인딩한 객체에 model.addAttribute("item", item)를 자동으로 해주기도 해서 view로 리턴시 데이터가 전달된다

* query string형태나 http body의 form-data형식을 처리한다. json데이터는 처리할 수 없다.

* query string및 form 형식이 아닌 데이터는 처리할 수 없다.

* @ModelAttribute를 사용할 객체는 생성자나 @Setter가 필요하다


#### @Requestbody

* http request body의 json,xml,text 데이터를 읽어 String이나 자바 객체로 받는다

* String으로 받으면 이후 타입을 바꿔주는 라이브러리(ojbectMapper(json) 등 받은 타입에 따라 다른 라이브러리 사용)를 사용해 자바 객체로 바꿔줘야 한다

* 자바 객체로 받으면 스프링의 HttpMessageConveter가 자동으로 자바 객체로 변환해준다

* HttpMessageConveter는 요청 body 값의 미디어 타입을 확인하고 타입에 맞는 HttpMessageConverter를 통해 java 객체로 변환된다.

* @Requestbody를 사용할 객체는 @Getter 과 기본 생성자가 필요하다.


### HttpMessageConveter

* HTTP 요청이나 응답 시 body에 데이터를 직접 보내거나 받는 경우(http api) 해당 데이터의 변환을 위해 스프링 mvc에서 자동 적용되는 인터페이스

* @RestController, @RequestBody, @ResponsBody, HttpEntity(RequestEntity), HttpEntity(ResponseEntity) 사용시 자동 적용   

* 클라이언트에서 서버로 http request body에 JSON 형식의 데이터를 담아 전송했을때 Java에서는 해당 JSON 형식의 데이터를 받기 위해서 JSON -> Java Object로의 변환이 필요하다.

* 컨트롤러에 @RestController 어노테이션이 있다면 자바의 HttpMessageConverter가 json으로 전송된 데이터를 자바 객체로 변환한다. 

* 마찬가지로 요청된 데이터를 처리 후, 서버에서 클라이언트로 다시 응답 데이터 responseBody를 보낼 때도 Java Object에서 JSON 또는 XML 같은 형식으로의 변환이 필요하다. 

* @RestController 어노테이션에는 @ResponseBody 어노테이션이 포함되있으므로 추가작업을 하지 않아도 자바객체 -> json으로 자동 변환된다.

* 즉, 요청시 바이트스트림(바이트스트림중의 하나인 json) -> HttpMessageConveter -> 자바 객체. 이 과정이 역직렬화(DeSerialization)이다.

* 그리고 응답시 자바 객체 -> HttpMessageConveter -> 바이트스트림(바이트스트림중의 하나인 json). 이 과정은 직렬화(Serialization)이다.



### xml 데이터 받는 방법

* jackson dataformat-xml 라이브러리를 추가한다.

* 헤더값에 application/xml을 보내주면 xml로 서버가 자바 객체를 xml로 변환해 응답한다.

* application/xml이 없다면 json(기본값)으로 응답한다.


### 바이트스트림

* 1 byte를 입출력 할 수 있는 스트림

* 자바에서 입 * 출력 스트림을 통해 흘러가는 데이터의 기본 단위
