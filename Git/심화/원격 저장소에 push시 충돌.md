### 충돌

* 하나의 브랜치를 여러명이서 받아 각각 작업후 push했을때 첫번째 사람 이후부터는 오류가 난다.

```
git push // 두번째 사람부터 오류남
```

* 첫번째 사람이 push함으로서 원격 저장소에는 새로운 커밋이 생겼는데 이후 사람들의 저장소에는 해당 커밋이 없어 일치하지 않기 때문이다.

* 즉 원격에 적용된 새 버전이 있으므로 커밋이 불가능한 상태다.

* 이 경우에는 pull해서 원격의 버전을 받아온 다음 push 해야한다.

* 그런데 pull로 받아와 합칠때, 하나의 커밋에서 분기된 후 첫번째사람이 원격에 push한 커밋과 내가 로컬에서 만든 커밋을 어떻게 합칠지 정해야 한다.

```
// merge 방식
git pull --no-rebase


// rebase 방식
git pull --rebase

// 둘중 하나로 pull 이후 push
```

<img src="https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/%EB%A8%B8%EC%A7%80%20%EC%A0%84.jpg" alt="머지 전">

* Add Dongho to Leopards커밋에서 분기되어 원격 저장소에 커밋 하나가 push되었고, 로컬에 내 커밋이 있는 상태이다.

* 원격에 새 버전이 있어 push가 불가능하니 pull을 사용해 먼저 동기화를 시켜야 한다. 

* 즉 원격 저장소의 Edit Leopards coach커밋을 내 로컬 저장소의 Edit Leopards manager커밋과 합쳐야 한다.

* 이때 merge, rebase 두 가지 방법이 있다.

<img src="https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/%EB%A8%B8%EC%A7%80.jpg" alt="머지">

* merge는 두 커밋을 하나의 커밋으로 합치는 방법이다. 즉 두 커밋이 합쳐진 1개의 커밋이 생성된다.

* Edit Leopards coach의 merge 이전 커밋 기록은 유지된다.

<img src="https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Git/rebase.jpg" alt="rebase">

* rebase는 원격 저장소의 커밋 라인에 내 커밋을 이어 붙인다.

* 분기 커밋 이후 Edit Leopards coach커밋인 원격 커밋 라인에 내 커밋 Edit Leopards manager을 이어붙인다.

* Edit Leopards manager의 rebase 이전 커밋 기록은 삭제된다.

* merge나 rebase 이후 push한다.


### pull 충돌

* 만약 pull merge, rebase에서 충돌이 발생한다면 파일을 직접 수정해 충돌을 해결한다.

* merge는 어떤 방식으로 충돌을 해결해도 하나의 commit이 새로 생긴다.

* rebase는 원격 저장소의 내용을 선택해 충돌을 해결했을때 두 충돌 파일이 충돌 이외 부분이 모두 동일하다면 굳이 두개의 커밋 기록을 남길 필요가 없어 분기 커밋 다음 하나의 커밋만 생성된다.

