### content-type 

* http 메시지 바디의 데이터 형식을 지정
* body에 담긴 데이터가 어떤 타입의 데이터인지 설명

### http header, body
* http header에는 (요청/응답)에 대한 요구사항이
* http body에는 그 내용이 적혀있고,
* Response header 에는 웹서버가 웹브라우저에 응답하는 메시지가 들어있고, 
* Reponse body에 데이터 값이 들어가있다.


### @Requestbody, @Responsebody

* 클라이언트에서 서버로 http request body에 JSON 형식의 데이터를 담아 전송했을때 Java에서는 해당 JSON 형식의 데이터를 받기 위해서 JSON -> Java Object로의 변환이 필요하다.

* 받을 매개변수 앞에 @RequestBody를 명시해주면 자바의 HttpMessageConverter가 json으로 전송된 데이터를 자바 객체로 변환한다. 

* 마찬가지로 요청된 데이터를 처리 후, 서버에서 클라이언트로 다시 응답 데이터 responseBody를 보낼 때도 Java Object에서 JSON 또는 XML 같은 형식으로의 변환이 필요하다. 

* @RestController 어노테이션에는 @ResponseBody 어노테이션이 포함되있으므로 추가작업을 하지 않아도 자바객체 -> json으로 자동 변환된다.

* 요청시 바이트스트림(바이트스트림중의 하나인 json) -> HttpMessageConveter -> 자바 객체. 이 과정이 역직렬화(DeSerialization)이다.

* 응답시 자바 객체 -> HttpMessageConveter -> 바이트스트림(바이트스트림중의 하나인 json). 이 과정은 직렬화(Serialization)이다.


### 바이트스트림

* 1 byte를 입출력 할 수 있는 스트림

* 자바에서 입 * 출력 스트림을 통해 흘러가는 데이터의 기본 단위
