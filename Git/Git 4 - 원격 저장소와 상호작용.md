### 원격 저장소

* 다른 컴퓨터의 저장소. 깃허브의 원격 저장소는 깃허브가 관리하는 컴퓨터 속의 저장소

* 소스트리와 깃허브를 SSH 통신이 가능하도록 연동하기

    - SSH 개념

        - SSH(Secure Shell) : 안전하게 정보를 주고받을 수 있는 통신 방법

        - 공개 키(public key), 개인 키(private key) 두 개를 생성해야 한다

        - 공개 키는 모두에게 공개된 키, 개인 키는 나만 알고 있어야 하는 키 이다

            - 여기서 키는 암호 또는 암호화된 문자열 이다

        - SSH 는 암호화된 통신 방법이므로 내가 깃허브와 주고받는 내용은 암호화되어 전송된다

        - 누가 내용을 본다 해도 이해할 수 없지만 본인은 개인 키가 있어서 깃허브에게 자신임을 증명하고, 내용을 이해할 수 있다

        ```
        // SSH 키 생성 방법

        1. 아무곳에서 우클릭 후 git bash 들어가기

        2. ssh-keygen 입력

        3. Enter passphrase 가 뜨는데 사용할 암호를 입력하거나, 암호를 사용하지 않으려면 아무것도 입력하지 않고 Enter

        4. Enter passphrase agin 이 뜨면 앞서 입력한 암호를 재입력하거나, 입력하지 않았다면 Enter

        5. SSH 생성됨. Your identification has ~ 가 개인 키가 저장된 경로, Your public key ~ 가 공개 키가 저장된 경로이다
        ```

    - 소스트리에 개인 키 등록

        - 소스트리 실행 -> 상단의 도구 -> 옵션

        - SSH 클라이언트 설정의 SSH 클라이언트 항목을 OpenSSH 로 변경

            - PuTTY / Plink 또는 OpenSSH 를 사용해 공개 키, 개인 키를 생성할 수 있다

            - 앞서 설명한 SSH 키 생성 방식은 OpenSSH 이므로 OpenSSH 선택

            <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/SSH%20%ED%82%A4%20%EC%84%A4%EC%A0%95.JPG" alt="SSH 키 설정">

        - SSH 키 경로가 자동으로 등록되지 않았다면 도구 -> SSH 키 추가 -> 앞서 생성한 개인 키가 저장된 경로로 들어가서 등록 

    - 깃허브에 공개 키 등록

        - 깃허브 프로필 클릭 -> Settings -> 좌측 메뉴에서 SSH and GPG keys -> New SSh key 클릭

        <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/%EA%B9%83%ED%97%88%EB%B8%8C%EC%97%90%20SSH%20%EA%B3%B5%EA%B0%9C%20%ED%82%A4%20%EB%93%B1%EB%A1%9D.JPG" alt="깃허브에 SSH 공개 키 등록">

        - Title : 키의 제목, Key : 공개 키 파일 (id_rsa.pub 내부에 적힌 내용) 입력 후 Add SSH key 클릭

    - 소스트리와 깃허브 연동

        - 소스트리 -> Remote -> 계정 추가

        - 계정 편집 창에서 호스팅 서비스는 Github, 선호 프로토콜은 SSH 선택 후 Oauth 토큰 새로고침 클릭

        - 인증을 확인하는 브라우저 화면이 뜨는데 스크롤을 내려 Authorize Atlassian 클릭

        - 인증 완료 혹은 아무것도 없는 하얀 화면이 뜰 수도 있음

        - 계정 편집 창 좌측 하단에 인증 성공 체크가 생기면 연동 성공

        - 소스트리에 연동된 깃허브 계정이 뜨고, 새로고침을 누르면 가지고 있는 원격 저장소도 보인다 

### 원격 저장소와 상호작용

