### WebMvcConfigurer

* @Enable~ 로 시작하는 어노테이션들은 최신 전략들을 기반으로 설정을 자동화해준다.

* @Enable~ 을 통한 자동 설정들 외에 추가의 설정이 필요하면 ~ Configurer로 끝나는 인터페이스(빈 설정자)로
@Enable~이 적용되는 빈에 대해 추가적인 설정을 하거나 ~Configurer단독으로 구현해 오버라이드 할 수 있다.

```
// WebMvcConfigurer인터페이스 메서드 이름의 의미

    add~: 기본 설정이 없는 빈들에 대하여 새로운 빈을 추가함
    configure~: 수정자를 통해 기존의 설정을 대신하여 등록함
    extend~: 기존의 설정을 이용하며 추가로 설정을 확장함

```

* @EnableWebMvc를 통해 자동 설정되는 빈들의 설정자(setter)는 WebMvcConfigurer이다.

* 즉 스프링 컨테이너에 빈으로 자동 등록되는 @EnableWebMvc의 자바 클래스들을 설정해주는 인터페이스가 WebMvcConfigurer 이다.
