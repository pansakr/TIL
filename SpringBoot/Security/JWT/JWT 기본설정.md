### 사용할 jwt 토큰의 세부 설정

* 비밀 키, 만료시간, 헤더, 접두어를 설정한다.

```
public interface JwtVO {
    String SECRET = "메타코딩"; // HS256 대칭키
    int EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; // 일주일
    String TOKEN_PREFIX = "Bearer "; // 토큰 앞에 붙는 접두어를 설정한다.
    String HEADER = "Authorization"; // 헤더에 실어보낼 이름을 지정
}
```

### 토큰 생성, 검증 클래스 생성

* 로그인 성공시 jwt토큰을 생성해주고, 로그인한 사용자의 재요청시 토큰을 검증할 메서드를 만든다.

```
public class JwtProcess {

    // 로그인 성공시 사용할 토큰 생성 메서드. JwtVO의 설정을 사용해 토큰을 생성한다.
    public static String create(LoginUser loginUser){
        String jwtToken = JWT.create()
                .withSubject(loginUser.getUsername()) // 토큰 이름 설정
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtVO.EXPIRATION_TIME)) // 만료시간 설정
                .withClaim("id", loginUser.getUser().getId()) // 토큰에 담길 정보 설정
                .withClaim("role", loginUser.getUser().getRole()+"")
                .sign(Algorithm.HMAC512(JwtVO.SECRET)); // 서명에 사용할 암호화 알고리즘, 비밀키 설정
        return JwtVO.TOKEN_PREFIX + jwtToken;
    }

    // 로그인한 사용자가 재요청시 토큰 검증 (return 되는 LoginUser 객체를 강제로 시큐리티 세션에 직접 주입할 예정)
    public static LoginUser verify(String token){
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(JwtVO.SECRET)).build().verify(token);
        Long id = decodedJWT.getClaim("id").asLong();
        String role = decodedJWT.getClaim("role").asString();
        User user = User.builder().id(id).role(UserEnum.valueOf(role)).build(); // valueOf(role) -> role가 Enum타입으로 바뀌어서 들어온다.
        LoginUser loginUser = new LoginUser(user);
        return loginUser;
    }
}
```

### 인증 필터 생성

* 로그인 요청시 인증을 수행할 필터를 생성한다.

```
// /api/login 요청에만 실행된다.
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    // /login 이었던 기본 경로를 /api/login로 변경했다.
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        setFilterProcessesUrl("/api/login"); 
        this.authenticationManager = authenticationManager;
    }

    // post : /api/login 시 동작.
    // request에 담긴 id, pw로 인증용 토큰을 생성하고 db 검색 결과와 비교해 인증한다.
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)throws AuthenticationException {
                       
        try {
            // request에 json 데이터가 있기 때문에 변환해줄 ObjectMapper가 필요하다
            ObjectMapper om = new ObjectMapper();
            LoginReqDto loginReqDto = om.readValue(request.getInputStream(), LoginReqDto.class);

            // 강제 로그인
            // 사용자가 입력한 id,pw로 인증용 토큰 생성
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginReqDto.getUsername(), loginReqDto.getPassword());

            // UserDetailsService의 loadUserByUsername 호출. loadUserByUsername을 사용하여 로그인하면 세션이 생성된다.
            // JWT를 쓴다 하더라도 컨트롤러에 진입을 하면 시큐리티의 권한체크, 인증체크의 도움을 받을 수 있게 임시 세션을 만든다.
            // 이 세션의 유효기간은 response하면 끝!! 세션 로그인과 달리 세션을 유지시키지 않는다.

            // authenticate로 UserDetailsService -> userDetails 순서로 호출해 만든 인증 토큰으로 db를 검색해 인증한다. 
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            return authentication;

        }catch (Exception e){

            // 필터에서 발생한 예외는 RestControllerAdvice가 받지 못한다.
            // unsuccessfulAuthentication 호출함
            throw new InternalAuthenticationServiceException(e.getMessage());
        }
    }

    // 로그인 실패시 실행
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        CustomResponseUtil.fail(response, "로그인실패", HttpStatus.UNAUTHORIZED);
    }

    // 로그인 성공시 (return authentication 잘 작동하면) successfulAuthentication 메서드 호출
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
     
        // 세션에 임시 저장한 유저 정보를 가져온다.
        LoginUser loginUser = (LoginUser) authResult.getPrincipal();

        // 유저 정보로 토큰을 만든다.
        String jwtToken = JwtProcess.create(loginUser);
        // 만든 토큰을 헤더에 담는다.
        response.addHeader(JwtVO.HEADER, jwtToken);
        // 응답 객체에 유저 정보를 담는다.
        LoginRespDto loginRespDto = new LoginRespDto(loginUser.getUser());
        CustomResponseUtil.success(response, loginRespDto);
    }
}
```

#### 인증 필터의 attemptAuthentication()에서 사용한 authenticate()메서드의 세부사항

* authenticate() 실행시 UserDetailsService ->UserDetails 순으로 호출된다.

```
@Service
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {

    private final UserRepository userRepository;

    //
    @Override // loadUserByUsername는 세션 만들어줌
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userPS = userRepository.findByUsername(username)
                                    .orElseThrow(() -> new InternalAuthenticationServiceException("인증 실패"));
        return new LoginUser(userPS);
    }
}

@Getter
@RequiredArgsConstructor
public class LoginUser implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(() -> "ROLE_" + user.getRole());
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
```

#### 인증 필터에서 사용한 LoginReqDto, LoginRespDto

* LoginReqDto는 요청 id, pw만을 담기 위한 클래스이다.

