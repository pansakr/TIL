### 깃 설정

* 깃 명령어에는 지역(local),전역(global),시스템(system) 의 설정 범위가 있다

* 지역범위는 특정 저장소로 한정되고, 전역범위는 현재 사용자의 모든 저장소, 시스템범위는 해당 컴퓨터의 모든 저장소와 사용자에 적용된다

* 유저명과 유저 이메일 정하기

    ```
    git config --global user.name "유저네임"
    
    git config --global user.email "유저이메일"

    // 설정 목록보기
    // 설정이 제대로 되었는지 확인
    git config --list 
    ```

    - 명령어 뒤의 -은 축약어이고(ex =  meesage를 -m으로 표현), --은 옵션이란 뜻이다
    
    - 위에서는 git config의 옵션 --global을 사용한다는 뜻

### 버전 만들기

<img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/%EB%B2%84%EC%A0%84%20%EB%A7%8C%EB%93%A4%EA%B8%B0.JPG" alt="버전 만들기">

* git init : 로컬 저장소 생성

    - 저장소를 만드려는 경로에서 우클릭 -> git bash 실행

    - git init 입력 -> .git 폴더 생성됨

* git status : 현재 작업 디렉토리 상태 확인

    - a.txt 생성 -> git status 입력 -> 현재 상태 출력(커밋 여부, 추적하는 파일 등)

    - a.txt 는 추적하지 않는 파일이라서 빨간 색으로 나옴

* git add : 스테이지에 올리기

    - git add a.txt 입력 -> git status 로 상태 확인
    
    - a.txt 가 스테이지에 추가됨으로서 깃이 추적하는 파일로 등록됨

    - 따라서 녹색으로 나옴

    - 현재 디렉토리의 모든 변경사항을 스테이지에 추가하는 명령어 : git add .

* git commit -m : 커밋 메시지의 제목만 작성해 커밋

    - git commit -m "커밋 메시지 제목" 입력 -> 커밋 완료

* git log : 저장소의 커밋 목록 출력

    ```
    // 커밋 목록을 커밋당 한 줄로 짧게 출력 (커밋 해시와 커밋 메시지 제목만 출력)
    git log -- oneline

    // 각각의 커밋이 무엇을 어떻게 변경했는지 출력
    git log --patch 또는 git log -p

    // 각 커밋을 그래프의 형태로 출력 (소스트리의 커밋 그래프와 유사)
    git log --graph

    // 모든 브랜치의 커밋 목록 조회
    // git log 는 현재 브랜치를 기준으로 커밋 목록을 조회해서 다른 브랜치의 커밋은 못본다
    // 이 옵션을 사용하면 어떤 브랜치에서 커밋 목록을 조회하든 모든 커밋을 볼 수 있다
    git log --branches
    ```

* git commit -am : 스테이지에 추가와 커밋 동시에 하는 방법

    - 깃이 변경사항을 추적하는 파일에만 사용 가능

    - 새로 생성된 파일은 깃이 변경사항을 추적하지 않으므로 사용 불가

* git commit : 커밋 메시지 본문 입력

    - 커밋 메시지의 본문을 입력할 때 사용

        - git commit -m 명령어는 커밋 제목만 입력할 때 사용

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/Vim.JPG" alt="Vim">

    - 사용 시 Vim 창이 나오고, a나 i를 눌러 입력 모드로 전환해야 한다

    - a나 i를 눌렀을 때 제일 하단에 INSERT 가 나오면 입력 모드로 전환된 것이다

    - 첫 줄에 제목, 한칸 띄고 본문 입력

    - ESC를 눌러 입력 모드에서 명령 모드로 전환 -> 하단의 INSERT 가 사라짐

    - :w 를 입력 후 Enter를 눌러 저장 -> :q 를 입력해 창 닫기

    - 또는 :wq 를 입력 후 Enter를 눌러도 된다

### 태그 관리

<img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/%ED%83%9C%EA%B7%B8.JPG" alt="태그">

* git tag [태그이름] : HEAD(현재 브랜치의 최신 커밋) 가 가리키는 커밋에 태그 붙이기

* git tag [태그이름] [커밋해시] : 특정 커밋에 태그를 붙이는 방법

    - git log 또는 git log --oneline 명령어로 커밋 해시 확인

* git tag, git tag --list, git tag -l : 태그 목록 조회

* git tag --delete [태그], git tag -d [태그] : 태그 삭제


### 버전 비교

* git diff : 최근 커밋과 작업 디렉토리 비교

    - 저장소의 마지막 커밋에 a.txt가 있고, 작업 디렉토리에서 b.txt를 추가

    - git diff 입력 시 마지막 커밋에 비해 b.txt가 추가되었으므로 + b.txt 가 출력됨

