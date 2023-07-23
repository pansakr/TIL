### ip, port

* ip는 컴퓨터를 찾을때 필요한 주소를 나타내며, port는 컴퓨터 안에서 프로그램을 찾을 때를 나타낸다.

* ip를 통해 네이버 서버가 있는 컴퓨터로 찾아가고, port를 통해 네이버 서버에서 실행중인 여러 프로그램 중 메일 프로그램을 찾아간다.


### SOP(Same Origin Policy)

* 다른 출처(Origin)의 리소스를 사용하는 것을 제한하는 보안 방식

* url의 protocol, host, port 세가지가 다 같으면 같은 출처(Origin)라고 본다.


#### SOP 흐름

* Https://www.aaaaa.com:80 - 접속하면 "안녕" 이라는 문서를 응답하는 사이트

* Https://www.bbbbb.com:80 - 접속하면 "잘가" 라는 문서를 응답하고 Https://www.aaaaa.com:80으로 자동요청하는 코드가 작성되어있는 사이트

* 내가 Https://www.aaaaa.com:80 사이트에 클릭 또는 주소창에 입력해 접속하면 "안녕" 이라는 문서를 받아온다.

* 브라우저가 요청출처와 사이트의 출처가 같은지 확인 후 동일하기 때문에 서버가 응답해준 문서를 화면에 보여준다.

* 주소창 입력시 출처(Origin)를 같게 직접 입력해 요청하고, 클릭시 출처(Origin)로 변환되서 요청이 가기 때문에 같은 출처(Origin)끼리의 요청이라 데이터를 받아올 수 있다.  

* 이번엔 같은 방식으로 Https://www.bbbbb.com:80사이트에 접속하면 "잘가" 라는 문서를 받아오고 Https://www.aaaaa.com:80사이트로 요청을 자동적으로 보내는 코드가 실행되어 "안녕" 이라는 문서를 받아온다.

* 브라우저가 요청과 사이트의 출처가 같은지 확인하는데 Https://www.bbbbb.com:80사이트에서 요청했기에
요청 출처는 Https://www.bbbbb.com:80이고, 서버의 출처는 Https://www.aaaaa.com:80이기 때문에
받아온 응답 문서를 화면에 보여주지 않고 null로 보여준다.

### CORS(Cross Origin Resources Sharing)

* 한 출처에서 실행 중인 웹 애플리케이션이 다른 출처의 자원에 접근할 수 있는 권한을 부여하도록 브라우저에 알려주는 체제

* SOP 정책을 위반해도 CORS 정책에 따르면 다른 출처의 리소스라도 허용한다.

#### CORS의 기본 동작

```
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // CORS를 적용할 URL패턴을 정의
                .allowedOrigins("http://localhost:8082") // 자원 공유를 허락할 Origin을 지정
                .allowedMethods( // 허용할 HTTP method를 지정
                        HttpMethod.GET.name(),
                        HttpMethod.POST.name(),
                        HttpMethod.PUT.name(),
                        HttpMethod.PATCH.name(),
                        HttpMethod.DELETE.name(),
                        HttpMethod.OPTIONS.name()
                )
                .maxAge(3600);  // 이후 얼마나 사전요청을 보내지 않을지
    }
}
```
* 클라이언트에서 http요청 헤더의 Origin필드에 Origin값을 담아 전달

* 서버는 응답헤더에 Access-Control-Allow-Origin필드를 추가하고 값으로 이 리소스를 접근하는 것이 허용된 출처 url 담아 응답

* 클라이언트에서 요청 Origin과 서버가 보내준 응답의 Access-Control-Allow-Origin필드의 값을 비교해 유효하다면 가져오고 그렇지 않다면 받아온 응답을 사용하지 않는다.

* 즉 서버에서 Access-Control-Allow-Origin 헤더에 허용할 출처를 기재해서 클라이언트에 응답하면 된다.


#### CORS의 사전 요청(Prefilight Request)

* 웹브라우저는 기본적으로 cross origin에 대해서 http요청 전이 서버 측에서 해당 요청을 보낼 수 있는지 확인하는 Prefilight Request를 보낸다.

* Prefilight Request는 http options 메서드를 사용

* Prefilight Request를 사용하는 이유는 cors 오류는 웹브라우저에서 발생하지만 서버에서 정상적으로 요청을 처리해도 클라이언트에서는 서버에서 오류가 난 것처럼 보일 수 있기 때문에 사전에 확인한다.

* 사전요청을 보내면 한 api에 대해 두번 요청을 보내는 것이기에 성능의 저하가 발생한다.

* 그래서 한번 사정요청을 보내면 이후 일정 시간 동안 사전요청을 보내지 않고 요청할 수 있게 설정할 수 있다.

* 사전 요청으로 받아오는 값중 Access-Control-Max-Age의 값으로 이후 얼마나 사전요청을 보내지 않을지 확인할 수 있다.


#### 단순 요청(Simple Request)

* 사전 요청을 생략하고 바로 서버에 본 요청을 보내 응답받은 헤더의 Access-Control-Allow-Origin 값으로 CORS 정책 위반 여부를 검사하는 것.

* 아래 조건을 모두 만족하는 경우에만 단순요청을 보낸다.

* 요청 메서드가 get, head, post 중 하나일 경우

* 헤더가 Accept, Accept-Language, Content-Language, Content-Type, DPR, Downlink, Save-Data, Viewport-Width, Width 일 경우

* Content-Type 헤더가 application/x-www-form-urlencoded, multipart/form-data, text/plain중 하나일 경우
