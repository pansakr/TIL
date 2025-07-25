### 브랜치

* '버전의 분기'로 버전을 여러 흐름으로 나누어 관리하는 방법

    - 하나의 버전(커밋) 에서 브랜치를 나눈다

    - 각자의 브랜치에서 작업한다

    - 나눈 브랜치를 합친다

* 브랜치의 전체적인 설명과 체크아웃, HEAD 개념

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/%EB%B8%8C%EB%9E%9C%EC%B9%98%20%EA%B0%9C%EB%85%90.jpg" alt="브랜치 개념" width="500" height="600">
    <br>
    
    - master(main)
    
        - 깃이 제공하는 기본 브랜치

        - 3번째 커밋에서 foo 브랜치 분기, 4번째 커밋에서 bar 브랜치 분기

        - main 브랜치 입장에서는 커밋이 3개만 보인다

    - foo 브랜치

        - main 브랜치의 3번째 커밋에서 분기된 브랜치로 main 브랜치의 커밋 세 개를 모두 포함

        - foo 브랜치 입장에서는 커밋이 5개 (main 1, 2, 3 foo 4, 5)

    - bar 브랜치

        - main 브랜치의 4번째 커밋에서 분기된 브랜치로 main 브랜치의 커밋 네 개를 모두 포함

        - bar 브랜치 입장에서는 커밋이 6개 (main 1, 2, 3, 4 bar 5, 6)

    - HEAD

        - 커밋을 가리키는 일종의 표시

        - 기본적으로 현재 작업 중인 브랜치의 최신 브랜치를 가리킨다

        - 브랜치를 나누고 합치는 과정에서 HEAD 의 위치를 바꿀 수 있다

        - checkout으로 HEAD의 위치를 변경해 이전 커밋상태를 볼 수 있다

        - HEAD 를 이동시키면 새로운 임시 브랜치가 만들어지며 커밋해 새로운 분기를 만들 수 있다

        ```
        // ^, ~, ~숫자 셋 모두 같은 의미고 표시 개수만큼 이전 커밋으로 이동한다
        // HEAD~5 <- HEAD를 5커밋 이전으로 이동시킨다
        git checkout HEAD^ 

        // 커밋 해시를 사용해서도 이동 가능하다
        git checkout 커밋 해시

        // HEAD 이동을 한단계 되돌리기
        git checkout -
        ```
        - HEAD를 사용해 리셋하기

        ```
        // 현재 head의 위치에서 2커밋만큼 뒤로 reset한다
        git reset --hard HEAD~2 
        ```

    - 체크아웃

        - 특정 브랜치에서 작업할 수 있도록 작업 환경을 바꾸는 것

        - 특정 브랜치로 체크아웃하면 HEAD 의 위치가 해당 브랜치의 최신 커밋을 가리키게 되고, 작업 디렉토리는 체크아웃한 브랜치의 모습으로 바뀐다

            - main 브랜치로 체크아웃 -> 작업 디렉토리에 4 개의 커밋

            - foo 브랜치로 체크아웃 -> 작업 디렉토리에 5 개의 커밋

            - bar 브랜치로 체크아웃 -> 작업 디렉토리에 6 개의 커밋

* 소스트리로 브랜치 사용

    - 로컬 저장소 생성 -> a.txt 생성후 커밋, b.txt 생성후 커밋, c.txt 생성후 커밋

    - 상단의 브랜치를 클릭해 새로운 브랜치 foo 생성

    - 좌측의 브랜치 하단에서 main, foo 두 브랜치를 자유롭게 체크아웃 가능

        - 굵게 표시된 브랜치 이름이 현재 체크아웃된 브랜치

    - foo_d.txt 생성 후 커밋, foo_e.txt 생성 후 커밋

    - 탐색기로 살펴봤을 때 main 브랜치로 체크아웃하면 a, b, c 세 개의 파일만 보이고, foo 브랜치로 체크아웃하면 a, b, c, foo_d, foo_e 다섯 개의 파일이 보인다

    - main 브랜치로 체크아웃해서 bar 브랜치 생성 후 bar_d, bar_e, bar_f 하나씩 생성 후 커밋 반복

    - bar 브랜치로 체크아웃하면 a, b, c, bar_d, bar_e, bar_f 여섯 개의 파일이 보인다


