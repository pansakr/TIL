### Oauth2 구현

* Oauth2(구글/네이버 등) 제공자에 애플리케이션, 리다이렉트 URI 등록

    - Oauth2 제공자는 등록된 서버에게만 요청을 허용하기 때문

    - 로그인 요청이 성공했을 때, 미리 등록된 리다이렉트 URI 로 리다이렉트 된다

    - 등록 시 client_id, client_secret 를 얻을 수 있음

    - 등록 방법

        - Google Cloud Console 접속

        - 왼쪽 상단 프로젝트 선택 -> 새 프로젝트

        - 이름 설정 후 만들기

        - 내 프로젝트 -> 왼쪽 상단의 3단 아이콘 -> API 및 서비스 -> Oauth2 동의 화면

        - 개요
        
            - 시작하기 -> 앱 이름, 사용자 지원 이메일 등 정보 입력

            - 앱 이름 : 사용자가 보게 될 앱의 실제 이름(네이버, 당근마켓 등 브랜드 이름)

            - 사용자 지원 이메일 : 사용자 문의를 처리할 이메일 주소 입력

            - 대상 : 외부

            - 연락처 정보 : 생성한 프로젝트의 변경사항이 있을 시 알림받을 이메일 주소 입력

        - 대상

            - 테스트 사용자 -> 내 계정 등록

            - 앱이 테스트 상태일 경우 등록된 이용자만 Oauth2 를 이용할 수 있다

        - 데이터 액세스

            - openid : OIDC 활성화

            - .../auth/userinfo.email : 구글 계정의 email 주소 확인

            - .../auth/userinfo.profile : 공개로 설정한 개인정보

        - API 및 서비스 -> 사용자 인증 정보

            - 상단의 사용자 인증 정보 만들기 -> OAuth2 클라이언트 ID

            - 유형 : 웹 애플리케이션

            - 이름 : 식별용 적당한 이름

            - 승인된 리디렉션 URI : 사용자가 구글 로그인 성공 후 리디렉션 될 URI 입력

                - 시큐리티 기본값 : http://localhost:8080/login/oauth2/code/google

            - 승인된 JavaScript 원본 : 브라우저에서 OAuth2 를 사용할 때 사용

                - React 프론트 있을 경우만 사용

            - 완료 시 client_id, client_secret 획득

* 구글 로그인 버튼 생성

    ```html
    <a href="/oauth2/authorization/google">Google 로그인</a>
    ```

    - /oauth2/authorization/google 경로로 요청이 가도록 설정

    - /oauth2/authorization/{registrationId} 는 Spring Security가 제공하는 시작 URL

    - {registrationId}가 google이면, application.yml에 등록된 google 설정을 찾아서 구글 인증 서버로 보낼 Authorization Request 를 만들어준다

* application.yml 설정

    ```yml
    spring:
        security:
            oauth2:
                client:
                    registration:
                        google:
                            client-id: YOUR_CLIENT_ID
                            client-secret: YOUR_CLIENT_SECRET
                            scope:
                                - openid
                                - email
                                - profile
    ```

    - 사용자가 구글 로그인 버튼을 눌렀을 때 application.yml 에 등록된 설정을 참고해 Authorization Request 를 만듬

* 사용자가 구글 로그인 페이지에서 로그인 성공 -> Google이 code 발급 → redirect_uri로 전송

    - Google Console 에 등록된 redirect URI 로 Authorization Code 응답됨

* SecurityConfig 에서 oauth2Login 필터체인 활성화

    ```java
    @Configuration
    @RequiredArgsConstructor
    public class SecurityConfig {

        private final CustomOAuth2UserService customOAuth2UserService;

        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

            http
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/", "/login").permitAll()
                    .anyRequest().authenticated()
                )

                // OAuth2 로그인 필터 체인 활성화
                // Authorization Code → Token 교환 자동 수행됨
                // Customizer.withDefaults() : 시큐리티 기본 설정을 따름
                .oauth2Login(Customizer.withDefaults());

            return http.build();
        }
    }
    ```
    - 구글에서 리다이렉트 된 요청(/login/oauth2/code/google) 을 OAuth2LoginAuthenticationFilter 가 가로챔

    - 해당 필터가 자동으로 Authorization Code, client-secret 을 access_token, id_token 으로 교환, id_token 자동 검증

    - userinfo / id_token 로 기본 OidUser(사용자 정보) 생성 후 시큐리티 세션 생성 후 저장

