### DB 서버 구축

* 리눅스 운영체제에 DB(Mysql) 설치

    - 저장소 업데이트 : apt update -> MySql 설치 : apt -y install mysql-server mysql-client

    - 서비스 상시 가동 : systemctl enable mysql

    - 서비스 재시작 : systemctl restart mysql

    - 서비스 상태 확인 : systemctl status mysql

    - 포트 허용 : ufw allow 3306

    - 터미널에서 mysql 입력시 접속됨

* DB 사용자 설정

    - MySql 설치 시 기본 사용자로 root 사용자가 생성됨

        - MySql 서버의 root 사용자의 비밀번호는 지정되지 않은 상태

    - 원칙적으로 MySql 서버에 접속하려면 mysql -u DB사용자이름 -p 로 접속해야 함
    
    - 그런데 리눅스 사용자의 이름도 root로 같고, MySql 서버의 root 사용자의 비밀번호가 지정되지 않았기 때문에 mysql 만 입력해도 접속이 됬었던 것

    - 이것은 보안에 좋지 않으므로 비밀번호 설정 등 사용자 설정을 해줘야 한다

    - 관리자 root 비밀번호 설정

        - mysql 접속

        - ALTER USER 'root'@'localhost' IDENTIFIED WITH caching_sha2_password BY '1234';

            - root 사용자의 비밀번호를 1234 로 지정

    - 이제 db 접속 시 mysql -u root -p 명령으로 서버에 접속한 후 비밀번호를 추가로 입력해야 한다

    - 외부 접속 허용

        - MySql 은 기본적으로 외부에서 접속되지 않도록 설정되어 있다

        - /etc/mysql/mysql.conf.d/mysqld.cnf 파일을 열고 31줄의 bind-address 를 주석처리

        - systemctl restart mysql 명령으로 서비스(MySql) 재시작

### 외부에서 DB 서버 접속

* Mysql 서버에 접속하려면 아래의 클라이언트 프로그램이 필요하다

    - MySql Workbench

    - HeidiSQL

    - MySql Shell

    - TablePlus, DBeaver, Toad 등

* 접속

    - MySql 에 등록된 사용자만 외부에서 접속할 수 있다

    - MySql 은 사용자를 user@hostIP 형식으로 관리하기 때문에 등록되지 않은 IP 주소는 외부에서 접속이 불가능하다

    - 계정 생성

        - MySql 서버가 설치된 컴퓨터 접속 -> 터미널에서 mysql -u root -p 로 MySql 서버 접속

        - 사용자 생성 : CREATE USER winuser@'192.168.111.%' IDENTIFIED BY '4321'

            - 192.168.111.% 는 192.168.111.xxx 로 시작하는 IP 주소 모두 접속할 수 있게 허용

        - 권한 부여 : GRANT ALL ON *.* TO winuser@'192.168.111.%';

        - 확인 : SELECT user, host FROM user WHERE user NOT LIKE '';

    - Window 에 MySql Shell 설치

    - 시작 -> MySql Shell 실행

    - \connect --mysql --user winuser --host 192.168.111.100 입력 후 비밀번호 4321 입력

        - winuser 로 192.168.111.100 에 설치된 mysql 서버에 접속하겠다는 의미

        - MySql 은 사용자를 사용자@호스트IP 로 관리하므로 위 명령어는 winuser@WindowIP 로 자동 변환되어 192.168.111.100 로 요청됨

    - 접속 성공
