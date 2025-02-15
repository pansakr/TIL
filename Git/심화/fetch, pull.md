### git fetch

* 원격 저장소의 커밋을 가져오고 merge는 하지 않는다.

* merge 전 최신 커밋이 있는지 확인하는 용도로 사용한다.

```
// 원격 저장소에서 모든 브랜치를 가져온다.
git fetch [원격 저장소 이름]

// 원격 저장소의 특정 브랜치만 가져온다.
git fetch [원격 저장소 이름] [브랜치 이름]

// fetch로 가져온 브랜치 목록 확인
git branch -r

예시 목록 - origin/dev 

// fetch로 가져온 브랜치로 이동해 변경사항 확인
// fetch는 merge하지 않기 때문에 임시 브랜치로 이동된다.
git checkout [git branch -r로 확인한 fetch로 가져온 브랜치 이름]

// 확인 후 충돌시 수정해주고, 이상 없다면 브랜치 이동 후 merge
// git switch [merge 할 브랜치이름]

// git pull
```

### git pull

* fetch + merge 이다.

* 브랜치의 내용을 가져와 합치는 것으로, 주로 push하려는 브랜치에 커밋이 새로 생성됬을때 가져와 최신화 시키는 용도로 사용한다.

* pull 설정으로 rebase, rebase false, ff only 중 하나를 지정할 수 있다.

```
// pull 할 때 merge한다.
// merge 커밋이 새로 생성되고, 부모 브랜치와 합칠때 merge 커밋이 두개 생성된다.
git config pull.rebase false

// pull 할 때 rebase 한다.
// 원격의 최신 커밋이 현재 로컬에서 작업중이던 커밋 이전으로 생성되고 merge시 커밋 하나만 생성된다.
git config pull.rebase true

// fast-foward 일때만 pull을 허용한다.
git config pull.ff only

// fast-forward
// 1. 원격 브랜치의 커밋 히스토리가 로컬 브랜치의 커밋 히스토리에 완전히 포함 되어 있거나 
// 2. 로컬 브랜치의 커밋 히스토리가 원격 브랜치의 커밋 히스토리에 포함 될 경우 

// 그런데 1의 경우는 원격에 변경사항이 없다는 의미이므로 원래 pull없이 바로 push가 된다.

// 2의 경우 브랜치를 로컬에 받아와서 커밋 하기 이전을 뜻하는데, 사실상 추가 작업하지 않고 push하는 경우는 없으므로 커밋 이후 rebase로 합쳐서 1의 상황으로 만드는것을 뜻한다.   

// 추가 커밋을 적게 만드는게 관리하기 편하기 때문에 rebase 옵션을 사용하는것이 좋다.
```
