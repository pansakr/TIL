### Elastic Beanstalk 배포

* Elastic Beanstalk

    - AWS 가 제공하는 배포 자동화 서비스(Paas)

    - 웹 서비스 운영에 필요한 소프트웨어들이 설치/설정 되어있다

        - JDK 등 필요한 소프트웨어들이 설치되어 있고, 설정도 되어 있음

    - 오토 스케일링, 로드 밸런서, 모니터링 등 여러 옵션을 추가할 수 있다

* Elastic Beanstalk 시작

    - AWS에서 Elastic Beanstalk 검색 -> 애플리케이션 생성

    - 환경 티어 : 웹 서버 환경

    - 플랫폼 : Java, 플랫폼 브랜치 : Corretto 17 running on 64bit Amazon Linux 2

    - 애플리케이션 코드 : 샘플 애플리케이션

    - 사전 설정

        - 서버 옵션 선택 (로드 밸런서 사용 여부 및 스팟 요금 등)

            - 단일 인스턴스(프리 티어 사용 가능) : EC2 1개만 사용

            - 단일 인스턴스 스팟(스팟) : EC2 1개 + 스팟 요금

            - 고가용성 : EC2 여러 개 + Auto Scaling (로드 밸런서 있음 - ALB 로 생성됨)

            - 고가용성(스팟 및 온디맨드 혼합) : EC2 여러 개 + 스팟 요금 (로드 밸런서 있음)

            - 스팟
            
                - AWS의 남는 EC2 용량을 싸게 빌려주는 임시 서버

                - 대신 AWS가 언제든지 필요에 따라 회수할 수 있다

    - 서비스 엑세스 구성 

        - 서비스 역할

            - Benstalk 서비스 에게 IAM 역할 지정

            - Beanstalk가 EC2를 만들고, 모니터링하고, 로그를 CloudWatch에 보내는 등 aws api를 호출하려면 IAM 으로 해당 권한들을 준 역할을 만들어서 부여해 줘야 함

            - 본인이 사용할 서비스에 맞게 권한을 부여한 역할을 생성하면 된다

            - 기존에 생성해둔 역할 문서를 선택하거나, 없다면 역할을 새로 생성해야 한다

        - EC2 인스턴스 프로파일

            - EC2 인스턴스에게 IAM 역할 지정

            - EC2 인스턴스 내부에서 CloudWatch 등 aws api 를 호출하려면 IAM 으로 해당 권한들을 준 역할을 만들어 부여해야 함

            - 역할에 대해 명시되어 있는 파일이 EC2 인스턴스 프로파일이다

    - 나머지 설정은 선택 사항으로 건너뛰기 -> 생성

* 기본 설명

    - 왼쪽의 환경 탭 -> 환경으로 이동 -> Elastic Beanstalk 으로 실행한 서버로 접속됨

    - Elastic Beanstalk 생성 시 Elastic Beanstalk 이 EC2 인스턴스를 자동 생성함

        - EC2 인스턴스 에서 세부 사항 확인 가능

        - 퍼블릭 IP로 접속하려면 보안 그룹 -> 인바운드 규칙 편집 -> 5000 포트를 열어줘야 함

    - 구조

        ```
        [클라이언트]
            ↓
        [AWS ALB (로드 밸런서)]
            ↓
        [EC2 - Nginx (리버스 프록시/내부 로드밸서)]
            ↓
        [EC2 - Spring Boot 앱]
        ```

        - AWS ALB
        
            - 전 세계에서 오는 클라이언트의 요청을 여러 EC2 로 나눔

            - 클라이언트는 로드 밸런서의 IP 로 요청 -> 로드 밸런서가 EC2 서버로 분산해서 전달

        - Nginx
        
            - 정적 파일 제공, 내부 앱으로 전달(Spring Boot)

            - 클라이언트가 Nginx 의 IP로 직접 접속 시도 시 거부당함

            - Nginx 가 설치된 EC2 서버는 외부의 모든 요청을 거부하고, 내부 로드밸런서의 80포트 요청만 허용하고 있기 때문

        - Spring Boot 앱 : 실제 벡엔드 서비스 로직 수행 

* 로드 밸런서

    - 서버에 가해지는 부하를 분산해 주는 기술

    - ELB(Elastic Load Balancer)

        - AWS 에서 제공하는 로드 밸런서 서비스

        - ALB(Application Load Balancer)

            - ELB 의 종류 중 하나로 HTTP, HTTPS 전용
            
            - Spring Boot 웹앱, REST API 등에 사용됨

        - NLB(Network Load Balancer)

            - ELB 의 종류 중 하나로 TCP, UDP 레벨에서 빠른 처리 지원, 초고속/고성능 서비스용
            
            - 실시간 게임 서버, 대규모 TCP 서버 등에 사용됨

* 배포

    - Elastic Beanstalk -> 환경 -> 생성해둔 환경 접속 -> 업로드 및 배포

    - 파일 선택으로 빌드해둔 .jar 파일 선택

    - 버전 레이블 : 해당 애플리케이션 버전의 이름 작성(임의 작성)
