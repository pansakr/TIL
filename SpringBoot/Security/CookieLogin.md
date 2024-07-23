### 쿠키 로그인

* [쿠키란?](https://github.com/pansakr/TIL/blob/main/SpringBoot/%EC%8A%A4%ED%94%84%EB%A7%81%20%EA%B8%B0%EB%B3%B8%EA%B0%9C%EB%85%90/%EC%84%B8%EC%85%98%2C%20%EC%BF%A0%ED%82%A4.md) 

```java
@Controller
..class{

    // 로그인 폼 이동 
     @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form){
        return "login/loginForm";
    }

    // 로그인 요청시 호출
    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletResponse response){
        if(bindingResult.hasErrors()){
            return "login/loginForm";
        }

        // 로그인 요청 정보를 가지고 회원 정보 검증
        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        if(loginMember == null){
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다");
            return "login/loginForm";
        }

        // 검증 완료 시 쿠키 생성 후 응답
        //쿠키에 시간 정보를 주지 않으면 세션 쿠키(브라우저 종료시 모두 종료)
        Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
        response.addCookie(idCookie);
        return "redirect:/";
    }

    // 로그아웃시 쿠키 제거
    @PostMapping("/logout")
    public String logout(HttpServletResponse response){
        expireCookie(response, "memberId");
        return "redirect:/";
    }

    // 쿠키 제거 메서드
    private void expireCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        // 쿠키 시간 0설정
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
```
```java
// 홈 컨트롤러
@Controller
..class{

    // @CookieValue로 쿠키 간편하게 조회
    // name="memberId" - http 요청에 포함된 "memberId" 라는 이름의 쿠키 값을 memberId 매개변수에 바인딩함. value 옵션도 같은 역할을 한다
    // required = false "memberId" 라는 이름의 쿠키가 없어도 예외 발생하지 않음
    @GetMapping("/")
    public String homeLogin(@CookieValue(name="memberId", required = false) Long memberId, Model model){

        // 쿠키가 없다면 홈화면
        if(memberId == null){
            return "home";
        }

        // 쿠키의 멤버키로 회원정보 조회. 없으면 홈화면
        Member loginMember = memberRepository.findById(memberId);
        if(loginMember == null){
            return "home";
        }

        // 있다면 화면에 보여줄 정보를 위해 model에 담아 로그인 홈 리턴
        model.addAttribute("member", loginMember);
        return "loginHome";
    }
}
```

* 로그인 성공시 쿠키의 value에 loginId(PK)를 담아 쿠키 생성 후 응답

* 이후 재 요청시 쿠키가 없거나 쿠키값에 해당하는 사용자가 없으면 전체 홈화면 응답

* 쿠키가 있고, 쿠키값에 해당하는 사용자가 있으면 회원 홈 화면 응답
