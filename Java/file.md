### 파일(file)

* 저장 장치(디스크)에 정보를 저장하는 단위

* 정보를 저장 후 이름을 붙여 관리한다.

### 데이터 입출력

* 데이터는 사용자로부터 키보드, 마우스 등을 통해서 입력 될 수 있고

* 파일 또는 네트워크를 통해서 입력될 수 있다.

* 모니터, 파일 등으로도 출력할 수도 있다.

### Stream Api

* 컬렉션, 배열에 저장된 요소들을 하나씩 참조하며 처리하는 것은 Stream API이다.

### 입출력 Stream

* 데이터 입출력에서의 Stream은 데이터를 연속으로 읽거나 쓰는 행위이다.

* 프로그램 사용시 외부에서 데이터를 읽거나 외부로 출력하는 작업을 할때 데이터는 어떠한 통로를 통해서 이동
하는데 이 통로를 Stream이라고 한다.

* 스트림은 단방향 통신을 하기에, 하나의 스트림으로 입출력을 동시에 할수 없다.

* 데이터를 입력 받을 때 InputStream, 출력할 때 OutputStream을 사용한다.

* 프로그램이 네트워크상의 다른 프로그램과 데이터를 교환하기 위해서 양쪽 모두 입력,출력 스트림이 따로 필요하다.

### Java.io 패키지

* 자바의 기본적인 데이터 입출력을 제공한다.

### InputStream, OutputStream

* io패키지에 속해 있고 바이트 단위 입출력을 위한 최상위 추상 스트림 클래스다.

* 모든 바이트 기반 입출력 스트림은 이 클래스를 상속받아서 만들어진다.

* 모든 바이트 기반 입출력 스트림의 공통된 역할을 추상화시켜 메소드로 가지고 있다.

* 상속받는 하위 스트림 클래스

* FileInputStream / FileOutputStream

* DataInputStream / DataOutputStream

* ObjectInputStream / ObjectOutputStream

* BufferedInputStream / BufferedOutputStream

* PrintStream

### Reader, Writer

* 문자 단위 입출력을 위한 최상위 입출력 추상 스트림 클래스

* 모든 문자 단위 입출력 스트림은 이 클래스를 상속받는다.

* 상속받는 하위 스트림 클래스

* FileReader / FileWriter

* InputStreamReader / OutputStreamWriter

* BufferedReader / BufferedWriter

* PrinterWriter

### Byte 단위로 입출력하는 FileInputStream, FileOutputStream

```
 public static void main(String[] args) {

    // FileInputStream클래스는 Closeable인터페이스를 구현하고있는 추상클래스 InputStream을 상속받고있어 try - catch - resource로 작성이 가능하다.

        try(

            // ()에 파일 이름 작성 시 현재 디렉토리의 파일이 선택이 되는데 현재 디렉토리에 파일 생성 후  
            // 실행을 해도 인텔리제이의 경우 프로그램이 실행되는 working directory 경로가 프로젝트명으로 
            // 되어있어 실제 파일이 저장된 경로와 프로그램 실행 경로가 다르기 때문에 
            // 프로그램 실행 경로인 working directory에서 파일을 찾지 못해 오류가 난다.
            // 해결 방법은 working directory에 파일을 생성하거나
            // working directory 경로를 파일이 저장된 경로로 변경해 주면 된다.

            FileInputStream fis = new FileInputStream("File01.txt")
            ;FileOutputStream fos = new FileOutputStream("Test_File02.txt"))
            {
            int i;
            while((i = fis.read()) != -1){
                fos.write(i);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());

        }
    }

```

* 한글을 출력할 경우

```
public static void main(String[] args) {

        try(FileInputStream fis = new FileInputStream("han1.txt")
           ;FileOutputStream fos = new FileOutputStream("Test_han1.txt")){

               fos.write(fis.read());
               fos.write(fis.read());
               fos.write(fis.read());

        }catch (Exception e){

            System.out.println(e.getMessage());

        }
    }

결과 - Test_han1.txt 파일 생성
       Test_han1.txt 내용 - 한


han1.txt
한글 테스트
```

* 영문은 한 글자가 1byte이지만 한글은 UTF-8인코딩 방식에서 한글 한 문자가 3byte이기 때문에 
* 1byte단위로 읽는 read()를 세 번 읽어야 한 글자를 읽을 수 있게 된다.

### 문자 단위로 읽고 쓰는 클래스 FileReader, FileWriter

```
 public static void main(String[] args) {

        try(FileReader fr = new FileReader("han1.txt")
            ; FileWriter fw = new FileWriter("Test_han1.txt")){

               fw.write(fr.read());
               fw.write(fr.read());
               fw.write(fr.read());

        }catch (Exception e){

            System.out.println(e.getMessage());

        }
    }

결과 - Test_han1.txt 파일 생성
       Test_han1.txt 내용 - 한글 + 공백한칸
```

* 문자 단위로 읽고 쓰기 때문에 3번 읽어서 한,글,공백한칸 이 출력되었다.