* git diff --staged 또는 git diff --cached : 최근 커밋과 스테이지 비교

    - 위 설명에서 b.txt를 스테이지에 추가한 후 git diff --staged 입력 시 + b.txt 가 출력됨

* git diff [커밋] [커밋] : 커밋끼리 비교

    - 커밋 해시를 확인해 입력하는데, 첫번째 커밋을 기준으로 두번째 커밋과 비교한다

    - git diff [이 커밋을 기준으로] [이 커밋이 달라진 점]

    - 첫번재 커밋에 a.txt 가 있고, 두번째 커밋에 a.txt, b.txt 가 있다고 가정

    - 첫번째 커밋을 기준으로 두번째 커밋을 비교하면 + b.txt 가 출력

    - 두번째 커밋을 기준으로 첫번째 커밋을 비교하면 - b.txt 가 출력

* 커밋 해시 대신 HEAD 로 비교

    - HEAD^ 또는 HEAD~1 : 현재 브랜치의 최신 커밋에서 하나 이전 커밋을 가리킴

    - HEAD^^ 또는 HEAD~2 : 최신 커밋에서 두 개 이전 커밋을 가리킴

    - 즉 ^의 개수 또는 ~ 뒤에 붙는 숫자가 HEAD 에서 몇 번째 이전을 나타내는지 의미함

* git diff [브랜치] [브랜치] : 브랜치끼리 비교

    - 커밋 비교와 같다

    - git diff [기준 브랜치] [기준과 비교할 브랜치]

### 작업 되돌리기

* reset

    - git reset --soft [돌아갈 커밋 해시] : 소프트 리셋

        - 3개의 커밋이 있을때 두번째 커밋으로 소프트 리셋한다면 두번째 커밋 이후 스테이지 추가까지 한 상태로 리셋

    - git reset --mixed [돌아갈 커밋 해시] : 믹스 리셋

        - 3개의 커밋이 있을때 두번째 커밋으로 믹스 리셋한다면 두번째 커밋 이후 작업 디렉토리 까지만 변경된 상태로 리셋 (스테이지 추가 안된 상태)

    - git reset --hard [돌아갈 커밋 해시] : 하드 리셋

        - 3개의 커밋이 있을때 두번째 커밋으로 하드 리셋한다면 두번째 커밋 이후 아무것도 없는 상태로 리셋

* revert

    - 해당 커밋을 취소한 새로운 커밋을 추가하는 방식

    - git revert [취소할 커밋 해시] 

        - 3개의 커밋이 있을때 revert로 세번째 커밋을 취소한다면 세번째 커밋이 취소된 상태를 가지고 있는 4번째 커밋이 생성됨

        - revert 명령어 입력 시 vim 으로 이동되고, 입력되어있는 기본 커밋 메시지를 놔두거나, 직접 커밋 메시지를 입력 후 :wq로 저장하고 빠져나오면 된다
     
    - revert 충돌 발생시 해결
 
        ```
        git commit -m "1번 커밋"  // a파일 추가
        git commit -m "2번 커밋"  // b파일 추가
        git commit -m "3번 커밋"  // c파일 추가, a파일 수정
        
        // a파일 충돌로 오류 발생
        git revert 1번커밋 해시코드 
        
        // revert 명령으로 a파일을 삭제해야 하는데 3번에서 a를 수정해서 충돌 발생
        // a파일을 삭제하거나 다른 방법으로 충돌을 해결해 줘야 한다
        
        // 충돌난 a파일 삭제
        git rm a.txt
        
        // 이후 revert 계속하기
        git revert --continue
        ```

    - 커밋하지 않고 revert

        ```
        // 특정 시점의 작업만 되돌리고 커밋은 하지 않는다
        // 원하는 다른 작업을 추가한 다음 함께 커밋할 수 있다
        git revert --no-commit 해시코드
        ```

* git reflog

    - 실수로 reset해서 git log 에 복구할 커밋이 남아있지 않을 때 사용

    ```
    // 모든 커밋 기록 확인
    // 이전 버전으로 복구하려할때 커밋 기록이 남아있지 않을 시 사용
    git reflog
    ```

    - git reflog하면 한번이라도 커밋했다면 그 기록을 모두 출력해준다

    - 원하는 버전의 해시값을 찾아서 git reset 명령어로 복구한다


### 작업 임시 저장

* git stash 또는 git stash -m : 변경 사항 임시 저장

    - 작업 디렉토리에서 변경사항 생성 후 git stash -m "작업 1 저장" 하면 임시 저장되고 작업 디렉토리는 원래 상태로 초기화 된다

