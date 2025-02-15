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

### git flow 활용

<img src="https://github.com/pansakr/TIL/assets/118809108/5420d0a4-9d0c-4bbe-92e8-1cf13af3c954">

* github에 sns servie 프로젝트 생성(원격 main 브랜치 생성됨)

* 사람 a가 로컬에서 clone 후 main으로부터 dev 브랜치 생성

* dev에서 기본 세팅 커밋 후 원격에 push. 원격 저장소에 dev 브랜치가 생성되고 커밋 기록이 생긴다.

* 사람 b,c 가 기능 개발을 위해 해당 프로젝트를 각각의 로컬에 clone한다.

* main 브랜치만 받아지므로 git fetch로 원격의 변경사항을 받고 git switch -t dev 명령어로 원격의 dev 브랜치와 같은 이름의 로컬 브랜치를 생성함과 동시에 해당 브랜치에 내용을 복사한다.

* 사람 b, c는 dev 브랜치로부터 feature/기능1, feature/기능2 브랜치를 만들고 push 한다.

* 원격에 feature 브랜치가 2개 생성된다.

* 각각 기능을 완성해 원격의 feature에 push까지 완료하고 b가 먼저 dev에 merge 한다.

* 분기 시점 이후로 dev에 변경사항이 없어 fastforward 지만, 커밋 기록을 남기기 위해 --no-ff명령어로 merge한다.

* 완료 후 로컬 dev로 push하고 git branch -d feature/기능1 명령어로 로컬의 브랜치 삭제, git push origin -d feature/기능1로 원격의 브랜치를 삭제한다.

* 사람 c가 이어 merge 하는데 분기 시점 이후로 변경점이 있어 git pull로 최신화 하고 dev에 merge한다.

* 3way merge라 기록이 남으니 --no-ff는 사용하지 않아도 된다.

* dev를 원격에 push하고 마찬가지로 사용이 끝난 로컬, 원격의 feature/기능2 브랜치를 삭제한다.

* 모든 기능 추가가 완료되었고, dev브랜치가 이상이 없으면 main과 merge 하고 원격에 push 한다.

```
// 사용한 주요 명령어

git clone

git fetch

// 지정한 원격 저장소 이름과 동일한 로컬 저장소 생성후 내용 복사
git switch -t 원격 저장소 이름

// 원격 저장소의 브랜치 삭제
git push origin -d 삭제할 원격 저장소 이름
```
