### H2 databaes

* 테스트 용도로 사용하고 로컬에서 실행가능한 db

```
// 설정 방법
1. h2 database 의존성 추가 -> 외부 라이브러리 목록에서 h2 버전 확인 ex) 2.2.224
2. www.h2database.com 접속 -> Download 칸의 All Downloads -> Archive Downloads
3. 스프링 프로젝트의 h2 라이브러리 버전과 동일한 버전으로 Zip 다운로드
4. 압축풀고 h2.bat 실행 -> 브라우저에 h2 db 접속화면 뜸
5. jdbc url에 jdbc:h2:~/test 입력해 연결(최초 한번만) -> 파일 생성 완료
6. 이후로는 jdbc:h2:tcp://localhost/~/test 로 연결
```