* git stash list : 임시 저장된 작업 내역 조회

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/stash%20%EC%A1%B0%ED%9A%8C.JPG" alt="stash 조회">

    - 숫자가 적을수록 최근 내역

* git stash apply [스태시] : 임시 저장된 작업 적용

    - [스태시] 는 stash@{0} 을 의미한다

    - 명시하지 않는다면 가장 최근의 임시 저장한 항목이 적용됨

* git stash drop [스태시] : 임시 저장된 작업 삭제

    - 임시 저장된 작업을 적용해도 스태시 목록은 삭제되지 않아서 직접 삭제해 줘야 한다
 
* git stash clear : 임시 저장된 작업 모두 삭제

### 브랜치 관리

<img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/%EB%B8%8C%EB%9E%9C%EC%B9%98%20%EA%B4%80%EB%A6%AC.JPG" alt="브랜치 관리">

* git branch : 브랜치의 목록과 현재 작업 중인 브랜치 * 로 표시

* git branch -a : 원격 브랜치를 포함해 표시

* git branch [브랜치] : 브랜치 생성

* 브랜치 전환

    - git checkout [브랜치] : 브랜치 전환 (체크아웃)
    
        - 체크아웃 : 해당 브랜치로 작업 환경을 바꾸는 것
    
    - git checkout -b [브랜치] : 브랜치 생성과 동시에 전환

    - git switch [브랜치] : 브랜치 전환 전용 명령어
 
        - git checkout 은 브랜치 뿐만 아니라 파일 복원, 커밋 복원 등 여러 기능을 수행하는 명령어이다
     
        - 따라서 git switch 를 사용하는 것이 좀더 안전하다

    - git switch -c [브랜치 이름] : 브랜치 생성과 동시에 전환

* git merge [브랜치] : 브랜치 병합

* 충돌 시 해결

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/git%20bash%20%EC%B6%A9%EB%8F%8C.JPG" alt="git bash 충돌">

    - git merge foo 입력 시 같은 파일을 다르게 수정했기 때문에 충돌 발생

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/%EC%B6%A9%EB%8F%8C%20%ED%8C%8C%EC%9D%BC.JPG" alt="충돌 파일">

    - 충돌 발생 파일 내용

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/%EC%B6%A9%EB%8F%8C%20%ED%8C%8C%EC%9D%BC%20%ED%95%B4%EA%B2%B0.JPG" alt="충돌 파일 해결">

    - 둘 중 main 파일을 적용해 충돌 해결

    - 이후 스테이지 추가 -> 커밋 시 vim 으로 전환됨

    - 기본 병합 커밋 메시지 사용하거나 직접 입력 후 :wq로 저장하고 빠져나오기
    
    - 충돌 해결해 병합 완료

- git branch -d [브랜치] : 브랜치 삭제

- git branch [브랜치] : 브랜치 재배치 (rebase)

    ```
    // rebase 충돌시
    // rebase는 커밋들 하나하나를 합치는 방식이기 때문에 커밋마다 충돌이 있을시 각각 해결해줘야 한다
    
    // merge와 마찬가지로 충돌 파일을 직접 수정하고 add -> git rebase --continue 한다
    git add .
    git rebase -- continue  // rebase할 다음 커밋의 충돌이 없으면 완료, 있다면 다음 커밋의 충돌부분이 에러메시지로 뜬다
    
    // 충돌 부분이 더 있어 에러메시지가 떴다면 해결 후 다시 git rebase -- continue 입력
    // 정상적으로 rebase가 완료될 때까지 반복한다.
    
    // main에도 변경사항을 적용하기 위해 main브랜치로 이동해 rebase한 브랜치와 merge해준다.
    git merge rebase한 브랜치
    
    // rebase는 커밋들을 그대로 이어붙이기 때문에 2개의 커밋을 rebase했다면 메인 브랜치에 2개의 커밋이 추가된다.
    // 그런데 rebase과정에 충돌이 나서 해결 과정에 main쪽을 선택했다면 해당 커밋은 변경사항이 없어지는 셈이고, rebase의 의미도 없어지기 때문에 커밋이 기록되지 않는다
    
    // rebase 취소
    git rebase --abort
    ```

### 원격 저장소와 상호작용

* git clone [원격 저장소]: 원격 저장소 복제

    - 원격 저장소 접속 -> code -> 주소 복사

    - git bash -> git clone [복사한 주소]

