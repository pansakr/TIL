### Mockito

* 가짜 객체를 쉽게 만들고 관리하게 해주는 도구

    - 단위 테스트는 특정 메서드(테스트 대상)의 로직이 정확히 동작하는지 독립적으로 검증하는 것이 목표

    - 이때 테스트 대상 메서드가 의존하는 다른 클래스들(DB 접근, 외부 API 호출, 라이브러리 등)을 가짜(Mock) 객체로 대체하는데, 이 가짜 객체를 쉽게 만들고 관리하게 해줌

* 테스트에 실제 의존성 객체를 사용하면 발생하는 문제

    - 테스트 속도가 느려짐 : DB 에 실제 데이터를 저장하거나 삭제하는 과정은 속도가 느림

    - 예측 불가능한 결과 : 외부 API 응답이 네트워크 상황이나 외부 서버 상태에 따라 달라질 수 있음

    - 테스트 환경 구축의 어려움 : 테스트를 위해 복잡한 환경 설정이 필요할 수 있음

* Mocito 를 사용하면 의존성들을 통제해서 오직 테스트 대상 클래스의 비즈니스 로직에만 집중할 수 있음

* 주요 기능

    - 내가 원하는 행동 정의

        ```java
        // 예시: when(A가 호출되면).thenReturn(B를 반환해라)
        given(userQueryRepository.existsByEmail(anyString())).willReturn(false);
        ```

        - existsByEmail() 호출 시 무조건 false 로 응답하도록 설정

    - 검증

        ```java
        // 예시: verify(이 객체는).times(한 번).save 메소드가 호출되어야 한다
        verify(userRepository, times(1)).save(any(User.class));
        ```

        - userRepository 객체는 한번만 save() 메서드가 호출되어야 한다