### 문자열을 읽어서 파일로 출력하는 InputStream, FileOutputStream

```
 public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        System.out.println("이름을 입력하세요 : ");
        String name = sc.nextLine();
        name += "\n" + name;
        System.out.println(name);

        InputStream is = new ByteArrayInputStream(name.getBytes());

        try(FileOutputStream fw = new FileOutputStream("test_han5.txt")){
            int i;
            while ((i = is.read()) > -1){
                fw.write(i);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    결과 
    이름을 입력하세요 : 자바
    자바
    자바

    test_han5.txt
    자바
    자바
```

* 파일이 아니기 때문에 FileInputStream 대신 상위 클래스인 InputStream을 사용한다.

* InputStream은 byte 단위로 사용하기 때문에 출력할 때도 byte 단위로 출력하는 FileOutputStream클래스를 사용한다.

* 문자열을 byte 단위로 사용하는 InputStream 클래스로 생성하려면 String객채의 getByte()를 사용해서 

* 문자열을 바이트 배열로 변경한 결과를 ByteArrayInputStream 클래스로 받아 InputStream 객체로 넘긴다.

### 버퍼

* 하나의 장치에서 다른 장치로 데이터를 전송할 때 서로의 데이터의 전송 속도나 처리 속도의 차이를 극복하기 위한 임시 저장 공간

#### 동영상 스트리밍에서의 버퍼

* 동영상은 영상이 진행되는 빨간색 부분, 서버로부터 동영상을 내려받은 회색부분, 아직 받지 않은 투명부분으로 구분할 수 있다.

* 만약 동영상을 보는 속도와 데이터를 받는 속도가 같다면 동영상 시간만큼 데이터를 받아야 한다.

* 이를 방지하기 위해 버퍼라는 임시 저장공간을 두고 동영상 데이터를 일정 부분 최대한 빠르게 받아 저장해 둔다.

* 동영상을 보다 보면 버퍼에 동영상이 모두 다운로드 되어 있다.

* 인터넷이 느리던 시절에는 동영상을 버퍼에 다운로드 하는 속도가 시청속도를 따라가지 못해 동영상이 멈추는 일이 있었고 이를 버퍼링 걸린다 라고 이야기했었다.

#### 입출력에서의 버퍼

* 프로그래밍에서의 버퍼는 cpu와 보조기억장치 사이에서 사용되는 임시 저장 공간을 의미한다.

* cpu는 매우 빠르게 데이터를 처리할 수 있지만 보조기억장치는 데이터를 보내주는 속도가 매우 느리다.

* 이 경우 cpu가 아무리 빨라도 데이터를 보내주는 속도가 느리니 효율성이 매우 떨어지게 된다.

* 이 때 버퍼를 사용하는데 버퍼는 보조기억장치보다 훨씬 빠른 주기억 장치(RAM)을 이용한다.

* 보조기억장치가 버퍼에 데이터를 보내 쌓아두면 cpu가 다른 일을 처리하다가 어느정도 쌓이면 한꺼번에 가져와서 처리한다.

### 버퍼를 이용하는 파일 스트림 BufferedInputStream, BufferedOutputStream

* 용랑이 큰 음악 파일이나 동영상 파일은 버퍼를 활용한다.

```
 public static void main(String[] args) {

        try(FileInputStream fis = new FileInputStream("TestFile.java");
            BufferedInputStream bis = new BufferedInputStream(fis);
            FileOutputStream fos = new FileOutputStream("TestFile01.exe");
            BufferedOutputStream bos = new BufferedOutputStream(fos))
        {
         int i;
         while ((i = bis.read()) != -1){ // bis에 데이터가 있으면 반복해 출력한다.
             bos.write(i);
         }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }finally {
            System.out.println("파일 복사를 완료했습니다.");
        }
    }
```

* 파일을 FileInputStream으로 읽은 후 그 인스턴스를 BufferedInputStream 생성자의 인자로 사용해 다시 생성한다

* 출력도 FileOutputStream으로 생성하고 그 인스턴스를 BufferedOutputStream의 인자로 넣어 생성한다.


### 버퍼를 이용하는 파일 스트림으로 웹사이트의 이미지를 다운로드

```
public static void main(String[] args) throws Exception{

        /// Java API에서 제공하는 URL을 이용하여 웹사이트의 주소를 다루는 객체
        // URL() 생성자에 이미지 url 주소를 입력한다.
        URL url = new URL("\thttps://ssl.pstatic.net/melona/libs/res/www/timeboard/premium/icn-affordance-play-balloon-w@2x.png"); 

        // 용랑이 클 수 있는 파일이기 때문에 버퍼를 이용한 BufferedInputStream, BufferedOutputStream 으로 읽고 쓴다.
        InputStream in = new BufferedInputStream(url.openStream());
        OutputStream out = new BufferedOutputStream(new FileOutputStream("1ba.png"));

        for(int i; (i = in.read()) != -1;){
            out.write(i);
        }
        in.close();
        out.close();
        
    }
```

### 
