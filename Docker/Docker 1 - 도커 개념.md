### 가상화

* 컴퓨터의 자원을 추상화 하는 개념을 의미함

* 하이퍼바이저 : 가상화 개념을 사용해 가상 머신을 생성하는 소프트웨어

    - VMware, VirtualBox → 우리가 직접 만질 수 있는 VM 툴

    - KVM, Hyper-V → 서버급 가상화 프로그램
 
    - 가상 머신
    
        - 호스트 컴퓨터에 설치되는 가상 컴퓨터
        
        - 일반 컴퓨터처럼 cpu, 메모리, 하드디스크 등을 가지고 있지만, 실제로는 호스트 컴퓨터의 자원을 할당받는다 

* 하이퍼바이저를 사용해 하나의 컴퓨터 자원을 여러 논리적 자원으로 나누어 사용할 수 있다

    - VMware 를 사용해 하나의 컴퓨터에 여러 가상 머신 생성

    - 하나의 가상 머신마다 운영체제를 설치해 결과적으로 하나의 컴퓨터에서 여러 운영체제 실행 가능

* 가상화의 종류

    - 호스트 가상화

        - 운영체제 설치 후 하이퍼바이저를 설치해 다수의 가상머신을 만들어 실행하는 방법

        - 각 가상머신 내부에는 게스트 운영체제가 설치됨

        - 호스트 운영체제에서 여러 가상머신을 실행할 때 사용하는 방법

        - VMware, VirtualBox 등

    - 하이퍼바이저 가상화

        - 호스트 운영체제를 필요로 하지 않고 부팅 시 가상머신을 선택하는 방법

    - 컨테이너 가상화

        - 운영체제 설치 후 도커를 설치해 다수의 컨테이너를 통해 애플리케이션을 실행하는 방식

        - 호스트/하이퍼바이저는 운영체제까지 통째로 복사하지만, 도커는 운영체제 커널은 공유하고 앱 실행 환경만 격리하는 방식이다

        - 도커, 쿠버네티스 해당

### 도커

* 애플리케이션을 실행하는 데 필요한 모든 걸 하나로 묶어서, 어디서든 똑같이 실행되게 해주는 도구

    - 도커는 도커 이미지 라는 것을 사용

    - 이 이미지 안에는 코드, 라이브러리, 설정 파일 등 프로그램 실행에 필요한 모든 것이 들어 있음

    - 그래서 개발자 컴퓨터, 테스트 서버, 운영 서버 어디에서든 동일한 환경에서 실행 가능

* 내 컴퓨터에서는 되는데 서버에서는 안되는 문제를 방지할 수 있다

    - 소프트웨어 A 는 1.0 버전의 소프트웨어 C 가 설치된 환경에서만 실행되고, 소프트웨어 B는 2.0 버전의 소프트웨어 C 가 설치된 환경에서만 실행된다고 가정

    - 서버 1 에는 1.0, 서버 2 에는 2.0 이 설치되어 있음

    - 소프트웨어 A는 서버 1에서만 실행되고, 서버 2에서는 실행 불가

    - 소프트웨어 B는 서버 2에서만 실행되고, 서버 1에서는 실행 불가

    - 이런 경우 도커를 사용해 소프트웨어 C를 버전별로 격리시켜서 하나의 서버에 소프트웨어 A, B 모두를 설치할 수 있다

        - 도커는 컨테이너 단위로 애플리케이션을 실행하고, 컨테이너들은 서로 격리되어 있으므로 독립성을 보장한다

        - 위 상황에서 도커는 소프트웨어 C 의 버전 (1.0, 2.0) 마다 개별로 컨테이너를 구성하고, 컨테이너 단위로 실행한다 

* 도커 이미지

    - 읽기 전용 실행 패키지

    - 소프트웨어를 배포할 때 필요한 코드, 라이브러리, 환경 설정 파일들을 한데 모아 격리시킨 후 실행 가능한 패키지로 만든 것
    
### 도커 구성요소

* 도커 클라이언트 (docker-cli)

    - 셸 형태의 프로그램

    - 명령어 행으로 도커 데몬 api를 활용해 build, pull, run 등의 명령을 내리며 도커 데몬과 통신

    - 컨테이너, 이미지, 볼륨 등을 관리

* 도커 호스트

    - 도커를 설치한 서버 혹은 가상머신

    - 물리 서버가 될 수도 있고, 가상 서버가 될 수도 있다

    - 도커 데몬 (dockerd)

        - 도커 호스트 내부에서 실행 중인 도커 서비스

        - 도커 클라이언트에서 요청한 도커 api의 요청을 수신하고, 도커 이미지와 컨테이너 등을 관리


* 도커 레지스트리

    - 도커 이미지를 저장하거나 배포하는 시스템

    - 공개, 개인 레지스트리로 나뉜다

    - 도커 허브는 가장 유명한 공개 레지스트리 

