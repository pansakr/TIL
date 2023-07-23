### open-in-view, DB접근 세션 생성, 종료 시점

* 디스패처 서블릿이 알맞은 컨트롤러를 찾을때 db에 접근할 수 있는 세션이 만들어진다.

* yml의 jpa:one-in-view 옵션이 false일경우 로직 처리후 service에서 controller로 돌아갈때 세션이 종료된다.

* 이때 FetchType.LAZY면 템플릿 엔진에서 Entity의 참조 객체를 호출할때 세션이 종료되었기에 예외가 발생한다. 

* FetchType.EAGER 이라면 db를 조회할때 참조 테이블까지 모두 영속성 컨텍스트에 저장하기 때문에 세션이 종료되어도 괜찮다.

* yml의 jpa:one-in-view 옵션이 true 일경우 view단까지 세션을 open한다는 의미이므로 컨트롤러에서 디스패처 서블릿으로 응답할때 세션이 종료된다.

* view까지 db세션이 살아있기에 FetchType.LAZY도 예외가 발생하지 않고 데이터를 받아올 수 있다.


### 영속성 컨텍스트의 변경된 오브젝트 감지

* service가 끝나는 시점에 변경된 오브젝트를 db에 자동 flush한다. - 더티체킹

* @Transactional(readOnly = true)옵션을 주면 변경 감지를 안한다.

* 변경 감지를 하는 연산이 실행되기 않기 때문에 성능이 조금 올라간다.