* git remote add [원격 저장소 이름] [원격 저장소 경로]: 원격 저장소 추가

    - 로컬 저장소에 원격 저장소를 추가해야 상호작용할 수 있다

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/%EC%9B%90%EA%B2%A9%20%EC%A0%80%EC%9E%A5%EC%86%8C%20%EC%B6%94%EA%B0%80.JPG" alt="원격 저장소 추가">

    - git init 으로 로컬 저장소 생성

    - 이 로컬 저장소는 현재 어떤 원격 저장소와도 연결되어 있지 않음

    - git remote add [origin] [경로] 를 사용해 로컬 저장소에 추가

        - 이름은 마음대로 사용 가능하지만 보통 origin을 사용

        - 경로는 원격 저장소 -> code 에서 확인

        - 이후 origin 이라는 이름으로 추가된 원격 저장소화 상호 작용 가능

* git remote : 추가된 원격 저장소 목록 확인

* git remote -v 또는 git remote --verbose : 원격 저장소의 이름과 경로 확인

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/git%20remote%20-v.JPG" alt="git remote -v">

    - fetch : 데이터를 가져오는 곳

    - push : 데이터를 업로드하는 곳

    - 일반적으로 fetch와 push 는 같은 URL을 가지지만, 다르게 설정하는것도 가능하다

* git remote rename [기존 원격 저장소 이름] [바꿀 원격 저장소 이름] : 원격 저장소 이름 변경
 
* git remote set-url [원격 저장소 이름] [바꿀 원격 저장소 주소] : 원격 저장소 주소 변경

    - 원격 저장소의 주소가 바뀌었을 경우 사용
 
    - ex)깃허브 원격 저장소의 이름이 test1 -> test2 로 변경 시 해당 저장소의 주소도 바뀜
 
    - 만약 로컬에 해당 원격 저장소에 연결되어 있다면 기존 주소로 push 하기 때문에 바꿔줘야 함
 
    - 얼마 동안은 바꾸지 않아도 리디렉션돼서 작동하지만 시간이 지나면 권한/토큰 문제로 실패하기 때문에 바로 바꿔주는 것이 좋음    

* git remote remove [원격 저장소 이름] : 원격 저장소 삭제

* 브랜치 이름 변경

    - git branch -M [브랜치 이름] : 현재 브랜치 이름을 [브랜치 이름] 으로 변경

        ```
        // 현재 old-branch 브랜치에 있을 때 new-branch로 변경
        git branch -m new-branch
        ```
  
    - git branch -M [기존 브랜치 이름] [새 브랜치 이름] : 다른 브랜치에서 특정 브랜치 이름 변경

        ```
        // 현재 main 브랜치에 있을 때, old-branch를 new-branch로 변경
        git branch -m old-branch new-branch
        ```


* git push [원격 저장소 이름] [로컬 브랜치] : 원격 저장소 이름으로 로컬 브랜치를 푸시

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/%EC%9B%90%EA%B2%A9%20%EC%A0%80%EC%9E%A5%EC%86%8C%20%EC%97%B0%EA%B2%B0%2C%20%ED%91%B8%EC%8B%9C.JPG" alt="원격 저장소 연결, 푸시">

    - git push origin main

        - 원격 저장소 origin 으로 로컬 저장소 main 브랜치의 변경 사항을 푸시하는 명령어

        - 깃은 로컬 저장소의 브랜치 이름과 같은 원격 저장소의 브랜치에 푸시하도록 기본 설정되어 있다

        - 만약 로컬 저장소의 브랜치 이름과 같은 원격 저장소의 브랜치가 없다면 새로 만든 후 푸시한다

    - git push -u origin AA

        - -u 는 -upstream 으로 로컬 브랜치를(AA) 원격 저장소의 브랜치와(AA) 연결하는 설정이다

        - 이후 git push 또는 git pull 만으로 연결해놨던 브랜치로 푸시 또는 풀 할 수 있다

        - git push -u를 한 번 실행하면 이후부터는 git push나 git pull만 입력해도 원격 브랜치와 자동으로 연결

        - 자동 연결은 브랜치 별로 설정할 수 있다

        - git branch -vv : 업스트림이 설정된 브랜치 확인 

        - git branch -u origin/new-branch : 업스트림 변경

* git fetch 또는 git fetch [원격 저장소 이름]: 원격 저장소를 가져만 오기

    - 원격 저장소를 로컬에 clone 해서 작업 중 원격 저장소에 변경 사항이 생겼을 때 사용
 
