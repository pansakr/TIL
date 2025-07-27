### EC2 배포 v2

* 환경 변수의 적용 범위

    - .bashrc 에 등록 후 재부팅 혹은 source 사용 : 영구으로 적용

    - 터미널에서 export 명령어로 환경변수 적용 : 터미널 종료 시 다시 적용해야 함

    - 쉘 스크립트로 파일을 만들고 해당 파일에서 환경변수 등록

        - 해당 파일에서 source 명령어를 사용 시 파일이 실행되는 동안에만 사용되고, 터미널엔 적용 안됨

        - 해당 파일을 .bashrc 에 등록하고 .bashrc 파일에 source 명령어 적용시 영구적으로 적용

* 환경 변수 등록 및 사용

    - 등록

        - vim var.sh

        ```shell
        #!/bin/bash

        GITHUB_ID="codingspecialist"
        PROJECT_NAME="aws-v2"
        PROJECT_VERSION="0.0.1"
        PROJECT_PID="$(pgrep -f ${PROJECT_NAME}-${PROJECT_VERSION}.jar)"
        JAR_PATH="${HOME}/${PROJECT_NAME}/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar"

        export GITHUB_ID
        export PROJECT_NAME
        export PROJECT_VERSION
        export PROJECT_PID
        export JAR_PATH
        ```

        - source ./var.sh 입력 시 환경 변수로 적용됨

        - 하지만 .bashrc 파일에 적용한게 아니기 때문에 재시작 시 정보가 사라짐

    - 사용

        - var.sh 의 변수들을 deploy.sh 파일에서만 사용

        - vim deploy.sh

            ```shell
            #!/bin/bash

            source ./var.sh

            echo $GITHUB_ID
            ```    

        - var.sh 는 deploy.sh 파일이 실행되는 동안에만 적용됨

        - 터미널에 $GITHUB_ID 입력 시 값이 나오지 않음

