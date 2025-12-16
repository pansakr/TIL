### Oauth2

* 자신이 소유한 리소스에 소프트웨어 애플리케이션이 접근할 수 있도록 허용해 줌으로써 접근 권한을 위임해주는 개방형 표준 프로토콜

* 리소스 오너(사용자), 클라이언트, 권한 부여 서버, 리소스 서버로 구성되어 있다.

```
리소스 오너(사용자) - 웹 서비스를 이용하려는 유저. 리소스의 소유자(개인정보와 같은 자원들)

클라이언트 - 개인이 만든 애플리케이션 서버. 사용자와 혼동하면 안된다

권한 부여 서버 - 권한을 부여(토큰 발급)해주는 서버. 사용자는 이 서버로 id,pw를 넘겨 인증해 Authorization Code를 발급받는다.
이후 클라이언트가(사용자 아님) 이 서버로 Authorization Code를 넘겨 토큰을 발급받는다.

리소스 서버 - 클라이언트가 접근하려는 사용자의 정보를 가지고 있는 서버. 클라이언트는 발급받은 토큰을 넘겨 리소스가 가지고있는 자원을 받아올 수 있다.
``` 

* 사용자가 클라이언트의 서비스를 사용하기 위해 클라이언트가 권한 부여 서버로부터 리소스 서버에 접근할 수 있는 토큰을 받는것이 Aauth2의 인증 흐름이다.

* 토큰을 얻는 방법을 그랜트 라고 하고 그랜트에는 승인 코드, 암호, 갱신 토큰, 클라이언트 자격 증명의 유형이 있다.


### 권한 서버에 클라이언트 인증(그랜트 아님)

* 사용자가 클라이언트 서버에(개인 앱) 요청하면 클라이언트는 서비스에 필요한 사용자의 개인정보를 리소스 서버에 요청해 받아온다. 

* 권한 부여 서버가 발급해준 토큰으로 사용자를 대신해 로그인을 진행하고 개인 정보들에 접근 하는것이다.

* 즉 사용자가 클라이언트에 요청시 클라이언트가 사용자의 정보를 얻기 위해 리소스 서버에 재요청하는 방식이다.

* 중요한 역할인 만큼 권한 부여 서버는 사용자를 대신하는 클라이언트가 등록된 클라이언트인지 확인하고 클라이언트가 요청한 정보들이 사용자가 수락한게 맞는지 확인한다. 

```
// OAuth 에 클라이언트 등록하기
애플리케이션 이름, 서비스 url, 요청할 정보, 리디렉션 url을 입력한다.

등록을 완료하면 Clint ID, Clint Secret, Authorized redirect URIs을 받는다.

// OAuth2에 클라이언트를 등록할때 클라이언트가 제공할 권한(성별, 연령, 생일 등)을 정할 수 있다.
// 리디렉션 url은 인증 성공 시 사용자를 리디렉션할 위치를 권한 부여 서버에 알려주는 것이다.
// 권한 부여 서버가 Authorized Token을 제공할 때도 리디렉션 url을 이용한다.

```

* 사용자가 소셜 로그인 버튼 클릭시 클라이언트는 권한 서버에 클라이언트 id, pw, redirect url를(사용자 id,pw 아님) 보내 등록된 클라이언트임을 인증하고 사용자를 로그인 페이지로 리디렉션 해준다.

<img src="https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Spring/Security/%EC%A0%95%EB%B3%B4%EC%A0%9C%EA%B3%B5%20%EB%8F%99%EC%9D%98.jpg" alt="정보제공 동의">

* 이후 로그인 페이지에서 정보를 입력해 로그인 시 사용자에게 클라이언트가 요청한 정보를 확인시켜준다.


### 승인 코드 그랜트

<img src="https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Spring/Security/aouth2%20%EA%B0%9C%EB%85%90.jpg" alt="aouth2 개념">

* 클라이언트 서버는 구글 로그인을 구현했다고 가정한다.

* 사용자는 클라이언트의 서비스를 이용하려 하고, 인증이 필요하기에 클라이언트는 사용자에게 구글 로그인 버튼을 응답한다.

* 사용자가 버튼을 클릭하면 클라이언트가 권한 부여 서버에게 등록된 클라이언트임을 인증하고 권한 부여 서버의(구글 인증 서버) 로그인 페이지(구글 로그인 페이지) 를 리디렉션한다.

* 사용자가 id, pw를 입력해 로그인 성공 시 권한 부여 서버는 등록된 redirect URI로 Authorized Token(승인 코드)을 발급한다.

* Authorized Token은 클라이언트가 리소스에 접근할 수 있도록 사용자가 인증했다는 증명이다.

* 클라이언트는 Authorized Token을 권한 부여 서버에 제시해 액세스 토큰을 발급받는다.


### 액세스 토큰 전에 승인 코드를 발급하는 이유

* 토큰을 바로 발급한다면 등록되어 인증한 클라이언트가 토큰을 받았는지 확인하지 못하기 때문이다.

* 클라이언트는 1차로 받은 승인 코드와 함께 클라이언트 자격 증명을 다시 보내 자신이 로그인 요청을 보낸 클라이언트가 맞다는 것을 증명해야 한다.    


### Oauth2 기본 설정(깃허브 로그인)