* 원격 브랜치 로컬에 받아오기

    - 협업 중에 새로운 브랜치가 생긴 경우 해당 브랜치를 받아와야 할 때 사용
 
    - 또는 로컬에서 삭제된 원격 브랜치를 다시 받아오거나, 팀 규칙상 로컬에서 직접 브랜치를 만들지 않는 경우에도 사용
    
    ```
    // 원격의 변경사항 확인
    git fetch
    
    // 원격 브랜치이름과 같은 이름의 로컬 브랜치 생성 후 추적
    // 추적 : 로컬 브랜치가 특정 원격 브랜치와 연결되어 있어, git pull / git push 시 연결된 원격 브랜치에 변경 사항을 가져오거나 푸시하는 것
    git switch -t [원격 브랜치이름]
    ```

    - 원격 브랜치에 내용(커밋)이 있다면, git switch -t [origin/브랜치명] 을 실행할 때 내용도 로컬로 복사됨

    - 원격 브랜치가 있는데 커밋이 없으면 비어있는 상태의 로컬 브랜치가 생성됨

* git pull 도는 git pull [원격 저장소 이름]: 원격 저장소를 가져와서 합치기

    - 패치와 병합을 동시에 하는 방법
 
    - pull 설정 : rebase true, rebase false, ff only 중 하나 지정 가능

    - rebase false
  
    ```
    // git pull을 실행할 때 merge 방식으로 동작하도록 설정
    // 원격 브랜치의 최신 커밋을 로컬 브랜치에 병합할 때, 새로운 merge 커밋이 생성된다
    git config pull.rebase false
    
    // 예시
    // 로컬 브랜치(main)에서 commit 1, 2를 추가했고, 원격 저장소에는 commit 3이 추가된 상황
    원격 저장소: A --- B --- C  (원격 main)
    로컬 저장소: A --- B --- D --- E  (로컬 main)
    
    // git pull 실행
    // 원격 브랜치(C)와 로컬 브랜치(D, E)가 병합되어 merge 커밋(M) 이 새롭게 생성됨
    A --- B --- D --- E --- M (merge 커밋)
           \
            C  (원격 브랜치)
    ```

    - rebase true

    ```
    // git pull을 실행할 때 rebase 방식으로 동작하도록 설정
    // 원격의 최신 커밋을 로컬 브랜치의 커밋 이전에 위치시키고, 로컬에서 작업한 커밋을 다시 쌓는 방식으로 진행된다
    git config pull.rebase true
    
    // 예시
    원격 저장소: A --- B --- C  (원격 main)
    로컬 저장소: A --- B --- D --- E  (로컬 main)
    
    // git pull --rebase 실행 후
    A --- B --- C --- D' --- E' (rebase된 로컬 main)
    
    // 원격 브랜치(C)를 기준으로 로컬 브랜치의 커밋이(D, E) 뒤에 다시 쌓인다
    // merge 커밋이 생성되지 않는다
    ```

    - ff only
 
    ```
    // pull할 때 fast-forward가 가능한 경우에만 허용
    // fast-forward가 불가능한 상황이라면 pull이 거부
    // fast-forward : 병합할 때 추가적인 merge 커밋 없이 브랜치가 그대로 앞당겨지는 경우
    git config pull.ff only
    
    // 예시
    원격 저장소: A --- B --- C  (원격 main)
    로컬 저장소: A --- B  (로컬 main)
    
    // git pull을 실행 후
    A --- B --- C  (로컬 main, fast-forward)
    
    // 이와 달리, 로컬에서 추가적인 커밋이 있는 경우
    // fast-forward가 불가능하므로, git pull을 실행하면 거부됨
    원격 저장소: A --- B --- C  (원격 main)
    로컬 저장소: A --- B --- D  (로컬 main)
    ```

### 이외의 명령어

* git clean

    - 깃에서 추적하지 않는 파일들을 삭제

    ```
    git clean -옵션
    
    //예시
    git clean -df
    ```
    <img src="https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/clean.jpg" alt="clean">
    
    - 위의 옵션들을 조합하여 사용한다

* git restore

    - working directory, staging area 의 작업들을 이전으로 되돌린다
    
    ```
    // 특정 파일의 변경사항을 working directory에서 삭제
    // working directory 의 변경사항을 취소하고 싶을때 사용
    git restore [파일명]
    
    // 모든 변경사항을 working directory에서 삭제
    git restore .
    
    // 스테이지에 올라간 특정 파일을 취소 (add 취소) 해서 working directory 로 되돌린다
    git restore --staged [파일명]

    // 모든 변경 사항을 스테이지에서 취소
    git restore --staged .
    
    // 파일을 특정 커밋의 상태로 되돌리기
    git restore --source [커밋해시] [파일명]
    
    // 특정 파일만 해시코드의 커밋 상태로 되돌아간다
    // git은 이 파일을 수정했다고 인식하기 때문에 add -> commit하면 된다
    ``````
