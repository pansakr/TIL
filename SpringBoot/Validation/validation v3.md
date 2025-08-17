### 예외 메시지 처리 V1

* 예외 perperties 파일을 만들어서 FieldError 생성시 인자로 예외 메시지를 넣어줄 수 있다

```java
// 기본 설정파일 - application.properties
// spring.messages.basename=messages 까지는 기본값이고, 그 뒤로 파일 이름을 추가로 입력하면 그 파일도 사용할 수 있다  
spring.messages.basename=messages, errors

// 예외 설정파일 - errors.properties
// 메시지, 국제화 처럼 사용할 수 있다
required.item.itemName=상품 이름은 필수입니다
range.item.price=가격은 {0} ~ {1} 까지 허용합니다
max.item.quantity=수량은 최대 {0} 까지 허용합니다
totalPriceMin=가격 * 수량의 합은 {0}원 이상이어야 합니다. 현재 값 = {1}

@Controller
..class{

    @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        // 검증 로직
        if(!StringUtils.hasText(item.getItemName())){
            // item.getItemName() - rejectedValue, false - bindingFailure, new String[]{".."} - codes
            // null - arguments, null - defaultMessage
            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, new String[]{"required.item.itemName"}, null, null));
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){
            // 다른것은 위와 같고 new Object[]{1000, 1000000} - arguments 가 추가됨
            // arguments는 errors.properties의 {0},{1}에 치환될 값이다
            bindingResult.addError(new FieldError("item", "price", item.getPrice(), false, new String[]{"range.item.price"}, new Object[]{1000, 1000000}, null));
        }
        ...
    }
}

// field 생성자 2가지
public FieldError(String objectName, String field, String defaultMessage);

// 위 코드에서 이걸 사용함
// rejectedValue - 사용자가 입력한 값, bindingFailure - 바인딩 실패 여부
// codes - 오류 메시지, arguments - 오류 메시지에 사용할 인자, defaultMessage - 기본 오류 메시지(오류 메시지 사용시 null로 해도 됨)
public FieldError(String objectName, String field, @Nullable Object rejectedValue, boolean bindingFailure, @Nullable String[] codes, @Nullable Object[] arguments, @Nullable String defaultMessage)
```

### 예외 메시지 처리 V2

* rejectValue() , reject() 를 사용하면 FieldError, ObjectError를 직접 생성하지 않고 단순하게 검증 오류를 다룰 수 있다

* rejectValue() - fieldError, reject() - ObjectError

* rejectValue() , reject()가 FieldError, ObjectError를 생성해준다

* FieldError, ObjectError는 내부적으로 MessageCodesResolver를 사용한다

```java
// 예외 설정파일은 동일

@Controller
..class{

    @PostMapping("/add")
    public String addItemV4(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        // 검증 로직
        if(!StringUtils.hasText(item.getItemName())){
            bindingResult.rejectValue("itemName", "required");
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){
            bindingResult.rejectValue("price", "range", new Object[]{1000, 1000000}, null);
        }
        if(item.getQuantity() == null || item.getQuantity() >= 9999){
            bindingResult.rejectValue("quantity", "max", new Object[]{9999}, null);
        }

        // 특정 필드가 아닌 복합 룰 검증
        if(item.getPrice() != null && item.getQuantity() != null){
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000){
                bindingResult.addError(new ObjectError("item", new String[]{"totalPriceMin"}, new Object[]{10000, resultPrice},  null));
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }
    }
}

//
void rejectValue(@Nullable String field, String errorCode,
@Nullable Object[] errorArgs, @Nullable String defaultMessage);

field : 오류 필드명
errorCode : 오류 코드(이 오류 코드는 메시지에 등록된 코드가 아님. messageResolver를 위한 오류 코드이다.)
errorArgs : 오류 메시지에서 `{0}` 을 치환하기 위한 값
defaultMessage : 오류 메시지를 찾을 수 없을 때 사용하는 기본 메시지
```

### MessageCodesResolver

* 검증 오류 코드로 메시지 코드들을 생성한다

* MessageCodesResolver에서 생성한 메시지 코드들이 ObjectError, FieldError 의 인자로 사용되고, rejectValue()로 ObjectError, FieldError를 생성한다