#### 사용할 oauth2 애플리케이션을 깃허브에 등록한다.

<img src="https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Spring/Security/aouth2%20%EB%93%B1%EB%A1%9D.jpg" alt="aouth2 등록">

* 등록이 완료되면 깃허브는 clint id, pw를 제공한다.

#### 보호할 웹 페이지 생성

```
// 컨트롤러 클래스
@Controller
public class MainController {

    @GetMapping("/")
    public String main(){
        return "main.html";
    }
}

// html파일
<body>
    <h2>Main Page</h2>
</body>
```

#### 시큐리티 설정파일에 oauth2 필터 추가

* oauth2 로그인이 동작하게 filter를 추가한다.

```
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http.oauth2Login(); // oauth2필터가 필터체인에 추가된다.

        http.authorizeRequests()
                .anyRequest()
                .authenticated();

        return http.build();
    }
}
```

#### 클라이언트를 권한 부여 서버와 연결

* 인증과 권한 부여를 해줄 서버인 깃허브를 연결한다.

* 권한 부여 서버에 클라이언트도 자격 증명을 해야 하기 때문에 ClientRegistration 클래스를 사용해 필요한 id, pw등을 설정한다.

* 이 클래스에는 클라이언트 자격 증명에 필요한 세부 정보들을(id, pw, url 등) 설정할 수 있다.

* 그런데 구글, 페이스북 같은 대표적인 권한 부여 공급자에 대한 인증은 CommonOAuth2Provider를 사용해 간편하게 설정할 수 있다.

* CommonOAuth2Provider는 대표적인 공급자들에 대한 인증에 이용할 수 있는 ClientRegistration의 일부를 미리 설정해놓은 클래스로 설정을 간소화할 수 있다.

* 즉 모든 공급자들의 설정은 ClientRegistration로 해도 되지만, 대표적인 공급자들에 한해서 CommonOAuth2Provider를 사용하면 간편하게 설정할 수 있고, 대표적인 공급자가 아니라면 ClientRegistration로 설정해야 한다.

```
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private ClientRegistration clientRegistration(){
        return CommonOAuth2Provider.GITHUB.getBuilder("gitbub") // 미리 정의해놓은 git설정 사용
                .clientId("8367ac226fc45f65ef42")  // 클라이언트 자격 증명 설정
                .clientSecret("1474a62205217d26abcee162c31fbe9abbaa3325")
                .build();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http.oauth2Login();

        http.authorizeRequests()
                .anyRequest()
                .authenticated();

        return http.build();
    }
}
```

#### ClientRegistration을 시큐리티에 등록

* 생성한 클라이언트 자격 증명을 시큐리티에 등록해야 사용할 수 있다.

<img src="https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Spring/Security/%ED%81%B4%EB%9D%BC%EC%9D%B4%EC%96%B8%ED%8A%B8%20%EC%9E%90%EA%B2%A9.jpg" alt="클라이언트 자격">

* oauth2 로그인 시 oauth2인증 필터는 ClientRegistrationRepository에서 클라이언트 자격 증명에 관한 세부 정보를 얻는다.

* ClientRegistrationRepository는 ClientRegistration를 참조해 정보를 얻는다.

* UserDetailsService객체가 사용자 이름으로 UserDetails를 찾은 것처럼 ClientRegistrationRepository는 등록 id로 ClientRegistration를 찾는다.

* ClientRegistrationRepository의 구현체인 InMemoryClientRegistrationRepository을 빈으로 등록시키고, ClientRegistration를 찾을 수 있도록 설정한 ClientRegistration를 매개변수에 넣는다.

```
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public ClientRegistrationRepository clientRepository(){ 

        var c = clientRegistration();
        return new InMemoryClientRegistrationRepository(c); // 설정 완료한 ClientRegistration를 매개변수로 넣는다.
    }

    private ClientRegistration clientRegistration(){
        return CommonOAuth2Provider.GITHUB.getBuilder("gitbub")
                .clientId("8367ac226fc45f65ef42")
                .clientSecret("1474a62205217d26abcee162c31fbe9abbaa3325")
                .build();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http.oauth2Login();

        http.authorizeRequests()
                .anyRequest()
                .authenticated();

        return http.build();
    }
}
```

### 리소스 서버가 토큰을 검증하는 방법

* 클라이언트가 토큰을 제시해 리소스 서버에 요청하면 리소스 서버는 토큰이 유효한지 검증해야 한다.

* 첫번째 방법은 리소스 서버가 권한 부여 서버를 호출해 발행한 토큰이 맞는지 확인하는 것이다.

* 두번째 방법은 리소스 서버와 권한 부여 서버가 공유 db를 사용하고 권한 부여 서버가 토큰을 발행할 때 db에도 저장한다.

* 클라이언트 요청 시 리소스 서버는 db에 접근해 토큰을 검증할 수 있다.

* 세번째 방법은 암호화 서명을 이용하는 것이다. 권한 부여 서버는 토큰을 발행할때 토큰에 서명하고 리소스 서버는 이 서명이 맞는지 검증한다. 여기에는 json 웹 토큰을 이용한다.

* 토큰 서명에는 키를 사용하는데 서명과 검증에 같은 키를 사용하면 대칭키, 다른 키를 사용하면 비대칭 키 라고 한다.