* 구글에서 받은 사용자 정보 추가 설정(필수)

    - 위 설정까지 작성하면 로그인은 되지만 그 정보로 할 수 있는게 없다

        - 구글 로그인 시 id_token 내부에 담긴 정보들
        
            - sub : 구글 사용자 고유 ID

            - email, name

        - 시큐리티는 이 정보를 받아 OidcUser 객체를 만들어 세션에 등록한다

        - 즉 구글 로그인한 사용자 정보가 세션에만 존재하는 상태

    - 내 서비스 DB 에 구글 로그인 사용자 정보가 저장되지 않음

        - 따로 추가 설정한 적이 없기 때문에 내 애플리케이션의 DB 에는 해당 정보가 없는 상태

        - 로그인한 구글 사용자가 서비스를 이용하기 위해 필요한 필수적인 정보들(권한 등)이 없다 

        - 따라서 구글 로그인 시 내 DB 와 매핑되도록 설정해야 한다 (자동가입 등)

    - 필터 설정 추가    

    ```java
    // 위 설정에 추가
    ...
    .oauth2Login(oauth2 -> oauth2

        // id_token 검증 + userinfo 로딩 후 처리
        .userInfoEndpoint(userInfo -> userInfo
            .oidcUserService(customOAuth2UserService)
        )
        ...
    );
    ```

    - userInfoEndpoint() : access_token / id_token 받은 뒤 사용자 정보에(userInfo) 추가 설정

    - customOAuth2UserService 클래스 작성

        - customOAuth2UserService : 구글 로그인한 사용자 정보에 대해 추가 작업을 하는 클래스

        ```java
        @Service
        @RequiredArgsConstructor
        public class CustomOAuth2UserService extends OidcUserService {

            private final UserRepository userRepository;

            @Override
            @Transactional
            public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {

                OidcUser oidcUser = super.loadUser(userRequest);

                String registrationId = userRequest.getClientRegistration().getRegistrationId(); // "google"
                String providerId = oidcUser.getSubject(); // sub
                String email = oidcUser.getEmail();

                // find() : 인자로 온 String 값과 일치하는 ENUM 을 반환해주는 메서드
                ProviderType providerType = ProviderType.find(registrationId)
                .orElseThrow(() -> new OAuth2AuthenticationException(
                        new OAuth2Error(OAuth2ErrorCode.UNSUPPORTED_PROVIDER.name())));

                // provider, providerId 가 있으면 기존 회원 처리
                UserEntity userEntity = userRepository.findByProviderAndProviderId(provider, providerId)

                    // 없으면 이메일 중복체크
                    .orElseGet(() -> {

                        // 이메일이 null 인 경우
                        if (email == null || email.isBlank())
                            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCode.EMAIL_REQUIRED.name()));

                        // 이메일이 null 은 아니지만  중복인 경우
                        if (userQueryRepository.existsByEmail(email))
                            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCode.EMAIL_DUPLICATED.name()));

                        return userRepository.save(
                                new UserEntity(email,
                                        UserRole.USER,
                                        UserStatus.ACTIVE,
                                        ProviderType.GOOGLE,
                                        providerId)
                        );
                });

                // 권한 세팅
                var authorities = List.of(new SimpleGrantedAuthority(userEntity.getRole()));

                // 기본 타입인 OidcUser 대신 Principal 로 교체
                return new AppPrincipal(
                    authorities,
                    oidcUser.getIdToken(),
                    oidcUser.getUserInfo(),
                    userEntity.getId(),
                    userEntity.getRole()
                );
            }
        }
        ```
        ```java
        @RequiredArgsConstructor
        @Getter
        public enum ProviderType {

            GOOGLE("google"),
            NAVER("naver");

            private final String provider;

            public static Optional<ProviderType> find(String provider){
                return Arrays.stream(values())
                        .filter(p -> p.provider.equalsIgnoreCase(provider))
                        .findFirst();
            }
        }
        ```
    - 기본 타입(OidcUser) 대신 세션에 저장될 커스텀 클래스(AppPrincipal) 생성

        - 기본 타입에는 시 내 서비스에서 사용하는 정보가 없기 때문에 매번 세션에서 꺼내고, 그 값으로 DB 에서 검색해 와야 한다
 
            ```java
            @GetMapping("/me")
            public String me(Authentication authentication) {

                // 세션에 저장된 값 꺼내기
                OidcUser oidcUser = (OidcUser) authentication.getPrincipal();

                String email = oidcUser.getEmail();

                // 꺼낸 정보로 DB 조회
                UserEntity user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new IllegalStateException("사용자 없음"));

                // 조회한 정보 세팅
                Long userId = user.getId();
                String role = user.getRole();

                return "me";
            }
            ```

            - 기본 타입에는 서비스 이용에 필요한 필수 정보인 권한, 유저 상태 등이 없다

        - 커스텀 클래스 사용

        ```java
        public class AppPrincipal extends DefaultOidcUser {

            private final Long userId;
            private final String role;

            public AppPrincipal(Collection<? extends GrantedAuthority> authorities,
                                OidcIdToken idToken,
                                OidcUserInfo userInfo,
                                Long userId,
                                String role) {
                super(authorities, idToken, userInfo);
                this.userId = userId;
                this.role = role;
            }
            public Long getUserId() { return userId; }
            public String getRole() { return role; }
        }
        ```
