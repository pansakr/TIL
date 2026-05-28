### JWT 구현

* 화면 이동하는 html form 요청은 세션, 화면 내부의 특정 범위의 데이터 요청은 jwt 

* JWT 설정

    - 라이브러리 추가

    ```gradle
    // 컴파일 시 필요한 인터페이스/추상화
    // Jwts.builder(), Clamins, JwtException 등
    implementation 'io.jsonwebtoken:jjwt-api:0.11.5'

    // 서명,검증 구현체
    runtimeOnly  'io.jsonwebtoken:jjwt-impl:0.11.5'

    // JWT Claims <-> JSON 직렬화/역직렬화
    runtimeOnly  'io.jsonwebtoken:jjwt-jackson:0.11.5'
    ```

    ```yml
    app:
    jwt:
        # Base 64 랜덤 문자열   
        secret: "B1XNilH1josE2NMAI1XvavaZ2WEz6y6MxPT6v7kf9i8="
        access-minutes: 30
    ```

* JWT 생성, 검증 클래스 생성

    ```java
    import java.security.Key;   // 자바 암호화 최상위 인터페이스
    import io.jsonwebtoken.security.Keys; // JJWT 라이브러리에의 키 생성 전용 유틸 클래스

    @Component
    public class JwtTokenProvider {

        @Value("${app.jwt.secret}")
        private String secret;

        @Value("${app.jwt.access-minutes}")
        private long accessTime;

        // BASE64 로 인코딩되어 있던 비밀키를 JWT 서명에 사용하기 위해 디코딩
        private Key key() {
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        }

        // Access Token 생성
        public String createAccessToken(Long userId, List<String> roles) {
            Instant now = Instant.now();    // 발급 시간
            Instant exp = now.plus(Duration.ofMinutes(accessTime));  // 만료 시간

            return Jwts.builder()
                    .setSubject(String.valueOf(userId)) // userId 문자열로 변환
                    .claim("roles", roles)
                    .setIssuedAt(Date.from(now))    // 발급 시간
                    .setExpiration(Date.from(exp))  // 만료 시간
                    .signWith(key(), SignatureAlgorithm.HS256)  // 서명할 알고리즘
                    .compact(); // JWT 문자열로 최종 생성
        }

        // 토큰 유효성 검증 (위조/변조되지 않았고, 만료되지 않았는지 검사)
        public boolean validate(String token) {
            try {

                // 토큰 파싱, 서명 검증, 만료 체크
                Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token);
                return true;

                // 필터 단계에선 예외 던지지 말고 유효/무효 여부만 판단
            } catch (JwtException | IllegalArgumentException e) {
                return false;
            }
        }

        /**
         * JWT -> Authentication 변환
         * 필터에 SessionCreationPolicy.STATELESS 설정 했으므로 SecurityContext 를 세션에 저장하지 않음
         * 세션 생성x, 요청 끝나면 SecurityContext 사라짐
         * 시큐리티는 SecurityContext 에 Authentication 이 있어야 인증/권한 등을 알 수 있음
         * 즉 JWT 의 사용자 정보를 시큐리티 컨텍스트에 임시로 담아놓고 요청이 끝날 때까지 사용하기 위한 메서드
        */
        public Authentication toAuthentication(String token) {

            // JWT 검증 후 Claims 추출
            Claims claims = Jwts.parserBuilder().setSigningKey(key()).build()
                    .parseClaimsJws(token)
                    .getBody();

            Long userId = Long.valueOf(claims.getSubject());

            List<String> roles = (List<String>) claims.get("roles");

            // Security 권한 객체 생성
            // hasRole, hasAuthority 등에서 사용
            List<GrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            // 시큐리티의 User(커스텀 가능) 객체 생성 후 인증 완료된 Authentication 생성
            User principal = new User(String.valueOf(userId), "", authorities);

            // 시큐리티는 SecurityContext 에 Authentication 이 있어야 인증/권한 등을 알 수 있음
            // UsernamePasswordAuthenticationToken 는 Authentication 를 구현
            // 인증이 완료된 UsernamePasswordAuthenticationToken 객체 생성
            return new UsernamePasswordAuthenticationToken(principal, token, authorities);
        }

        // 쿠키에서 토큰 추출
        public String resolveFromCookie(HttpServletRequest request, String cookieName) {
            Cookie[] cookies = request.getCookies();
            if (cookies == null) return null;
            for (Cookie c : cookies) {
                if (cookieName.equals(c.getName())) return c.getValue();
            }
            return null;
        }
    }
    ```

