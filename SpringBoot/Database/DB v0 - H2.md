### H2 databaes

* 로컬에서 실행 가능하며 테스트 용도로 사용하는 DB

```
// 설정 방법

1. h2 database 의존성 추가 -> 외부 라이브러리 목록에서 h2 버전 확인 ex) 2.2.224

2. www.h2database.com 접속 -> Download 칸의 All Downloads -> Archive Downloads

3. 스프링 프로젝트의 h2 라이브러리 버전과 동일한 버전으로 Zip 다운로드

4. 압축풀고 h2.bat 실행 -> 브라우저에 h2 db 접속화면 뜸

5. saved setting 을 Generic H2(Embedded) 로 교체 하고 jdbc url에 jdbc:h2:~/생성할DB이름 을 입력해 DB생성(최초 한번만) ex) jdbc:h2:~/datajpa -> DB 생성 완료 

6. 화면 상단 제일 왼쪽의 연결 끊기를 눌러 되돌아가고, 이후로는 saved setting 을 Generic h2(Server) 로 교체하고 jdbc:h2:tcp://localhost/~/datajpa 로 연결
```
