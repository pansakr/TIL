### 절대경로와 상대경로

<img src = "https://raw.githubusercontent.com/pansakr/TIL/refs/heads/main/%EC%9D%B4%EB%AF%B8%EC%A7%80/Computer%20Science/%EC%A0%88%EB%8C%80%EA%B2%BD%EB%A1%9C%2C%20%EC%83%81%EB%8C%80%EA%B2%BD%EB%A1%9C.jpg" alt="절대경로, 상대경로">

* 위 그림에서 경로 부분의 main은 오타이다. main -> portfolio 이다
```
// style.css에서 photo.jpa링크 시 ../로 상위경로인 css폴더로 이동 후 images폴더 -> portfolio -> photo.jpg로 이동한다
// 절대경로는 html파일의 위치를 기준으로 정해지는 상대적인 경로가 아닌 절대적인 경로이기 때문에 html파일의 위치가 변경되어도 링크가 어긋나지 않는다
// 상대경로는 현재 html파일의 위치를 기준으로 링크경로를 작성하기 때문에 html위치가 변경되면 링크 경로도 그것에 맞춰 바꿔줘야 한다
```

* 절대경로는 Root부터 해당 파일까지의 전체 경로를 나열한것

* 상대경로는 현재 파일의 위치를 기준으로 연결하려는 파일의 경로를 적는것

* 상대경로는 어떤 경로로 부터 비교하는지 항상 비교 대상인 기준 경로가 있어야 한다

* 상대경로는 졀대 경로로 변환되어 os에게 전달되고, 상대경로는 기준 경로를 기준으로 절대경로가 구성된다

```
/ 기호는 Root(최상위 디렉토리)
./ 기호는 현재 위치. 보통 ./는 생략한다
../ 기호는 현재 위치의 상위 경로
```

### URI(Uniform Resource Identifier), URL(Uniform Resource Locator)

* URI - 자원을 식별하기 위한 문자열의 구성

* URL - 자원의 위치를 나타내는 주소

* URL은 URI의 하위개념이다

```
https://example.io 는 https://example.io 라는 서버를 나타내기 때문에 URL이면서 URI이다

https://example.io/images 의 경우 example.io 서버의 images라는 네트워크 상의 자원의 위치를 의미하기 때문에 URL이면서 URI이다

https://example.io/images/dog.jpeg 의 경우 example.io 서버의 images 디렉터리 아래의 dog.jpeg를 
가리키므로 URL이면서 URI이다

https://example.io/user/123 의 경우 URL은 https://example.io/user 까지고 원하는 정보에 도달하기 위한 식별자 123을 포함하면 URI이다. 즉, URI이지만 URL은 아니다

https://example.io/profile?id=11 의 경우도 마찬가지로 https://example.io/profile 까지는 URL이지만 원하는 정보에 도달하기 위한 식별자(여기서는 ?id=11)를 포함하면 URI가 된다. 즉, URI이지만 URL은 아니다
```
