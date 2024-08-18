### ConversionService

* 개별 컨버터들을 모아 사용할 수 있는 기능

* ConversionService 인터페이스 사용

```java
    @Test
    void conversionService() {

        // 컨버전 서비스 객체 생성. DefaultConversionService -> ConversionService 구현
        DefaultConversionService conversionService = new DefaultConversionService();

        // 컨버전 서비스에 컨버터 등록
        conversionService.addConverter(new StringToIntegerConverter());
        conversionService.addConverter(new IntegerToStringConverter());
        conversionService.addConverter(new StringToIpPortConverter());
        conversionService.addConverter(new IpPortToStringConverter());

        //사용
        assertThat(conversionService.convert("10", Integer.class)).isEqualTo(10);
        assertThat(conversionService.convert(10, String.class)).isEqualTo("10");

        IpPort ipPort = conversionService.convert("127.0.0.1:8080", IpPort.class);
        assertThat(ipPort).isEqualTo(new IpPort("127.0.0.1", 8080));

        String ipPortString = conversionService.convert(new IpPort("127.0.0.1", 8080), String.class);
        assertThat(ipPortString).isEqualTo("127.0.0.1:8080");
    }
```
```java
// 사용자 지정 컨버터들
public class IntegerToStringConverter implements Converter<Integer, String> {

    @Override
    public String convert(Integer source) {
        log.info("convert source={}" , source);
        return String.valueOf(source);
    }
}

public class StringToIntegerConverter implements Converter<String, Integer> {

    @Override
    public Integer convert(String source) {
        log.info("convert source={}", source);
        return Integer.valueOf(source);
    }
}

public class IpPortToStringConverter implements Converter<IpPort, String> {

    @Override
    public String convert(IpPort source) {
        log.info("convert source={}", source);

        // IpPort 객체 -> "127.0.0.1:8080"
        return source.getIp() + ":" + source.getPort();
    }
}

public class StringToIpPortConverter implements Converter<String, IpPort> {

    @Override
    public IpPort convert(String source) {

        log.info("convert source={}", source);
        String[] split = source.split(":");
        String ip = split[0];
        int port = Integer.parseInt(split[1]);

        return new IpPort(ip, port);
    }
}
```


* 스프링은 내부에서 ConversionService 를 사용해서 타입을 변환한다. 

* @RequestParam 같은 곳에서 이 기능을 사용해서 타입을 변환한다.


### 스프링에 적용

* 스프링은 내부에서 ConversionService 를 사용한다

* WebMvcConfigure 인터페이스의 addFormatters() 를 사용해 컨버터를 등록하기만 하면 스프링은 ConversionService 에 컨버터를 추가해준다 

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 컨버터 등록
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToIntegerConverter());
        registry.addConverter(new IntegerToStringConverter());
        registry.addConverter(new StringToIpPortConverter());
        registry.addConverter(new IpPortToStringConverter());
    }
}
```
```java
@RestController
public class HelloController {

    // /hello-v2?data=10 요청
    // 직접 추가한 StringToIntegerConverter()가 사용된다
    @GetMapping("/hello-v2")
    public String helloV2(@RequestParam Integer data) {
        System.out.println("data = " + data);
        return "ok";
    }

    // StringToIpPortConverter() 사용됨
    @GetMapping("/ip-port")
    public String ipPort(@RequestParam IpPort ipPort) {
        System.out.println("ipPort IP = " + ipPort.getIp());
        System.out.println("ipPort PORT = " + ipPort.getPort());
        return "ok";
    }
}
```

* 스프링 내부에서 많은 기본 컨버터들을 제공하기 때문에 사실 StringToIntegerConverter 는 등록하지 않아도 실행은 잘 된다

* 하지만 StringToIpPortConverter() 는 기본 컨버터에 없기 때문에 추가해 줘야 실행된다

* 컨버터를 추가하면 기본 컨버터보다 우선순위를 가진다
