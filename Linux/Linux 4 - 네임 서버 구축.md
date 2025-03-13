### 네임 서버(Domain Name System - DNS 서버)

* 도메인 네임 시스템(DNS)

    - IP 주소에 이름을(도메인) 부여하는 것

        - 도메인 : 인터넷에 연결된 컴퓨터나 서버를 식별하기 위해 사용되는 문자 기반의 주소

    - 네트워크에 연결된 컴퓨터를 구분하는 방법은 IP 주소이다

    - 따라서 웹 브라우저로 웹 서버에 접속하려면 111.222.333.444 같은 IP 주소를 알아야 한다

    - 이러한 IP 주소는 외우기 어려우므로 각각의 IP 주소에 이름을 부여한다

* 도메인 이름 체계

    <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Linux/%EB%8F%84%EB%A9%94%EC%9D%B8%20%EC%9D%B4%EB%A6%84%20%EC%B2%B4%EA%B3%84.JPG" alt="도메인 이름 체계">

    - 루트 도메인(.)

        - 도메인 네임 시스템의 최상위 레벨이며 모든 도메인의 시작점

        - URL 입력 시 생략되지만, 모든 도메인 주소는 .으로 끝난다

        - www.naver.com. <- 마지막 .이 생략된 상태로 주로 사용됨

        - 루트 도메인을 관리하는 네임 서버는 최상위 도메인을 관리하는 네임 서버인 com, net 등과 국가 도메인인 kr, fr 등을 관리한다

    - 최상위 도메인

        - 루트 도메인 바로 아래 단계

        - .com, .net, .org 등이 포함됨

        - 최상위 도메인을 관리하는 네임 서버를 최상위 도메인 서버 또는 TLD 네임 서버 라고 한다 

        - 최상위 도메인을 관리하는 네임 서버는 2차 도메인을 관리하는 네임 서버를 관리한다

    - 2차 도메인

        - 특정 기업이나 기관이 등록한 도메인

        - naver, goole 등

        - 2차 도메인을 관리하는 네임 서버를 2차 네임 서버 또는 권한 네임 서버 라고 한다

        - 서브 도메인을 관리한다

    - 서브 도메인

        - 2차 도메인의 하위 도메인
        
        - www, mail 등 


* 네임 서버의 역할

    - URL을 IP 주소로 변환시켜주는 서버

    - 인터넷 도입 초기에는 IP 주소가 많지 않았으므로 URL과 대응되는 IP가 각자의 컴퓨터에 저장되어 있었다

    - 이 파일이 hosts 파일이고, 111.222.333.444 www.xxx.com 형식으로 저장되어 있었다

    - 웹 브라우저에서 URL 주소를 입력했을 때 각자의 컴퓨터에 있는 hosts 파일을 검색해 해당 URL에 대응하는 IP 주소를 얻을 수 있었다

    - 하지만 IP 주소가 기하급수적으로 늘어남에 따라 이를 전문적으로 처리해주는 서버 컴퓨터가 필요해졌고, 이것이 DNS 서버이다