* OAuth2 성공 핸들러

    - OAuth2 인증 성공 시 실행되는 클래스

    ```java
    @Component
    @RequiredArgsConstructor
    public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

        private final JwtTokenProvider jwtTokenProvider;

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request,
                                            HttpServletResponse response,
                                            Authentication authentication) throws IOException {

            // 이전 클래스에서 생성한 principal 에서 userId / roles 꺼내기
            // OAuth2 2 - 구현 참조
            OidcUserPrincipal p = (OidcUserPrincipal) authentication.getPrincipal();

            String accessToken = jwtTokenProvider.createAccessToken(
                    p.getUserId(),
                    p.getRoles().stream().map(Enum::name).toList()
            );

            ResponseCookie cookie = ResponseCookie.from("ACCESS_TOKEN", accessToken)
                    .httpOnly(true)
                    .secure(false)        // 운영(HTTPS)에서는 true
                    .sameSite("Lax")      // CSRF 방어 옵션
                    .path("/api")         // API에만 쿠키가 가도록 범위 최소화
                    .maxAge(Duration.ofMinutes(30))
                    .path("/")            // 쿠키카 사용될 경로
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            // 원래 가려던 곳으로 보내거나 홈으로
            getRedirectStrategy().sendRedirect(request, response, "/");
        }
    }
    ```

* 필터 설정

    ```java
    @Component
    @RequiredArgsConstructor
    public class ApiJwtAuthenticationFilter extends OncePerRequestFilter {

        private final JwtTokenProvider jwtTokenProvider;

        // shouldNotFilter() : 결과가 false 일때 아래의 doFilterInternal() 실행, true 면 건너뜀
        @Override
        protected boolean shouldNotFilter(HttpServletRequest request) {

            // 요청 url 이 api 로 시작하면 true 
            // ! 에 의해 false -> 아래의 doFilterInternal() 필터 실행됨
            return !request.getRequestURI().startsWith("/api/");
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {

            String token = jwtTokenProvider.resolveFromCookie(request, "ACCESS_TOKEN");

            if (token != null && jwtTokenProvider.validate(token)) {
                Authentication auth = jwtTokenProvider.toAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            filterChain.doFilter(request, response);
        }
    }
    ```

* 필터체인 설정

    ```java
    @Configuration
    @EnableWebSecurity
    @RequiredArgsConstructor
    public class SecurityConfig {

            private final JwtAuthenticationFilter jwtAuthenticationFilter;
            private final OAuth2UserService<OidcUserRequest, OidcUser> customOAuth2UserService;
            private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
            private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
            private final TraceIdFilter traceIdFilter;
            private final ApiAuthenticationEntryPoint apiAuthenticationEntryPoint;
            private final ApiAccessDeniedHandler apiAccessDeniedHandler;

        @Bean
        @Order(1)
        public SecurityFilterChain apiFilter(HttpSecurity http) throws Exception {
            http

                // traceIdFilter 위치 조정
                // 시큐리티 필터들보다 먼저 실행되게 해서 요청에 tracdId 를 만들도록 설정
                // SecurityContextHolderFilter 는 시큐리티 필터 중 앞쪽에 위치하므로 그보다 먼저 실행되게 설정한 것 
                .addFilterBefore(traceIdFilter, SecurityContextHolderFilter.class);
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .securityMatcher("/api/**")
                .csrf(AbstractHttpConfigurer::disable) // API가 GET 위주면 OK. 쓰기 API면 CSRF 전략 필요
                .authorizeHttpRequests(auth -> auth
                    // 회원가입(POST /api/users)
                    .requestMatchers(HttpMethod.POST, "/api/users").permitAll()

                    // 이메일 중복 체크(GET /api/users/{email})
                    // PathVariable은 ** 와일드카드로 처리
                    .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()

                    // 그 외 API는 인증 필요
                    .anyRequest().authenticated()
                )

                // 세션 생성하지 않음
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 예외 처리
                .exceptionHandling(ex -> ex

                        // 인증(401) 예외 발생 시 호출
                        .authenticationEntryPoint(apiAuthenticationEntryPoint)

                        // 인가(403) 예외 발생 시 호출
                        .accessDeniedHandler(apiAccessDeniedHandler)
                );

            return http.build();
        }


        @Bean
        @Order(2)
        public SecurityFilterChain webFilter(HttpSecurity http) throws Exception {

            http

                .addFilterBefore(traceIdFilter, SecurityContextHolderFilter.class)

                // 웹만
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/", "/login", "/oauth2/**", "/css/**", "/js/**", "/images/**")
                    .permitAll()
                    .anyRequest().authenticated()
                )

                // 폼 로그인(세션)
                .formLogin(formLogin -> formLogin
                    .loginPage("/login")
                    .loginProcessingUrl("/login")
                    .defaultSuccessUrl("/")
                    .failureUrl("/login")
                    .usernameParameter("email")
                    .passwordParameter("password")
                )

                // OAuth2 로그인(세션) + 성공 시 JWT 쿠키 발급 핸들러 추가
                .oauth2Login(oauth2 -> oauth2
                    .loginPage("/login")
                    .userInfoEndpoint(userInfo -> userInfo
                        .oidcUserService(customOAuth2UserService)
                    )
                    .successHandler(oAuth2LoginSuccessHandler)   // ACCESS_TOKEN 쿠키 발급
                    .failureHandler(oAuth2LoginFailureHandler)
                );

            // 지금은 csrf disable 해놨는데, 웹 폼이면 enable이 더 자연스러움
            // 테스트는 이렇게 유지하고, 나중에 CSRF 켜는 걸 추천

            return http.build();
        }
    }
    ```
