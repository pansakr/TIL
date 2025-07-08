### Nginx

* 리버스 프록시 역할을 수행할 수 있는 웹 서버

    - 소규모 서비스 : Nginx가 웹 애플리케이션과 같은 서버에 설치된 단일 서버 구조

        ```
        [한 대의 서버]
        ┌──────────────────────────┐
        │ Nginx (80/443 포트)      │ ← 리버스 프록시
        │ Spring Boot (8080 포트) │ ← 웹 애플리케이션 서버
        └──────────────────────────┘
        ```

        - Nginx가 localhost:8080 같은 내부 주소로 트래픽을 전달

        - 빠르고 설정이 간단하다

        - 성능이 느리고 확장성 제한

    - 대규모 서비스 : Nginx가 별도 서버에 설치된 구조

        ```
        [사용자 브라우저]
        ↓ (도메인)
        [ Nginx 서버 1대 (80/443) ]
            ↓ (리버스 프록시)
        ┌───────────────┬──────────────┐
        ↓               ↓              ↓
        [웹 서버1]    [웹 서버2]     [웹 서버3]
        (Spring)    (Node.js)    (Django 등)
        ```

        - Nginx는 네트워크 앞단(게이트웨이 역할)에 위치

        - 도메인에 따라 각 웹 서버로 분산 처리

        - 여러 도메인을 하나의 Nginx가 관리 가능

        - 장점: 구조적 분리, 보안↑, 확장↑

        - 실무에서는 대부분 이 구조를 사용

* 설정 방법

    - nginx 설정 파일 수정 : nano /etc/nginx/sites-available/default

    ```
    // 응답 설정 1
    server {
        listen 80;
        server_name yourdomain.com;  # IP 주소도 가능

        location / {
            proxy_pass http://localhost:8080/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
        }
    }

    // 응답 설정 2
    server {
        listen 80;
        server_name 192.168.000.000;
    }

    // 응답 설정 3
    server {
        listen 70;
        ...
    }
    ```

    - listen [80]
    
        - Nginx가 80 포트로 오는 요청을 받음

        - HTTP 요청이 80 포트를 사용하므로 일반 HTTP 요청을 수신

    - server_name [도메인 이름 또는 IP]

        - 명시한 도메인 이름/IP 가 일치하는 요청만 처리

        - 요청 Host 헤더에 명시된 도메인 또는 IP가 server_name과 일치하면 해당 블록이 처리 (location 블록으로 넘김)

        - Nginx는 80포트에 들어오는 요청들 중, Host 헤더가 192.168.111.100인 걸 이 server 블록에 매칭

    - location /

        - URL 경로가 /로 시작하는 모든 요청 처리

    - proxy_pass [http://localhost:8080/]

        - Nginx는 요청을 받아서 proxy_pass 에 지정된 주소로(웹 서버) 전달

        - 브라우저 -> Nginx -> 명시된 주소(웹 서버 : Spring Boot 등)

        - 단일 서버의 경우 http://localhost:8080/ 명시
        
            - 브라우저 -> Nginx -> 로컬에서 실행중인 웹 서버(Spring Boot 등) 로 전달됨

    - proxy_set_header [Host] [$host];

        - 클라이언트로 부터 받은 요청을 로컬에서 실행중인 웹 서버에 전달할때 원래 요청 정보를 포함시키기 위한 설정

        - Host : 사용자가 입력한 호스트를 그대로 전달

    - proxy_set_header [X-Real-IP] [$remote_addr];

        - 클라이언트로 부터 받은 요청을 로컬에서 실행중인 웹 서버에 전달할때 원래 요청 정보를 포함시키기 위한 설정

        - X-Real-IP: 사용자의 실제 IP 주소를 Spring 서버에 전달 (Spring 쪽에서 클라이언트 IP 파악 가능)

### Nginx 리버스 프록시 서버 구성 (단일 서버 구조)

* Spring Boot 프로젝트 빌드 후 실행

* Nginx 설치

    - sudo apt update

    - sudo apt install nginx

    - 설치 후 http://서버IP 접속 시 Nginx 환영 페이지가 나오면 정상

* Nginx 설정 파일 수정

    - 설정 파일 열기 : nano /etc/nginx/sites-available/default

    ```
    server {
        listen 80;
        server_name yourdomain.com;  # IP 주소도 가능

        location / {
            proxy_pass http://localhost:8080/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
        }
    }
    ```
* 설정 테스트 & Nginx 재시작

    - 설정 파일 문법 확인 : nginx -t 

    - nginx 서비스 재시작 : systemctl restart nginx

* 방화벽 확인

    - 80 포트 개방 : ufw allow 80

    - 포트 상태 확인 : ufw status

* 실행 : 주소창에 서버 IP 주소 입력
