### 명령어

* 한/영 전환 : Shift + Space

* 시스템 종료

    - poweroff, shutdown -P now, halt -p, init 0

* 재부팅

    - reboot, shutdown -r now, init 6  

* ls (List)

    - ls : 현재 디렉토리의 파일 목록

    - ls /etc/systemd : /ect/systemd 디렉토리의 목록

    - ls -a : 현재 디렉토리의 목록(숨김 파일 포함)

    - ls -l : 현재 디렉토리의 목록을 자세히 보여줌

    - ls *.conf : 확장자가 conf인 목록을 보여줌

    - ls -l /etc/systemd/n* : /etc/systemd 디렉토리의 목록 중 앞 글자가 n 인 것의 목록을 자세히 보여줌
    
* cd (Change Directory) : 디렉토리 이동

    - cd : 현재 사용자의 홈 디렉토리로 이동

        - 현재 사용자가 root 면 /root 디렉토리로 이동

    - cd ~ubuntu : ubuntu 사용자의 홈 디렉토리로 이동

    - cd .. : 한단계 상위의 디렉토리로 이동

        - ..은 현 디렉토리의 부모 디렉토리를 의미

        - /etc/systemd 라면 한단계 상위인 /etc 로 이동

    - cd /etc/systemd : /etc/systemd 로 이동 (절대 경로)

    - cd ../etc/systemd : 상대 경로로 이동

        - .. 로 한단계 위로 이동한 뒤 다시 /etc/systemd 로 이동

* pwd (Print Working Directory) : 현재 디렉토리 경로 확인

* touch : 파일 생성

    - touch abc.txt : abc.txt 라는 빈 파일을 생성하고, 해당 이름의 파일이 있다면 최종 수정 시간을 현재 시각으로 변경

* rm (Remove) : 삭제

    - rm abc.txt : abc.txt 삭제

    - rm -i abc.txt : 삭제 시 정말 삭제할 지 확인 메시지 나옴

    - rm -f abc.txt : 삭제 시 확인하지 않고 삭제 (f 는 Force 의 약자)

    - rm -rf abc : abc 디렉토리의 그 하위의 디렉토리를 강제로 전부 삭제 

* cp (Copy) : 복사

    - cp abc.txt cba.txt : abc.txt를 cba.txt 라는 이름으로 바꿔서 복사

    - cp -r abc cba : abc 디렉토리를 cba 디렉토리로 복사

* mv (Move) : 이동

    - mv abc.txt /etc/systemd/ : abc.txt 를 /etc/systemd/ 디렉토리로 이동

    - mv aaa bb ccc ddd : aaa, bbb, ccc 파일을 /ddd 디렉토리로 이동

    - mv abc.txt www.txt : abc.txt 의 이름을 www.txt 로 변경

* mkdir(Make Directory) : 디렉토리 생성

    - mkdir abc : 현재 디렉토리 아래에 /abc 라는 디렉토리 생성

    - mkdir -p /def/fgh : /def/fgh 디렉토리를 생성하는데 만약 /fgh 디렉토리의 부모 디렉토리인 /def 디렉토리가 없다면 자동 생성해줌(p 는 parents 의 약자)

* rmdir(Remove Directory) : 디렉토리 삭제

    - rmdir abc : abc 디렉토리 삭제

* cat(Concatenate) : 파일 내용 화면 출력

    - cat a.txt b.txt : a.txt와 b.txt 를 연결해서 파일의 내용을 화면에 보여줌

* head, tail : 텍스트 형식 파일의 앞 10행 또는 마지막 10행만 화면에 출력

    - head /etc/systemd/user.conf : 해당 파일의 앞 10행을 화면에 출력

    - head -3 /etc/systemd/user.conf : 앞 3행만 화면에 출력

    - tail -5 /etc/systemd/user.conf : 마지막 5행만 화면에 출력
    
    - tail -f : 파일의 마지막 10줄을 출력하고, 그 이후 새로 추가되는 내용도 실시간으로 계속 출력

* more : 텍스트 형식 파일을 페이지 단위로 화면에 출력. 스페이스바는 다음 페이지, b는 앞 페이지, q는 종료

    - more /etc/systemd/system.conf : 해당 파일 페이지 단위로 화면에 출력

    - more +10 /etc/systemd/system.conf : 10행 부터 페이지 단위로 화면에 출력

* less : more과 비슷하지만 추가로 화살표나 페이지 업/다운 키도 사용할 수 있다

    - less /ect/systemd/system.conf

    - less +10 /ect/systemd/system.conf

* file : 파일 종류 표시

    - file /etc/systemd/system.conf : system.conf 는 텍스트 파일이므로 아스키 파일로 표시

    - file /bin/gzip : gzip 는 실행 파일이므로 'ELF 64-bit LSB shared object' 파일로 표시

* clear : 터미널 화면 지우기
 
* chmod [권한 숫자] [파일 이름] : 권한 변경

    - ls - l 입력 시 나오는 파일의 권한은 숫자로 나타낼 수 있다
    
        - 읽기(read - r)는 4, 쓰기(write - w)는 2, 실행(execute - x)는 1로 약속되어 있다

        - 위 숫자를 모두 더한 7 은 모든 권한, 6 은 읽기와 쓰기 권한, 5 는 읽기와 실행 권한, 4는 읽기 권한만 있는 주는 것이다 

        - 즉 777 은 소유자, 그룹, 누구나 의 3그룹 모두에게 모든 권한을 주는 것이다
        
        - 644 는 소유자는 읽기와 쓰기 권한, 그룹, 누구나 는 읽기 권한만 주는 것이다

    - ex) sudo chmod 644 index.html

