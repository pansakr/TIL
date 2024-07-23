### oauth2 로그인 후 유저 정보 받는 방법

* oauth2 로그인 성공시 loadUser() 메서드가 호출되어 유저정보를 OAuth2User 타입으로 리턴한다.

```
@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    // 구글로부터 받은 userRequest 데이터에 대한 후처리되는 함수
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        
        // userRequest에 담긴 클라이언트 자격증명, 토큰 조회
        System.out.println("getClientRegistration : " + userRequest.getClientRegistration());
        System.out.println("getAccessToken : " + userRequest.getAccessToken().getTokenValue());

        // userRequest정보 -> loadUser함수 호출 -> 구글로부터 회원프로필을 받아준다.
        System.out.println("getAttributes : " + super.loadUser(userRequest).getAttributes());

        OAuth2User oAuth2User = super.loadUser(userRequest);
        return super.loadUser(userRequest);
    }
}
```

* OAuth2User는 UserDetails처럼 Authentication에 저장되고, 컨트롤러에서 Authentication타입으로 호출할 수 있다.

```
// 컨트롤러

// 일반 로그인시
@GetMapping("/test/login")
    public @ResponseBody String testLogin(Authentication authentication,
                                          @AuthenticationPrincipal PrincipalDetails userDetails){
        System.out.println("/test/login ==============");
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        System.out.println("authentication : " + principalDetails.getUser());

        System.out.println("userDetails : " + userDetails.getUser());
        return "세션 정보 확인하기";
    }

// oauth2 로그인시
@GetMapping("/test/oauth/login")
    public @ResponseBody String testOAuthLogin(
            Authentication authentication,
            @AuthenticationPrincipal OAuth2User oauth){
        System.out.println("/test/login ==============");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        System.out.println("authentication : " + oAuth2User.getAttributes());

        System.out.println("oauth2User : " + oauth.getAttributes());
        return "OAuth 세션 정보 확인하기";
    }
```

* 이렇게 되면 일반 로그인은 UserDetails의 구현체 타입으로 호출하고, oauth2 로그인은 OAuth2User타입으로 호출해야하는 불편함이 생긴다.

* 일반 로그인 시 사용자 정보를 구성하는 인터페이스인 UserDetails의 구현체가 OAuth2User 인터페이스도 구현하면 하나의 구현체로 각각의 방식으로 로그인한 유저 정보를 얻어올 수 있다.
