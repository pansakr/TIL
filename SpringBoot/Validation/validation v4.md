### 검증 로직 V1

* 검증 로직 부분을 분리해 별도의 검증 클래스로 만들고, 컨트롤러에서 검증 클래스를 사용한다

```java
// 검증 클래스. @Component로 스프링 빈으로 자동 등록한다
// Validator - 스프링에서 제공하는 검증 인터페이스
@Component
public class ItemValidator implements Validator {

    // 파라미터로 넘어오는 클래스가 Item 타입과 같은지 확인. 자식클래스도 통과
    // 지금 코드에서는 사용되지 않고 아래 검증 로직 V2에서 사용됨
    @Override
    public boolean supports(Class<?> clazz) {
        return Item.class.isAssignableFrom(clazz);
    }

    // target - 검증 대상 객체, errors - BindingResult
    @Override
    public void validate(Object target, Errors errors) {
        Item item = (Item) target;

        // 검증 로직
        if(!StringUtils.hasText(item.getItemName())){
            errors.rejectValue("itemName", "required");
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){
            errors.rejectValue("price", "range", new Object[]{1000, 1000000}, null);
        }
        if(item.getQuantity() == null || item.getQuantity() >= 9999){
            errors.rejectValue("quantity", "max", new Object[]{9999}, null);
        }

        // 특정 필드가 아닌 복합 룰 검증
        if(item.getPrice() != null && item.getQuantity() != null){
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000){
                errors.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }
    }
}

@Controller
@RequiredArgsConstructor
..class{

    // 의존성 생성자 주입
    private final ItemValidator itemValidator;

    @PostMapping("/add")
    public String addItemV5(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        
        // 검증 클래스 직접 호출
        itemValidator.validate(item, bindingResult);

        // 검증 실패시 ..

        // 성공 로직
        ...
    }
}
```

### 검증 로직 V2

* @Validated를 사용해 검증 클래스 자동 호출

* @Validated - 검증 클래스를 실행하라는 어노테이션

```java
// 검증 클래스는 동일

@Controller
@RequiredArgsConstructor
..class{

    private final ItemValidator itemValidator;

    // WebDataBinder를 추가함으로서 이 컨트롤러에서는 검증 클래스를 자동으로 적용할 수 있다
    // WebDataBinder - 스프링 mvc에서 사용하는 클래스로 객체에 파라미터 바인딩, 검증 역할을 한다
    // @InitBinder - 이 컨트롤러에서만 작동. 글로벌 설정은 별도로 해야함
    @InitBinder
    public void init(WebDataBinder dataBinder){
        dataBinder.addValidators(itemValidator);
    }

    // @Validated - 이 어노테이션이 붙으면 WebDataBinder에 등록한 검증 클래스를 찾아서 실행한다
    // 여러 검증기를 등록한다면 검증 클래스의 supports()를 호출해 true인 것을 호출한다
    // 검증 클래스 validate()로 검증할때 Item과 BindingResult를 매개변수로 사용한다
    // 검증 결과는 bindingResult에 담긴다
    @PostMapping("/add")
    public String addItemV5(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        // 검증 실패시 ..

        // 성공 로직
        ...
    }
}
```