### 병합 (merge)

* 브랜치를 하나로 통합하는 것

* 빨리 감기 병합 (fast-forward merge)

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/%EB%B3%91%ED%95%A9%20%EC%9D%B4%EC%A0%84.jpg" alt="병합 이전">
    <br>

    <img src="https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/%ED%8C%A8%EC%8A%A4%ED%8A%B8%20%EB%B3%91%ED%95%A9.jpg" alt="패스트 병합">
    <br>
    
    - 어느 커밋으로부터(A 브랜치의 최신 커밋) 분기된 브랜치를(B) 다시 합칠때 원래의 커밋에 변화가 없을때 사용

        - 한쪽 브랜치에만(B) 새로운 커밋이 있는 상태

        - 이때 둘을 병합시 한쪽 커밋에만 변경사항이 있기 때문에 병합 결과는 변경사항이 있는 브랜치와 똑같아진다

        - 그래서 새로운 커밋(병합 커밋)이 만들어지지 않고 원래의 커밋(A) HEAD 위치만 새로운 커밋이 있는 브랜치(B) 위치로 옮겨진다

    - 이 방법은 병합된 브랜치 기록이 남지 않는다

        - 병합된 브랜치의 기록을 남기면서 fast-forward merge 하는 방법

        ```
        git merge --no-ff 병합할 브랜치
        ```

* 일반 병합 (3way merge)

    - 원래의 커밋에 변화가 있을때 사용

        - 원격 저장소의 main으로부터 분기해 커밋 후 푸시하려 할 때 다른 사람이 먼저 푸시해 main브랜치가 변경되었을 때 사용

* 병합시 깃이 상황을 판단해 fast forward, 3way merge 중 하나를 자동으로 실행

* 소스트리에서 병합

    - 소스트리에서는 두가지 방법으로 병합할 수 있다

    - main 브랜치, main으로부터 분기된 sub1, sub2 브랜치

    - main 브랜치에 sub1 브랜치 병합 (첫번째 방법)

        - main 브랜치로 체크아웃 -> 좌측 브랜치 목록의 sub1 브랜치 우클릭 -> 현재 브랜치로 sub1 병합

            - 이때 병합된 브랜치 기록을 남기려면 'fast-forward가 가능해도 새 커밋으로 생성' 체크

        - 완료 후 sub1 브랜치 우클릭 -> sub1 삭제  

    - main 브랜치에 sub2 브랜치 병합 (두번째 방법)

        - main 브랜치 체크아웃 -> 상단의 병합 선택 -> 병합할 sub2 브랜치의 커밋 선택

        - 마지막 커밋(sub2 브랜치의 최신 커밋) 선택 시 sub2 브랜치의 모든 커밋이 main과 병합
        
        - 그 이전 커밋 선택 시 선택한 커밋까지만 main 브랜치와 병합

            - sub2 브랜치에 세번째 커밋까지 있을때 첫번째 커밋 선택 시 첫번째 커밋만 병합

            - 그 다음 세번째 커밋 선택 시 나머지 두번째, 세번째 커밋이 병합

### 충돌 해결

* 병합하려는 두 브랜치가 같은 내용을 다르게 수정했을 때 발생

    ```
    // main 브랜치에 이름이 first 인 커밋이 있고, 해당 커밋에 a.txt 파일 하나와 그 내용은 a 인 상태

    1. foo 브랜치로 분기 후 a.txt 의 내용을 foo 로 변경

    2. main 브랜치로 체크아웃 후 a.txt 의 내용을 main 으로 변경

    3. main 브랜치에서 foo 브랜치를 병합하려 할 때 main 브랜치는 a.txt의 내용이 main 이고, foo 브랜치는 a.txt 의 내용이 foo 로 같은 파일을 다르게 수정했기 때문에 충돌 발생

    4. 충돌이 발생하면 커밋하지 않은 변경사항이 생기고, 스테이지에 올라가지 않은 파일과 스테이지에 올라간 파일 항목에 충돌이 발생한 파일이 추가된다

    5. 충돌이 발생한 파일들의 충돌을 해결한 뒤 다시 커밋해야 브랜치가 올바르게 병합된다
    ```

