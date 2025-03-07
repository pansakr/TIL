### 멀티 부팅

* 하드디스크나 SSD 의 파티션을 분할한 다음 각 파티션에 다른 운영체제를 설치해 컴퓨터를 시작할 때 시동할 운영 체제를 선택할 수 있도록 하는 기능

    - 한 번에 하나의 운영체제만 가동할 수 있다

### 가상머신(Virtual Machine)

* 진짜 컴퓨터가 아닌 가상으로(Virtual) 존재하는 컴퓨터(기계 : Machine)

* 가상머신 소프트웨어 

    - 가상머신을 생성하는 소프트웨어

    - 설치된 운영체제(메인 OS) 안에 가상의 컴퓨터를 만들고, 그 가상의 컴퓨터 안에 다른 운영체제(게스트 OS) 를 설치/운영할 수 있도록 제작된 프로그램

    - 유명한 제품으로 VMware 가 있다

### 설치

* vmware.com 접속 -> 상단의 Products -> See Desktop Hypervisors

* mac 은 VMware Fusion Pro for Mac, 윈도우는 VMware Workstation Pro for PC 선택

* Free Software Downloads available HERE 선택 -> workstation 검색

* workstation pro 선택 -> 윈도우 혹은 리눅스 선택해 다운로드

    - 다른 제품인 workstation player 는 무료 버전으로 Pro 버전에 비해 성능이 낮다

    - player 버전은 한번에 하나의 VM 만 실행 가능하고 여러 기능이 지원되지 않는다

    - pro 버전이 유료에서 무료로 바뀐 이후로 player 는 더이상 사용하지 않는다

### VMware 사용방법

* 가상머신 생성

    - vmware 실행 -> 왼쪽 상단의 home 탭 -> Create a New Virtual Machine 선택 또는 file -> New Virtual Machine 선택

    - Typical(기본값) -> I will inster... 선택 (가상머신 생성만 하고 설치는 안하는 옵션)

    - 사용할 운영체제와 버전 선택 (여기선 리눅스 : Ubuntu 64-bit 선택함)

    - Specify Disk Capacity 는 가상 하드디스크의 용량을 지정하는 부분으로 사용할 만큼 설정

* 가상머신 부품 변경

    - 생성된 가상머신 탭 클릭 -> 가상머신 이름 아래에 Edit virtual .. 클릭

    - 메모리
    
        - 호스트 컴퓨터가 가진 메모리를 가상머신과 나눠 갖는다

        - 호스트 컴퓨터가 16GB 의 메모리일때 가상머신에 4GB 할당 시 각각 12GB, 4GB 씩 사용
    
        - 호스트 컴퓨터의 메모리를 나눠주는 시점은 가상머신을 부팅하는 시점이다

        - 가상머신을 끄면 나눠줬던 메모리가 다시 호스트 컴퓨터로 돌아온다

    - CPU

        - cpu의 개수와 코어 변경 가능

    - Hard Disk(SCSI)

        - 가상머신에는 이곳에서 설정한 용량으로 인식시키기만 하고 실제 용량은 적게 사용한다

        - 용량 변경 방법

            - 하단의 Remove 로 제거 -> 다시 하단의 Add -> Hard Dksk -> SCSI 선택

            - Create a new virtual disk -> 용량 설정 -> Store virtual disk as a single file 선택

                - Store virtual disk as a single file : 가상의 하드디스크를 1개의 파일로 관리

                - Split virtual disk into multiple files : 가상의 하드디스크를 여러 개의 파일로 관리

            - 가상의 하드디스크 이름 지정 후 finish

    - CD/DVD (SATA)

        - 가상머신에 CD/DVD 장착

        - 호스트 컴퓨터의 장치를 그대로 사용하거나 *.iso 파일을 CD/DVD 파일로 사용

* 가상머신 부팅

    - vmware 실행 -> 중앙의 Open a Virtual Machine 또는 File - Open -> 부팅하려는 가상머신 파일(xx.vmx) 선택

    - 화면 상단이나 왼쪽 상단의 초록색 세모 버튼 혹은 VM -> Power -> Start Up Guest 선택

        - 종료는 VM -> Power -> Shut Down Guest

    - Cannot connect the virtual device sata0:1 because .. 메시지는 DVD 장치가 없을 경우 나오는 메시지므로 no 선택

    - 바이오스 설정

        - VM - Power - Power On to Firmware

        - 마우스가 움직이지 않는다면 Ctrl + Alt

* 가상머신 네트워크 설정

    - vmware 실행 -> 상단의 Edit -> Virtual Network Editor

    - 생성된 창의 오른쪽 아래에 Change Settings 가 있다면 클릭, 없다면 그냥 진행

    - VMnet8 선택 후 서브넷 ip를 192.168.111.0 으로 변경

* 호스트 OS에서 게스트 OS로 파일 전송

    - ISO 파일 사용

        - ISO : DVD 나 CD 의 내용을 하나의 파일로 제작해 놓은 것

    - 전송할 파일을 ISO 파일로 만들면 게스트 OS의 CD/DVD 장치에서 사용할 수 있다

    - vmware 실행 -> 가상머신 부팅 -> VM -> Removable Devices -> CD/DVD (SATA) -> Settings

    - User ISO image file -> Browse -> 사용할 ISO 파일 선택

    - 이때 Device status 부분에 Connected 가 체크되어 있어야 한다
