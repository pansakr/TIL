### git의 HEAD

* 현재 속한 브랜치의 가장 최신 커밋

* checkout으로 HEAD의 위치를 변경해 이전 커밋상태를 볼 수 있다.

* 헤더를 이동시키면 새로운 임시 브랜치가 만들어진다.

* 헤더를 이동시킨 위치에서 새 브랜치를 생성 후 커밋해 새로운 분기를 만들 수 있다.

```
// ^, ~, ~숫자 셋 모두 같은 의미고 표시 개수만큼 이전 커밋으로 이동한다.
// HEAD~5 <- HEAD를 5커밋 이전으로 이동시킨다.
git checkout HEAD^ 

// 커밋 해시를 사용해서도 이동 가능하다.
git checkout 커밋 해시

// HEAD이동을 한단계 되돌리기
git checkout -
```
* git reset에 HEAD를 사용해 리셋하기

```
// 현재 head의 위치에서 2커밋만큼 뒤로 reset한다.
git reset --hard HEAD~2 
```

### fetch와 pull

* fetch - 원격 저장소의 최신 커밋을 로컬로 가져오기만 한다.

* pull - 원격 저장소의 최신 커밋을 로컬로 가져와 merge 또는 rebase 한다.

```
// 원격의 변경사항을 가져온다.
git fetch

// 원격의 변경사항을 다운받지 않고 확인만 해본다.
git checkout 원격 브랜치 이름

// 확인 후 합칠 로컬 브랜치로 이동해서 merge 또는 rebase
git pull
```

### 3가지 공간

* working directory, Staging area, Repository 가 있다.

#### Working directory

* 작업 공간. 실제 생성, 수정, 삭제 하는 파일들이 담긴 폴더를 의미한다.

* Working directory내부의 파일들 중 add 명령어로 Staging area로 이동된 tracked상태의 파일들만 깃이 관리한다.

* git add 명령어로 Staging area로 이동한다.

* untracked - add된 적 없는 파일, ignore된 파일

* tracked - add된 적 있고 변경내역이 있는 파일

#### Staging area

* git add한 파일들이 존재하는 임시 저장 공간

* commit 명령어로 Repository로 이동한다.

#### Repository

* 작업 결과(커밋단위)가 저장되는 곳

* .git 폴더를 의미한다.


### 파일의 삭제, 변경

```
// 파일의 삭제
파일을 마우스를 조작해 삭제 // 삭제가 Working directory에 있다. delete가 add되지 않은 상태 

// 명령어로 삭제
git rm // 삭제가 Staging area에 있다. 즉 delete가 add된 상태

// git reset --hard 로 복원한다.
```

```
// 파일의 변경
git mv 본래이름 바꿀이름  //이름이 변경된 파일이 add까지 되어있다.
```

### add 취소하기

```
git restore --staged 파일명
```
* add되어 staging area에 있는 파일을 working directory로 되돌린다.

* --staged를 빼면 working directory에서도 제거된다. 

* 즉 변경사항이 취소되어 이전 커밋상태로 되돌아간다.


### reset의 3가지 옵션

```
// 커밋을 되돌릴때 사용한다.
git reset --옵션 
```

* commit이 마음에 들지 않아 이전 버전으로 되돌리려는 상황이다.

* hard - 수정사항을 repository, staging area, working directory에서 전부 제거한다.

* mixed(기본값) - 수정사항을 repository, staging area에서 제거한다. 즉 수정된 파일만 남아있다. add되지 않은 상태

* soft - 수정사항을 repository에서만 제거한다. 수정된 파일이 남아있고 add까지 되어있는 상태
