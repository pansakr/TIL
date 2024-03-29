### 운영체제(Operating System)

* 컴퓨터의 성능을 높이고 사용자에게 편의성 제공을 목적으로 하는 컴퓨터 하드웨어 관리하는 프로그램

* 운영체제를 사용하는 주된 목적은 컴퓨터의 하드웨어를 관리하는 것이다.

* cpu, 메모리, 디스크, 키보드, 마우스, 모니터, 네트워크 등 하드웨어를 잘 관리해주어야 컴퓨터를 효율적으로 사용할 수 있다.

* 그리고 사용자에게 편의를 제공하는 목적도 가지고 있다.

* 운영체제가 없다면 하드웨어의 모든 관리를 사용자가 해야하고 그러면 컴퓨터를 사용하는데 큰 불편함을 겪을 것이다.

* 운영체제에는 커널과 명령어 해석기가 있다.

* cpu, os가 64bit일 경우 64bit 플랫폼이라 한다.


### 명령어 해석기(Command Interpreter, Shell)

* 사용자가 컴퓨터에게 전달하는 명령을 해석하는 프로그램으로 사용자와의 상호작용을 가능하게 한다.


### 커널(Kernel)

* 운영체제의 핵심으로 입출력 제어, 접근 통제, 컴퓨터 자원(cpu, memory)들을 관리하는 역할을 한다.

* 사용자가 system shell을 통해 컴퓨터 자원을 사용할 수 있게 해주는 자원관리자이다.



### 컴퓨터의 부팅

* 전원이 켜지면 CPU에서 ROM에 있는 내용을 읽는다. 

* ROM 안에는 POST(Power-On- Self-Test), 부트 로더(Boot Loader)가 저장되어 있다.

* POST는 전원이 켜지면 가장 처음에 실행되는 프로그램으로 현재 컴퓨터의 상태를 검사한다.

* POST작업이 끝나면 부트로더가 실행된다. 부트로더는 하드디스크에 저장되어 있는 운영체제(정확히 커널)를 찾아서 메인 메모리에 가지고 온다.

* 메모리에 상주하고 있는 운영체제의 부분이 커널이다.

* 이러한 부트 로더의 과정을 부팅이라고 한다.

* 운영 체제가 종료하는 시점은 컴퓨터의 전원이 꺼지는 시점이다.


### 유저 모드(영역)와 커널 모드(영역)의 차이점

* 운영체제는 여러 프로그램이 동시에 실행될 수 있는 멀티 태스킹 환경에서 동작한다.

* 그로므로 프로그램 간에 서로 충돌을 일으키는 문제를 막기 위해 하드웨어에 보안 기법이 필요하다.

* 운영체제는 메모리를 안전하게 관리하게 위해서 유저 모드와 커널 모드로 나누어 관리한다.

* 명령어 수행 과정에서 cpu는 항상 메모리에 이번에 수행해야 할 instruction의 주소를 건네준 후 관련된 데이터나 코드를 받아서 실행한다.

* 이 과정에서 지금 현재 상태가 유저 모드인지 커널 모드인지가 중요하다.

* 메모리에는 운영체제를 실행시키기 위해 필요한 메모리 공간, 프로그램이 동작하기 위해 사용되는 메모리 공간이 있다.

* 커널 모드는 운영체제가 CPU의 제어권을 가지고 운영 체제 코드를 실행하는 모드로, 모든 영역에 접근할 수 있다.(os가 할당받은 메모리 영역)

* 유저 모드는 일반 사용자 프로그램이 실행되며 오직 자신의 메모리 영역에만 접근할 수 있다.(프로세스가 할당받은 메모리 영역)

* 유저모드와 커널모드를 구분하기 위해 cpu내부에 모드비트를 사용한다. 모드비트가 0이면 커널, 1이면 사용자 모드이다.

* 만약 사용자 프로그램이 중요한 연산을 수행해야한다면 System Call을 통해 운영체제에게 서비스를 대신해 줄 것을 요청한다.

