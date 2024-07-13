### 검증

* 요청에 포함된 데이터가 올바른지 확인
```java
// dto
@Data
public class Item {

    private Long id;
    private String itemName;
    private Integer price;
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}

// 
@Controller
...class{

    // 상품 등록 요청시 실행
    // 요청 데이터를 item에 매핑하고 item에 올바른 데이터가 들어있는지 검증. 검증 실패 시 예외
    @PostMapping("/add")
    public String addItem(@ModelAttribute Item item, RedirectAttributes redirectAttributes, Model model) {

        // 검증 오류 결과를 보관
        Map<String, String> errors = new HashMap<>();

        // 검증 로직
        if(!StringUtils.hasText(item.getItemName())){
            errors.put("itemName", "상품 이름은 필수입니다.");
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){
            errors.put("price", "가격은 1,000 ~ 1,000,000 까지 허용합니다");
        }
        if(item.getQuantity() == null || item.getQuantity() >= 9999){
            errors.put("quantity", "수량은 최대 9,9999 까지 허용합니다");
        }

        // 특정 필드가 아닌 복합 룰 검증
        if(item.getPrice() != null && item.getQuantity() != null){
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000){
                errors.put("globalError", " 가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice);
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if(!errors.isEmpty()){
            log.info("errors = {} ", errors);
            model.addAttribute("errors", errors);
            return "validation/v1/addForm";
        }

        // 성공 로직
        ...
}

// 국제화 설정파일
label.item=상품
label.item.id=상품 ID
label.item.itemName=상품명
...
```
```html
    <!--글로벌 에러 세팅.  errors객체에 'globalError' 가 나오지 않는다면 렌더링 되지 않는다-->
    <div th:if="${errors?.containsKey('globalError')}">
        <p class="field-error" th:text="${errors['globalError']}">전체 오류 메시지</p>
    </div>

    <div>
        <!--label.item.itemName은 국제화 설정파일 값 사용-->
        <label for="itemName" th:text="#{label.item.itemName}">상품명</label>

         <!--errors객체에 'itemName' 이 있으면 html tag class를 form-control field-error(예외 전용 css, html 설정), 없으면 form-control(정상) 으로 바꿔준다-->
         <input type="text" id="itemName" th:field="*{itemName}"
                th:class="${errors?.containsKey('itemName')} ? 'form-control field-error' : 'form-control'"
                 class="form-control" placeholder="이름을 입력하세요">

         <!--errors객체에 'itemName' 이 있으면 itemName의 값을 입력, 없다면 div 태그 렌더링 되지 않음-->    
         <div class="field-error" th:if="${errors?.containsKey('itemName')}" th:text="${errors['itemName']}">
               상품명 오류
        </div>
      </div>
```

* errors?. 에서 .은 springEL이 제공하는 문법으로 앞에 오는 객체가 null일때 NPE 발생 대신, null을 반환하는 문법이다

* th:if 에서 null 반환시 실패 처리되어 해당 태그가 렌더링 되지 않는다

* StringUtils 설명 - https://github.com/pansakr/TIL/blob/main/SpringBoot/%EC%9C%A0%ED%8B%B8%20%ED%81%B4%EB%9E%98%EC%8A%A4/StringUtils.md
