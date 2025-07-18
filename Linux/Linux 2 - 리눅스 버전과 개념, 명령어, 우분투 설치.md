### 리눅스 버전

* 리눅스 토르발스가 리눅스의 핵심 부분인 커널을 제작

* 버전(커널) 종류

    - Prepatch

        - 새로운 기술이 포함되지만 안정화 되지 않음

        - 빠르게 신기능을 확인하는 시험 버전으로 사용 가능

    - Mainline

        - Prepatch 보다 안정적이며 새로운 기능이 포함

    - Stable

        - Mainline 에서의 버그가 대부분 잡힌 안정화된 커널

    - Longterm

        - 장기 지원 커널

        - 안정화된 Stable 버전보다 좀 더 안정적이며 업데이트가 오랫동안 지원됨

* 리눅스 배포판

    - 여러 회사나 단체에서 리눅스 커널에 컴파일러, 쉘, 기타 응용프로그램을 추가해 쉽게 설치할 수 있도록 만든 것

    - 종류는 수백가지가 넘으며 그중 유명한 배포판으로 데비안 리눅스, 우분투 리눅스 등이 있다

    - 우분투 리눅스
    
        - 우분투 데스크톱, 우분투 서버 두 가지를 기본으로 배포한다

        - 이외에도 우분투를 바탕으로 하는 쿠분투, 우분투 버지 등 여러 배포판이 있다


### 우분투 설치

* ubuntu.com 접속 -> desktop 혹은 server 다운로드 (.iso 파일)

* vmware 실행 -> 사용할 가상머신 탭 클릭 -> CD/DVD -> Use ISO image file 에 받은 파일 선택

* 가상머신 부팅 -> Try or Install Ubuntu -> 설치가 진행되고 그래픽 화면이 나오면 Install Ubuntu 클릭


### 리눅스 개념

* 디렉토리 구조

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Linux/%EB%A6%AC%EB%88%85%EC%8A%A4%20%EB%94%94%EB%A0%89%ED%86%A0%EB%A6%AC%20%EA%B5%AC%EC%A1%B0.JPG" alt="리눅스 디렉토리 구조">

    - 리눅스의 루트 디렉토리(최상위 디렉토리)는 / 이다 

    - 리눅스는 디렉토리 경로를 / 로 구분하며, 이때 제일 앞의 / 는 루트 디렉토리고(폴더), 그 뒤의 / 는 경로 구분용 슬래시다

        - /boot/grub/fonts -> 맨 앞의 / 는 루트 디렉토리, 뒤의 / 두개는 경로 구분용 슬래시

        - 윈도우는 \ (백슬래시) 로 경로를 구분한다