* 재배포를 고려한 크론 종료

    - 최초 배포

        - ec2 서버에 8080 포트로 서버가 실행됨 -> PID 는 1111

        - PID (1111)를 1분마다 감시하며 서버가 종료 되었는지 확인하고, 종료 되었을 시 재시작해 주는 로직이 cron에 등록됨

    - 재배포

        - EC2 서버에서 실행되고 있는 기존 프로젝트(8080) 를 종료시킴
        
        - 깃허브에서 다시 프로젝트를 다운받고 jar 파일을 빌드 후 실행

    - 재배포 과정은 30초, cron 은 1분마다 감시한다면 재배포를 위해 프로세스를 종료시켰을 때 도중에 cron이 작동해 로직이 꼬일 위험이 있다

        - 재배포 시 기존 8080 서버를 무조건 종료시켜야 하는데, 이때 cron이 종료를 감지하고 재실행 시킬 가능성이 있음

        - 그렇게 되면 재배포 로직이 제대로 작동하지 않게 됨

    - 재배포 도중에는 cron 이 작동될 필요가 없으므로 기존 cron을 종료시키는 과정이 필요하다

        - 재배포가 모두 끝나고, 기존 cron 을 다시 등록하면 된다

    - vim deploy.sh (재배포를 고려한 스크립트)

        ```shell
        #!/bin/bash

        # 1. env variable
        source ./var.sh
        echo "1. env variable setting complete"

        # 2. cron delete
        touch crontab_delete
        crontab crontab_delete
        rm crontab_delete
        echo "2. cron delete complete"

        # 3. server checking
        if [ -n "${PROJECT_PID}" ]; then
            # re deploy
            kill -9 $PROJECT_PID
            echo "3. project kill complete"
        else
            # first deploy
            # 3-1 apt update
            sudo apt-get -y update 1>/dev/null
            echo "3-1. apt update complete"

            # 3-2 jdk install
            sudo apt-get -y install openjdk-11-jdk 1>/dev/null
            echo "3-2. jdk install complete"

            # 3-3 timezone
            sudo timedatectl set-timezone Asia/Seoul
            echo "3-3. timezone setting complete"
        fi

        # 4. project folder delete
        rm -rf ${HOME}/${PROJECT_NAME}
        echo "4. project folder delete complete"

        # 5. git clone
        git clone https://github.com/${GITHUB_ID}/${PROJECT_NAME}.git
        sleep 3s
        echo "5. git clone complete"

        # 6. gradlew +x
        chmod u+x ${HOME}/${PROJECT_NAME}/gradlew
        echo "6. gradlew u+x complete"

        # 7. build
        cd ${HOME}/${PROJECT_NAME}
        ./gradlew clean build
        echo "7. gradlew build complete"

        # 8. start jar
        nohup java -Dspring.profiles.active=prod -jar ${JAR_PATH} 1>${HOME}/log.out 2>${HOME}/err.out &
        echo "8. start server complete"

        # 9. cron registration
        touch crontab_new
        echo "* * * * * ${HOME}/check-and-restart.sh" 1>>crontab_new
        # register the others... you use >> (append)
        crontab crontab_new
        rm crontab_new
        echo "9. cron registration complete"
        ```

        - 1. env variable : 환경 변수 설정

        - 2. cron delete : 크론 작업 삭제

        - 3. server checking

            - -n : 문자열의 길이가 0이 아니면 true

            - 프로세스가 실행 중이라면(PROJECT_ID 의 값이 있다면) 프로세스 종료

            - 프로세스가 실행 중이 아니라면 최초 배포로 보고 apt update, 자바 설치, 타임존 설정 실행

            - 1>/dev/null : 로그 표준 출력을 쓰레기통으로 버리는 설정

                - 업데이트나 설치 로그를 보관할 필요는 없기 때문

        - 4. project folder delete

            - 최초 배포 시 git에서 다운받은 폴더가 없지만, 재배포 시 기존 폴더가 존재하므로 삭제해야 한다

            - -r : 폴더 내부에 다른 파일이 있을 때 다 같이 삭제하는 옵션

            - -f : 폴더 내부 파일을 삭제할 때 정말 삭제해도 되냐고 물어보게 되는데, 그때 스크립트가 멈추게 되므로 그것을 방지하기 위해 강제로 삭제하는 옵션

        - 5. git clone

            - sleep 3s : 3초 대기

            - clone 으로 프로젝트를 모두 다운받을 때까지 기다려야 
            
                - git clone 명령은 CPU가 동기식으로 실행할지, 비동기식으로 실행할지 알 수 없다

                - 동기식으로 실행되면 다운로드 완료 후 다음 명령(빌드)을 실행하므로 정상 작동

                - 비동기식으로 실행되면 다운로드를 요청해두고 다음 명령(빌드) 를 실행하게 되어 문제 발생

            - 다운이 완료되지 않았는데 다음 과정에서 빌드 로직을 진행하면 문제가 생긴다

        - 6. gradlew +x : gradlew 권한 부여

        - 7. build : gradlew 빌드

        - 8. start 

            - -D : 자바에서 시스템 속성을 설정하는 옵션

                - -Dsrping.profiles.active=dev : 활성화할 프로필 설정 (dev 프로필 활성화)

                - -Dfile.encoding=UTF-8 : 파일 인코딩 설정

            - prod 프로필로 스프링 부트 프로젝트 백그라운드 실행
        
        - 9. cron registration

            - 크론 작업으로 사용할 스크립트 파일 등록

    - check-and-restart.sh

        ```shell
        #!/bin/bash
        source ./var.sh

        echo "[DEBUG] HOME=$HOME" 1>a.log
        echo "[DEBUG] PROJECT_NAME=$PROJECT_NAME" 1>>a.log
        echo "[DEBUG] PROJECT_VERSION=$PROJECT_VERSION"  1>>a.log
        echo "[DEBUG] JAR_PATH=$JAR_PATH" 1>>a.log
        echo "[DEBUG] PROJECT_PID=$PROJECT_PID" 1>>a.log

        if [ -z "${PROJECT_PID}" ]; then
            nohup java -Dspring.profiles.active=prod -jar ${JAR_PATH} 1>${HOME}/log.out 2>${HOME}/err.out &
        fi
        ```

        - PROJECT_PID 가 0 이면 스프링 부트 실행

* 배포 파일 압축 후 새로운 서버에 압축 풀기

    - tar -cvf deploy.tar check-and-restart.sh deploy.sh var.sh : 배포를 위해 만든 위의 3가지 스크립트를 tar로 압축

    - 로컬이나 별도 저장 장치로 전송

    - 새로운 ec2 서버 생성 후 deploy.tar 파일을 옮겨서 압축 풀고 deploy.sh 파일 실행 -> 배포 완료
