### 깃이 관리하는 세개의 공간

* 작업 디렉토리 (Working Directory)

    - 버전 관리의 대상인 프로젝트가 위치하는 공간

    - 프로젝트에 변경 사항을 만들 수 있다

        - 프로젝트에 파일, 폴더를 새로 추가할 수 있고 기존의 파일을 변경하거나 삭제할 수도 있다

* 스테이지 (Stage)

    - 작업 디렉토리의 변경 사항 중 다음 버전이 될 파일들이 올라가는 공간

    - 작업 디렉토리에서 다음 버전이 될 파일을 스테이지로 옮기는 것을 스테이지시킨다, add 시킨다 라고 하며 스테이지에 추가된 파일을 추가된 파일, 스테이지된 파일 이라고 한다

* 저장소 (Repoistory)

    - 스테이지에 올라온 파일들을 바탕으로 새로운 버전이 만들어지는 공간

    - 저장소에 새로운 버전을 만드는 것을 커밋한다 라고 표현한다

* 새로운 버전 생성 과정

    - 작업 디렉토리에서 변경 사항 생성
    
    - 스테이지에 add 로 파일 추가
    
    - 저장소에 커밋 해서 새로운 버전 생성


### 소스트리를 사용해 첫번째 버전 만들기

* 저장소 생성

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/%EC%A0%80%EC%9E%A5%EC%86%8C%20%EC%83%9D%EC%84%B1.jpg" alt="저장소 생성">

    - 소스트리 실행 후 오른쪽 상단의 Create 버튼을 눌러 위 화면 진입

    - 저장소 생성 후 우측 상단의 탐색기 버튼을 눌러 직접 로컬 저장소 확인

* 저장소에 파일 추가

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/%EC%83%88%EB%A1%9C%EC%9A%B4%20%ED%8C%8C%EC%9D%BC%20%EC%B6%94%EA%B0%80.jpg" alt="새로운 파일 추가">

* 소스트리로 확인

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/%EC%8A%A4%ED%85%8C%EC%9D%B4%EC%A7%80%EC%97%90%20%EC%98%AC%EB%9D%BC%EA%B0%80%EC%A7%80%20%EC%95%8A%EB%8A%94%20%ED%8C%8C%EC%9D%BC%20%ED%99%95%EC%9D%B8.jpg" alt="스테이지에 올라가지 않는 파일 확인">

    - 추가한 파일들이 스테이지에 올라가지 않은 파일들로 나타남

* 모두 스테이지에 올리기

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/%EB%AA%A8%EB%91%90%20%EC%8A%A4%ED%85%8C%EC%9D%B4%EC%A7%80%EC%97%90%20%EC%98%AC%EB%A6%AC%EA%B8%B0.jpg" alt="모두 스테이지에 올리기">

    - 추가한 파일들 모두를 사용해 새로운 버전을 만들 것이므로 모두 스테이지에 추가

* 변경 사항 확인

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/%EA%B0%81%20%ED%8C%8C%EC%9D%BC%EC%9D%84%20%EB%88%8C%EB%9F%AC%20%EB%B3%80%EA%B2%BD%EC%82%AC%ED%95%AD%20%ED%99%95%EC%9D%B8.jpg" alt="각 파일을 눌러 변경사항 확인">

    - 각 파일들을 눌러 변경사항 확인하기

    - 초록색과 + 표시는 각 파일에서 새로 추가된 내용을 의미

* 커밋 메시지 작성 후 커밋

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/%EC%BB%A4%EB%B0%8B%20%EB%A9%94%EC%8B%9C%EC%A7%80.jpg" alt="커밋 메시지">

    - 커밋 메시지 : 버전을 설명하는 메시지

        - 어떤 파일을 어떻게 추가/삭제/변경 했고, 왜 그렇게 했는지

        - 제목과 본문으로 이루어져 있으며 본문은 생략 가능

        - 첫 줄에 제목, 한 줄 띄고 본문 작성

    - 커밋 메시지 작성 후 스테이지에 추가된 파일들을 커밋해 새로운 버전 생성

* 커밋 완료 후 상태

    - 왼쪽의 파일 상태에는 커밋할 내용이 없다고 표시됨

    - History 에서 생성한 커밋, 커밋 메시지, 만들어진 시간, 작성자 등을 확인 할 수 있다


### 소스트리를 사용해 두번째 버전 만들기

* 기존 파일 변경

    - a.txt 내용 변경, c.txt 삭제

* 변경 후 소스트리 상태

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/%EB%B3%80%EA%B2%BD%20%ED%9B%84%20%EC%86%8C%EC%8A%A4%ED%8A%B8%EB%A6%AC.jpg" alt="변경 후 소스트리">

    - 스테이지에 올라가지 않은 파일에 변경된 파일(a.txt 변경, c.txt 삭제) 확인

* 스테이지에 추가 후 커밋

    - 파일 상태로 가서 변경 파일을 스테이지로 올리기

    - 커밋 메시지 작성 후 커밋

