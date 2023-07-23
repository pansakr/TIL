### csrf토큰

* 시큐리티 활성화 시 클라이언트의 요청에 대한 응답으로 html페이지를 리턴해줄때 input 태그에 csrf 토큰이라는
난수값을 세팅해 돌려준다.
```
//시큐리티가 csrf토큰을 추가해 돌려준 html파일. 
<input type="text" name="username" placeholder="유저네임" required="required" csrf="임의의 난수값"/>

// 실제 html 파일. csrf 부분은 시큐리티가 추가해 주는것으로 해당 html파일을 보면 저 부분은 없다.
<input type="text" name="username" placeholder="유저네임" required="required" />
```

* 이후 사용자가 해당 페이지로 추가 요청을 보냈을때 csrf토큰이 있으면 정상적인 사용자, 그렇지 않으면 비정상적인
사용자로 판단해 예외 페이지를 응답한다.
