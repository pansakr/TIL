### EC2 배포 v1

* 배포 순서

    - EC2 인스턴스 임대

    - 깃허브 프로젝트 다운로드
    
        - git clone [주소]

    - JDK 설치
    
        - sudo apt update

        - sudo apt install openjdk-17-jdk
    
    - ubuntu timezone 세팅

    - gradlew 실행 권한 부여 -> 빌드

        - cd ~/프로젝트명

        - chmod u+x gradlew

        - ./gradlew clean build
    
    - nohup 명령어로 spring boot 실행 (백그라운드로 jar 실행)

        - nohup java -jar $SPRING_PATH 1>log.out 2>err.out &

    - Linux/스프링 부트 프로젝트 배포.md 참조

* 스프링 부트 백그라운드 실행

    - nohup java -jar xxx.jar &

        - .jar 파일이 있는 곳으로 이동 후 입력해야 함

        - 백그라운드 실행 시 nohup.out 파일이 생성되고, 이곳에 로그가 쌓인다

        - nohup : 터미널을 닫아도 프로그램이 종료되지 않게 함

        - java : jvm 실행 명령어

        - -jar : JAR(Java ARchive) 파일을 실행하겠다는 옵션

        - & : 백그라운드 실행 – 터미널을 점유하지 않고 실행됨

    -  로그 저장 파일 변경 후 실행 (권장)

        - nohup java -jar xxx.jar 1>log.out 2>err.out &

            - 1>log.out : 표준 출력을 log.out 파일에 저장

            - 2>err.out : 표준 에러를 err.out 파일에 저장

            - 실행이 안될 때 에러 로그만 찾는게 빠르기 때문에 정상/에러 로그를 분리 하는게 좋다

            - > 또는 1> : 표준 출력(stdout) - System.out.println, 로그 등

            - 2> : 표준 에러(stderr) - 에러 메시지

* 타임존 설정
 
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

    - pgrep -f .jar : 현재 실행 중인 프로세스 중 .jar라는 문자열이 들어간 명령어를 찾아서 해당 PID(프로세스 ID)를 출력

* 스프링 부트 종료

    - netstat -nlpt : 실행 중인 포트 와 pid 확인

    - kill -9 [pid] : pid 종료 

* 스프링 부트 종료 스크립트 작성

    - 스크립트 파일 생성 : vim spring-stop.sh

    ```shell
    echo "Springboot Stop....." # 모니터에 글자 출력

    # pgrep -f v1-0.0.1-SNAPSHOT.jar : 실행할 때 입력한 명령어 중 v1-0... 가 있는 프로세스를 찾아서 PID 출력 
    # 그리고 그 값을 Spring_PID 변수에 담음
    Spring_PID=$(pgrep -f v1-0.0.1-SNAPSHOT.jar) 

    echo $Spring_PID # 변수 내용 출력 

    kill -9 $Spring_PID # 변수 내용에 해당하는 PID 종료
    ```

    - ls -l 로 권한 확인 후 chmod 로 실행 권한 부여

    - ./spring-stop.sh 실행 -> 글자가 출력되며 8080 포트 종료됨

* cron 으로 프로젝트 종료/재시작

    - 종료

        - vim spring-stop.sh

        ```shell
        echo "SPRING-BOOT STOP..."
        SPRING_PID=$(pgrep -f v1-0.0.1-SNAPSHOT.jar)
        kill -9 $SPRING_PID
        ```
        
        - spring-stop.sh 파일에 실행 권한 부여 
        
        - 실행 시 스프링 부트 서버 종료됨

        - netstat -nlpt 로 8080 포트가 종료되었음을 확인 
    
    - 재시작

        - vim spring-restart.sh

        ```shell
        SPRING_PID=$(pgrep -f v1-0.0.1-SNAPSHOT.jar)
        SPRING_PATH="/home/ubuntu/aws-v1/build/libs/v1-0.0.1-SNAPSHOT.jar"

        echo $SPRING_PID
        echo $SPRING_PATH

        # -z : 문자열의 길이가 0이면 TRUE
        if [ -z "$SPRING_PID" ]; then
                echo "스프링이 종료된 상태..."
                echo "스프링 재시작 - $(date)" 1>>/home/ubuntu/cron-restart/spring-restart.log
                nohup java -jar $SPRING_PATH 1>log.out 2>err.out &
        else
                echo "스프링이 시작된 상태..."
        fi
        ```

        - spring-restart.sh 파일을 실행했을 때 스프링 부트 서버가 종료 상태라면 재실행 됨

    - cron 에 등록

        - vim deploy.sh

        ```shell
        # 1. 배포 프로세스
        echo "deploy start..."
        echo "1. JDK install"
        echo "2. github project download"
        echo "3. gradlew 실행 권한 주기"
        echo "4. project build 하기"
        echo "5. ubuntu timezone setting 하기"
        echo "6. nohup으로 springboot 실행시키기"

        #2. 스프링 서버 종료 시 재시작
        echo "crontab 등록 - spring restart..."
        crontab -l > crontab_new
        echo "* * * * * /home/ubuntu/cron-restart/spring-restart.sh" 1>>crontab_new
        crontab crontab_new
        rm crontab_new
        ```

    - deploy.sh 를 실행시켜서 crontab에 spring-restart.sh 등록

    - spring-stop.sh 를 실행해 스프링 부트 서버 종료

    - 크론에서 매 분 spring-restart.sh 를 실행하기 때문에 스프링 부트 서버 자동 실행됨

* 재배포

    - 기존 서버 중지

        - 만들어둔 spring-stop.sh 실행

        - 또는 ps -ef | grep java | grep -v grep 로 PID 찾기 -> kill -p [PID]

    - 프로젝트 다운, gradlew 실행 권한 부여, 빌드, jar 실행
