### 도커파일(Dockerfile)

* 도커 이미지를 만들기 위해 필요한 명령어들을 담고 있는 텍스트 파일

    - 도커를 실행하려면 도커 이미지가 있어야 한다

    - 도커 이미지는 실행 환경을 담은 파일

    - 도커파일은 이미지를 만들기 위한 설계도 역할을 한다

* 리눅스 또는 윈도우 중 하나의 운영체제를 기반으로 작성 가능

    - 컨테이너는 호스트 OS의 커널을 공유

    - 파일 시스템, 명령어, 셸 등 사용자 공간은 도커 이미지에서 제공
    
        - Dockerfile의 FROM 명령어로 정의

        - 커널과 이미지의 환경이 다르면 실행할 수 없다

        - 그러므로 사용자 공간이 리눅스로 정의되었다면, 해당 이미지는 리눅스 커널을 사용하는 리눅스 기반 이미지 이다

        - 반대로 사용자 공간이 리눅스로 정의되었다면, 윈도우 기반 이미지가 된다 

        - Dockerfile 의 FROM 예시

        ```
        # Debian 리눅스를 slim하게 만든 후, 거기에 OpenJDK 17을 설치한 이미지
        # 리눅스 기반 이미지이므로 리눅스 운영체제에서 실행해야 한다
        FROM openjdk:17-jdk-slim
        ```

    - 커널과 이미지의 환경이 같거나 다른 경우

        - 리눅스 커널 + 리눅스 기반 이미지(FROM에 리눅스 사용자 공간 정의) = OK

        - 윈도우 커널 + 왼도우 기반 이미지 -> OK

        - 윈도우 커널 + 리눅스 기반 이미지 -> X

        - 리눅스 커널 + 윈도우 기반 이미지 -> X

    - 업무에서는 대부분 리눅스 사용

        - 도커는 리눅스 커널 기술에 기반해 만들어짐

        - 경량성, 호환성, 실무 환경의 대부분이 리눅스 서버인 이유로 현업에서는 대부분 리눅스 기반 Dockerfile을 작성

* 도커파일 작성 예시

    ```txt
    # 1. 베이스 이미지 설정 (리눅스 기반 OpenJDK 이미지)
    FROM openjdk:17-jdk-slim

    # 2. 빌드할 도커 이미지 내의 디렉토리 설정
    WORKDIR /app

    # 3. 호스트에서 jar 파일 복사
    COPY build/libs/hellodocker.jar app.jar

    # 4. 자바 애플리케이션 실행
    CMD ["java", "-jar", "app.jar"]
    ```

    - FROM : 어떤 환경을 기반으로 만들지 선택

    - WORKDIR : 이미지 내부에서 명령어가 실행될 기본 경로 지정

        - 모든 명령어는 WORKDIR 로 지정한 경로 (위에서는 /app) 을 기준으로 실행된다

    - COPY : 호스트의 파일을 도커 이미지의 파일 시스템 경로로 복사

    - RUN : 이미지 빌드 시 실행할 명령

    - CMD : 컨테이너 실행 시 실행할 명령

### 도커 볼륨

* 컨테이너가 생성되고 삭제되더라도 데이터가 유지되도록 도와주는 데이터 저장소

    - 컨테이너 안에 파일을 저장하면, 컨테이너가 삭제될 때 같이 사라짐

    - 그래서 중요한 데이터를 영구적으로 저장하려면, 볼륨이 필요함

* 볼륨은 호스트 컴퓨터의 디렉토리에 저장됨

    - 리눅스 기준 : /var/lib/docker/volumes/

* 사용 예시

    ```bash
    # 볼륨 생성
    docker volume create my-volume

    # 실행
    docker run -it --name my-container -v my-volume:/app/data image1

    # 컨테이너 내에서 파일 생성
    echo "Hello Volume" > /data/hello.txt

    # 컨테이너 삭제
    docker rm my-container

    # 같은 볼륨으로 새 컨테이너 실행
    docker run -it -v my-volume:/app/data image1

    # 이전에 생성한 파일이 그대로 있음
    cat /data/hello.txt

    # 볼륨 삭제 명령어
    docker volume rm my-data
    ```

    - docker run -it --name my-container -v my-volume:/app/data image1

        - --name : 컨테이너의 이름 지정

        - -v my-volume:/app/data : 컨테이너의 /app/data 디렉토리를 my-volume 볼륨에 연결

        - image1 : 임의의 이미지

        - -it : 컨테이너 쉘에 들어가서 명령어를 직접 입력할 수 있게 해주는 옵션

    - echo "Hello Volume" > /data/hello.txt

        - Hello Volume 이라는 문자열을 data/hello.txt 라는 파일로 저장

        - echo : 문자열을 출력하는 명령어

        - "Hello Volume!" : 출력할 문자열 (큰따옴표 안 내용)

        - > : 리디렉션 연산자 — 출력 결과를 파일로 저장하겠다는 뜻

        - /data/hello/txt : 저장할 파일 경로
