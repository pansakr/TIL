### CPU의 코어

* CPU는 코어를 가지고 연산을 한다.

* 스레드는 코어를 시분할 사용한다. 

*시분할 - 스레드들이 각각 아주 짧은 시간 단위로 순서를 번갈아가며 코어를 사용한다.

### PCB(process Controller Block)

* 운영체제가 프로세스를 제어하기 위해 프로세스의 상태 정보를 저장해 놓는 곳

* 이 정보들에는 프로세스ID, 프로세스 상태(준비,대기,실행 상태), 입출력 정보 등이 있다.

* 하나의 PCB에는 하나의 프로세스의 정보가 있다.
```
// 프로그램 실행 -> 프로세스 생성 -> 프로세스에 메모리 할당 
// 할당받은 메모리 공간에 코드,데이터, 스택 생성 -> 이 프로세스의 메타데이터들이 PCB에 저장
// 프로세스 생성시 pcb가 생성되고 완료시 제거된다.
```

* cpu는 프로세스의 상태에 따라 교체 작업이 이루어지는데 교체 전 지금까지 작업했던 프로세스의 상태를
pcb에 저장한다.

* 이렇게 수행중인 프로세스를 변경할 때 cpu의 레지스터 정보가 변경되는 것을 Context Swiching이라고 한다.

### Context Swiching 

* cpu가 이전의 프로세스 상태를 pcb에 보관하고, 다른 프로세스의 정보를 pcb에서 읽어서 레지스터에 적재하는 과정

* 인터럽트가 발생하거나 실행 중인 cpu 사용 허가시간을 모두 소모하거나 입출력을 위해 대기하는 경우 
Context Swiching이 발생한다.

* 여러 프로세스를 동시 사용하는것처럼 하기 위해 Context Switching을 사용한다.

* cpu는 한번에 하나의 프로세스만 수행 가능하지만 여러 프로세스를 매우 빠른 속도로 Context Swiching해서
처리하기 때문에 동시에 처리하는것처럼 보인다.

* 즉 멀티태스킹 환경에서 각각의 프로세스의 정보를 저장 -> 실행 -> 복귀후 다시 실행 -> 저장 -> 실행 의 무수한 반복이다.