* 클론(clone) : 원격 저장소 복제

    - 복사할 깃허브 저장소 접속 -> Code -> HTTPS 또는 SSH 선택 후 바로 밑의 경로 복사

    - 소스트리 실행 -> 상단 Clone -> 소스경로/URL 에 붙여넣기

    - 목적지 경로에 저장할 경로 지정 -> 클론 완료

    - 원격 저장소의 파일들이 복사되었고, 쌓인 커밋들도 모두 볼 수 있다

    - 내 저장소 클론은 Remote -> 목록의 저장소 중 사용할 저장소 clone -> 이후 동일

    - 클론 후 브랜치

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/%ED%81%B4%EB%A1%A0%20%ED%9B%84%20%EB%B8%8C%EB%9E%9C%EC%B9%98.JPG" alt="클론 후 브랜치">

    - main : 클론 시 로컬 저장소가 가지는 기본 브랜치

        - 원격 저장소 설정에 따라 다른 이름일 수 있다

    - origin/main : 원격 저장소의 기본 브랜치(main) 를 가리키는 브랜치 (리모트 트래킹 브랜치)

        - 원격 저장소의 기본 브랜치 이름에 따라 달라질 수 있다

    - origin/HEAD : 원격 저장소 HEAD 를 가리킨다

    - origin : 원격 저장소 경로에 붙은 일종의 별명

        <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/origin.JPG" alt="origin">

        - 원격 저장소를 지칭할 때 매번 경로를 사용하는 것은 번거로우므로 origin 으로 이름을 붙인 것

        - 변경할 수 있지만 대부분 그대로 사용한다

* 푸시(push) : 원격 저장소에 밀어넣기

    - 원격 저장소에 로컬 저장소의 변경 사항을 밀어넣는 것

    - 클론한 로컬 저장소에 변경사항 생성후 소스트리로 Push 클릭해서 원격 저장소에 반영

* 패치(fetch) : 원격 저장소를 일단 가져만 오기

    - 원격 저장소의 변경 사항을 로컬로 가져오고 싶을 때 사용

    - 변경 사항을 가져오기만 하고 로컬 브랜치와 병합하진 않는다

        - 패치한 변경사항은 로컬 저장소의 .git 디렉토리에 있는 리모트 트래킹 브랜치(origin/main) 에 저장됨

        - 패치만 하고 병합하지 않으면 패치한 내용은 리모트 트래킹 브랜치에만 남고, 로컬 브랜치에는 적용되지 않는다

        - 내 로컬 브랜치는 변화가 없고, 원격 변경 사항만 추적할 수 있는 상태가 됨

    ```
    // 원격 저장소 H 를 a, b 가 같이 사용하는 상황
    // H 에는 a.txt 하나만 있는 상태

    1. a가 H를 클론        -> a 와 원격의 H 모두 a.txt 로 동일

    2. b가 H에 b.txt 추가  -> 원격의 H 는 a.txt, b.txt 가 있고, a는 a.txt 만 있는 상태  

    3. a가 패치 실행       -> 원격의 변경사항을 확인할 수 있지만 로컬 저장소에는 반영되지 않은 상태

    4. a가 병합 실행       -> 원격의 변경사항을 로컬 저장소와 병합해 로컬에서도 변경사항 확인 가능  
    ```

    - 패치 전

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/%ED%8C%A8%EC%B9%98%20%EC%A0%84.JPG" alt="패치 전">

    - 패치 후

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/%ED%8C%A8%EC%B9%98%20%ED%9B%84.JPG" alt="패치 후">

    - 원격 저장소의 main을 가리키는 origin/main 브랜치가 네 번째 커밋을 가리키게 변경됨

    - 로컬의 main 브랜치는 여전히 세 번째 커밋을 가리키므로 로컬의 브랜치는 변경되지 않았다

    - 즉 패치해도 원격 저장소의 내용이 로컬 저장소에 병합되지 않는다

* 풀(pull) : 원격 저장소를 가져와서 합치기

    - 패치와 병합을 동시에 하는 방법

    - 원격 저장소에 변경 사항 생성 후 소스트리 접속 -> 상단의 풀 클릭


* 풀보단 패치 후 병합 권장

    - 풀은 패치 후 병합까지 자동으로 하기 때문에 만약 원격 저장소에 변경 사항이 있으면 충돌이 발생한다

    - 발생할 충돌을 해결해 푸시해도 되지만, 패치를 통해 먼저 확인하고 변경해 병합하는게 더 안전하다

* 풀 하지 않고 원격 저장소에 바로 푸시

    - 원격 저장소에 변경 사항이 없으면 그래도 된다

    - 변경 사항이 있다면 로컬 저장소와 원격 저장소가 일치하지 않아 푸시 실패

    - 그래서 원격의 변경 사항을 로컬에 반영하고(패치 또는 풀 이후 병합) 푸시해야 한다