* 명령어

    - 한/영 전환 : Shift + Space

    - 시스템 종료

        - poweroff, shutdown -P now, halt -p, init 0

    - 재부팅

        - reboot, shutdown -r now, init 6  

    - ls (List)

        - ls : 현재 디렉토리의 파일 목록

        - ls /etc/systemd : /ect/systemd 디렉토리의 목록

        - ls -a : 현재 디렉토리의 목록(숨김 파일 포함)

        - ls -l : 현재 디렉토리의 목록을 자세히 보여줌

        - ls *.conf : 확장자가 conf인 목록을 보여줌

        - ls -l /etc/systemd/n* : /etc/systemd 디렉토리의 목록 중 앞 글자가 n 인 것의 목록을 자세히 보여줌
    
    - cd (Change Directory) : 디렉토리 이동

        - cd : 현재 사용자의 홈 디렉토리로 이동

            - 현재 사용자가 root 면 /root 디렉토리로 이동

        - cd ~ubuntu : ubuntu 사용자의 홈 디렉토리로 이동

        - cd .. : 한단계 상위의 디렉토리로 이동

            - ..은 현 디렉토리의 부모 디렉토리를 의미

            - /etc/systemd 라면 한단계 상위인 /etc 로 이동

        - cd /etc/systemd : /etc/systemd 로 이동 (절대 경로)

        - cd ../etc/systemd : 상대 경로로 이동

            - .. 로 한단계 위로 이동한 뒤 다시 /etc/systemd 로 이동

    - pwd (Print Working Directory) : 현재 디렉토리 경로 확인

    - touch : 파일 생성

        - touch abc.txt : abc.txt 라는 빈 파일을 생성하고, 해당 이름의 파일이 있다면 최종 수정 시간을 현재 시각으로 변경

    - rm (Remove) : 삭제

        - rm abc.txt : abc.txt 삭제

        - rm -i abc.txt : 삭제 시 정말 삭제할 지 확인 메시지 나옴

        - rm -f abc.txt : 삭제 시 확인하지 않고 삭제 (f 는 Force 의 약자)

        - rm -rf abc : abc 디렉토리의 그 하위의 디렉토리를 강제로 전부 삭제 

    - cp (Copy) : 복사

        - cp abc.txt cba.txt : abc.txt를 cba.txt 라는 이름으로 바꿔서 복사

        - cp -r abc cba : abc 디렉토리를 cba 디렉토리로 복사

    - mv (Move) : 이동

        - mv abc.txt /etc/systemd/ : abc.txt 를 /etc/systemd/ 디렉토리로 이동

        - mv aaa bb ccc ddd : aaa, bbb, ccc 파일을 /ddd 디렉토리로 이동

        - mv abc.txt www.txt : abc.txt 의 이름을 www.txt 로 변경

    - mkdir(Make Directory) : 디렉토리 생성

        - mkdir abc : 현재 디렉토리 아래에 /abc 라는 디렉토리 생성

        - mkdir -p /def/fgh : /def/fgh 디렉토리를 생성하는데 만약 /fgh 디렉토리의 부모 디렉토리인 /def 디렉토리가 없다면 자동 생성해줌(p 는 parents 의 약자)

    - rmdir(Remove Directory) : 디렉토리 삭제

        - rmdir abc : abc 디렉토리 삭제

    - cat(Concatenate) : 파일 내용 화면 출력

        - cat a.txt b.txt : a.txt와 b.txt 를 연결해서 파일의 내용을 화면에 보여줌

    - head, tail : 텍스트 형식 파일의 앞 10행 또는 마지막 10행만 화면에 출력

        - head /etc/systemd/user.conf : 해당 파일의 앞 10행을 화면에 출력

        - head -3 /etc/systemd/user.conf : 앞 3행만 화면에 출력

        - tail -5 /etc/systemd/user.conf : 마지막 5행만 화면에 출력

    - more : 텍스트 형식 파일을 페이지 단위로 화면에 출력. 스페이스바는 다음 페이지, b는 앞 페이지, q는 종료

        - more /etc/systemd/system.conf : 해당 파일 페이지 단위로 화면에 출력

        - more +10 /etc/systemd/system.conf : 10행 부터 페이지 단위로 화면에 출력

    - less : more과 비슷하지만 추가로 화살표나 페이지 업/다운 키도 사용할 수 있다

        - less /ect/systemd/system.conf

        - less +10 /ect/systemd/system.conf
    
    - file : 파일 종류 표시

        - file /etc/systemd/system.conf : system.conf 는 텍스트 파일이므로 아스키 파일로 표시

        - file /bin/gzip : gzip 는 실행 파일이므로 'ELF 64-bit LSB shared object' 파일로 표시

    - clear : 터미널 화면 지우기
 
    - chmod [권한 숫자] [파일 이름] : 권한 변경

        - ls - l 입력 시 나오는 파일의 권한은 숫자로 나타낼 수 있다
     
            - 읽기(read - r)는 4, 쓰기(write - w)는 2, 실행(execute - x)는 1로 약속되어 있다

            - 위 숫자를 모두 더한 7 은 모든 권한, 6 은 읽기와 쓰기 권한, 5 는 읽기와 실행 권한, 4는 읽기 권한만 있는 주는 것이다 
 
            - 즉 777 은 소유자, 그룹, 누구나 의 3그룹 모두에게 모든 권한을 주는 것이다
            
            - 644 는 소유자는 읽기와 쓰기 권한, 그룹, 누구나 는 읽기 권한만 주는 것이다

        - ex) sudo chmod 644 index.html
   
    - netstat -nlpt : 실행 중인 포트 확인 (net-tools 설치 필요)


