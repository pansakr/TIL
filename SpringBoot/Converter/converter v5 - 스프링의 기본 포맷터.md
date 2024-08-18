### 스프링이 제공하는 기본 포맷터

* @NumberFormat, @DateTimeFormat 으로 스프링이 지원하는 포맷터를 사용할 수 있다

```java
@Controller
public class FormatterController {

    @GetMapping("/formatter/edit")
    public String formatterForm(Model model) {

        Form form = new Form();
        form.setNumber(10000);
        form.setLocalDateTime(LocalDateTime.now());

        model.addAttribute("form", form);

        return "formatter-form";
    }

    @PostMapping("/formatter/edit")
    public String formatterEdit(@ModelAttribute Form form) {
        return "formatter-view";
    }

    @Data
    static class Form {
        
        // 지정한 포맷으로 출력
        @NumberFormat(pattern = "###,###")
        private Integer number;

        // 지정한 포맷으로 출력
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime localDateTime;
    }
}
```
```html
<body>
    <form th:object="${form}" th:method="post">
        
        number <input type="text" th:field="*{number}"><br/>
        
        localDateTime <input type="text" th:field="*{localDateTime}"><br/>
        
        <input type="submit"/>
    </form>
</body>
```
```html
<body>
<ul>
    <li>${form.number}: <span th:text="${form.number}" ></span></li>
    <li>${{form.number}}: <span th:text="${{form.number}}" ></span></li>
    <li>${form.localDateTime}: <span th:text="${form.localDateTime}" ></span></li>
    <li>${{form.localDateTime}}: <span th:text="${{form.localDateTime}}" ></span><li>
</ul>
</body>

<!-- 결과
${form.number}: 10000
${{form.number}}: 10,000
${form.localDateTime}: 2021-01-01T00:00:00
${{form.localDateTime}}: 2021-01-01 00:00:00
-->
```
