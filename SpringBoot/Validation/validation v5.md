### Bean Validation

* 검증 로직을 어노테이션으로 사용해 모든 프로젝트에 적용할 수 있게 공통화하고, 표준화한 것

* 검증 어노테이션과 인터페이스들의 모음이고, 실제로는 이를 구현한 구현체들을 사용한다

* 일반적으로 사용하는 구현체는 하이버네이트 Validator(검증기 또는 검증 클래스)이다. 이름만 하이버네이트고 ORM과는 관련이 없다

* javax.validation 으로 시작하면 특정 구현에 관계없이 제공되는 표준 인터페이스이고, org.hibernate.validator 로 시작하면 하이버네이트 validator 구현체를 사용할 때만 제공되는 검증 기능이다. 요즘 웹 애플리케이션은 하이버네이트도 의존성 추가해서 사용하기 때문에 신경쓰지 않아도 된다

* @Valid - 자바 표준 검증 어노테이션, @Validated - 스프링 전용 검증 어노테이션

```
// Bean Validation 동작 과정
1. 스프링 부트는 spring-boot-starter-validation 라이브러리를 추가하면 LocalValidatorFactoryBean 을 글로벌 Validator로 등록한다.

2. 이 Validator는 @NotNull 같은 애노테이션을 보고 검증을 수행한다

3. 이렇게 글로벌 Validator가 적용되어 있기 때문에, @Valid, @Validated 만 적용하면 된다.

4. 검증 오류가 발생하면, FieldError, ObjectError 를 생성해서 BindingResult 에 담아준다

// 글로벌 Validator를 직접 등록하면 스프링 부트는 Bean Validator를 글로벌 Validator 로 등록
하지 않는다. 따라서 어노테이션 기반의 빈 검증기가 동작하지 않는다.
```
```
// 검증 순서
1. httpRequest 파라미터를 @ModelAttribute가 적용된 객체의 필드에 바인딩
2. 성공하면 다음, 실패하면 typeMismatch로 FieldError 추가

// 바인딩에 성공한 필드만 Bean Validation이 적용된다
// 숫자 범위를 제한하는 검증이 적용되어있는 필드에 문자가 들어오면 의미가 없기 때문
```
```java
@Data
public class Item {

    @NotNull
    private Long id;

    @NotBlank(message = "공백x")
    private String itemName;

    @NotNull
    @Range(min = 1000, max = 1000000)
    private Integer price;

    @NotNull
    @Max(9999)
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}

@Controller
..class{

    // 검증할 객체 앞에 검증을 수행한다는 뜻인 @Validated 추가, 검증할 객체 뒤에 에러 넣어줄 BindingResult 추가
    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @Validated @ModelAttribute Item item, BindingResult bindingResult) {
        ...
    }
}
```

### 동일한 모델 객체를 다르게 검증할때

* 같은 모델 객체를 검증하지만 검증 방법은 다를 때 사용한다

#### groups

* @Validated의 groups 옵션을 사용. @Valid 에는 해당 옵션이 없다

* 거의 사용 안함

```java
// 저장용 그룹 생성
public interface SaveCheck {
}

// 수정용 그룹 생성
public interface UpdateCheck {
}

// 그룹 적용
@Data
public class Item {

    @NotNull(groups = UpdateCheck.class) //수정시에만 적용
    private Long id;

    @NotBlank(groups = {SaveCheck.class, UpdateCheck.class})
    private String itemName;

    @NotNull(groups = {SaveCheck.class, UpdateCheck.class})
    @Range(min = 1000, max = 1000000, groups = {SaveCheck.class, UpdateCheck.class})
    private Integer price;

    @NotNull(groups = {SaveCheck.class, UpdateCheck.class})
    @Max(value = 9999, groups = SaveCheck.class) //등록시에만 적용
    private Integer quantity;

    ...
}

// 저장 로직에 적용. 검증할 객체에 SaveCheck.class가 붙어있는 필드만 검증한다
@PostMapping("/add")
public String addItemV2(@Validated(SaveCheck.class) @ModelAttribute Item item,
BindingResult bindingResult, RedirectAttributes redirectAttributes) {
    ...
}

// 수정 로직에 적용. 검증할 객체에 UpdateCheck.class가 붙어있는 필드만 검증한다
@PostMapping("/{itemId}/edit")
public String editV2(@PathVariable Long itemId, @Validated(UpdateCheck.class)
@ModelAttribute Item item, BindingResult bindingResult) {
    ...
}
```

#### 전용 요청, 응답 객체 생성

* 요청마다 바인딩할 객체를 생성해준다. 실제로 주로 사용하는 방법
