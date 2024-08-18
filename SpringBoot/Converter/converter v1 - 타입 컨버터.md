### 스프링 타입 컨버터

* HTTP 요청 파라미터는 모두 문자로 처리된다. 

* 요청 파라미터를 자바에서 다른 타입으로 변환해서 사용하고 싶으면 숫자 타입으로 변환하는 과정을 거쳐야 한다.

```java
@RestController
public class HelloController {

    // /hello-v1?data=10 요청
    @GetMapping("/hello-v1")
    public String helloV1(HttpServletRequest request){

        // 요청 파라미터로 넘어온 data = 10 은 문자 타입이다
        String data = request.getParameter("data");

        // 그렇기 때문에 숫자로 변환하는 과정을 거쳐야 한다
        Integer intValue = Integer.valueOf(data);

        System.out.println("intValue = " + intValue);
        return "ok";
    }
}
```

* 과거에는 개발자가 직접 변환했지만 스프링 사용시 자동 변환된다

```java
// /hello-v2?data=10 요청
// 10 은 숫자 타입이지만 숫자 타입으로 변환되어 data 변수에 바인딩 된다
@GetMapping("/hello-v2")
public String helloV2(@RequestParam Integer data) {
    
    System.out.println("data = " + data);
    return "ok";
}
```

* 만약 개발자가 새로운 타입을 만들어 변환하고 싶으면 컨버터 인터페이스를 구현해 등록하면 된다


### 사용자 지정 타입 컨버터

* Converter<S, T> 인터페이스를 구현해 사용자 지정 컨버터를 만들 수 있다 

* Converter<받는 타입, 응답 타입>

* Converter 경로 - org.springframework.core.convert.converter 


```java
@Getter
@EqualsAndHashCode
public class IpPort {

    private String ip;
    private int port;

    public IpPort(String ip, int port){
        this.ip = ip;
        this.port = port;
    }
}
```
```java
// String 을 받아 IpPort 객체로 변환해주는 컨버터
@Slf4j
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
```java
// IpPort 객체를 받아 String 으로 변환해주는 컨버터
@Slf4j
public class IpPortToStringConverter implements Converter<IpPort, String> {

    @Override
    public String convert(IpPort source) {
        log.info("convert source={}", source);

        // IpPort 객체 -> "127.0.0.1:8080"
        return source.getIp() + ":" + source.getPort();
    }
}
```

```java
// 테스트
@Test
void stringToIpPort() {

    StringToIpPortConverter converter = new StringToIpPortConverter();
    String source = "127.0.0.1:8080";
    IpPort result = converter.convert(source);
    assertThat(result).isEqualTo(new IpPort("127.0.0.1", 8080));

}

@Test
void ipPortToString() {

    IpPortToStringConverter converter = new IpPortToStringConverter();
    IpPort source = new IpPort("127.0.0.1", 8080);
    String result = converter.convert(source);
    assertThat(result).isEqualTo("127.0.0.1:8080");
    
}
```