* 네임 서버와 IP 조회

    - 터미널을 열고 nslookup 입력
    
        - nslookup : Name Server lookup의 약자로서 DNS 관련 정보를 조회할 때 사용하는 유틸리티 프로그램

    - server 입력 : 현재 설정된 네임 서버의 IP 주소 확인

    - 구글, 유튜브, 네이버 등의 URL 입력시 해당하는 IP 주소 확인 가능

    - DNS 서버에 연결되지 않으면 url을 주소창에 입력해도 접속할 수 없다
    
    - 하지만 url과 대응되는 ip 주소를 알면 주소창에 직접 입력해서 접속할 수 있고, hosts 파일에 추가해도 DNS 서버 없이 접속할 수 있다 

        <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Linux/IP%20%EC%A3%BC%EC%86%8C%20%EC%B6%94%EA%B0%80.JPG" alt="IP 주소 추가">

        - hosts 파일에 위 형식으로 ip 주소를 추가하면 DNS 서버 없이도 해당 URL에 접속할 수 있다

        - hosts 파일에 네이버의 ip 주소에 구글 URL 입력 시 구글 URL 로 접속을 시도하면 네이버로 접속된다

    - URL 입력 시 IP 주소를 획득하는 과정

        <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Linux/IP%20%ED%9A%8D%EB%93%9D%20%EA%B3%BC%EC%A0%95.JPG" alt="IP 획득 과정">

        - /etc/host.conf : URL 입력 시 IP 주소를 얻기 위해 무엇을 먼저 확인할지 설정된 파일

            - order hosts, bind 거나 생략됬으면 /etc/hosts 를 먼저 찾아보고 관련 정보가 없다면 그 다음 DNS 서버를 사용

            - order bind, hosts 이라면 반대

        - /etc/resolv.conf : 네임 서버가 설정된 파일

    - 네임 서버는 URL 주소를 IP 주소를 변환해 주는 것일 뿐 컴퓨터에 연결된 네트워크에는 영향을 미치지 않는다

        - 위 예시처럼 네임 서버가 없더라도 IP 주소를 알면 인터넷은 정상적으로 작동한다

        - 하지만 URL의 IP 주소를 아는 경우가 거의 없으므로 실질적으로는 인터넷을 사용할 수 없어 네트워크가 안 되는 것처럼 느껴지는 것 뿐이다

    - 네임 서버 작동 순서

        <img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Linux/%EB%84%A4%EC%9E%84%20%EC%84%9C%EB%B2%84%20%EC%9E%91%EB%8F%99%20%EC%88%9C%EC%84%9C.JPG" alt="네임 서버 작동 순서">

        - PC의 웹 브라우저에 www.naver.com 을 입력

        - PC 가 리눅스일 경우 /etc/resolv.comf 파일을 열어 nameserver 네임서버IP 부분을 찾아 로컬 네임 서버를 알아낸다
        
            - 리눅스에는 /etc/resolv.conf 에 로컬 네임 서버가 IP가 저장되어 있다

            - hosts 파일은 네임 서버가 아닌 로컬에서 도메인에 해당하는 IP 주소를 찾을 때 사용하는 파일

        - 로컬 네임 서버에 www.naver.com 의 IP 주소를 물어본다

        - 로컬 네임 서버는 자신의 캐시 DB를 검색해 www.naver.com 의 정보가 있는지 확인한다

        - 찾지 못하면 로컬 네임 서버가 ROOT 네임 서버에 www.naver.com 의 IP 주소를 물어본다

        - ROOT 네임 서버도 주소를 모르므로 com 네임 서버의 주소를 알려주며 여기에 물어보라고 한다

        - 로컬 네임 서버가 com 네임 서버에 www.naver.com 의 주소를 물어본다

        - com 네임 서버도 www.naver.com 의 주소를 모르므로 naver.com 의 도메인을 관리하는 네임 서버인 naver.com 네임 서버의 주소를 알려 주며 이곳에 물어모라고 한다

        - 로컬 네임 서버가 naver.com 네임 서버에 www.naver.com 주소를 물어본다

        - naver.com 네임 서버는 네이버에서 구축한 네임 서버이므로 www.naver.com 이라는 이름을 가진 컴퓨터의 목록이 모두 있다

            - www.naver.com 의 IP 주소도 알고 있으므로 IP 주소를 알려준다

        - 로컬 네임 서버는 www.naver.com 의 IP 주소를 요구한 PC 에 알려준다

        - 권한 네임 서버(naver.com) 도 로컬 네임 서버로 활용되어 다른 네임 서버의 IP 주소를 찾는 역할을 할 수도 있다

            - 위 예시에서 naver.com 네임 서버는 자신이 관리하는 IP 주소를 응답하는 역할을 했다

            - 하지만 naver.com 네임 서버가 다른 컴퓨터에 로컬 네임 서버로 등록되어 있다면 다른 서버의 IP 주소를 찾는 역할도 함께 수행하게 된다

    - 캐싱 전용 네임 서버

        - 다른 네임 서버(권한 네임 서버)에서 얻은 응답을 캐시에 저장하고 재사용하여 성능을 향상시키는데 최적화된 네임 서버

        - 도메인 정보를 직접 관리하지 않는다

            - 사용자가 www.naver.com을 입력

            - 캐싱 전용 네임 서버가 해당 도메인에 대한 정보를 캐시에 가지고 있는지 확인

            - 없다면 외부 네임 서버(루트 → TLD → 권한 네임 서버)를 통해 IP 주소를 조회

            - 조회한 IP 주소를 사용자에게 응답하고, 캐시에 저장

            - 동일한 www.naver.com 요청이 들어오면 캐시에 저장된 데이터를 그대로 응답 (외부 네임 서버와 통신하지 않음)

            - 결과적으로 네트워크 부하 감소 & 응답 속도 향상 

        - 로컬 네임 서버가 캐싱 기능을 포함하는 경우가 많다

            - 로컬 네임 서버가 캐싱 전용 네임 서버라는 뜻이 아니다
            
            - 로컬 네임 서버는 도메인 정보를 직접 관리하는 네임 서버인데 추가로 캐싱 기능까지 있는 경우가 많다는 뜻이다

### 네임 서버 구축

* DNS 소프트웨어 설치

    - apt -y install bind9 bind9utils

    - 네임 서버도 다른 서버들처럼 네임 서버를 지원하는 프로그램(DNS 소프트웨어) 가 설치된 컴퓨터이다

    - DNS 소프트웨어에는 BIND, Unbound 등이 있다

