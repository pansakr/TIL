### QueryDsl

* sql, jpql을 코드로 작성할 수 있도록 해주는 빌더 api

* entity와 매핑되는 QClass 객체를 사용해서 쿼리를 실행한다.

* jpql과 다르게 sql을 코드로 작성하기 때문에 IDE의 도움을 받을 수 있고 컴파일 단계에서 오류를 발견할 수 있다. 


### QClass

* QueryDsl로 작성하는 쿼리의 대상이 되는 클래스

* JPAAnnotationProcessor이 컴파일 시점에 @Entity를 기반으로 생성한 Static Class


### Annotation Processor

* 컴파일 단계에서 Annotation에 정의된 일렬의 프로세스를 동작하게 하는 것

* 컴파일 단계에서 실행되기 때문에 빌드 단계에서 에러를 출력하게 할 수 있고, 바이트 코드를 생성할 수 있다.

* 자주 사용되는 예로 @Override, Lombok라이브러리의 @Getter, @Setter 등이 있다.


### QueryDel 설정방법

* build.gradle

```
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

* config

```
@Configuration
public class QueryDsl {

    // @PersistenceContext는 EntityManager를 빈으로 등록할때 사용한다.
    private EntityManager em;

    @Bean
    public JPAQueryFactory jpaQueryFactory(){
        return new JPAQueryFactory(this.em);
    }
}

// 스프링은 싱글톤 기반으로 동작하기에 빈은 모든 쓰레드가 공유한다.
// EntityManager를 빈으로 등록하면 사용할때 여러 쓰레드가 동시에 접근해 동시성 문제가 발생해 EntityManger를 공유하면 안된다.
// 그러나 @PersistenceContext를 사용해 EntityManager를 주입받으면 주입받은 EntityManager를 proxy로 감싸고
// 이후 EntityManager 호출 마다 proxy를 통해 EntityManager를 생성해 Thread-Safe를 보장한다.
// 즉, 각 요청에 대해 독립적인 EntityManager가 만들어진다.
```

* repository

```
// QueryDsl을 위한 커스텀 인터페이스. 사용할 QueryDsl을 위한 기초를 작성한다.
public interface MemberRepositoryCustom {

    List<MemberEntity> getMembers();

    MemberEntity getMember(Long id);
}

// 커스텀 인터페이스를 다중 상속할 기존 repository
// 다중 인터페이스 상속으로 기존의 crud와 커스텀해 사용할 QueryDsl을 모두 사용할 수 있다. 
public interface MemberRepository extends JpaRepository<MemberEntity, Long>, MemberRepositoryCustom {
}

// 커스텀 인터페이스를 구현한 클래스. 사용할 QueryDsl을 구현한다.
@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MemberEntity> getMembers() {

        QMemberEntity qMember = QMemberEntity.memberEntity;

        return queryFactory.selectFrom(qMember).fetch(); // fetch() 사용리 리스트로 결과를 반환한다.
    }

    @Override
    public MemberEntity getMember(Long id) {

        QMemberEntity qMember = QMemberEntity.memberEntity;

        return queryFactory.select(qMember)
                           .from(qMember)
                           .where(qMember.id.eq(id))
                           .fetchOne();
    }
}


// 이후 service클래스에서 기존 repository사용하듯이 사용하면 된다.
```

### QueryDsl 리턴타입

* 체인의 마지막에 사용해 리턴 타입을 바꿀 수 있다.

* fetch() - 쿼리 결과를 리스트로 반환한다. 데이터가 없다면 빈 리스트를 반환한다.

* fetchOne() - 단건을 조회할때 사용한다. 결과가 없다면 null을 반환하고 둘 이상이면 NonUniqueResultException을 발생시킨다.

* fetchFirst() - 결과 중 첫번째 행을 가져온다. limit(1).fetchOne()과 같은 표현이다.



### QueryDsl 페이징

* 