* 런(실행) 레벨

    - 시스템의 동작 모드를 정의

    - 런 레벨 0 (Power Off) : 종료 모드

    - 런 레벨 1 (Rescue) : 단일 사용자 모드(시스템 복구 모드)

    - 런 레벨 2 (Mulit-User) : 텍스트 다중 사용자 모드 (네트워크, GUI 미지원)

    - 런 레벨 3 (Mulit-User) : 텍스트 다중 사용자 모드 (네트워크 지원, GUI 미지원)

    - 런 레벨 4 (Mulit-User) : 텍스트 다중 사용자 모드 (기본적으로 사용하지 않는 모드)

    - 런 레벨 5 (Graphical) : 그래픽 다중 사용자 모드 (네트워크, GUI 지원)

    - 런 레벨 6 (Reboot) : 시스템 재부팅

    - 2, 4번은 우분투에서 사용하지 않고 3번과 동일하게 취급한다

    - 런 레벨 확인, 변경 명령어

        - runlevel : 런 레벨 확인

        - init [런레벨] : 런레벨 변경

* 리눅스 에디터

    - gedit : x 윈도우에서 제공하는 최신 에디터

        - 터미널에서 gedit 입력 시 에디터 실행

    - nano : 우분투에서 제공하는 텍스트 편집기

        - 터미널에서 cd 명령으로 홈 디렉토리 이동

        - nano 명령 입력시 nano 편집기 실행

        - nano [파일 이름] : 해당 파일을 nano 편집기로 실행

    - vi : 유닉스/리눅스 시스템에 기본으로 포함되어 있는 에디터

        - 처음 실행 시 명령 모드

            - 명령 모드에서 i, a 를 눌러 입력 모드, :(콜론) 을 눌러 ex 모드로 전환 가능

        - 명령 모드에선 글 작성 안됨

        - i 또는 a 를 입력 해 입력 모드로 전환하여 글 작성 가능

        - 글 삭제/수정 이 필요하면 esc를 눌러 명령 모드로 복귀

        - x를 눌러 문자 제거 -> 다시 입력 모드로 전환해 글 작성

            - 확장판인 vim 사용 시 입력 모드에서 글 삭제 가능

        - ex 모드에서 저장, 종료, 취소 등을 수행

            - 명령 모드에서 :를 눌러 ex 모드로 전환

            - w(저장), q(종료), i(취소) 등을 입력

            - :wq test.txt : ex 모드로 전환해서 test3.txt 이름으로 저장 후 종료

            - :q! : 기존 변경 내용 무시하고 종료

                - 잘못 수정했을대 수정한 내용을 저장하지 않고 종료하는 용도

        - 정상적으로 종료되지 않았을 때 

            - 파일 수정 중 x 버튼을 클릭해 강제 종료하면 이후에 접속했을 때 E325: ATTENTION 이 포함된 화면이 나타난다

            - 이전 작업에서 수정 작업이 정상적으로 종료되지 않았기 때문에 해당 파일의 임시 스왑 파일(.swp) 이 존재해서 발생하는 문제이다

            - 비정상적으로 종료된 파일의 스왑 파일 이름은 .파일이름.swp 이다

            - ls -a 명령으로 파일을 확인하고 rm -f 파일이름 으로 해당 흐왑 파일을 삭제해주면 된다

