### DHCP(Dynamic Host Comfiguration Protocol) 서버

* DHCP : IP, 서브넷 마스크, 게이트웨이 주소, DNS 서버 주소를 할당하는 것

- DHCP 서버 : 자신의 네트워크 안에 있는 클라이언트 컴퓨터의 위 요청을 처리해주는 서버

    - 동적 IP 주소 : 부팅할 때마다 DHCP 서버로부터 얻어 오는 IP 주소

        - 부팅할 때마다 IP 주소가 변경될 수 있다

    - 고정 IP 주소 : 컴퓨터 네트워크 정보에 직접 입력하는 IP 주소 

* DHCP 서버 작동 순서

    - 클라이언트가 컴퓨터 부팅

    - 자동으로 DHCP 서버에 IP 주소 요청

        - 클라이언트 컴퓨터는 전원이 켜지면 자신의 네트워크와 연결된 모든 컴퓨터에 IP 주소 요청을 브로드캐스트 한다

        - 그중 다른 컴퓨터는 해당 요청을 무시하고 DHCP 서버는 응답한다

    - DHCP 서버는 자신이 관리하는 IP 목록 중 '할당 전' IP 를 클라이언트에게 할당해주고 해당 IP를 '할당되었음' 으로 바꾼다

    - 클라이언트는 할당받은 IP 주소로 인터넷 이용

    - 클라이언트가 컴퓨터 종료 시 DHCP 서버에게 IP 주소 반납

    - DHCP 서버는 반환된 IP 주소를 '할당되었음' 에서 '할당 전' 으로 변경

* VMware 에서 제공하는 DHCP 서버

    - 사용자의 PC 안에서만 동작하는 가상의 네트워크(VMnet1, VMnet8 등)를 만들고, 그 내부에서만 DHCP 서버를 실행한다

    - 사용자가 VMware 설치 시 내부적으로 가상의 네트워크(VMnet) 생성

    - VMware 는 사용자의 컴퓨터의 자원을 사용해 DHCP 서버를 실행

        - VMware 의 DHCP 서버는 물리적인 서버가 아니라, 사용자 컴퓨터의 자원을 활용한 가상 DHCP 서버이다

        - VMware 의 DHCP 가 제공하는 정보
        
            - Edit -> Virtual Network Editor -> Mnet8 -> DHCP Setting 또는 NAT Setting

            - DHCP : 서브넷 IP, 할당할 IP 주소 범위, 브로드캐스트 주소 를 알 수 있다

            - NAT : 게이트웨이 IP, DNS 를 알 수 있다

    - 가상머신이 부팅되면 VMware 가 실행한 DHCP 서버에 IP 요청을 보냄

        - 실제 인터넷이나 공유기로 가는것이 아니라, VMware 의 내부 네트워크에서만 요청이 처리됨

    - VMware 의 DHCP 서버가 사설 IP를 가상머신에 부여


### DHCP 서버 구축

* 클라이언트

    - 별도 프로그램 설치 필요 없음

    - [우분투 버지] 바탕화면 우측 위 네트워크 아이콘 -> NetWork Setting

    - 왼쪽의 네트워크 -> 유선 옆의 설정 아이콘

    - IPv4 -> 자동(DHCP)

    - 클라이언트가 DHCP 요청을 보낼 때 브로드캐스트 방식을 사용하기 때문에 DHCP 서버의 IP 를 지정할 필요 없음

        - 브로드캐스트 : 같은 네트워크 범위 안의 모든 호스트에게 요청 발송

* 서버

    - apt update -> apt -y install isc-dhcp-server 입력으로 dhcp 패키지 설치

    - /etc/dhcp/dhcpd.comf 파일에 제공할 네트워크 정보 입력

        ```
        // 맨 아래에 작성
        // [] 부분에 해당하는 내용 입력

        subnet [네트워크주소] netmaskr [넷마스크값] {

            // 클라이언트에 알릴 게이트웨이 주소
            option routers [게이트웨이주소];

            // 클라이언트에 알릴 네트워크의 범위
            option subnet-mask [넷마스크정보];

            // 클라이언트에 할당할 IP 주소의 범위
            range dynamic-bootp [시작IP] [끝IP];

            // 클라이언트에 알릴 네임 서버 주소
            option domain-name-servers [네임서버주소];

            // 클라이언트에 IP 주소를 임대할 기본적인 초 단위 시간
            default-lease-time [임대시간(초)];

            // 클라이언트가 하나의 IP 주소를 할당받은 후 보유할 수 있는 최대 초 단위 시간
            // 특정 컴퓨터가 IP 주소를 고정적으로 보유하는 것을 방지
            max-lease-time [최대임대시간(초)];
        }
        ```
