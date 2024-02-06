### div태그

* 박스 하나를 추가한다. display: block속성 가지고 있어 가로줄 전체를 차지한다.

* div만 추가하면 보이지 않고, 글자를 작성하거나 크기를 조절해 색상을 주면 박스가 보인다.

### 태그 css 속성의 상속

* 일부 css 속성은 자식 태그가 물려받아 적용된다.

* 상속되는 속성은 중요도가 낮기 때문에 자식 태그에 같은 속성이 있다면 자식 태그의 속성이 적용된다. 

*ex) <p><span></span></p> - p태그가 부모, span 태그가 자식이다.

<img src="div태그">

```html
<html>
<head>
    <meta charest="utf-8">
    <title>Document</title>
    <link href="css/main.css" rel="stylesheet">
</head>
<body>
    
    <img src="lion.png" class="profile">
    <h3 class="title">Harry1</h3>
    <p class="content">Front-end Developer</p>
    
    <!--화면 분할. 네모 박스를 넣는다.-->
    <!--폰트 정렬, 사이즈 같은 일부 속성들은 부모 태그에 작성하면 자식 태그가 상속(interit)받는다. -->
    <div class="box">
        <p>안녕하세요 개발자입니다</p>
    </div>
    
</body>
</html>
```

```css
.profile {
width: 100px; 
display: block; 
margin-left: auto; 
margin-right: auto
}

.title{
text-align: center; 
font-size: 16px;
}

.content{
    text-align: center;
}

.box {
    width: 200px;
    background-color: cadetblue;
    margin: 10px;  /*margin = 상하좌우 30px 여백, margin-top = 위쪽만 여백*/ 
    padding: 40px; /*안쪽 여백, padding-top = 위쪽만 안쪽 여백*/
    border: 4px solid black;  /*border: 테두리크기, 종류, 색상 - 테두리*/ 
    border-radius: 5px; /*모서리 둥글게*/
    
    /*display: block;   가운데 정렬 스타일. 그런데 div 태그는
    margin-left: auto;  display: block속성을 가지고 있어서 빼도 된다.
    margin-right: auto; display: block은 가로행을 전부 차지하는 속성이다. */
    
    margin-left: auto;  /*display: block뺀 가운데 정렬*/
    margin-right: auto;
    
    text-align: center; /*글자 가운데 정렬*/
    font-size: 15px;
    color: white;
    
    /* 그림자 효과
    box-shadow: none | x-position y-position blur spread color | inset | initial | inherit
    none : 그림자 효과를 없앤다.
    x-position : 가로 위치이다. 양수면 오른쪽에, 음수면 왼쪽에 그림자가 만들어진다. (필수)
    y-position : 세로 위치이다. 양수면 아래쪽에, 음수면 위쪽에 그림자가 만들어진다. (필수)
    blur : 그림자를 흐릿하게 만든다. 값이 클 수록 더욱 흐려진다.
    spread : 양수면 그림자를 확장하고, 음수면 축소한다.
    color : 그림자 색을 정한다.
    inset : 그림자를 요소의 안쪽에 만든다.
    initial : 기본값으로 설정한다.
    inherit : 부모 요소의 값을 상속받는다.*/
    box-shadow: 15px 15px 0px 5px black;
}
```