```java
..class{

    // MessageCodesResolver의 기본 구현체 DefaultMessageCodesResolver
    MessageCodesResolver codesResolver = new DefaultMessageCodesResolver();
     
    // ObjectError의 인자로 messageCodes()에서 생성된 메시지들이 사용됨
    @Test
    void messageCodeResolverObject(){

        // resolveMessageCodes() 결과로 aaa, b 가 생성됨 
        String[] messageCodes = codesResolver.resolveMessageCodes("aaa", "b");
        Assertions.assertThat(messageCodes).containsExactly("aaa.b", "aaa");
    }

    // FieldError의 인자로 messageCodes()에서 생성된 메시지들이 사용됨
    @Test
    void messageCodesResolverFiled(){
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item", "itemName", String.class);

        for (String messageCode : messageCodes) {
            System.out.println("messageCode = " + messageCode);
        }

        // 결과
        // "required.item.itemName",
        // "required.itemName",
        // "required.java.lang.String",
        // "required"
    }
}
```

#### DefaultMessageCodesResolve의 메시지 생성 규칙

* 오류 코드, 오브젝트 이름, 필드, 필드 타입을 활용해 하나가 아닌 여러 메시지 코드들을 만들어낸다

* ObjectError

```java
1 - code + "." + object name
2 - code

ex) 오류 코드: aaa, object name: b
1 - aaa.b
2 - aaa
```

* FieldError

```java
1 - code + "." + object name + "." + field
2 - code + "." + field
3 - code + "." + field type
4 - code

ex) 오류 코드: typeMismatch, object name "user", field "age", field type: int
1. typeMismatch.user.age
2. typeMismatch.age
3. typeMismatch.int
4. typeMismatch

// 오류 메시지 설정 파일.properties
typeMismatch.user.age = 메시지1
typeMismatch.age = 메시지2
typeMismatch.int = 메시지3
typeMismatch = 메시지4
```

* 메시지 코드는 구체적인것에서 덜 구체적인 순서대로 만들어진다 

* 오류 발생시 생성된 여러 메시지 코드들에 대응하는 오류 메시지들을(오류 설정 파일의 메시지) 순서대로 돌아가며 찾는다. 첫번째 오류 메시지가 있으면 띄워주고, 없다면 두번째, 두번째도 없다면 세번째.. 이런 식으로 자세한 것에서 범용적인 순서로 찾는다

* MessageCodesResolver 로 순서대로 생성된 메시지 코드 배열을 FieldError, ObjectError 의 생성자의 인자로 사용하고, rejectValue() , reject()로 FieldError, ObjectError를 간편하게 사용할 수 있다

* 요청 -> 검증 -> 검증 실패 시 bindingResult에 에러 코드, 필드 등 예외 정보 생성 -> 이 정보들로 MessageCodesResolver 규칙에 따라 메시지 코드 생성 -> 메시지 코드에 대응하는 오류 메시지들을 순서대로(구체적인 것부터) 찾아 응답

### 예외 메시지 처리 V3

* MessageCodesResolver 규칙을 이용한 에러 메시지 설정 파일 추가

```java
// 컨트롤러는 동일

// errors.properties
// 제일 구체적인 level1이 응답되고, level1을 주석하면 2가, 그다음은 3.. 순서대로 사용된다
#required.item.itemName=상품 이름은 필수입니다
#range.item.price=가격은 {0} ~ {1} 까지 허용합니다
#max.item.quantity=수량은 최대 {0} 까지 허용합니다
#totalPriceMin=가격 * 수량의 합은 {0}원 이상이어야 합니다. 현재 값 = {1}

#==ObjectError==
#Level1
totalPriceMin.item=상품의 가격 * 수량의 합은 {0}원 이상이어야 합니다. 현재 값 = {1}
#Level2 - 생략
totalPriceMin=전체 가격은 {0}원 이상이어야 합니다. 현재 값 = {1}

#==FieldError==
#Level1
required.item.itemName=상품 이름은 필수입니다
range.item.price=가격은 {0} ~ {1} 까지 허용합니다
max.item.quantity=수량은 최대 {0} 까지 허용합니다

#Level2 - 생략

#Level3
required.java.lang.String = 필수 문자입니다
required.java.lang.Integer = 필수 숫자입니다
min.java.lang.String = {0} 이상의 문자를 입력해주세요
min.java.lang.Integer = {0} 이상의 숫자를 입력해주세요
range.java.lang.String = {0} ~ {1} 까지의 문자를 입력해주세요
range.java.lang.Integer = {0} ~ {1} 까지의 숫자를 입력해주세요
max.java.lang.String = {0} 까지의 문자를 허용합니다
max.java.lang.Integer = {0} 까지의 숫자를 허용합니다

#Level4
required = 필수 값 입니다
min= {0} 이상이어야 합니다
range= {0} ~ {1} 범위를 허용합니다
max= {0} 까지 허용합니다
```
