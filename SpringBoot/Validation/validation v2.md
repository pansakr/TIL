### 검증2 - BindingResult 사용

* BindingResult - 스프링이 제공하는 검증 오류를 보관하는 객체

* @ModelAttribute에 바인딩 시 타입 오류가 발생하면 BindingResult가 없을땐 400에러가 발생하고, 있다면 오류 정보 FieldError를 BindingResult에 담아 컨트롤러를 정상 호출하게 된다

* @ModelAttribute 의 객체에 타입 오류 등으로 바인딩이 실패하는 경우 스프링이 FieldError를 생성해서 BindingResult 에 넣어준다

* BindingResult 는 검증할 대상 바로 다음에 와야한다

* BindingResult 는 Model에 자동으로 포함된다

* 필드에 오류가 있으면 FieldError 객체를 생성해서 bindingResult 에 담아두면 된다

* 특정 필드를 넘어서는 오류가 있으면 ObjectError 객체를 생성해서 bindingResult 에 담아두면 된다

```
// field error 파라미터 목록
objectName : 오류가 발생한 객체 이름
field : 오류 필드
rejectedValue : 사용자가 입력한 값(거절된 값)
bindingFailure : 타입 오류 같은 바인딩 실패인지, 검증 실패인지 구분 값
codes : 메시지 코드
arguments : 메시지에서 사용하는 인자
defaultMessage : 기본 오류 메시지
```

```java
@Controller
...class{

    // BindingResult의 순서는 반드시 @ModelAttribute 뒤에 와야한다 
    @PostMapping("/add")
    public String addItemV1(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        // 검증 로직
        if(!StringUtils.hasText(item.getItemName())){
            // item.getItemName() 파라미터는 사용자가 입력한 값이고, view에서 보여주기 위해 사용한다
            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, null, null, "상품 이름은 필수 입니다."));
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){
            bindingResult.addError(new FieldError("item", "price", item.getPrice(), false, null, null, "가격은 1,000 ~ 1,000,000 까지 허용 합니다"));
        }
        if(item.getQuantity() == null || item.getQuantity() >= 9999){
            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), false, null, null, "수량은 최대 9,9999 까지 허용 합니다"));
        }

        // 성공 로직
        ...
}
```
```html
   
<!--th:object="${item}"는 모델에서 전달해준 객체 -->
<form action="item.html" th:action th:object="${item}" method="post">

        <!--#fields 로 BindingResult 가 제공하는 검증 오류에 접근할 수 있다-->
        <div th:if="${#fields.hasGlobalErrors()}">
            <p class="field-error" th:each="err : ${#fields.globalErrors()}" th:text="${err}">글로벌 오류 메시지</p>
        </div>

        <div>
            <label for="itemName" th:text="#{label.item.itemName}">상품명</label>

            <!-- th:field="*{itemName} 이름으로 bindingResult에 오류가 있는지 확인 !-->
            <!--th:errorclass=".." - 에러가 있으면 html의 class에 지정한 클래스를 이어줌-->
            <input type="text" id="itemName" th:field="*{itemName}"
                   th:errorclass="field-error" class="form-control" placeholder="이름을 입력하세요">

            <!--th:errors : 해당 필드에 오류가 있는 경우에 태그를 출력-->
            <div class="field-error" th:errors="*{itemName}">
                상품명 오류
            </div>
        </div>
</form>
```
