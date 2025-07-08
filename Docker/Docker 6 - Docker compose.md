### 도커 컴포즈(Docker Compose)

* 여러 도커 컨테이너를 하나의 YAML 파일(docker-compose.yml)에 정의하고, 한 번의 명령어로 모든 컨테이너를 관리(실행, 중지)할 수 있게 해주는 도구

    - YAML/YML(YAML Ain't Markup Language)

        - 데이터를 계층적으로 표현할 수 있는 텍스트 기반의 포맷

        - 사람이 읽기 쉬움

        - 도커, 쿠버네티스, 깃허브 액션, 스프링 부트 설정, CI/CD 툴 등에서 사용

        - YAML, YML 은 같은 확장자를 의미하므로 어느것을 써도 무방함 

* 기존 방식

    - 설정이 여러 도커파일들에 흩어져 있어 협업과 변경/배포가 어려움

    - 도커 커스텀 네트워크를 직접 생성하고 실행해야 함

    - 명령어가 길고 번거로움

* 도커 컴포즈 사용

    - 이미지 빌드/실행, 네트워크 생성 등 반복 작업 자동화

    - 모두 docker-compose.yml에 정리되므로 협업과 변경/배포가 간단함

    - 자동으로 커스텀 네트워크 생성, 실행됨

* 도커 컴포즈 문서 기본 구조

    ```yml
    version: "3.8"          # Compose 파일 버전

    services:               # 실행할 컨테이너 서비스들을 정의

        서비스이름:
            image 또는 build
            ports / volumes / networks 등

        서비스이름2:
            ...

        서비스이름3:
            ...

    networks:               # 사용할 네트워크 정의 (선택 사항)
        네트워크이름:
            driver: bridge
    ```

    - Docker Compose는 내부적으로 모든 서비스 이름을 DNS 이름처럼 등록

        - 컨테이너끼리 통신할 때 services 하위에 등록된 서비스 이름으로 다른 컨테이너를 찾는다

* 명령어

    - docker-compose up -d : 백그라운드로 컨테이너 실행 (자동 빌드 포함)

    - docker-compose down : 모든 컨테이너, 네트워크, 볼륨 정리

    - docker-compose logs -f : 실시간 로그 확인

    - docker-compose ps : 실행중인 서비스 목록 확인


### 도커 컴포즈를 사용해서 Nginx + Spring Boot 구축 (Deploy-test1 저장소 참고)

* Nginx 설정 파일

    ```
    server {
        listen 80;
        server_name localhost;

        location / {
            proxy_pass http://spring:8080/; # docker compose 사용 시 컨테이너 이름 대신 서비스 이름 지정
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
        }
    }
    ```
    - 도커 컴포즈 사용 시 proxy_pass 옵션의 http:// 과 :8080 사이에 docker-compose.yml 에 명시한 서비스 이름을 사용해야 한다

        - Docker Compose는 내부적으로 모든 서비스 이름을 DNS 이름처럼 등록하기 때문

        - Docker Compose에 A 라고 등록했는데, proxy_pass는 B 라고 등록한다면 이름 불일치로 찾지 못한다

    - 도커 컴포즈 미사용 시 proxy_pass에서 사용할 수 있는 DNS 이름이 자동 생성되지 않는다

        - 컴포즈 미사용 시 proxy_pass 에 컨테이너 이름을 직접 지정해야 함

* 루트 디렉토리 아래에 docker-compose.yml 생성

    ```yml
    // docker-compose.yml
    version: "3.8"  # Compose 파일 형식 버전 (Docker Engine 호환성에 따라 사용)

    services:       # 컨테이너로 실행할 개별 애플리케이션 정의 (여기선 spring, nginx)

        spring:       # spring 서비스 정의 (Spring Boot 애플리케이션)

            build:
                context: .             # 이미지를 빌드할 때 기준이 되는 디렉토리(. 은 현재 디렉토리)
                dockerfile: Dockerfile # 현재 디렉토리에서 Dockerfile 과 이름이 동일한 도커파일 찾기

            #container_name: spring-app 
            expose:                  # 외부에서 직접 접근하진 않지만 nginx가 내부 통신용으로 접근할 포트
                - "8080"
            networks:
                - app-net              # 커스텀 네트워크에 연결

        nginx:        # nginx 서비스 정의

            image: nginx:latest      # Docker Hub에서 공식 nginx 이미지 사용
            #container_name: nginx-proxy

            ports:                   # 외부에서 접근할 수 있는 포트 매핑
                - "80:80"              # 호스트 80 → 컨테이너 80

            volumes:                 # Nginx 설정 파일 마운트
                - ./nginx/default.conf:/etc/nginx/conf.d/default.conf

            depends_on:              # nginx가 srping에 의존성을 가지도록 설정 (spring 컨테이너가 먼저 실행되도록 설정)
                - spring            

            networks:
                - app-net              # 같은 네트워크에 연결

    networks:
        app-net:
            driver: bridge           # 브리지 네트워크 (컨테이너 간 통신용)
    ```

    - version : 없어도 실행되도록 변경되었으므로 생략하는것이 좋음

    - services

        - 하위에 서비스 이름 정의

        - 도커 컨테이너의 이름을 찾을 때 이곳에 정의된 이름으로 찾는다

        - 예시) nginx 컨테이너는 'spring' 으로 spring boot 컨테이너를 찾는다 

    - context, dockerfile

        - context
        
            - 이미지를 빌드할 때 기준이 되는 디렉토리

            - context 에 지정된 경로로 Dockerfile 을 찾고 COPY, ADD 등을 처리함

        - dockerfile

            - 도커파일의 이름을 지정함

            - 도커파일이 여러개일 때 사용

            - context 로 지정한 경로에 도커파일이 여러개 있을때, 이 옵션으로 어떤 도커파일을 사용할 지 지정

            - 도커파일이 한개라면 context 만 지정하고 사용 안해도 됨

    - container_name

        - 해당 서비스의 컨테이너 이름을 명시적으로 지정

        - 사용하지 않는것이 좋음

            - 팀원마다 환경이 다르면 중복 이름이 발생할 수도 있고, 이런 경우 도커 컴포즈 실행이 실패함

        - 해당 옵션을 생략하면 도커 컴포즈는 컨테이너 이름을 [프로젝트명]_[서비스명]_[숫자] 규칙으로 자동 생성함

            - ex)hello_spring_1

            - 자동 생성 시 프로젝트별로 충돌 방지가 되고, 동일한 환경에서 여러 컴포즈를 실행 가능

            - 권장 방법

    - volumes : 컨테이너가 생성되고 삭제되더라도 데이터가 유지되도록 도와주는 데이터 저장소

    - Spring Boot 는 DockerFile 사용

        - 스프링 부트는 내가 작성한 소스코드가 들어가는 부분이고, 이것을 jar 로 빌드 후 커스텀 이미지 빌드에 사용한다

        - 그렇기 때문에 공식 이미지를 가져와 사용하지 못하고, 커스텀 이미지 빌드를 위해 DockerFile 이 반드시 필요하다

    - Nginx 는 DockerFile 미사용

        - 공식 이미지로 충분

        - 대부분은 설정 파일만 바꿔주고 굳이 Nginx 용 DockefFile 을 만들지 않아도 된다

        - 그러나 커스텀 HTML, 로그 포맷, 모듈 설치 등 추가적인 작업이 있다면 Dockerfile을 써야 함

    - depends_on 옵션으로 spring 컨테이너가 먼저 실행되도록 설정한 이유

        - Nginx가 Spring에 요청을 전달하려면 Spring이 먼저 실행돼 있어야 하기 때문

        - 그러나 depends_on 옵션은 실행 순서만 보장하는 것으로, 실제 서비스 준비까지는 보장하지 않는다

            - Spring Boot가 진짜로 준비 완료(= HTTP 요청 받을 준비가 완료)되기 전에 Nginx가 시작할 수도 있음

        - 완벽히 하기 위해 healthcheck 를 함께 사용하는것이 좋음

* 빌드 : ./gradlew clean build

* 컴포즈 실행 : docker-compose up -d
