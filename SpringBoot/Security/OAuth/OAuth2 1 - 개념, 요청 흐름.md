### OAuth2

* 사용자의 비밀번호를 알려주지 않고 다른 서비스가 내 자원(구글 등에 저장된 내 정보) 에 접근할 수 있게 권한을 위임하는 규칙

    - 사용자 A 는 B 앱을 이용하려고 함

    - B 앱은 구글 캘린더 정보를 이용해 여러 서비스를 제공하는 앱

    - A 가 B 앱에 OAuth2 로그인 요청 시 구글은 B 앱에 사용자의 구글 캘린더 정보에 접근할 권한을 줌

    - B 앱은 얻은 권한으로 실제 정보가 저장되어 있는 서버에 재요청해 정보를 받아 사용


### OIDC(OpenID Connect)

* OAuth2 를 확장한 신원 인증 프로토콜

* OAuth2 는 자원 접근 권한 위임 규칙이고, OIDC 는 사용자 인증 목적을 가진다

* 필요한 이유

    - 사용자가 OAuth2 로 로그인을 하는 건 구글 서버 등에서 인증이 이루어지는 것이다

    - 구글은 사용자가 본인임을 인증한 뒤, 3자 앱에게 사용자 대신 데이터에 접근해도 좋다는 권한을 부여한다 (접근 권한이 담긴 액세스 토큰)

    - 구글이 사용자를 인증하고 토큰을 발급한 것은 맞지만, 3자 앱 입장에선 그 토큰을 신뢰할 수 있는지, 사용자의 신원이 무엇인지 직접 확인해야 한다

        - 누군가 토큰을 위조한게 아니라 구글이 토큰을 정말 발급한게 맞는지

        - 토큰을 가진 사용자가 방금 로그인 한 그 사람이 맞는지 

* OIDC 는 ID 토큰을 제공하고, ID 토큰의 서명을 검증하는 과정을 통해 사용자의 신원을 확실하게 인증할 수 있다

* Oauth2 를 사용한다면 보안 측면 하나만 보더라도 OIDC 를 사용하는게 좋음

    - OAuth2의 흐름을 그대로 씀

### OAuth2 구성 요소

* 사용자

    - 구글/카카오/네이버 등의 계정을 가진 사람

* 클라이언트

    - 내 서비스 (Spring Boot 서버)

* Authorization Server (인증 서버)

    - 구글, 카카오, 네이버 등
    
    - 로그인 + 토근 발급 담당

    - 역할

        - 사용자 인증

        - 사용자 동의 처리

        - Authorization Code 발급

        - Access Token 발급 (API 접근권한 토큰)

* Resource Server (자원 서버)

    - 구글, 카카오, 네이버 등의 사용자 정보를 제공하는 서버

        - Authorization Server, Resource Server 가 개념적으로는 나뉘어져 있지만 같은 서비스인 경우도 있다

    - Access Token 을 검증하고 요청을 허용


### OAuth2 사용 전 선행해야 하는 것들

* OAuth2 제공자에 내 서버, Redirect URI 등록

    - OAuth2 는 등록된 서버의 요청만 받는다

        - 등록 시 client_id, client_secret(노출 x) 을 받을 수 있다 

    - OAuth2 는 미리 등록된 URI 로만 리다이렉트 해준다

* OAuth2 제공자 설정에 Scope 정의

    - 어떤 정보까지 요청할 건지 미리 명시

    - Scope = 접근 권한 범위


### OAuth2 흐름

* 사용자가 Oauth2 로그인 요청

    - 사용자가 구글/카카오/네이버 로그인 버튼 클릭

    ```java
    // 클릭 시 내부적으로 아래 요청이 전송됨
    GET /oauth2/authorization/google
    ```