* 같은 파일을 다르게 수정했을 때 깃은 두 파일 중 어느것으로 저장해야 할지 모르기 때문에 충돌이 발생하고 사용자가 해결해 줘야 한다

* 같은 내용을 다르게 수정한 브랜치 중 어떤 브랜치 내용을 최종적으로 반영할지 직접 선택하는 것을 '충돌을 해결한다' 라고 한다

* 소스트리에서 충돌 해결

    - 위의 설명과 같은 브랜치가 있고, 충돌이 발생했다고 가정

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/%EC%B6%A9%EB%8F%8C.JPG" width="700" height="600" alt="충돌">
    <br>

    - 충돌이 발생해서 커밋하지 않은 변경사항이 생성되었고, 스테이지에 올라가지 않은 파일, 스테이지에 올라간 파일에 충돌이 발생한 파일이 추가되었다

    - 스테이지에 올라가지 않은 파일에 <<, ==, >> 기호들이 추가되어 있다

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/%EC%B6%A9%EB%8F%8C%20%EC%84%A4%EB%AA%85.jpg" width="700" height="500" alt="충돌 설명">
    <br>
    
    - 기호는 일종의 영역 표기이다
    
    - === 기호를 기준으로 윗부분은 HEAD가 가리키는 브랜치, 즉 현재 체크아웃한 브랜치의 내용이 적혀있다

    - 아랫부분은 병합하려는 브랜치, 즉 foo 브랜치의 내용이 적혀있다

    - <<< 기호와 === 기호 사이의 내용을 선택할지, === 기호와 >>> 기호 사이의 내용을 선택할지 고르라는 표기이다

    - 두 영역중 반영할 부분을 직접 선택해 충돌을 해결해야 한다

    - 스테이지에 올라가지 않은 파일 우클릭 -> 충돌 해결

    - 내것을 이용한 해결, 저장소 것을 해결 중 하나 클릭

        - 내것은 현재 체크아웃된 브랜치(main) 를 반영, 저장소 것은 병합하려는 브랜치(foo)를 반영

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/%EC%B6%A9%EB%8F%8C%20%ED%95%B4%EA%B2%B0.JPG" alt="충돌 해결">
    <br>
    
    - 여기서는 내것을 이용해 해결 -> 한쪽을 사용해 충돌을 해결하시겠습니까? -> 확인

        - 이때 확인 창에 커밋 해시가 표기되는데, 이는 main 브랜치의 최신 커밋 해시다

        - 즉 충돌이 발생한 a.txt 파일에 main 브랜치의 최신 커밋 내용을 반영하겠다는 의미이다

    - 충돌은 해결했지만 브랜치 병합을 끝내려면 다시 커밋해야 한다

    - 파일 상태로 가면 커밋 메시지가 자동으로 기입되어 있고, 커밋이 활성화되어 있다

    - 커밋 -> 충돌 해결 후 병합 완료

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/%EC%B6%A9%EB%8F%8C%20%ED%95%B4%EA%B2%B0%20%EC%99%84%EB%A3%8C.JPG" alt="충돌 해결 완료">
    <br>
    

### 브랜치 재배치 (rebase)

* 브랜치가 뻗어나온 기준점을 변경하는 것

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/rebase%20%EC%84%A4%EB%AA%85%201.jpg" width="600" height="300" alt="rebase 설명 1">
    <br>
    
    - main 브랜치의 두번재 커밋에서 foo 브랜치가 분기되었고, 각 브랜치에 여러 커밋이 쌓인 상태

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/rebase%20%EC%84%A4%EB%AA%85%202.jpg" width="600" height="300" alt="rebase 설명 2">
    <br>
    
    - foo 브랜치를 네번째 커밋에서 분기되도록 변경

* 소스트리로 rebase

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/rebase%201.jpg" width="300" height="300" alt="rebase 1">
    <br>

    - 첫번째 그림과 같은 상태의 히스토리

    - 재배치하려는 브랜치 foo 로 체크아웃

    - main 브랜치의 4번 커밋으로 재배치할 것이므로 main의 네 번째 커밋에서 우클릭 -> 재배치

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/rebase%202.JPG" alt="rebase 2">
    <br>

    - 재배치 완료 후 모습
