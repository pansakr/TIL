* id, class로 html 요소 가져오기

* id는 중복 불가이고 class는 중복 가능하기 때문에 class로 찾는다면 번호를 붙여줘야(인덱싱) 한다.

```js
<!--문서의 id가 'hello'인 html 요소를 가져오고 내부 html을 '안녕'으로 바꿔줘 -->
<!--innerHTML 같은 옵션은 수백가지가 있다.-->
document.getElementById('hello').innerHTML = '안녕'; 

<!--인덱싱을 해주지 않으면 해당 클래스이름의 모든 요소를 찾는다.-->
<!--클래스이름이 title인 html요소 중 위에서부터 1번째([0]) 를 찾는다. -->
document,getElementByClassName('title')[0].innerHTML = '잘가'; 
```

* 코드를 깔끔하고 재사용할 수 있게 function 사용, js로 css 제어하기

* 컴퓨터는 html 위부터 읽기 때문에 html을 조작할 js를 밑에 작성하는것이 좋다. 

```js
<!--css의 스타일을 자바스크립트로 제어할 수 있다.-->
<div class="alert-box" id="alert">알림창</div> // display = 'none' 으로 숨겨져 있다.

<button onclick="알림창열기()">버튼</button>

<script>
function 알림창열기(){
        document.getElementById('alert').style.display = 'block'; // id가 alert인 html의 요소의 스타일을 display = 'block'으로 바꾼다.
    }
</script>
```

* function 파라미터

```js
    <div class="alert-box" id="alert">알림창
        <button onclick="알림창('none')">닫기</button>
    </div>
    <button onclick="알림창('block')">버튼</button>
    
    <script>
    
    <!--()에 전달할 파라미터를 넣어서 함수를 호출하면 js의 해당 파라미터의 자리로 전달된다. -->
    function 알림창(a){
        document.getElementById('alert').style.display = a;
    }

---------------------------------------------------------------------------------------------
<!--function 활용 예시-->
<body>

    <div class="alert-box" id="alert">
        <p id="title">알림창</p>
        <button onclick="알림창('none')">닫기</button>
    </div>

    <button onclick="알림창('block', '아이디입력하세요')">버튼1</button>
    <button onclick="알림창('block', '비번입력하세요')">버튼2</button>
    
    <script>

    function 알림창(a,b){
        document.getElementById('alert').style.display = a;
        document.getElementById('title').innerHTML = b;
    }

    </script>
</body>
```

* 이벤트리스너

* 이벤트리스너 내부의 function()을 순차적으로 실행된다 해서 콜백함수라 한다.

* onclick()을 사용하지 않아도 js코드만으로 html을 제어할 수 있다.

*event - 클릭, 입력, 스크롤, 드래그 등 조작등 가하는 것들 모두

```js

<div class="alert-box" id="alert">
    <p id="title">알림창</p>
    <button id="close">닫기</button>
</div>

<script>
    <!--id가 close인 html 요소에 click 이벤트가 발생하면 function()을 실행한다 -->
     document.getElementById('close').addEventListener('click', function(){
         document.getElementById('alert').style.display = 'none';
     }); 
</script>

-----------------------------------------------------------------------------------
<!--이벤트 리스너 활용-->

<!--네비게이션 바-->
<nav class="navbar navbar-light bg-light">
        
         <div class="container-fluid">
            
            <span class="navbar-brand">Navbar</span>
            
            <button class="navbar-toggler" type="button">
                
                <span class="navbar-toggler-icon"></span>
                
            </button>
        </div>
    </nav>
    
    <!--list-group 클래스는 display: none 상태로 숨김 상태이다.-->
    <!--클래스가 navbar-toggler-icon인 버튼을 누르면 아래 리스트가 생기거나 사라진다 -->
    <ul class="list-group">
        <li class="list-group-item">An item</li>
        <li class="list-group-item">A second item</li>
        <li class="list-group-item">A third item</li>
        <li class="list-group-item">A fourth item</li>
        <li class="list-group-item">And a fifth one</li>
    </ul>
    
    <script>
        
        <!--navbar-toggler-icon html 요소를 클릭하면 list-group 요소를 찾아 show 클래스가 없다면 붙이고, 있다면 없앤다  -->
        <!--show클래스 - display: block-->
        document.getElementsByClassName('navbar-toggler-icon')[0].addEventListener('click', function(){
            document.getElementsByClassName('list-group')[0].classList.toggle('show');
        })
        
    </script>
```

* 쿼리셀렉터

* html 요소를 찾을때 간편하게 사용할 수 있다.

```js
<script>

    <!--클래스가 list-group인 요소 중 맨 위의 것만 찾는다.-->
    document.querySelector('.list-group')

    <!--id가 list-group인 요소 중 맨 위의 것만 찾는다.-->
    document.querySelector('#list-group')

    document.querySelector('.list-group')

    <!--클래스가 list-group인 요소 중 위에서 3번째 것을 찾는다.-->
    document.querySelectorAll('.list-group')[2]

</script>
```

* j쿼리. body태그나 head 태그 내부에 cnd을 작성하면 사용할 수 있다.

* 자바스크리브 라이브러리로 코드양을 줄여준다.

* j쿼리와 자바스크립트를 .으로 섞어서 사용할 수 없다.

```js

<p class="hello">안녕</p>
    
    <script>
        
        <!-- 자바스크립트 코드-->
        //document.querySelector('.hello').innerHTML = '바보';
        
        <!--j쿼리 적용-->
        <!--document.querySelector가 $()로 줄었다.-->
        $('.hello').html('바보');
        $('.hello').css('color', 'red');

        <!--자바스크립트 이벤트 리스너-->
        document.querySelector('.hello').addEventListener('click', function(){})

        <!--j쿼리 이벤트 리스너. addEventListener가 on으로 바뀌었다.-->
        $('.hello').on('click', function(){})
    </script>
```

* 모달

```js
<!--모달창. none 상태-->
<div class="black-bg">
    <div class="white-bg">
        <h4>로그인하세요</h4>
        <button class="btn btn-danger" id="close">닫기</button>
    </div>
</div>

<script>
    <!--로그인 버튼 클릭하면 show-modal 클래스(block 속성 css)를 붙여 모달을 드러낸다-->
    $('#login').on('click', function(){
            $('.black-bg').addClass('show-modal');
        })
        
    <!--닫기 버튼을 누르면 show-modal 클래스를 제거해 모달을 none으로 되돌린다.-->
    $('#close').on('click', function(){
            document.querySelectorAll('.black-bg')[0].classList.toggle('show-modal');
    })

</script>
```
