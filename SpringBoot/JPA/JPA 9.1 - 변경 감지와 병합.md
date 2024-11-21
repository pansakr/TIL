### JPA의 변경 감지와 병합

* 데이터 수정 시 변경 감지를 사용하는것이 좋다

* 변경 감지

    - 영속 상태의 엔티티를 수정하면 커밋 시 변경사항을 감지해서 DB에 변경사항에 대한 update sql이 자동 실행되는 것

    - 준영속 상태의 엔티티에는 변경 감지가 동작하지 않는다

    *준영속 : 영속성 컨텍스트가 엔티티를 관리하지 않는 상태

* 병합(merge)

    - 준영속 상태의 엔티티를 영속 상태로 변경할 때 사용하는 기능

    - 준영속 상태의 엔티티를 영속 상태로 바꾸기 위한 병합의 내부 동작 방식
    
        1. merge(Member member) 실행. 준영속 엔티티를 인자로 가지고 있음
        
        2. 파라미터로 넘어온 준영속 엔티티의 식별자 값으로 1차 캐시에서 엔티티를 조회

        ```java
        Member member2 = memberRepository().findOne(member.getId);
        ```

            - 트랜잭션이 시작될 때 엔티티 매니저와 해당 엔티티 매니저가 관리하는 영속성 컨텍스트도 같이 생성되고, 트랜잭션이 종료될 때 엔티티 매니저가 종료되고 관리하는 영속성 컨텍스트도 같이 종료되는 구조

            - 그러므로 식별자를 통해 영속성 컨텍스트를 조회해도 트랜잭션이 새로 생성되서 엔티티 매니저와 해당 엔티티 매니저의 영속성 컨텍스트가 새로 생겼기 때문에 1차 캐시에는(영속성 컨텍스트) 식별자 값에 대응하는 엔티티가 없다

        3. 영속성 컨텍스트에 식별자에 대응하는 값이 없으므로 DB를 조회하고 그 값을 영속성 컨텍스트에 저장 후 반환
        
            - 조회한 데이터가 저장된 엔티티는 영속 상태의 엔티티가 되고, 영속 상태의 엔티티는 변경 감지가 동작함

        4. 조회해온 데이터를, set()을 호출해 인자로 받은 member 엔티티의 값으로 변경

            - 병합 내부에서의 변경 감지는 모든 필드를 변경하므로, member 엔티티의 필드 중 null 이 있다면 그대로 null 로 변경됨

        ```java
        .. merge(Member member1)

            Member member2 = memberRepository().findOne(member.getId);

            // member2 엔티티의 모든 필드가 변경됨
            // member1 의 필드 중 비어있거나 null 인 필드가 있다면 member2 의 해당 필드도 null 로 변경됨
            member2.setName(member1.getName());
            member2.set...
        ```
        
        5. 변경 시 변경 감지가 동작해서 커밋 시점에 DB에 변경 사항에 대한 update sql 실행
 

* 준영속 엔티티를 수정하는 방법

    - 변경 감지 기능 사용

        ```java
        // Book - 엔티티, BookForm - Book의 DTO

        // 컨트롤러
        // view 로부터 넘어오는 데이터를 BookForm 에 매핑, 데이터 중 Book의 Id 도 있음
        public String updateItem(@ModelAttribute("form") BookForm form){

            /**
            * Book 객체는 준영속 엔티티 -> 변경 감지가 일어나지 않음
            * Book 객체에는 이미 DB에 저장되었다가 조회된 식별자가 존재한다 (book_ID)
            * 이렇게 임의로 만들어낸 엔티티도 기존 식별자를 가지고 있으면 준영속 엔티티로 볼 수 있다
            */
            Book book = new Book();
            book.setId(form.getId());
            book.set..

            itemService.updateItem(book);
            return "redirect:/items";
        }

        // 서비스
        /**
         * 파라미터로 준영속 상태의 엔티티가 넘어옴 (book)
         * 같은 엔티티를(book) 조회해서 영속 상태로 만듬
         * 영속 상태의 엔티티를 수정해서 커밋 시 변경 감지 동작
        */
        @Transactional
        public void updateItem(Book book){ 
            Item findItem = itemRepository.findOne(book.getId());
            findItem.setPrice(book.getPrice());
            findItem.set..
        }
        ```
    
    - 병합 사용

        - merge() 사용시 내부적으로 위 방법와 비슷하게 동작

        - 그러나 변경 감지와 달리 모든 속성이 변경됨

        ```java
        // merge() 호출 시 member의 식별자로 DB 조회 후 영속 상태로 만들고, 변경 감지를 사용해 데이터를 변경
        // 그러나 merge() 내부에서 사용되는 변경 감지는 member의 모든 필드에 적용됨
        // 데이터가 없는 필드가 있다는 null 로 변경됨 
        merge(Member member)

        // 변경 감지 사용

        // 변경 감지 사용 시 엔티티의 원하는 필드만 변경 가능
        Member member1 = new Member();

        // Member 엔티티의 name 값만 변경
        member1.setName(..);
        ```
