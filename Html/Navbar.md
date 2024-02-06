### navbar

* 부모 태그에 작성한 css를 상속을 통해 자식 태그의 특정 태그에 적용하게 할 수 있다.

* 한번만 작성하면 여러 자식 태그에 적용할 수 있어 코드를 줄일 수 있다.

```html
!--div와 기능 똑같다. 가독성을 위해서 이름만 다르다.-->
    <nav>
        <ul class="navbar">
            <li><a href="#">영화</a></li>
            <li><a href="#">맛집</a></li>
            <li><a href="#">IT</a></li>
            <li><a href="#">컴퓨터</a></li>
        </ul>
    </nav>
```

```css
/*.navbar 클래스 안의 모든 자식의 li태그에 적용*/
.navbar li{
    display: inline-block; 
}

/*.navbar 클래스 안의 모든 자식의 a태그에 적용*/
.navbar a{
    font-size: 16px;
    text-decoration: none; /* a 태그의 밑줄 제거. 
                           클릭시 보라색으로 되는걸 없애는 옵션도 있다고 한다.*/
}               

/*
.navbar>li - 클래스 안의 직계 자식의 li태그에 적용(바로 아래 태그만)
.navbar>li{
    display: inline-block;
}
*/
```