* 네임 서버 설정 파일 수정

    - VMware 가 사용하는 가상머신 네트워크 주소에 있는 모든 컴퓨터가 네임 서버를 사용할 수 있도록 설정

        - bind 네임 서버는 기본적으로 로컬에서만 사용하도록 설정되어 있음

        - 같은 컴퓨터에서만 DNS 요청이 가능하므로, 다른 컴퓨터가 이 네임 서버를 사용할 수 있도록 설정하는 것

    - /etc/bind/named.conf.options 파일 수정

    ```
    21 행 dnssec-validation auto; -> dnssec-validation no;
    22 행 recursion yes;
    23 행 allow-query { any; };
    ``` 

* 서비스 작동

    - 네임 서버 상시 가동 : systemctl enable named

    - 네임 서버 재시작 : systemctl restart named

    - 네임 서버 상태 확인 : systemctl status named

* 포트 허용

    - 네임 서버의 포트인 53번 허용 : ufw allow 53

* 네임 서버 테스트

    - nslookup -> server [구축한 네임 서버 IP] -> 조회할 URL -> exit(종료할 시)


### 라운드 로빈 방식의 네임 서버

* 여러 서버나 자원에 대해 요청을 균등하게 분배하는 방식

* 부하 분산을(Load Balancing) 위해 사용

    - 사용자가 동일한 도메인을 요청할 때, 각 요청에 대해 다른 IP 주소를 반환

    - 예시

        - 사용자가 www.naver.com 에 접속할때 최종적으로 naver.com 네임 서버에 www.naver.com 의 IP 주소를 요청하게 됨

        - www.naver.com 웹 서버가 3대의 서버에 걸쳐 운영되고 각각의 IP 주소가 1.1.1.1, 2.2.2.2, 3.3.3.3 이라고 가정
        
        - naver.com 네임 서버는 요청받은 순서대로 1.1.1.1, 2.2.2.2, 3.3.3.3 의 IP 주소를 차례대로 알려 줌

        - 3대의 서버에 부하가 공평하게 나뉘게 된다


### 마스터 네임 서버

* 권한 네임 서버 중 DNS 데이터를 직접 수정할 수 있는 네임 서버

    - 권한 네임 서버를 여러개 사용해 마스터 네임 서버, 슬레이브 네임 서버 구조로 운영할 수 있다

    - 마스터 네임 서버는 DNS 데이터를 직접 수정할 수 있다

    - 슬레이브 네임 서버는 마스터 네임 서버의 데이터를 복제해 사용한다

    - DNS 서버의 안정성을 높이기 위해 사용한다

        - 마스터 네임 서버가 다운되더라도 슬레이브 네임 서버가 서비스를 제공할 수 잇다

* 예시

    - 마스터 네임 서버 naver.com 는 DB에서 직접 www.naver.com 의 IP 주소를 찾아 응답함

    - 하지만 네임 서버 자체의 부하 분산과 안정성을 위해 여러 개의 슬레이브 네임 서버를 운영

        - a.naver.com, b.naver.com 의 슬레이브 네임 서버 운영
     
        - naver.com 네임 서버가 다운되더라도 a.naver.com, b.naver.com 이 DNS 서비스를 제공할 수 있음
     
        - 그리고 마스터 네임 서버의 트래픽을 슬레이브 네임 서버로 균등하게 보내 부하 분산을 할 수 있음 

    - 슬레이브 네임 서버들은 마스터 네임 서버의 정보를 복제해서 사용

### 컴퓨터에서 어떤 네임 서버 사용할지 설정하는 방법

* /etc/resolv.conf 파일에 DNS 설정

    - 모든 네트워크에 적용됨
 
    - 하지만 부팅 시 /etc/netplan/90..yml 에 설정된 DNS 설정으로 /etc/resolv.conf 가 업데이트됨
 
* /etc/netplan/90..yml 파일에 DNS 설정

    - 네트워크 인터페이스(NIC) 마다 다른 DNS 서버 설정 가능
 
    ```
    network:
      version: 2
      ethernets:
        eth0:  # 유선 NIC
          addresses:
            - 192.168.1.100/24
          gateway4: 192.168.1.1
          nameservers:
            addresses:
              - 8.8.8.8  # Google DNS
              - 1.1.1.1  # Cloudflare DNS
    
      wifis:
        wlan0:  # 무선 NIC
          addresses:
            - 192.168.1.200/24
          gateway4: 192.168.1.1
          nameservers:
            addresses:
              - 9.9.9.9  # Quad9 DNS
              - 208.67.222.222  # OpenDNS
    ```
