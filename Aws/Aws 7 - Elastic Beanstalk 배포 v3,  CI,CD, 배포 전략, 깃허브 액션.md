### CI(Continuous Integration, 지속적인 통합)

* 코드를 중앙 저장소에 자주 통합(push)하고, 그때마다 자동으로 빌드 + 테스트하여 통합이 잘 되는지 확인하는 자동화 시스템

    - 개발자가 Git 에 코드를 push : CI 시작점

    - Push 후 자동 빌드 및 테스트 실행됨

* CI 동작

    - GitHub 에 Push -> 웹훅이 CI 도구/서버로 전달됨

    - CI 도구 작동(GitHub Actions 등)

    - CI 도구가 제공하는 환경이 뜸

        - GitHub Actions : GitHub의 클라우드 기반 가상 머신

        - Jenkins : 직접 설치한 서버, EC2, 로컬 등

        - GitLab CI : itLab이 제공하는 가상 머신 또는 직접 설정한 러너

    - GitHub 코드를 자동으로 Clone 후 ./gradlew build, ./gradlew test 같은 명령 실행

    - 결과 보고서 생성

* 웹훅

    - 어떤 이벤트가 발생했을 때, 특정 URL(서버)로 자동으로 HTTP 요청을 보내주는 시스템

    - 깃허브 저장소에 웹훅을 설정 후 https://a.com 주소 입력

    - 누군가 git push 하면 깃허브가 자동으로 post 요청을 a.com 에 보냄

    - 사용처
    
        - 깃허브 -> 젠킨스 또는 깃허브 액션으로 웹훅 보내 CI 시작할 수 있음

        - 결제 완료 시 서버에 알림 보내기

* 폴링

    - 일정 주기로 계속해서 서버에 request 요청을 보내 물어보는 것

    - 느리고 비효율적임

### CD(Continuous Delivery - 지속적인 서비스 제공 / Continuous Deployment - 지속적인 배포)

* CI가 완료된 코드를 자동으로 서버에 배포하는 시스템

* 지속적인 서비스 제공

    - 스테이징 서버까지 배포하고 운영 서버는 수동 배포

    - 스테이징 서버에서 QA 팀이 테스트 후 운영 서버에 배포함

* 지속적인 배포

    - 운영 서버까지 자동 배포

* CD 동작

    - CI 결과물을 가져오고 배포 대상 서버에 접속해 배포 실행

    - 성공/실패 알림

### 배포 전략

* 한 번에 모두 배포

    - 모든 서버를 중지하고 한꺼번에 배포

    - 배포 속도가 빠름

    - 배포 시 서버를 중단시켜야 해서 서비스도 멈춤

* 서버 추가 배치

    - 서버 하나를 새로 생성해 배포하고, 성공 시 로드 밸런서에 연결한 다음 연결되었던 기존 서버 1개 종료

    - 모든 서버가 새로운 배포 버전으로 대체될 때까지 반복

    - 무중단 배포 가능

    - 한대식 배포하고 연결하기 때문에 자원 소모가 적음

    - 배포 진행 중 에러 발생 시 롤백해야 함

        - 에러 발생한 서버를 종료하고 새로운 서버에 배포하면 안되나 라는 생각이 드는데, 안됨

        - 첫번째 배포는 성공하고 두번째 배포에 실패했다고 가정

        - 배포 오류 시 배포 버전 자체에 문제가 있었을 가능성이 크기 때문에 새로운 서버에 다시 배포를 시도하는 것은 실패를 반복할 수 있음

        - 오류난 부분만 수정해서 넣으면 버전 혼합상태가 되어 위험해짐

            - 서버마다 api나 로직이 다르게 동작할 가능성이 생김

            - 클라이언트는 로드밸런서를 통해 요청을 보내기 때문에, 어떤 요청은 v1 서버에, 어떤 요청은 v2 서버에 가게 됨

            - 같은 요청에 대해 결과가 다르게 나올 수 있음 → 버그로 이어짐

* 서버 변경 불가능 (블루/그린 배포)

    - 새로운 서버를 기존 서버 개수만큼 생성하고 배포 후 성공시 연결

    - 무중단 배포 가능

    - 배포 실패 시 서버를 종료하기만 하면 되기 때문에 롤백이 간편함

    - 기존 서버 개수 만큼의 배포를 한꺼번에 해야 하기 때문에 자원 소모가 큼


### 엘라스틱 빈스톡 배포 v3

