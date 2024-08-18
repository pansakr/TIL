### view 템플릿에 컨버터 적용

* view 로 데이터를 보여줄땐 문자로 보여주기 때문에 객체나 숫자들을 문자로 변환해줘야 한다

* 타임리프는 ${{...}} 를 사용하면 자동으로 컨버전 서비스를 사용해서 변환된 결과를 출력해준다

* 스프링이 제공하는 컨버전 서비스를 사용하므로, 우리가 등록한 컨버터들을 사용할 수 있다

* 타임리프 일반 변수 표현식 - ${...}

* 컨버전 서비스 적용 - ${{...}}

* th:field 는 에외적으로 컨버전 서비스를 적용하지 않아도 컨버팅이 적용된다

```java
@GetMapping("/converter-view")
public String converterView(Model model){
    model.addAttribute("number", 10000);
    model.addAttribute("ipPort", new IpPort("127.0.0.1", 8080));
    return "converter-view";
}
```
```html
<body>
<ul>

    <!--숫자 -> 문자 변환은 타임리프가 자동 변환한다-->
    <li>${number}: <span th:text="${number}" ></span></li>

    <!--Integer 타입인 10000을 String 타입으로 변환하는 직접 만든 컨버터가 적용되었다-->
    <!--그런데 숫자 -> 문자 변환은 타임리프가 자동 변환하기 때문에 컨버터를 적용하지 않을 때와 같다-->
    <li>${{number}}: <span th:text="${{number}}" ></span></li>

    <!--IpPort객체가 출력됨. 이 경우 객체.toString이 출력된다-->
    <li>${ipPort}: <span th:text="${ipPort}" ></span></li>

    <!--IpPort 객체를 String으로 변환하는 직접 만든 컨버터가 실행되어 문자가 출력된다-->
    <li>${{ipPort}}: <span th:text="${{ipPort}}" ></span></li>

    <!-- 실행 결과
    ${number}: 10000
    ${{number}}: 10000
    ${ipPort}: hello.typeconverter.type.IpPort@59cb0946
    ${{ipPort}}: 127.0.0.1:8080 -->
</ul>
</body>
```