* 마운트

    - 저장 장치를 특정 폴더에 연결시키는 과정

    - 리눅스에는 장치 파일이 존재한다

        - 리눅스는 하드웨어 장치를 파일처럼 다루기 때문에 모든 장치는 /dev 아래에 장치 파일로 나타난다

        - CD/DVD : /dev/cdrom

        - USB 저장 장치 : /dev/sdb, /dev sdb1 ..

    - 외부 저장 장치를 연결하면 그 종류에 따라 해당 장치 파일들과 연결된다

        - CD/DVD 삽입 시 /dev/cdrom 과 연결됨

        - usb 연결 시 /dev/sdb 등과 연결됨

    - 그러나 내용은 볼 수 없는 상태로 해당 장치 파일을 폴더와 연결시켜야 확인할 수 있는데, 이 과정이 마운트 이다

        - /dev/sdb1 을 A 폴더와 마운트 시 A 폴더에서 USB의 내용을 확인할 수 있음  

    - 예시 1 (GUI)

        - CD/DVD 를 마운트할 것이므로 기존 마운트 해제 -> umount /dev/cdrom

            - CD/DVD 장치의 이름 : /dev/cdrom, /dev/sr0 
            
            - /dev/cdrom 이 /dev/sr0 를 가리키고 있어서 둘다 사용해도 무방하지만 sr0 는 리눅스에 따라서 다른 이름이 사용될 수 있으니 cdrom으로 사용하는 것이 좋다

        - 바탕 화면 오른쪽 아래 cd 아이콘 -> setting -> CD/DVD 에서 Connected, Connect at power on 체크

        - Use ISO image file 에서 사용할 iso 파일 선택

        - 종료 후 왼쪽 대시보드에 CD 아이콘이 생성되며, 클릭하면 iso 파일의 내용을 확인할 수 있다

        - 터미널에서 mount 입력 시 제일 아래에 CD/DVD 장치인 /dev/sr0 가 /media/root/xx.iso 디렉토리에 마운트되어 있는 것을 확인할 수 있다

            - GUI 환경은 마운트할 폴더 자동 생성 후 자동 마운트

        - CD 아이콘을 클릭해 GUI 환경에서 마운트한 iso 파일을 확인할 수도 있고, 터미널로 iso 파일 내용을 확인할 수도 있다

            - cd /media/root/xx.iso (마운트된 파일의 경로 이동)

            - 특정 파일 이동 후 ls -l

        - umount /dev/cdrom 명령으로 마운트 해제

            - 마운트 된 디렉토리의 상위 디렉토리로 이동후 마운트 해제해야 한다

            - 현재 디렉토리가 /media/root/xx.iso 라면 마운트 된 디렉토리이기 때문에 마운트를 해제하지 못한다

            - cd /media 명령어로 상위 디렉토리로 이동후 마운트 해제해야 정상적으로 명령이 수행된다

    - 예시 2 (텍스트 모드)

        - 텍스트 모드 접속 -> CD/DVD, USB 연결 -> 리눅스의 장치 파일과 연결되었지만 확인은 불가능한 상태

        - 마운트할 폴더 생성

            - /media 경로에 a, b 생성

        - 장치 파일과 생성한 폴더 마운트

            - CD/DVD 는 A, USB 는 B 와 마운트

                - mount /dev/cdrom /media/a

                - mount /dev/sdb1 /media/b

            - a 폴더에선 CD/DVD 파일 확인 가능, b 폴더에선 USB 파일 확인 가능

        - 사용이 끝나면 마운트 해제

* 사용자 관련

    - adduser : 새로운 사용자 추가

        - 실행하면 /etc/password, /etc/shadow, /etc/group 파일에 새로운 행(새로운 사용자의 정보) 가 추가된다

    - passwd [newuser1] : user1 사용자의 비밀번호를 변경

    - usermod --shell /bin/csh [newuser1] : newuser1 사용자의 기본 셸을 c 셸로 변경

        - 셸 : 명령어를 입력하면 이를 해석하고 실행해 주는 프로그램

        - newuser1 사용자의 기본 셸이 c 셸로 변경되는 것이고, 해당 사용자가 다른 셸을 실행하는 것은 가능하다

        - 리눅스/유닉스 : Bash, Zsh, Fish, Tcsh, Sh 등

        - Windows : CMD, PowerShell

        - macOS	: Zsh, Bash, Fish 

    - usermod --groups ubuntu [newuser1] : newuser1 사용자의 보조 그룹에 ubuntu 그룹 추가

    - userdel [user1] : 사용자 삭제

    - chage -m 30 [user1] : 사용자에 설정한 암호를 사용할 수 있는 최대 일자

    - group [user1] : user1 사용자가 소속된 그룹을 보여줌

    - groupadd [newgraoup1] : newgroup1 이라는 그룹 생성

    - groupmod --new-name [mygroup1] [newgroup1] : newgroup1 그룹의 이름을 mygroup1 으로 변경

    - groupdel [newgroup2] : newgroup2 그룹 삭제

