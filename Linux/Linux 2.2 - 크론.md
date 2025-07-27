### cron

* 시간 기반 잡 스케줄러

    - 잡 : 컴퓨터에게 시키는 일거라 하나(명령, 스크립트 등)

    - 스케줄러 : 잡을 시간에 맞춰 자동 실행시켜주는 프로그램

    - 정해진 시간에 자동으로 어떤 일을 실행해주는 도구를 뜻함

* cron 작업을 설정하는 파일을 crontab 이라고 한다

    - cron 프로세스는 /etc/crontab 파일에 설정된 내용을 읽어서 작업을 수행함

    - crontab 에 내용을 추가하면 cron이 작동되어 내용이 주기적으로 실행됨

    - crontab 파일은 한 사용자당 하나만 존재한다

    - crontab의 표준 출력은 터미널이 아니라 이메일로 전송된다

        - crontab 의 예약된 작업 실행후 무언가 출력이(성공/실패 로그 등) 생기면 해당 crontab 소유자에게 메일이 발송된다

        - 메일이 없으면 출력은 버려지거나 로그에 기록되지 않는다

        - 출력을 파일로 리디렉션하면 파일에서 확인할 수 있다

    - crontab -e : crontab 파일 편집 명령어

        - 디렉토리 위치는 상관없이 현재 사용자 계정의 크론 설정을 열기 때문에 어느 위치에서든 실행 가능

        - 입력 시 어떤 편집기로 실행할지 선택지가 주어지고, 선택하면 편집기 화면으로 바뀐다

    - crontab -l : crontab 파일 출력

    - crontab [aaa] : aaa 파일 내용을 현재 사용자의 crontab 파일로 덮어씀

    - 예시

        - crontab -e -> 편집기 선택

        ```
        * * * * * ls -l 1>>cron.log     # 매 분/시간/일/월/요일 le -l 을 실행해서 cron.log에 저장하라는 설정
        ```
        - 첫 번째 * : 분   (0 ~ 59)

        - 두 번째 * : 시간 (0 ~ 23)

        - 세 번째 * : 일   (1 ~ 31)

        - 네 번째 * : 월   (1 ~ 12)

        - 다섯 번째 * : 요일(0 ~ 7)

    - cron 자동화

        - vim myScript.sh

            ```shell
            // 크론탭 파일의 내용을 크론탭_뉴 파일에 복사
            crontab -l 1>crontab_new

            // 아래 문자열을 크론탭_뉴 파일에 이어 붙임
            echo "* * * * * /home/ubuntu/job.sh" 1>>crontab_new

            // 크론탭에 crontab_new 파일을 반영
            crontab crontab_new

            // 등록이 되었으니 crontab_new 삭제
            rm crontab_new
            ```

            - 결과적으로 crontab_new 파일이 크론탭에 등록되어 매 분 마다 job.sh 를 실행하게 됨 

        - job.sh

            ```shell
            ls -l /home/ubuntu/cron.log
            ```

            - 크론잡 으로 매 분마다 실행되며, ls -l 결과를 cron.log에 저장