* 그러면 cpu의 제어권이 운영체제로 넘어가고 모드 비트가 0으로 세팅되어 필요한 작업을 수행하고 끝나면 모드 비트를 다시 0으로 만들어 사용자 프로그램에게 cpu를 넘겨준다. 

* 이러한 모드를 나누는 이유는 메모리에 운영체제를 실행시키기 위한 자원들이 위치한 영역도 있기 때문에
일반 사용자가 그 중요한 자원에 접근해 명령을 실행시킬수 없게 하기 위함이다.

*instruction - 프로세스의 명령

*영역,모드 둘다 같은 뜻이다. 이해하기 어려우면 해당 단어가 어울리는 것으로 바꿔서 생각해도 된다.

*유저모드 -> 커널모드로 바뀌는 것을 컨텍스트 스위칭이라고 한다.


### 디바이스 드라이버(Device Driver)

* 특정 하드웨어나 장치를 제어하기 위한 커널의 일부분으로 동작하는 프로그램

* 컴퓨터의 물리적인 장치를 구동하기 위해서는 해당 장치에 대한 동작을 구현한 소프트웨어를 작성해야하는데 이것을 device driver라고 부른다 

* 우리가 쓰는 모든 입출력장치와 운영체제(정확히 커널) 사이에서 명령어나 데이터를 전달해주는 역할

* 디바이스 드라이버가 없다면 컴퓨터는 입출력장치들을 인식하지 못한다.

* 커널 영역에서 동작하기 때문에 자신을 추상화한 인터페이스인 디바이스 파일을 유저 모드에 제공해준다.


*Device = 키보드, 마우스, 모니터, 프린터기 같은 주변기기


### 유저,커널 영역(모드)와 디바이스, 디바이스 드라이버가 연결되어 동작하는 과정 예시1

* hello world를 출력하고 싶다면 컴퓨터에 비디오 카드 디바이스가 연결되어 있어야 한다. (필요한 다른 디바이스는 있다고 가정)

* 비디오 카드 디바이스의 데이터를 커널 영역(커널 모드에서 접근 가능한 메모리 영역)의 디바이스 드라이버가 받아 변환해준다.

* 변환한 데이터를 커널 영역의 그래픽 엔진 시스템 소프트웨어가 받아준다. 디바이스의 종류마다 받아주는 시스템 소프트웨어가 다르다. 

* 비디오 카드 디바이스 드라이버는 자신을 추상화한 인터페이스(디바이스 파일. 파일의 형태이다.)를 유저 모드에 제공해준다.

* 이제 hello world를 출력했다. 그러면 os는 프로세스를 생성하고 저장장치의 프로그램으로부터 필요한 데이터를 복사해 메모리로 가져온다.

* 그리고 프로세스에 메모리 공간을 일정부분 할당해준다.

* cpu가 명령(hello world를 출력 명령)을 수행하기 위해 유저 모드로 프로세스가 할당된 메모리 주소에 접근해 필요한 데이터를 받아 연산 후 메모리에 돌려준다.

* 이때 인터럽트를 받으면 잠시 멈추고 해당 인터럽트 명령을 실행 완료하고 원래 하던 작업으로 돌아와 다시 시작한다.

* 프로세스는 받은 연산 결과로 인터페이스에 write하고, 이것을 구성요소(디바이스종류마다 다른 소프트웨어)로 넘기고 구성 요소는 디바이스 드라이버에 넘기고, 디바이스 드라이버는 하드웨어로 넘겨서 하드웨어가 해당하는 장치로 가서 출력을 수행한다.  

### 예시2

* HDD가 컴퓨터에 연결되어 있고 이를 위해 디바이스 드라이버가 HDD의 정보를 받아 커널 영역에 보내준다.

* 커널 영역에 파일시스템 소프트웨어가 생성되고 HDD의 인터페이스(디바이스 파일)를 유저 영역에 생성한다.

* 이제 사용자가 탐색기를 클릭시 해당 프로세스가 생성되고 CPU가 해당 프로세스에 접근해 데이터를 받아 연산하고 그 결과를 인터페이스 -> 파일 시스템 -> 드라이버 -> HDD -> 출력장치 -> 최종적으로 사용자에게 보여준다.
