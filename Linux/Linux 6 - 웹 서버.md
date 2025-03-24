### 웹 서버

* 설치

    - apt -y install lamp-server^ 

        - 위 명령으로 apache, php, mysql-server 를 한번에 설치할 수 있다

        - 위 세 가지를 앞 글자만 따서 APM 이라고 부르기도 한다

        - APM 을  리눅스에서 사용하는 경우 LAPM 이라고 한다 

    - 서비스 가동

        - 부팅 시 자동으로 서비스가(apache2 웹 서버) 실행되도록 설정 : systemctl enable apache2

        - 서비스 재시작 : systemctl restart apache2

        - 서비스 상태 확인 : systemctl status apache2

        - mysql 도 동일하게 가동

        - php는 apache2 에 포함된 기능이므로 별도로 가동하지 않아도 됨

    - 포트 허용

        - 외부에서 웹 서버에 접근할 수 있도록 포트 허용 : ufw allow 80 

    - 주소창에 서버 ip 입력시 apache2 가 설치된 웹 서버로 접속 가능