* 파일과 디렉토리의 소유권과 허가권

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Linux/%ED%8C%8C%EC%9D%BC%20%EC%86%8C%EC%9C%A0%EA%B6%8C%2C%20%ED%97%88%EA%B0%80%EA%B6%8C.JPG" alt="파일 소유권, 허가권">

    - 파일 유형
    
        제일 왼쪽의 - 는 파일이 어떤 종류인지 나타내며 디렉토리는 d, 일반적인 파일은 - 로 표시된다

        - b : 블록 디바이스 (하드 디스크, CD/DVD, USB 등) 
        
        - c : 문자 디바이스 (마우스, 키보드, 프린터 등의 입출력 장치)
        
        - l : 링크 (바로 가기)

    - 파일 허가권

        - rw-, r--, r-- 3 묶음으로 끊어서 읽어야 한다

        - r 는 read, w 는 write, x 는 execute 의 약자다

        - rw 는 읽거나 쓸 수 있지만 실행할 수는 없다는 의미

        - rwx 는 읽고, 쓰고, 실행이 가능하다는 의미

        - 첫 번째 묶음 rw- 는 소유자의 파일 접근 권한, 두 번째 묶음 r-- 는 그룹의 파일 접근 권한, 세 번째 묶음 r-- 는 그 외 사용자의 파일 접근 권한을 의미한다

        - 즉 sample.txt 는 소유자는 읽거나 쓸 수 있고, 그룹은 읽을 수만 있으며, 그 외 사용자도 읽을 수만 있도록 허가되어 있다는 뜻이다

    - 파일 소유권

        - 파일을 소유한 사용자와 그룹을 의미한다
     
        - 3번째 묶음 뒤의 root root 에서 첫번째 단어는 소유자가 누구인지, 두번째 단어는 소유 그룹이 누구인지 나타낸다

        - sample.txt 파일은 root 사용자가 소유자이며, root 그룹이 소유하고 있다

* 패키지 설치 명령어

    - dpkg, apt 가 있으며 apt가 dpkg의 개념과 기능을 포함하므로 대부분 apt를 사용한다

    - 리눅스에는 윈도우의 설치 파일인 .exe 와 비슷한 역할을 하는 .deb 가 존재하며 이를 패키지 라고 부른다

    - dpkg

        - .deb 파일을 다운로드하고, 다운로드된 경로로 이동해 dpkg 명령어를 사용해서 설치를 진행할 수 있다

            - .deb 파일 다운 -> 다운로드된 경로 이동 (/사용자/다운로드)

            - dpkg -i [패키지파일 이름.deb] 로 설치

            - dpkg -r [패키지이름] 설치된 실행파일 제거

                .deb 파일은 rm 패키지이름.deb 로 따로 제거해야 한다

        - 하지만 의존성 문제가 있는 패키지는 dpkg 명령어로 설치하지 못한다

            - A 패키지를 설치하려면 B 패키지를 먼저 설치해야 할때, 이를 무시하고 A 패키지를 dpkg 명령어로 설치하면 설치되지 않는다

            - 특정 패키지를 설치하기 위해 어떤 .deb 파일이 필요한지 모를때도 있고, 알아내더라도 해당 deb 파일이 또 다른 deb 파일에 의존성을 갖고 있을 수도 있다

    - apt

        - 우분투(Ubuntu), 데비안(Debian) 계열 리눅스에서 사용하는 패키지 관리 명령어
     
            - apt 가 apt-get 의 개선 버전
     
        - 정해진 서버(저장소)에 접속해서 패키지를 다운로드
     
            - /etc/apt/sources.list 경로에 저장소 URL 이 등록되어 있음  

        - dpkg 사용시 발생하는 패키지 의존성 문제를 해결해 준다

            - 특정 패키지를 설치하려 할 때 의존성이 있는 다른 패키지를 자동으로 먼저 설치해 준다

        ```
        apt update : 패키지 인덱스를(어떤 패키지가 존재하고, 어디에 있고, 어떤 버전이 있는지) 최신 상태로 갱신
        
        apt -y install [패키지이름] : 패키지 설치

        apt remove [패키지이름] : 패키지 삭제

        apt purge [패키지이름] : 설치된 패키지의 설정 파일을 포함해 완전히 제거
        ```