* 보안 그룹 생성

    - 보안 그룹 이름, 설명 잘 알아보도록 설정

    - 인바운드 규칙 편집

        ```
        포트 범위 : 22, 소스 : Anywhere IPv4

        포트 범위 : 80, 소스 : Anywhere IPv4

        포트 범위 : 3306, 소스 : 내 IP

        포트 범위 : 3306, 소스 : 현재 보안 그룹 ID
        ```

        - EC2 서버에 적용 시 22, 80 접근 가능
        
        - RDS 서버에 적용 시 내 아이피로 3306, EC2 에서 RDS 의 3306 포트로 접근 가능

            - RDS 서버에는 SSH, 웹 서버가 없으므로 22, 80 포트 접근 불가 

        - 현재 보안 그룹 ID가 지정이 안된다면 일단 기본 보안그룹으로 설정 후 다시 들어와서 변경

* RDS 생성

    - 데이터베이스 생성 방식 선택 : 표준 생성

    - 엔진 옵션 : 사용할 RDB

    - DB 인스턴스 식별자 : DB 서버 이름

    - 마스터 사용자 이름 : DB 서버의 루트 사용자 ID

    - 마스터 암호 : 루트 사용자 비밀번호

    - VPC : 연결할 EC2 인스턴스와 동일한 VPC

    - 퍼블릭 엑세스 : 예

    - 보안 그룹 : 위에서 설정한 보안 그룹 (연결할 EC2 인스턴스와 동일한 보안 그룹)

    - 추가 구성 : 데이터베이스 포트 3306

* RDS 타임존 설정

    - RDS 는 AWS RDS는 MySQL의 내부 설정을 직접적으로 바꿀 수 없도록 제한되어 있다

        - SET Global time_zone = 'Asia/Seoul'; 권한 문제로 해당 명령 실행 불가능

        - 파라미터 그룹 생성 후 설정해줘야 함

    - RDS 파라미터 그룹 생성 방법
    
        - RDS 파라미터 그룹 -> 파라미터 그룹 생성
    
        - 엔진 유형 : RDS 과 동일한 엔진 유형 선택

        - 파라미터 그룹 패밀리: RDS 와 동일한 DB 엔진 버전 선택 -> 생성완료

        - 생성한 그룹 선택 -> 편집

            - zone 검색 -> time_zone 의 값에 Asia/Seoul 적용 (권장)

            - init_connect 검색 -> SET time_zone = 'Asia/Seoul' (첫번째 방법이 안될 경우)

                - 사용자가 접속할 때마다 세션 단위로 자동 적용

    - RDS -> 수정 -> 추가 구성 탭의 파라미터 그룹 -> 생성한 파라미터 그룹 적용 -> 즉시 적용 -> RDS 재부팅

* 엘라스틱빈스톡 생성

    - 서비스 엑세스 구성 : EC2 키페어 등록

    - 인스턴스 트래픽 및 크기 조정 구성

        - EC2 보안 그룹 : 만들어둔 보안 그룹 선택 (RDS 와 동일한 보안 그룹)

        - 용량 - 오토 스케일링 그룹

            - 환경 유형 : 밸런싱된 로드

            - 인스턴스 : 최소값 2, 최대값 4

                - EC2가 2대 라는 것은 같은 서버가 복제되어 있다는 것이다

                - RDS 는 하나이고, 두 대의 EC2 서버가 RDS를 공유해서 사용

                - 로드밸런서는 EC2 두대에 부하를 분산해 요청들 전달

                - 클라이언트는 여러 대가 존재하는 EC2 가 아닌 로드밸런서에 요청

                - 클라이언트 -> 로드밸런서 -> EC2 -> RDS  

            - 평소에는 2대의 서버로 동작하다가 트래픽 증가 시 자동으로 4대의 서버로 증설해서 가동

            - 트래픽 감소 시 자동으로 2대로 축소해서 가동 

        - 로드 밸런서 유형 : 애플리케이션 로드 밸런서

            - 리스너

                - 어떤 포트로 들어온 요청을 처리할지 정의

                - 80 설정 시 80 포트로 오는 요청만 처리함

            - 리스너 규칙

                - 요청 경로(URL) 등에 따라 어떤 타겟 그룹으로 포워딩할지 정의

                - 엘라스틱 빈스톡 사용 시 프로세스 설정으로 대체됨

            - 타겟 그룹

                - 로드 밸런서가 요청을 분배할 대상들을 모아둔 그룹

                    - EC2 서버, 사설 서버 IP 등

                - 리스너가 포워딩할 곳

                - 엘라스틱 빈스톡 사용 시 프로세스 설정으로 대체됨

            - 프로세스

                <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Aws/%EC%97%98%EB%9D%BC%EC%8A%A4%ED%8B%B1%EB%B9%88%EC%8A%A4%ED%86%A1%20%ED%94%84%EB%A1%9C%EC%84%B8%EC%8A%A4.JPG" alt="엘라스틱빈스톡 프로세스">

                - 로드 밸런서의 라우팅 규칙 설정
                
                    - 로드 밸런서가 수신한 HTTP 요청을 어디로 보낼지 결정하는 것

                - 위 설정은 / 경로로 상태체크 후 정상이면 EC2 의 80 포트로 요청을 전달

                - EC2 의 IP는 지정한 적 없지만, 엘라스틱 빈스톡이 자동으로 등록해줌

    - 업데이트, 모니터링 및 로깅 구성

        - 환경 속성에서 사용할 환경 변수 지정