* netstat -nlpt : 실행 중인 포트 확인 (net-tools 설치 필요)

* ps -ef : 현재 실행 중인 모든 프로세스 목록 출력

    - e : 모든 사용자의 모든 프로세스를 보여줌 (--everyone의 약자)
    
    - f : 자세한 정보를 출력 (UID, PID, PPID 등) --full format 의 약자

* grep : 특정 문자열이나 패턴을 파일 또는 출력 결과에서 찾아주는 명령어

    - grep "8080" nohup.out	: nohup.out 파일에서 8080 관련 로그만 검색
    
    - grep "Exception" nohup.out : nohup.out 파일에서 Exception 만 골라서 검색
    
    - tail -f mylog.out | grep "ERROR" : tail -f 명령어와 함께 사용해서 "ERROR"가 포함된 줄만 실시간 모니터링
    
* 타임존

    - timedatectl : 설정된 타임존 확인
    
    - timedatectl list-timezones : 타임존 설정값 종류 확인
    
    - timedatectl list-timezones | grep Seoul : 그중 서울 확인
    
    - sudo timedatectl set-timezone Asia/Seoul : Asia/Seoul 타임존으로 설정

* PID 검색 : gprep [option] pattern

    - pgrep : 실행 중인 프로세스 중 특정 조건에 맞는 PID만 출력

    - -u : 특정 사용자에 대한 프로세스 출력

    - -l : PID 와 프로세스 이름 출력

    - -f : 실행할 때 입력한 전체 문장 검색

    - pgrep java : java 로 실행 중인 프로세스의 pid 출력

    - pgrep -f .jar : 현재 실행 중인 프로세스 중 실행할 때 입력한 명령어에서 .jar라는 문자열이 들어간 명령어를 찾아서 해당 PID(프로세스 ID)를 출력

* 리눅스 기본 입출력 스트림

    - 리눅스 시스템은 3가지 기본 입출력 스트림 가지고 있다

        ```
        번호 이름      설명            기본 목적지
        0    stdin    표준 입력        키보드 입력

        1    stdout   표준 출력        터미널 화면

        2    stderr   표준 에러 출력   터미널 화면
        ```
 
        - 표준 출력 : 프로그램이 정상적으로 실행될 때 출력할 메시지가 흘러가는 기본 출력 통로

            - 프로그램이 정상 실행되었을 때 출력할 메시지가 출력 통로를 통해 기본으로 설정된 입구인 터미널 화면까지 도착해 쉘에 표시된다
          
            - echo, ls, cat, java 등이 정상 실행 되었을 때 쉘에 출력되는 텍스트가 표준 출력이 사용된 것이다
         
        - 표준 에러 출력 : 프로그램이 에러나 경고 메시지를 출력할 때 사용하는 출력 통로
     
            - 표준 출력과 분리된 경로로 에러 메시지를 보낸다
     
* 리다이렉션 기호

    ```
    - >, 1> : 표준 출력의 기본 목적지를 파일로 리다이렉션할때 사용 (덮어쓰기)

        - echo "hello" > result.txt
     
            - echo "hello" 의 결과로 원래는 hello 가 표준 출력을 통해 쉘에 표시된다
          
            - 그런데 > 명령어로 기본 목적지를 result.txt 로 바꿈으로서 hello가 표준 출력을 통해 result.txt에 저장됨
          
        - result.txt 가 없다면 생성 후 hello 가 저장되고, result.txt 가 있다면 덮어쓴다 
     
    - 2> : 표준 에러 출력의 기본 목적지를 파일로 리다이렉션할 때 사용 (덮어쓰기)

    - >> : 표준 출력 기본 목적지 리다이렉션 (이어쓰기)

        - 파일이 없다면 생성 후 내용이 저장되고, 있다면 맨 끝에 이어 저장된다

    - 2>> : 표준 에러 출력 기본 목적지 리다이렉션 (이어쓰기)

    - &> : 표준 출력 + 표준 에러 출력 모두 리다이렉션 (덮어쓰기)
    ```

* 환경 변수

    - 운영체제가 프로그램을 실행할 때 해당 프로세스에게 미리 전달해주는 설정값 
    
    - 스프링 부트에서 DB 환경 설정을 환경 변수로 넘기기

        - export DB_PASSWORD=abc123

        - java -jar app.jar

        - Srping Boot 는 DB_PASSWORD 환경 변수를 읽어서 DB에 로그인할 때 사용

    - 리눅스의 PATH 변수

        - echo $PATH => /usr/local/...

        - 리눅스는 ls, java, curl 같은 명령어를 실행할 때 이 경로들 안에서 해당 명령어를 찾는다

        - 즉 PATH는 명령어를 실행할 때 어디를 먼저 찾을지 알려주는 환경 변수

* export : 환경 변수 적용 명령어

    - 환경 변수 적용

        ```shell
        export HELLO="hello ubuntu"

        echo $HELLO

        # hello ubuntu 출력됨
        ```

    - 재접속 시 export 로 등록했던 환경 변수 정보들이 사라진다

    - 환경 변수를 저장하는 리눅스의 설정 파일에 export 정보를 등록하면 영구적으로 저장된다

    - 해당 설정 파일은 어떤 shell을 사용하는지에 따라 달라지고, 우분투는 .bashrc 이다

        ```shell
        # 숨겨진 파일 보기
        ls -al

        vim bashrc

        # bashrc 파일의 제일 아래쪽에 추가
        export HELLO="hello ubuntu"
        ```

        - .bashrc 의 내용은 컴퓨터 부팅시 적용된다

        - 바로 적용 시키고 싶다면 source bashrc 입력
