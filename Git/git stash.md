### git stash

* 작업하던 중 급히 다른 작업을 먼저 해야 할때 진행 중이던 작업사항을 다른 곳으로 옮겨 임시 저장한다.

* 한 커밋에는 하나의 작업사항만 있어야 하기 때문에 급히 먼저 할 일이 있으면 진행 중이던 작업을 다른 곳에 임시로 옮겨둔다.

```
// add 후 사용 가능. add이전에 사용할 수 없다.
git stash

// 메시지와 함께 stash할 수 있다.
git stash -m "메시지"

// stash 이후 원하는 시점, 브랜치에서 다시 불러와 적용할 수 있다.
git stash pop

// 원하는 것만 stash할 수 있다. add 전, 후 모두 사용 가능
git stash -p

// 변경 목록들이 나오고 y, n 입력으로 stash여부를 결정할 수 있다.

// stash한 목록들을 볼 수 있다.
git stash list

// git stash list예시
stash@{0}: On main: Add Stash3
stash@{1}: WIP on main: 02196c1 Edit Leopards and Tigers

// stash 여러 명령어에 list목록의 stash번호들을 사용할 수 있다.
// 해당 번호의 stash 적용
git stash apply stash@{1}

// 해당 번호의 stash 삭제. 적용해도 stash 리스트에서는 사라지지 않아 직접 삭제해줘야 한다.
// pop 옵션을 사용하면 적용 후 삭제된다.
git stash drop stash@{1}

// stash list의 모든항목들 지우기
git stash clear
```
<img src="https://github.com/pansakr/TIL/assets/118809108/829bc13a-4f3f-40c7-8a19-09d8c425698e">