* containerd

    - 컨테이너 실행과 관리에 필요한 기능을 수행

    - 도커 이미지 전송, 컨테이너 실행, 스토리지, 네트워크 등을 포함

- rucn

    - 컨테이너 실행과 관련된 작업만 수행

* 도커 이미지

    - 읽기 전용 실행 패키지로, 프로그램을 실행하기 위한 환경 + 해당 프로그램까지 설치되어 있는 파일

        - 프로그램을 실행하기 위한 환경 (OS, 라이브러리, 설정 파일 등) 과 해당 프로그램이 설치된 상태

        - 소프트웨어를 배포할 때 필요한 코드, 라이브러리, 환경 설정 파일들을 한데 모아 격리시킨 후 실행 가능한 패키지로 만든 것

* 도커 컨테이너

    - 도커 이미지가 실행 중인 상태

    - 서로 다른 컨테이너는 격리된 환경에서 작동하므로 충돌하지 않는다

    - 각 컨테이너는 독립적으로 실행되며 자체적으로 파일 시스템을 포함하고 있다

    - 그러나 가상 머신처럼 자체 운영체제를 포함하지 않으며, 도커가 설치된 호스트의 운영체제의 커널을 공유한다

        - 운영체제 커널 : 프로세스 스키줄링, 시스템 콜

    - 파일 시스템, 셸, 명령어, 라이브러리, 패키지 등은 컨테이너의 이미지 별로 따로 존재한다

        - 이미지 별로 윈도우의 파일 시스템, 셸 등을 가지거나 리눅스의 파일 시스템, 셸 등을 가질 수 있다

        - 파일 시스템 : /bin, /lib, /app 등의 리눅스 디렉토리

        - 셸 : bash, sh 같은 커맨드 실행 환경

        - 명령어 : ls, cp, apt 등의 명령어


* 도커 실행 과정

    - docker run hello-world 입력

    - docker : 도커 관련 명령어를 입력하겠다는 의미

    - run : 이미지를 기반으로 컨테이너를 생성 후 실행

    - hello-world : 이미지 이름

    - hello-world 라는 이미지가 없다면 원격 저장소에서 해당 이미지 다운로드

### 도커 설치

* apt update : 패키지 인덱스 최신화

* apt install ca-certificates curl gnupg lsb-release : 도커 설치 전 필요한 패키지 설치

    - ca-certificates : HTTPS 통신 시 필요한 인증서들을 모아놓은 패키지

    - curl : URL로 데이터를 주고받는 툴

    - gnupg : GPG(암호화/디지털 서명) 도구. 공개 키 기반으로 패키지 서명을 검증할 때 사용

    - lsb-release : 리눅스 배포판 정보를 확인할 수 있게 해주는 툴

* sudo install -m 0755 -d /etc/apt/keyrings : /etc/apt/keyrings 디렉토리 생성

    - -m 0755는 해당 디렉토리의 권한 설정 (소유자는 읽기/쓰기/실행, 나머지는 읽기/실행 가능)

* sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc

    - Docker 의 GPG 키 다운로드해 /etc/apt/keyrings/docker.asc 에 저장

    - 패키지가 올바른지 검증하는데 사용

    - GPG 키

        - 디지털 서명과 암호화에 사용하는 기술

        - 소프트웨어가 정상적이고 안전한 출처인지 확인하는데 사용

            - docker 를 비롯한 소프트웨어들이 정상적인지, 변조되지 않았는지 확인하는데 사용

        - 공개키, 비공개 키로 이루어져 있음

        - 암호화

            - 공개 키 : 누구나 볼 수 있고, 데이터를 암호화 하는 데 사용

            - 비공개 키 : 나만 가지고 있고, 암호화된 데이터를 복호화 하는 데 사용

        - 디지털 서명

            - 공개 키 : 누구나 볼 수 있고, 소프트웨어를 검증 하는데 사용

            - 비공개 키 : 소프트웨어 개발팀만 가지고 있고, 소프트웨어에 서명 하는 데 사용

* sudo chmod a+r /etc/apt/keyrings/docker.asc : GPG 키에 대한 읽기 권한 부여

    - 모든 사용자에게 키 파일을 읽을 수 있는 권한을 부여

    - APT 가 GPG 키를 사용할 수 있도록 하기 위한 설정

* 도커 저장소 추가

    ```
    echo \
    "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
    $(. /etc/os-release && echo "${UBUNTU_CODENAME:-$VERSION_CODENAME}") stable" | \
    sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
    ```

    - Docker의 APT 저장소를 시스템에 등록

* sudo apt-get update : 패키지 목록 업데이트

    - Docker 저장소가 추가되었기 때문에 이제 Docker 관련 패키지를 찾을 수 있게 됨

* sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

    - Docker 관련 패키지 설치