* 예약, 일회성 작업

    - cron (예약)

        - 주기적으로 반복되는 일을 자동으로 실행하도록 예약해 놓는 것

        - 형식 : 분 시 일 월 요일 사용자 실행명령

        - 분에는 0 ~ 59, 시에는 0 ~ 23, 월에는 1 ~ 12, 요일에는 0 ~ 6 이 올 수 있다

        - * 표시는 모든 분, 시, 월, 요일을 의미하며 요일은 0부터 일요일로 시작한다

        - 00 05 1 * * root cp -r /home /backup

        - 매월 1일 05시 00분에 cp -r /home /backup 명령을 실행한다

    - at (일회성)

        - 일회성 작업을 예약

        - at 3:00am tomorrow 내일 새벽 3시

* 하드디스크 관리

    - SATA, SCSI : 저장장치를 컴퓨터(메인보드) 와 연결하는 방법

        - 저장장치를 메인보드에 꽂는 것은 동일하지만, 그 이후에 사용하는 연결 방식에 따라 데이터 전송 속도, 기능 등이 달라진다

        - SATA : 일반적인 HDD, SDD 연결 방식

        - SCSI : 서버에서 많이 쓰던 방식. 현재는 SAS 로 발전함

    - 리눅스에서는 SCSI 에 처음 장착된 장치의 이름을 /dev/sda 라고 부른다

        - SCSI 에 추가로 저장 장치가 장착되면 /dev/sdb, /dev/sdc 등으로 부른다

        - 그리고 하나의 저장 장치에 파티션이 나눠진 것을 /dev/sda1, /dev/sda2 .. 형식으로 부른다

### 셸

* 사용자가 운영체제에 명령을 내릴 수 있도록 해주는 인터페이스

* 사용자아 셸을 통해 운영체제에 명령을 내리면 운영체제가 하드웨어를 제어한다

    - 리눅스/유닉스 : Bash, Zsh, Fish, Tcsh, Sh 등

    - Windows : CMD, PowerShell

    - macOS	: Zsh, Bash, Fish 

* 우분투에서는 bash 셸을 기본적으로 사용한다

* 셸의 명령문 처리 형식

    ```
    (프롬프트) 명령 [옵션..] [인자..]
    ```

    - 프롬프트 : 사용자에게 입력을 요청하는 메시지 또는 기호

        - CMD : C:\Users\Username> 

        - Linux : user@hostname:~$

* 환경 변수

    - 운영체제가 실행되는 동안 유지되며, 프로그램이나 셸 스크립트에서 사용할 수 있는 변수

    - 주로 경로, 설정값, 사용자 정보, 시스템 정보 등을 저장하는 역할을 한다

    - echo $ [환경변수이름] 형식으로 명령을 실행할 수 있다

    - 주요 환경변수

        - HOME : 현재 사용자의 홈 디렉토리

        - BASH : bash 셸의 경로

    - printenv : 환경 변수 목록 출력

### 셸 스크립트 프로그래밍

* 스크립트

    - 명령어 또는 코드들의 모음

        - 리눅스/유닉스 운영체제는 Bash, Zsh 같은 셸을 사용하기 때문에 셸 스크립트(.sh) 라고 한다
        
        - 윈도우 운영체제는 명령 프롬프트(CMD) 를 사용하고 배치 스크립트(.bat) 라고 한다

    - 스크립트를 작성한다는 것은 여러 명령어들을 한 번에 실행하기 위해 하나의 파일에 작성하는 것을 의미

        - a.sh 에 명령어들을 작성해 두고 a.sh 만 실행시키면 작성해 두었던 명령어들이 실행된다

    - 즉, 우리가 평소에 터미널이나 명령 프롬프트에서 한 줄씩 입력하는 명령어들을 하나의 파일(스크립트 파일)에 정리해 두고, 한 번에 실행할 수 있도록 만드는 것
