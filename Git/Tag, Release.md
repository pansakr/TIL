### 태그

* 특정 시점을 키워드로 저장하고 싶거나 커밋에 버전 정보를 붙일때 사용한다.

* 버전은 Semantic Versioning을 따른다.

* 태그에는 ligthweight, annotated 가 있다.

* ligthweight - 특정 커밋을 가리키는 용도

* annotated - 작성자 정보와 날짜, 메시지 , GPG 서명 포함 가능

```
// 커밋(마지막 커밋)에 태그 달기(lightweight)
git tag 태그이름

// 현존하는 태그 확인
git tag

// 원하는 패턴으로 필터링하기
git tag -l '필터링문자'

// ex) git tag -l 'v1.*' // v1으로 시작하는 버전만 검색

// 원하는 태그의 내용 확인
git show v2.0.0

// 태그 삭제
git tag -d v2.0.0

// 커밋(마지막 커밋)에 태그 달기(annotated)
// vi화면으로 이동 후 태그 메시지를 입력해야 한다.
git tag -a 태그이름

// vi로 이동하지 않고 태그 메시지까지 한번에 입력하는 방법 
git tag 태그이름 -m '태그 메시지'

// 원하는 커밋에 태그 달기
git tag 태그명 커밋해시 -m 메시지

// 원하는 버전으로 체크아웃
git checkout 태그이름

// 특정 태그 원격에 올리기
git push 원격명 태그명

// 특정 태그 원격에서 삭제
git push --delete 원격명 태그명

// 로컬의 모든 태그 원격에 올리기
git push --tags
```

* 원격에 만들어진 태그로 release할 수 있다.

* 원하는 태그에서 Create release를 선택 후 제목과 내용을 입력해 PUblish release를 선택하면 다른 사람들이 다운받을 수 있게된다. 



### Semantic Versioning

* 2.0.0 과 같이 버전 상세정보를 major, minor, patch 로 한다.

* 기존 버전과 호환되지 않게 api가 바뀌면 major을 올린다.

* 기존 버전과 호환되면서 새로운 기능을 추가할 때는 minor을 올린다.

* 기존 버전과 호환되면서 버그를 수정했다면 patch를 올린다.
