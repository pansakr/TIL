### 도커 컴포즈 구성

* 하나의 스프링 부트 프로젝트 내부에 도커 컴포즈, nginx 설정 등을 작성하지 않고 책임별로 나누는 것이 좋다

    - 책임 분리 : 앱은 앱대로, 인프라는 인프라대로 역할이 명확

    - 테스트 용이 : 각 앱 디렉토리는 독립적으로 테스트/빌드 가능

    - 배포 준비 : 인프라만 따로 빼서 CI/CD 구성하기 쉬움

    - 협업 유리 : 앱 팀과 DevOps 팀의 분리 작업이 쉬움

    - 다중 서비스 확장 : apps/app2, apps/admin 등 추가해서 마이크로서비스 구성 가능

```
my-project/
├── infra/                         <- 인프라 디렉토리 (Docker Compose 중심)
│   ├── docker-compose.yml
│   ├── nginx/
│   │   └── default.conf
│   ├── db/
│   │   └── init.sql (선택)
│   └── .env                       ← 환경 변수 관리
├── apps/
│   └── spring-app/                <- Spring Boot 프로젝트
│       ├── Dockerfile
│       ├── build.gradle
│       ├── src/
│       └── ...

```

### 책임별로 디렉토리를 나눠 도커 컴포즈 + Nginx + Spring Boot 구축 (Deploy-test2 저장소 참고)

* 디렉토리 구조

```
Deploy/
├── infra/                        
│   ├── docker-compose.yml
│   ├── nginx/
│   │   └── default.conf
├── apps/
│   └── hellop/
│       ├── Dockerfile
│       ├── build.gradle
│       ├── src/
│       └── ...
```

* docker-compose.yml

```yml
version: "3.8"

services:

  spring:
    build:
      context: ../apps/hellop   # 이미지 빌드 시 기준 디렉토리
    expose:
      - "8080"
    networks:
      - app-net

  nginx:
    image: nginx:latest
    ports:
      - "80:80"
    volumes:
      - ./nginx/default.conf:/etc/nginx/conf.d/default.conf
    depends_on:
      - spring
    networks:
      - app-net

networks:
  app-net:
    driver: bridge
```

* 나머지 파일은 도커 컴포즈를 사용해서 Nginx + Spring Boot 구축 과 동일

* 컴포즈 실행 시 ../apps/hellop 경로에 있는 도커파일을 참고해 이미지를 빌드하고 실행한다

* 실행 순서

    - 스프링 부트 빌드 : cd apps/hellop -> ./gradlew clean build

    - 도커 컴포즈 실행 : cd infra -> docker-compose up -d