* 완료 후 소스트리 상태

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/%EB%91%90%EB%B2%88%EC%A7%B8%20%EC%BB%A4%EB%B0%8B%20%EC%99%84%EB%A3%8C.jpg" alt="두번째 커밋 완료">

    - 동그라미 하나는 하나의 버전(커밋 하나) 를 의미

    - 두 개의 동그라미가 연결된 것은 두번째 커밋이 첫번째 커밋에서부터 만들어진 버전임을 나타냄


### .gitignore

* 무시할 파일/폴더 목록을 적은 파일로 버전 관리 대상에 포함시키지 않을때 사용

* .gitignore 를 사용해 버전에 포함하지 않을 파일이나 폴더를 자동으로 무시할 수 있다

    - 깃은 작업 디렉토리 안에 생성/수정/삭제가 이루어질 경우 항상 해당 변경사항을 체크한다

    - 버전 관리 대상에서 제외하고 싶은 파일이나 폴더를 .gitignore 파일에 등록해 무시할 수 있다


### .gitignore 를 사용해 무시할 파일 등록

* .gitignore 파일 생성

    - 일반 텍스트 파일로 생성 후 .txt 확장자 제거

    - 깃은 정확히 .gitignore 파일명을 인식하므로 오타 주의

    - .gitignore 또한 작업 디렉토리 내의 파일이기 때문에 스테이지에 올라가지 않은 파일 항목에 추가됨

* .gitignore 파일에 무시할 파일 등록

    <ing src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/git%20ignore%20%EB%93%B1%EB%A1%9D.jpg" alt="git ignore 등록">

    - e.txt 파일 생성 후 .gitignore 에 등록

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/git%20ignore%20%EB%93%B1%EB%A1%9D%20%ED%9B%84%20%EC%86%8C%EC%8A%A4%ED%8A%B8%EB%A6%AC.jpg" alt="git ignore 등록 후 소스트리">

    - 작업 디렉토리에 e.txt 파일이 추가됬는데도 소스트리의 스테이지에 올라가지 않은 파일 항목에 e.txt 항목이 생성되지 않음

* .gitignore 파일에 무시할 폴더 등록

    - .gitignore 파일에 ignore/ 작성

        - ignore 라는 이름의 폴더는 깃이 무시하겠다는 의미

    - ignore 폴더 생성 후 내부에 파일을 생성해도 ignore 폴더와 내부 파일들이 스테이지에 올라가지 않는 파일에 추가되지 않음

* 작성 방법

```
# 모든 file.c
file.c

# 최상위 폴더의 file.c
/file.c

# 모든 .c 확장자 파일
*.c

# .c 확장자지만 무시하지 않을 파일
!not_ignore_this.c

# logs란 이름의 파일 또는 폴더와 그 내용들
logs

# logs란 이름의 폴더와 그 내용들
logs/

# logs 폴더 바로 안의 debug.log와 .c 파일들
logs/debug.log
logs/*.c

# logs 폴더 바로 안, 또는 그 안의 다른 폴더(들) 안의 debug.log
logs/**/debug.log
```

### 커밋 해시

<img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/%EC%BB%A4%EB%B0%8B%20%ED%95%B4%EC%8B%9C.jpg" alt="커밋 해시">

* 각 커밋이 가진 고유한 ID

* 커밋 해시로 각각의 커밋을 구분한다


### 태그와 릴리즈

* 릴리즈

    - 개발한 소프트웨어를 사용자에게 선보이는 것

    - 커밋이 쌓여 서비스가 완성되면 완성된 버전을 릴리즈 한다

* 태그

    - 커밋에 붙일 수 있는 꼬리표

        - ex) 릴리즈 버전 1.0.0

    - 릴리즈할 완성된 버전도 하나의 커밋이므로 커밋 해시를 가진다

    - 하지만 커밋 해시는 가독성이 좋지 못하므로 유의미한 버전(릴리즈) 에 사용하기에는 적합하지 않다

        - 여러 개의 커밋에서 의미 있는 커밋(릴리즈 버전 등)을 한눈에 알아보기 어려움

        - 가독성이 좋지 않음

    - 이때 태그를 사용함으로서 여러 개의 커밋에서 의미 있는 커밋을 한눈에 알아볼 수 있다


### 소스트리로 태그 붙이기

* 네번째 커밋 생성

* 태그 추가

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/%ED%83%9C%EA%B7%B8%20%EC%B6%94%EA%B0%80.jpg" alt="태그 추가">

    - 상단의 태그 버튼 클릭 후 태그 추가

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/%ED%83%9C%EA%B7%B8%20%ED%9B%84%20%EC%86%8C%EC%8A%A4%ED%8A%B8%EB%A6%AC.jpg" alt="태그 후 소스트리">


### 버전 표기법

* 대부분 vX.Y.Z 로 표기

    - vX

        - 주(Major)

        - 가장 중요한 버전으로, 새로운 버전이 기존 버전과 호환되지 않을 정도로 큰 변화가 있을 때 증가

    - Y

        - 부(Minor)

        - 새로운 버전이 기존 버전과 호환되며 새로운 기능을 추가했을 때 증가

    - Z

        - 패치(Patch)

        - 새로운 버전이 기존 버전과 호환되며 버그를 수정한 정도의 작은 변화가 있을때 증가
     
* 사용 명령어

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
