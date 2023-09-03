### CSRF토큰

<img src="https://github.com/pansakr/TIL/assets/118809108/979d372b-bec0-4558-bed5-57479b1f94ac">

* 서버측 애플리케이션에서 생성되어 클라이언트와 공유되는 인증 문자열 값이다.

* 사용자가 처음 GET 요청으로 웹 페이지를 열때 서버가 CSRF토큰을 생성해 HTTP세션에 저장하고 해당 토큰을 응답에 포함시킨다.

* 이후 POST범위에 포함되는 HTTP METHOD 요청이(PATCH, DELTE 등) 왔을때 헤더에 응답한 CSRF토큰이 없거나 일치하지 않다면 요청을 수락하지 않는다.

* CSRF토큰은 보통 HTML형식의 숨겨진 태그를 사용해 전달한다.

* CsrfFilter활성화 후 클라이언트의 요청시 서버는 CSRF토큰을 생성해 HTTP세션에 저장 후 클라이언트에게 응답과 함께 전달한다.

* 이후 클라이언트가 HTML양식의 버튼을 클릭하여 서버에 추가 요청을 보낼시 CSRF토큰은 HTML형식의 숨겨진 필드에 포함되어 있으므로 CSRF토큰 값이 요청 매개 변수로 전송된다.

* 서버는 요청 파라미터에 지정된 CSRF토큰 값과 HTTP POST메소드를 사용하여 액세스할 때 HTTP세션에 유지된 CSRF토큰 값이 동일한지 확인한다.

* 토큰 값이 일치하지 않으면 잘못된 요청으로 오류가 발생한다
```
// get - /add 요청으로 아래 add.html을 응답받아 Add버튼을 클릭해 서버로 post - /csrf 요청을 보낸다.
// hidden태그 내부의 _csrf 값이 없다면 서버가 csrf토큰을 클라이언트로부터 받지 못해 post요청을 거부한다.
// get 요청은 csrf토큰이 없어도 할 수 있다.

// add.html파일
<body>
    <h2>POST 요청 전 CSRF 토큰 포함시키는 페이지</h2>
    <form action="/csrf" method="post">
        <button>Add</button>

        <!-- 아래 코드가 없으면 클라이언트의 csrf토큰을 서버에 전달하지 못해 post요청을 거부당한다.-->
        <input type="hidden" th:name="${_csrf.parameterName}"
                             th:value="${_csrf.token}" />
    </form>
</body>
```