* 깃허브 액션

    - CI

        - deploy.yml 파일 필요

            - 깃허브 저장소에 코드가 반영되었을 때 CI 서버로 이벤트가 일어나는데, 이때 .yml 파일에 정의된 내용으로 CI 가 진행됨

            - 클론 -> 코드변경 -> 푸시 -> 깃허브에 반영됨 -> 틜거 발생 -> .yml의 내용대로 CI 진행

            - 최상위폴더에서 .github/workflows 폴더 아래 *.yml 의 이름으로 있어야 한다

            - 파일의 이름은 상관없고, 확장자만 .yml 이면 됨

                ```yml
                name: aws-v5
                on:
                  push:
                    branches:
                    - main

                # https://github.com/actions/setup-java
                # actions/setup-java@v2는 사용자 정의 배포를 지원하고 Zulu OpenJDK, Eclipse Temurin 및 Adopt OpenJDK를 기본적으로 지원합니다. v1은 Zulu OpenJDK만 지원합니다.
                jobs:
                  build:
                    runs-on: ubuntu-latest
                    steps:
                        - name: Checkout
                          uses: actions/checkout@v3
                        - name: Set up JDK 11
                          uses: actions/setup-java@v3
                          with:
                            java-version: 11
                            distribution: zulu
                        - name: Pemission
                          run: chmod +x ./gradlew
                        - name: Build with Gradle
                          run: ./gradlew clean build

                        # UTC가 기준이기 때문에 한국시간으로 맞추려면 +9시간 해야 한다
                        - name: Get current time
                          uses: 1466587594/get-current-time@v2
                          id: current-time
                          with:
                            format: YYYY-MM-DDTHH-mm-ss
                            utcOffset: "+09:00"
                
                        - name: Show Current Time
                          run: echo "CurrentTime=${{steps.current-time.outputs.formattedTime}}"

                        # EB에 CD 하기 위해 추가 작성
                        - name: Generate deployment package
                          run: |
                            mkdir deploy
                            cp build/libs/*.jar deploy/application.jar
                            cp Procfile deploy/Procfile
                            cp -r .ebextensions deploy/.ebextensions
                            cd deploy && zip -r deploy.zip .
                        - name: Deploy to EB
                          uses: einaregilsson/beanstalk-deploy@v21
                          with:
                            aws_access_key: ${{ secrets.AWS_ACCESS_KEY }}
                            aws_secret_key: ${{ secrets.AWS_SECRET_KEY }}
                            application_name: aws-v5-beanstalk # 엘리스틱 빈스톡 애플리케이션 이름!
                            environment_name: Aws-v5-beanstalk-env # 엘리스틱 빈스톡 환경 이름!
                            version_label: aws-v5-${{steps.current-time.outputs.formattedTime}}
                            region: ap-northeast-2
                            deployment_package: deploy/deploy.zip

                        # aws_access_key, aws_secret_key 를 반드시 깃허브에 등록해줘야 CD 성공함 !!          
                ```

            - job

                - 하나의 작업 단위로, 이름은 임의 설정 가능

                - 위 파일에선 build 가 job 의 이름

                - job 의 최종 목적은 .jar 파일을 만들어내는 것

            - runs on

                - 설정한 환경 설치

                - ubuntu 18.04 입력 시 ci 서버에 해당 환경이 설치됨

            - steps

                - 한 단계씩 실행할 작업 목록

                - name

                    - 시퀀스의 이름으로 생략 가능

                - uses

                    - 미리 만들어진 GitHub Action을 불러다 쓰는 방식

                        - GitHub Action : 반복적인 작업을 쉽게 자동화할 수 있도록, GitHub 사용자(또는 GitHub 자체)가 미리 만들어둔 자동화 스크립트 묶음

                        - actions/checkout@v3 : 깃허브 저장소의 코드를 CI 서버에 업로드

                        - actions/setup-java : JDK 설치

                - run

                    - 직접 쉘 명령어를 실행

                - shell

                    - run에서 인식하는 명령의 shell 종류 설정

                - aws_access_key, aws_secret_key 등록

                    - Deploy to EB 단계에서 aws의 s3에 deploy.zip 파일을 전송하는데, 접근 권한이 있어야 전송이 가능하다

                    - AWS 에서 권한 있는 IAM 사용자를 생성하고 해당 사용자의 엑세스 키를 등록해주어야 한다

                    - IAM 사용자 생성

                        - AWS -> IAM 검색 -> 사용자 -> 사용자 생성

                        - 사용자 세부 정보 지정 : 사용자 이름 지정

                        - 권한 설정 : 직접 정책 연결 -> 권한 정책 -> AdministratorAccess-AWSElasticBeanstalk

                    - 엑세스 키 생성

                        - 생성한 사용자 선택 -> 보안 자격 증명 -> 엑세스 키 만들기

                        - 액세스 키 모범 사례 및 대안 -> 서드 파티 서비스

                        - 엑세스 키, 시크릿 키 저장하거나 .csv 파일 다운로드

                    - 깃허브에 등록

                        - 사용할 저장소 -> Setting -> 왼쪽의 Secrets and variables -> Actions

                        - New Repository secrets 선택 

                            - Name : AWS_ACCESS_KEY

                            - Secret : 발급한 IAM 액세스 키 -> 완료

                            - AWS_SECRET_KEY 도 같은 방법으로 등록

        - 깃허브 액션 활성화

            - 저장소 -> Actions -> I understand my workflows, go ahead and enable them

        - Personal Access Token(PAT) 생성, 자격 증명 삭제

            - OAuth App, 모바일 로그인 방식은 workflow 권한이 없어 깃허브 액션 관련 파일의 push 를 허용하지 않는다

            - PAT 토큰 생성 후 권한에 repo, workflow 을 체크해야 깃허브 액션 push 가 가능하다

            - 토큰 발급

                ```
                1. 깃허브 우측 상단 프로필 
                
                2. 좌측의 Developer settings 
                
                3. Personal access tokens

                4. Tokens(classic)

                5. Generate new token(classic)

                6. 이름 아무거나, 만료 기간 적절히 설정

                7. 권한 체크에 repo, workflow 포함

                8. 생성 완료 후 토큰 복사하고 보관해두기(다시 못봄)
                ```

            - 기존 자격 증명 삭제, 새 자격 증명 저장

                - 자격 증명
                
                    - 깃이 깃허브에 접근할 때 사용하는 로그인 정보

                    - 한번 로그인하면 윈도우의 자격 증명 관리자에 자동 저장됨

                    - 한번 로그인 해두면 git push 할 때마다 다시 아이디/비밀번호 를 물어보지 않는 이유가 이것이다

                    - 새 토큰을 생성해도 깃은 기존 자격 정보로 로그인하려고 하기 때문에 깃허브 액션 파일의 push 권한이 없는 상태가 지속된다

                    - 때문에 기존 자격 증명을 삭제하고, 토큰으로 인증해서 새 자격 증명을 얻어야 한다

                - 자격 증명 삭제

                    - 윈도우 자격 증명 관리자 -> 윈도우 자격 증명 -> GitHub 관련 항목 삭제

                - 토큰 로그인 (새 자격 증명 저장)

                    - git push 시 로그인 창이 나오고 토큰을 입력하면 깃이 새 자격 증명을 저장함

            - 이제 깃허브 액션 파일도 정상적으로 push 된다

        - push 한 저장소의 Actions 탭에 들어가면 진행 상황과 기록을 확인할 수 있다

    - CD

        - ~~.ebextensions, Procfile 필요~~

        - 위에서 작성한 .yml 로 CD 까지 가능