```
@Setter
@Getter
public class LoginReqDto{
    private String username;
    private String password;
}
```

* LoginRespDto는 화면에서 사용할 응답 데이터를 담기 위한 클래스이다.

```
@Getter
@Setter
public class LoginRespDto{
    private Long id;
    private String username;
    private String createdAt;

    public LoginRespDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.createdAt = CustomDateUtil.toStringFormat(user.getCreatedAt());
    }
}
```

#### 인증 필터에서 사용한 CustomResponseUtil

* 성공, 실패 응답 객체를 만드는 클래스

```
public class CustomResponseUtil {

    // 로그인 성공시 반환할 응답 객체
    public static void success(HttpServletResponse response, Object dto){

        try{
            ObjectMapper om = new ObjectMapper();
            ResponseDto<?> responseDto = new ResponseDto<>(-1, "로그인 성공", dto);
            String responseBody = om.writeValueAsString(responseDto);
            response.setContentType("application/json; charset=utf-8");
            response.setStatus(200);
            response.getWriter().println(responseBody); // 메시지를 포장하는 공통 응답 DTO
        }catch (Exception e){
            log.error("서버 파싱 에러");
        }

    }

    // 로그인 실패시 반환할 응답 객체
    public static void fail(HttpServletResponse response, String msg, HttpStatus httpStatus){

        try{
            ObjectMapper om = new ObjectMapper();
            ResponseDto<?> responseDto = new ResponseDto<>(-1, msg, null);
            String responseBody = om.writeValueAsString(responseDto);
            response.setContentType("application/json; charset=utf-8");
            response.setStatus(httpStatus.value());
            response.getWriter().println(responseBody); // 메시지를 포장하는 공통 응답 DTO
        }catch (Exception e){
            log.error("서버 파싱 에러");
        }

    }

}
```

### 인가 필터 생성

* 로그인 사용자가 재요청시 토큰을 검증한다.

```
// 모든 요청에 동작한다.
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        if(isHeaderVerify(request, response)){
            // 토큰이 존재함
            String token = request.getHeader(JwtVO.HEADER).replace(JwtVO.TOKEN_PREFIX,"");
            // 토큰 검증
            LoginUser loginUser = JwtProcess.verify(token);

            // 임시 세션에 id와 role만 저장한다.
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    loginUser, null, loginUser.getAuthorities()); // id와 role만 있다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        // 인가 작업을 마치고 다음 필터로 넘긴다. 필터가 끝났다면 컨트롤러로 넘어간다.
        chain.doFilter(request, response);
    }

    // 해당 서버에서 만든 jwt토큰이 있는지 체크
    private boolean isHeaderVerify(HttpServletRequest request, HttpServletResponse response){
        String header = request.getHeader(JwtVO.HEADER);
        if(header == null || !header.startsWith(JwtVO.TOKEN_PREFIX)){
            return false;
        }else {
            return true;
        }
    }

}
```

* 인가 작업 완료 후 컨트롤러에 접근시 세션에 임시 저장한 권한 정보를 가지고 시큐리티 설정파일에 설정한 권한에 맞는 url만 호출할 수 있게 된다.

* 임시 세션은 응답 시 사라지며 쿠키/세션 로그인 방식처럼 유지되지 않는다.

### 시큐리티 설정파일

* 작성한 인증, 인가 필터를 적용한다.

```
@Configuration
public class SecurityConfig {

    @Bean // Ioc 컨테이너에 BCryptPasswordEncoder() 객체가 등록됨
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // 생성해둔 JWT 인증, 인가 필터 등록
    public class CustomSecurityFilterManager extends AbstractHttpConfigurer<CustomSecurityFilterManager, HttpSecurity>{

        @Override
        public void configure(HttpSecurity http) throws Exception {
            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
            http.addFilter(new JwtAuthenticationFilter(authenticationManager));
            http.addFilter(new JwtAuthorizationFilter(authenticationManager));
            super.configure(http);
        }
    }

    // 인증, 인가 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        log.debug("디버그 : filterChain 빈 등록됨");
        http.headers().frameOptions().disable(); // iframe 허용안함
        http.csrf().disable(); // enable이면 post맨 작동안함
        http.cors().configurationSource(configurationSource());

        // 세션쿠키 방식의 인증 메카니즘으로 인증처리를 하지 않겠다. 
        // 더이상 SecurityContext에 인증 정보를 저장하지 않는다.
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // react, 앱으로 요청할 예정
        http.formLogin().disable();
        // httpBasic는 브라우저가 팝업창을 이용해서 사용자 인증을 진행한다.
        http.httpBasic().disable();

        // 등록한 jwt 인증, 인가 필터 적용
        http.apply(new CustomSecurityFilterManager());

        // 인증 실패 Exception 가로채기.
        http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {

            CustomResponseUtil.fail(response, "로그인을 진행해 주세요", HttpStatus.UNAUTHORIZED);
        });

        // 권한 실패시
        http.exceptionHandling().accessDeniedHandler((request, response, e) -> {
            CustomResponseUtil.fail(response, "권한이 없습니다", HttpStatus.FORBIDDEN);
        });

        http.authorizeRequests()
                .antMatchers("/api/s/**").authenticated()
                .antMatchers("/api/admin/**").hasRole(""+UserEnum.ADMIN)
                .anyRequest().permitAll();

        return http.build();
    }

    // cors 설정
    public CorsConfigurationSource configurationSource(){
        
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*"); // GET, POST, PUT, DELETE (JavaScript 요청 허용)
        configuration.addAllowedOriginPattern("*"); // 모든 IP 주소 허용
        configuration.setAllowCredentials(true); // 클라이언트에서 쿠키 요청 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
```
