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

### 파일의 삭제, 변경

* 마우스를 조작해 삭제

  - Working directory 만 삭제가 적용됨
  
  - 삭제된 상태가 스테이지에 올라가지 않은 상태 

* 명령어로 삭제

  - git rm
 
  - 삭제된 상태가 스테이지에 올라간다
 
* 파일 이름 변경

  ```
  git mv 본래이름 바꿀이름
  ```

  - 이름이 변경된 파일이 스테이지까지 올라간다

### add 취소하기

```
git restore --staged 파일명
```
* add되어 staging area에 있는 파일을 working directory로 되돌린다.

* --staged를 빼면 working directory에서도 제거된다. 

* 즉 변경사항이 취소되어 이전 커밋상태로 되돌아간다.
