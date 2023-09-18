### GitFlow

* 협업을 위한 브랜치 전략

<img src="https://github.com/pansakr/TIL/assets/118809108/ea69ec5c-b2f2-4db9-a23c-db6ad1e38f07">

#### main 브랜치

* 사용자들에게 서비스할 버전들이 최종적으로 merge되는 곳

* tag를 붙여 버전을 명시한다.


#### Develop 브랜치

* 다음 출시 버전, 배포를 위해 개발을 하는 브랜치


#### feature 브랜치

* 기능 개발을 하는 브랜치

* 새로운 기능이 필요할 때마다 Develop로부터 분기한다.

* 개발이 완료되면 Develop로 merge한다.


#### release 브랜치

* 출시 전 최종 테스트를 하는 브랜치

* Develop로부터 분기한다.

* 만약 수정사항이 생긴다면 다시 Develop브랜치로 merge해 수정 한다.

### hotfixes 브랜치

* 출시된 버전에서 발생한 버그를 수정하는 브랜치

* main으로부터 분기한다.
