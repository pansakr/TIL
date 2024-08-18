### Formatter

* 객체를 특정한 포멧에 맞추어 문자로 출력하거나 그 반대의 역할을 하는 것에 특화된 기능을 가짐

* 숫자 1000을 문자 1000 으로 타입을 변환할때 천 단위에 쉼표를 넣어 1,000 으로 출력하거나, 날짜 객체를 2024-01-01 12:00:00 같이 포맷을 해 출력할때 사용한다  

* Converter는 입력과 출력 타입에 제한이 없는 범용 타입 변환 기능이고, Formatter는 문자 변환에 특화되었다고 볼 수 있다

```java
@Slf4j
public class MyNumberFormatter implements Formatter<Number> {

    // 문자 -> 숫자
    @Override
    public Number parse(String text, Locale locale) throws ParseException {
        log.info("text={}, locale={}", text, locale);
        NumberFormat format = NumberFormat.getInstance(locale);
        return format.parse(text);
    }

    // 숫자 -> 문자
    @Override
    public String print(Number object, Locale locale) {
        log.info("object={}, locale={}", object, locale);
        return NumberFormat.getInstance(locale).format(object);
    }
}
```
```java
class MyNumberFormatterTest {

    MyNumberFormatter formatter = new MyNumberFormatter();

    @Test
    void parse() throws ParseException {
        Number result = formatter.parse("1,000", Locale.KOREA);
        assertThat(result).isEqualTo(1000L); //Long 타입 주의
    }
    @Test
    void print() {
        String result = formatter.print(1000, Locale.KOREA);
        assertThat(result).isEqualTo("1,000");
    }
}
```

### Formatter 를 지원하는 컨버전 서비스

* 컨버전 서비스에는 컨버터만 등록할 수 있고, 포맷터를 등록할 수 는 없다

* 포맷터를 지원하는 컨버전 서비스를 사용하면 컨버전 서비스에 포맷터를 추가할 수 있다

```java
@Test
void formattingConversionService() {

    // FormattingConversionService 는 ConversionService 관련 기능을 상속받아 컨버터, 포맷터 둘다 등록할 수 있다
    DefaultFormattingConversionService conversionService = new
DefaultFormattingConversionService();
    
    // 컨버터 등록
    conversionService.addConverter(new StringToIpPortConverter());
    conversionService.addConverter(new IpPortToStringConverter());
    
    // 포맷터 등록
    conversionService.addFormatter(new MyNumberFormatter());

    // 컨버터 사용
    IpPort ipPort = conversionService.convert("127.0.0.1:8080", IpPort.class);
    assertThat(ipPort).isEqualTo(new IpPort("127.0.0.1", 8080));

    // 포맷터 사용
    // 포맷터도 convert() 로 사용할 수 있다
    assertThat(conversionService.convert(1000, String.class)).isEqualTo("1,000");
    assertThat(conversionService.convert("1,000", Long.class)).isEqualTo(1000L);
}
```

* 포맷터 적용

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addFormatters(FormatterRegistry registry) {
    
    // MyNumberFormatter()와 아래 두 컨버터의 문자 -> 숫자, 숫자 -> 문자 변환 기능이 겹치기 때문에 컨버터가 우선순위가 되어 주석처리 
    // registry.addConverter(new StringToIntegerConverter());
    // registry.addConverter(new IntegerToStringConverter());

    // 컨버터
    registry.addConverter(new StringToIpPortConverter());
    registry.addConverter(new IpPortToStringConverter());

    // 포맷터
    registry.addFormatter(new MyNumberFormatter());
}
}
```
