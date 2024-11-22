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
