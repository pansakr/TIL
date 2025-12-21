### 검증 어노테이션 커스텀

```java
@Documented
@Constraint(validatedBy = {}) // 별도의 Validator 클래스가 없다는 의미
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.CONSTRUCTOR })
@Retention(RetentionPolicy.RUNTIME)

// 조합된 검증 어노테이션
@NotBlank(message = "이메일을 입력하세요")
@Email(message = "이메일 형식이 올바르지 않습니다")
public @interface ValidEmail {

    // 검증 실패 시 기본 메시지(사용자가 덮어쓰면 그 메시지가 우선됨)
    String message() default "유효하지 않은 이메일입니다";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
```

* @Documented

    - JavaDoc 등에 표시될 수 있는 어노테이션이라는 의미

    - 없어도 동작하지만 관례적으로 넣음

* @Constraint

    - 이 어노테이션은 Bean Validation(제약 조건) 이라고 선언한는 부분

    - validatedBy = {} 는 Validator 클래스를 사용하지 않고, 내부에 조합된 @NotBlank, @Email 을 그대로 사용하겠다는 의미

* @Target

    - 어디에 적용할 수 있는지 지정

* @Retention(RetentionPolicy.RUNTIME)

    - 런타임까지 어노테이션 정보 유지

    - Bean Validation이 런타임에 동작하므로 필수

* 주의점

    - groups(), payload() 도 필수로 정의해야 한다

    - Bean Validation 스펙은 모든 @Constraint 어노테이션이 반드시 이 3가지를 가져야 한다고 규정하고 있다

    ```java
    String message()

    Class<?>[] groups()

    Class<? extends Payload>[] payload()
    ```
    
    - 하나라도 없을 시 ConstraintDefinitionException 예외가 발생함

    - Class<?>[] groups()

        - 검증을 그룹별로 나눌 때 사용

        ```java
        @Email(groups = SignUpCheck.class)
        ``` 

        - 거의 사용되지 않음

    - Class<? extends Payload>[] payload()

        - 검증 실패 시 추가 메타데이터를 전달하는 용도

        - 거의 사용되지 않음
