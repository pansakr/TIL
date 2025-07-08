### 도커 컴포즈를 사용해서 Nginx + Spring Boot 구축 + 별도 서버의 Postgresql 연동 (Deploy-test2 v.1.1.0 참고)

* 우분투에 PostgresSQL 설치 : sudo apt install postrgresql

* 우분투 방화벽 설정 : sudo ufw allow 5432

* PostgreSLQ 에서 외부 접속 허용

    - postgresql.conf

        - sudo nano /etc/postgresql/16/main/postgresql.conf 입력

        - #listen_addresses = 'localhost' -> listen_addresses = '*' 수정

    - pg_hba.conf (접속 권한 설정)

        - sudo nano /etc/postgresql/16/main/pg_hba.conf 입력

        - 맨 아래에 host all all 0.0.0.0/0 md5 추가

        - 모든 IP의 모든 사용자에게 접속 허용하는 옵션

        - md는 비밀번호 인증

* PostgreSQL 재시작 : sudo systemctl restart postgresql

* 우분투의 IP 주소 확인 : ifconfig

* 윈도우에서 연결 테스트 : ping 192.168.111.129

* postgresql 접속 및 사용자, DB, 테이블 생성

    ```
    # 첫 접속 (postgresql 설치 시 기본적으로 postgres 라는 슈퍼유저 계정이 생성됨)
    sudo -u postgres psql

    # 사용자 생성
    CREATE USER myuser WITH PASSWORD 'mypassword';

    # 데이터베이스 생성 + 권한 부여
    CREATE DATABASE deploytest2;
    GRANT ALL PRIVILEGES ON DATABASE deploytest2 TO myuser;
    GRANT CREATE ON SCHEMA public TO jay;
    ALTER SCHEMA public OWNER TO myuser;

    또는 ALTER USER myuser WITH SUPERUSER; # 둘중 하나 실행

    # 종료
    \q

    # 생성한 유저로 재접속
    psql -U myuser -d deploytest2 -h localhost

    # 테이블 생성
    CREATE TABLE member (
        id SERIAL PRIMARY KEY,
        name VARCHAR(100) NOT NULL
    );

    # 테스트 데이터 생성
    INSERT INTO member (name) VALUES ('홍길동');
    INSERT INTO member (name) VALUES ('김철수');

    # 확인
    SELECT * FROM member;

    --------------
    # DB 접속 전환
    \c deploytest2
    ```

    - 일반 사용자로 접속 시 명령어의 의미

        - psql -U myuser -d deploytest2 -h localhost

        - -U : 접속할 postgresql 사용자명

        - -d : 접속할 데이터베이스 이름

        - -h : tcp/ip 를 사용해 네트워크로 postgresql 접속

    - -h 사용/생략 시 차이점

        - -h 옵션 생략

            - 리눅스에서는 파일 시스템에 있는 소켓 파일을 통해 postgresql 에 접속할 수 있다

            - 네트워크를 사용하지 않고, 운영체제의 파일 시스템 경로를 통해 postgresql 서버에 연결

            - 일반 사용하는 소켓 파일에 접근할 권한이 없으면 에러난다 
            
            - 위에서 첫 접속시 입력한 sudo -u postgres psql 방법도 소켓을 이용한 방법
            
        - -h 옵션 사용

            - IP/포트 를 이용한 일반적인 네트워크 접속 방식

            - localhost, 서버 IP 등으로 postgresql 서버에 접속

        - 윈도우는 유닉스 도메인 소켓이라는 개념이 없으므로 무조건 tcp/ip 방식으로 접속됨

        - 관리자 계정 사용 접속 시 주의사항

            - postgresql 설치 시 postgres 라는 슈퍼 유저가 기본 생성됨

            - 해당 슈퍼 유저를 소켓 방식으로 접속 시 비밀번호 필요 없음

            - 하지만 tcp/ip 방식으로 접속 시 비밀번호가 무조건 필요함

            - 문제는 postgres 슈퍼유저는 비밀번호가 없는 상태로 기본 지급되기 때문에 tcp/ip 방법으론 무조건 접속 실패함

            - 그래서 소켓 방식으로 접속 후 비밀번호를 재설정한 다음에 tcp/ip 방식으로 접속할 수 있음 

### Spring Boot application.yml 수정

```yml
spring:
  datasource:
    url: jdbc:postgresql://IP주소:5432/DB이름
    username: 유저이름
    password: 비밀번호
  jpa:
    hibernate:
      ddl-auto: none
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

* 실행

    - 빌드 : ./gradlew clean build

    - 컴포즈 실행 : docker-compose up -d

* 파일 수정 후 재실행 시 명령어

    - ./gradlew clean build

    - docker-compose down -v

    - docker-compose up -d --build

    - ./gradlew clean build, docker-compose up -d 명령어로 실행하면 수정한 파일들이 도커 이미지에 반영이 안됨

    - ./gradlew clean build + docker-compose up -d

        - dockerfile의 내용이나 jar 가 변경되었어도 이전에 캐시된 이미지가 사용될 수 있음

        - 즉 yml, .java 등 수정 후 재빌드한 jar 파일로 도커 이미지가 새로 생성되지 않고, 이전에 만든 도커 이미지가 실행됨

    - docker-compose down -v + docker-compose up --build

        - down -v : 기존 컨테이너, 네트워크, 볼륨 완전 삭제

        - up --build : dockerfile을 기준으로 이미지 다시 빌드

