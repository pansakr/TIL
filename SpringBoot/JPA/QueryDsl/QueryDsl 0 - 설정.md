### Query Dsl

* 스프링 부트 3.0 이상 설정 방법

    - build.gradle

    ```java
    plugins {
    id 'java'
    id 'org.springframework.boot' version '3.2.0'
    id 'io.spring.dependency-management' version '1.1.4'
    }


    group = 'study'
    version = '0.0.1-SNAPSHOT'
    sourceCompatibility = '17'


    configurations {
        compileOnly {
            extendsFrom annotationProcessor
        }
    }


    repositories {
        mavenCentral()
    }


    dependencies {
        implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
        implementation 'org.springframework.boot:spring-boot-starter-web'
        implementation 'com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.0'
        compileOnly 'org.projectlombok:lombok'
        runtimeOnly 'com.h2database:h2'
        annotationProcessor 'org.projectlombok:lombok'
        testImplementation 'org.springframework.boot:spring-boot-starter-test'


        //test 롬복 사용
        testCompileOnly 'org.projectlombok:lombok'
        testAnnotationProcessor 'org.projectlombok:lombok'


        //Querydsl 추가
        implementation 'com.querydsl:querydsl-jpa:5.0.0:jakarta'
        annotationProcessor "com.querydsl:querydsl-apt:${dependencyManagement.importedProperties['querydsl.version']}:jakarta"
        annotationProcessor "jakarta.annotation:jakarta.annotation-api"
        annotationProcessor "jakarta.persistence:jakarta.persistence-api"
    }


    tasks.named('test') {
        useJUnitPlatform()
    }


    clean {
        delete file('src/main/generated')
    }
    ```

    - Q 클래스 생성 방법

        - 오른쪽 Gradle 아이콘 -> Tasks 폴더 -> Other 폴더 -> compile.java

        - 실행 후 build -> generated -> sources -> annotationProcessor -> java

        - java 폴더 내부에 main 디렉토리 구조가 그대로 있는데, 엔티티 클래스가 위치한 곳에 가보면 Q클래스가 생성되어 있다

* 스프링 부트 2.xx 설정 방법

    - build.gradle

    ```java
    buildscript {
    	ext {
    		queryDslVersion = "5.0.0"  // 스프링 2.6 버전 이상은 5.0 버전을 사용해야 한다.
    	}
    }
    
    plugins {
    	id 'java'
    	id 'org.springframework.boot' version '2.7.9'
    	id 'io.spring.dependency-management' version '1.0.15.RELEASE'
    	id "com.ewerk.gradle.plugins.querydsl" version "1.0.10"  // querydsl 플러그인 추가
    }
    
    group = 'com.ex'
    version = '0.0.1-SNAPSHOT'
    
    java {
    	sourceCompatibility = '11'
    }
    
    configurations {
    	compileOnly {
    		extendsFrom annotationProcessor
    	}
    }
    
    repositories {
    	mavenCentral()
    }
    
    dependencies {
    	implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    	implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
    	implementation 'org.springframework.boot:spring-boot-starter-validation'
    	implementation 'org.springframework.boot:spring-boot-starter-web'
    
        // 버전을 {queryDslVersion}처럼 표기할때는 ""쌍따옴표를 사용해야 한다.
    	implementation "com.querydsl:querydsl-jpa:${queryDslVersion}"	// QueryDsl 의존성 추가
    	annotationProcessor "com.querydsl:querydsl-apt:${queryDslVersion}" // QueryDsl annotationProcessor 추가
    
    	compileOnly 'org.projectlombok:lombok'
    	developmentOnly 'org.springframework.boot:spring-boot-devtools'
    	runtimeOnly 'org.postgresql:postgresql'
    	annotationProcessor 'org.projectlombok:lombok'
    	testImplementation 'org.springframework.boot:spring-boot-starter-test'
    }
    
    // QueryDsl 추가설정
    
    // querydsl 빌드 경로 변수
    def querydslDir = "$buildDir/generated/querydsl"
    
    querydsl {
    	jpa = true
    	querydslSourcesDir = querydslDir
    }
    
    // build 시 사용할 sourceSet 추가
    sourceSets {
    	main.java.srcDir querydslDir
    }
    
    configurations {
    	querydsl.extendsFrom compileClasspath
    }
    
    // querydsl 컴파일시 사용할 옵션 설정
    compileQuerydsl{
    	options.annotationProcessorPath = configurations.querydsl
    }
    ```
