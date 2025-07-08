### 도커 컨테이너 간 통신

* 도커 컨테이너는 서로 격리되어 있어서 설정 없이 만들면 서로 통신할 수 없다

    - A 컨테이너가 다른 컨테이너와 통신하려면 둘은 서로 같은 Docker 네트워크에 속해 있어야 한다

    - Nginx 컨테이너가 proxy_pass http://spring:8080 설정을 사용하려면 spring이라는 이름으로 Spring Boot 컨테이너를 찾을 수 있어야 함

* 도커 네트워크

    - 컴퓨터에 장착된 NIC(네트워크 카드) 를 조회하면 eth0, wlan0 등으로 조회됨

    - 소프트웨어가 자동 생성하거나 명령어로 직접 생성한 가상 네트워크를(NIC) 조회하면 veth0, virtio0 등으로 조회됨

        - 소프트웨어마다 자동 생성하는 가상 네트워크의 이름이 다르다

        - docker0, br-xxxx 는 도커 설치시 자동 생성되는 Bridge 인터페이스로 컨테이너와 호스트를 연결하는 역할을 한다

            - 이들은 가상 NIC 처럼 조회되지만, Bridge 인터페이스

            - Bridge : NIC와 NIC, 또는 여러 컨테이너를 연결해주는 다리

            - NIC : 네트워크에 접속하는 입구

    - 도커 설치시 자동 생성되는 docker0 가상 네트워크는 역할이 제한적이라서 컨테이너 간의 통신 필요 시 사용자 정의 브리지 네트워크를 새로 만들어 사용한다

        - docker0 사용 시 컨테이너 이름으로 통신, 컨테이너간 접근 설정, DNS 자동 연결 등이 안됨

    - 도커의 가상 NIC 종류

        - docker0 : 도커의 기본 bridge 네트워크

        - br-xxxx : docker network create 명령어로 직접 생성한 사용자 정의 bridge 네트워크

        - vethXXX : 컨테이너마다 하나씩 생성되는 가상 NIC (host <-> container 연결용)

    - 전체 구조 흐름

        ```
        [브라우저 요청]
            ↓
        [실제 NIC: eth0 (유선랜)]
            ↓
        [docker0 or br-xxxx (가상 브리지)]
            ↓
        [veth123 ↔ 컨테이너1]
        [veth456 ↔ 컨테이너2]
        ```

* 사용자 정의 브리지 네트워크 생성 방법

    ```
    docker network create app-network
    ```
    - app-network라는 이름의 커스텀 브리지 네트워크를 생성

    ```
    docker run -d --name spring --network app-network spring-app
    docker run -d --name nginx --network app-network -p 80:80 nginx-proxy
    ```

    - 통신할 여러 컨테이너에 동일한 커스텀 브리지 네트워크를 사용해 실행

        - docker run : 새로운 컨테이너 실행

        - -d : detach 모드 (백그라운드에서 실행)

        - -name spring : 컨테이너 이름을 spring으로 설정 (나중에 이름으로 참조 가능)

        - --network app-network : app-network라는 사용자 정의 도커 네트워크에 연결

        - spring-app : 사용할 도커 이미지 이름

        - -p 80:80 : 호스트의 80번 포트를 컨테이너의 80번 포트에 포트 포워딩

        - 이후 각각의 컨테이너 실행 시 --network app-network 옵션을 주면 같은 네트워크에 있으므로 통신 가능

    
### 도커로 Nginx, Spring Boot 연동해서 실행

* Spring Boot 프로젝트 이미지 빌드

* Nginx 설정 파일 작성

    - 루트 프로젝트 하위에 nginx 폴더 생성 -> nginx 폴더에 default.conf 파일 생성

    ```
    # Nginx 응답 설정
    server {
        listen 80;
        server_name localhost;

        location / {
            proxy_pass http://spring:8080/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
        }
    }

* Nginx 도커파일 작성

    - nginx 폴더에 Dockerfile 생성

    ```
    FROM nginx:latest

    // 이미지 실행 시 default.conf 파일이 /etc .. 경로로 복사됨
    COPY default.conf /etc/nginx/conf.d/default.conf
    ```

* Nginx 도커 이미지 빌드

    - docker build -t nginx ./nginx

* Docker 네트워크 생성

    - 두 컨테이너 연결용 네트워크

    ```
    docker network create app-network
    ```

* Nginx, Docker 컨테이너 실행

    - 스프링 부트 실행 : docker run -d --name spring --network app-network spring-app

    - Nginx 실행 : docker run -d --name nginx --network app-network -p 80:80 nginx-proxy

* 주소창에 localhost:80 입력해 접속

    - 웹 브라우저에서 요청 -> 도커 호스트가 요청을 받아서 내부 Nginx 컨테이너에 전달 -> Nginx 컨테이너는 스프링 부트 컨테이너에 전달