* OncePerRequestFilter 를 상속한 OAuth2AuthorizationRequestRedirectFilter 가 요청을 가로챈다

    - client_id, scope, redirect_uri, state 생성

    - OAuth2 Authorization Request 생성

    - 브라우저를 OAuth2 제공자(구글/카카오)로 리다이렉트

    ```
    302 Redirect → https://accounts.google.com/o/oauth2/v2/auth?...
    ```

    - http.oauth2Login() 설정 활성화 시 OAuth2AuthorizationRequestRedirectFilter 가 필터체인에 자동으로 등록됨

* 내 서버는 인증 서버의(구글 등) 로그인 페이지로 리다이렉트하고, 사용자는 리다이렉트된 OAuth2 화면에서 로그인 진행 및 권한 제공 동의

    - 사용자의 구글/카카오/네이버 등의 계정 정보를 내 서버가 써도 되는지 확인

    - 예시) 웹사이트 A 가 당신의 프로필 정보에 접근하는 것을 허용하시겠습니까?

* 로그인 성공 및 권한 제공 동의 시 OAuth2 제공자가(구글 등) 내 서버가 미리 지정해둔 리다이렉트 URL 로 사용자의 브라우저를 리디렉션하며 파라미터에 Authorization Code 를 보냄

    - Authorization Code

        - Access Token 으로 교환하기 위한 일회성 임시 인증 코드

    - Authorization Code 교환 실패 시 예외 발생

        - code 만료, client_id / client_secret 불일치, redirect_uri 불일치 시 예외 발생

        - OAuth2AuthorizationException, OAuth2AuthenticationException 예외 발생

* AbstractAuthenticationProcessingFilter 를 구현한 OAuth2LoginAuthenticationFilter 가 요청을 가로챔

    - 전달받은 Authorization Code 코드에서 code, state 추출

    - 미인증 상태의 OAuth2LoginAuthenticationToken 생성

    ```java
    // 예시. 개발자가 실제로 구현하진 않아도 됨
    OAuth2LoginAuthenticationToken (authenticated = false)
    ```

* AuthenticationManager (인터페이스) 에게 인증을 위임

    ```java
    // 예시. 개발자가 실제로 구현하진 않아도 됨
    ProviderManager.authenticate(OAuth2LoginAuthenticationToken)
    ```

    - AuthenticationManager 의 구현체인 ProviderManager 에게 미인증 토큰이 전달됨

* ProviderManager 는 전달받은 토큰을 처리할 적절한 AuthenticationProvider(인터페이스) 를 찾는다

    - AuthenticationProvider 의 구현체 중 하나인 OAuth2LoginAuthenticationProvider 가 실제 인증 로직 수행

        - OAuth2LoginAuthenticationProvider : OAuth2 인증을 처리하는 클래스

    - OAuth2LoginAuthenticationProvider 인증 수행 과정

        - Authorization Code, client_id + client_secret 를 인증 서버에 보내 Access Token 으로 교환

            - OIDC 도 활성화 했다면 Access Token 에 더해서 id_token(JWT) 이 추가로 응답됨

        - Access Token 으로 리소스 서버에 사용자 정보 조회 요청

    - 성공

        - 인증된 Authentication 객체 생성

        ```java
        OAuth2AuthenticationToken (authenticated = true)
        ```

        - OAuth2User(이메일, 이름 등), 권한 정보, clientRegistrationId (google, kakao 등) 정보를 가지고 있다

    - 실패

        - Access Token 은 받았지만 사용자 정보 조회 실패

            - 제공자 API 장애 등

            - OAuth2AuthenticationException 발생

        - 사용자 정보는 받았지만 시스템 정책상 로그인 불가능

            - 이메일 없음, 차단된 사용자 등

* 인증된 Authentication 객체가 OAuth2LoginAuthenticationFilter 필터까지 반환됨

* SecurityContextHolder(시큐리티 세션) 에 Authentication 저장

    ```
    // 폼 로그인과 동일

    1. 세션 없으면 생성

    2. SecurityContext를 세션에 저장

    3. 서블릿 컨테이너가 JSESSIONID 쿠키 발급
    ```

* 다음 요청부터 로그인 상태 유지
