### 도커로 자바 웹 프로젝트 실행하기

* Spring Initializr 로 spring web 의존성을 추가해 프로젝트 생성

* 간단한 Hello Wolrd 컨트롤러 생성

* Gradle로 빌드

    - 인텔리제이의 터미널을 사용해 ./gradlew build

    - build/libs/ 경로에 helloword-0.0.1-SNAPSHOT.jar 생성됨

* Dockerfile 작성

    - 프로젝트 루트에 Dockerfile 이라는 이름으로 생성(대소문자 구분, 확장자 없음)

    - 프로젝트 루트는 프로젝트 폴더 바로 하위를 뜻함 (build.gradle, settings.gradle 이 있는 위치와 동일한 곳)

    ```
    # 1. Java가 포함된 베이스 이미지
    FROM openjdk:17-jdk-slim

    # 디버깅 도구 설치 (curl, bash, wget 등)
    RUN apt-get update && apt-get install -y curl bash wget

    # 2. 도커 컨테이너의 작업 디렉토리 설정
    WORKDIR /app

    # 3. 호스트 컴퓨터의 빌드된 jar 파일을 컨테이너로 복사
    # 경로와 jar 파일명이 정확히 일치해야 함
    COPY build/libs/helloworld-0.0.1-SNAPSHOT.jar app.jar

    # 4. 앱 실행
    ENTRYPOINT ["java", "-jar", "app.jar"]
    ```

* 도커 이미지 빌드, 실행

    - 도커 이미지 빌드 전 도커가 Linux 컨테이너 모드로 실행되어 있어야 한다

        - 리눅스 컨테이너 모드 : WSL2 또는 Hyper-V 위에서 실행되는 가상화된 리눅스 환경

        - 윈도우 운영체제에서 리눅스 기반 이미지를 실행할 때 위 방법을 사용

        - 리눅스 운영체제에서 리눅스 기반 이미지를 사용할 때는 리눅스 자체가 리눅스 기반 이미지를 실행할 수 있는 자연 환경이기 때문에 가상 환경 필요 없음

    - 실행 환경

        - 호스트 : 윈도우

        - 실행할 이미지 : 리눅스 기반 이미지

    - 윈도우 호스트에서 리눅스 기반 이미지를 실행
    
    - 리눅스 이미지는 리눅스 커널을 사용한다

    - 만약 도커가 리눅스 컨테이너 모드로 되어있지 않다면 리눅스 이미지를 실행하지 못한다

    - 이미지 빌드

    ```
    docker build -t helloworld .
    ```

    - -t : 태그 이름 지정

    - -t helloworld : 태그 이름을 helloworld 로 지정

    - . : 현재 디렉토리

    - 도커 빌드 시 어디서부터 Dockerfile을 읽을지 경로를 지정해야 한다

    - 컨테이너 실행

        ```
        docker run -p 8080:8080 helloworld
        ```

        - 왼쪽 8080 : 호스트의 포트

        - 오른쪽 8080 : 컨테이너 안에서 열리는 포트

        - localhost:8080 요청 시 호스트의 8080 에서 요청을 받고, 이것을 컨테이너의 8080 포트로 전달한다
