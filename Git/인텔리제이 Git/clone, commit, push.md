### 인텔리제이에서 git clone 방법과 clone 한 프로젝트를 수정 후 커밋, 푸시하는 방법

1. 아무곳에서 Shift 키를 두 번 눌러 검색 팝업 열기

2. 검색 필드에 clone을 입력하고 Create Repository 옵션을 선택

3. 몇가지 옵션이 있는데 두번째 옵션인 GitHub를 클릭후 계정 로그인을 해서 인텔리제이와 연동

4. GitHub 계정에 있는 Repository 목록들이 보일텐데 원하는 저장소를 클릭후 Directory옵션에서 받을 경로를 설정 -> Clone 버튼을 누르면 GitHub의 원격 저장소에 있던 프로젝트가 로컬 저장소로 복사됨

  - 첫번째 옵션 Repository URL은 저장소 URL을 사용하여 지정된 디렉토리에 저장소를 복제할 수 있다
  
  - 네번째 옵션 Space는 널리 사용되는 다른 버전 제어 시스템에 연결하고 거기에 저장된 리포지토리를 복제할 수 있다

5. 복사한 프로젝트에서 작업 수행 후 새로운 브런치가 필요하다면 오른쪽 아래 5시방향 main 버튼을 클릭후 new Branch 버튼을 클릭 -> 브런치의 이름을 입력하고 만들기를 클릭

  - 이후 커밋 - 푸시 방법은 브런치 생성여부와 상관없이 같다

6. ctrl k를 누르거나 오른쪽위 3시방향의 체크표시 아이콘을 클릭하여 커밋 도구 창 열기 -> 여기에서 커밋할 파일을 선택할 수 있다

7. 파일목록 아래의 검은색 영역에 커밋 메시지를 입력하고 커밋 버튼을 클릭

8. 커밋을 수행한 후 원격 저장소로 푸시할 수 있다. ctrl shift k를 누르거나 오른쪽위 3시방향의 화살표 아이콘을 클릭하여 푸시 대화 상자를 연다

9. 푸시 클릭 -> 연결된 GitHub 계정에 변경사항이 반영됨
