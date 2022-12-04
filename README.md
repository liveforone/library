# Public library Info System
> 도서관 도서 종합 관리 시스템(솔루션)

# 1. 기술 스택
* Spring Boot 3.0.0
* Language : Java17
* DB : MySql
* ORM : Spring Data Jpa
* Spring Security
* LomBok
* Gradle
* Apache commons lang3

# 2. 설명
* 도서관의 도서 관리 시스템이다.
* 회원은 MEMBER, ADMIN으로 나뉘며
* 어드민은 모든것을, 일반 회원은 대출, 반납, 도서 검색이 가능하다.
* 대출 후 7일이 지나면 연체가 되고,
* 연체 횟수 10회 달성시 대출 불가능
* 대출 엔티티의 status는 총 3가지가 존재 : 대출, 연체, 반납
* 도서 검색으로 대출 수량을 확인 가능하며 대출수량이 재고 수량을 넘어가면 대출이 불가능함.

# 3. 설계
## ERD Diagram
![스크린샷(151)](https://user-images.githubusercontent.com/88976237/205427611-fefcdf19-1df9-418e-8181-455665e2eb2a.png)

## json body
### member
```
{
  "email": "yc1234@gmail.com",
  "password": "1234"
}
{
  "email": "admin@library.com",
  "password": "1234"
}
```
### book
```
{
    "title" : "어린왕자",
    "writer" : "생택쥐페리",
    "count" : 5
}
```
## API 설계
### member
```
/ - get
/member/signup - get/post
/member/login - get/post
/member/prohibition - get
/member/my-page - get
/member/search/email - get
/member/search/nickname - get
/member/nickname-post - post
/member/change-email - post
/member/change-password - post
/member/withdraw - post
/admin - get
```
### book
```
/book - get
/book/search/title - get
/book/search/book-code - get
/book/post - get/post
/book/{id} - get
/book/edit/{id} - get/post
/book/delete/{id} - post
```
### borrow
```
/borrow/by-name/{name} - get
/borrow/book-id/{bookId} - get
/borrow/{id}
/borrow/post/{bookId}
/borrow/return/{id} - post
```
## 연관관계
```
borrow ->  book, member (manyToOne) 단방향
```

# 4. 스타일 가이드
* 유저를 제외한 모든 객체의 [널체크](https://github.com/liveforone/study/blob/main/GoodCode/%EA%B0%9D%EC%B2%B4%20null%EC%B2%B4%ED%81%AC%EC%99%80%20%EC%A4%91%EB%B3%B5%EC%B2%B4%ED%81%AC.md) + 중복 체크
* 함수와 긴 변수의 경우 [줄바꿈 가이드](https://github.com/liveforone/study/blob/main/GoodCode/%EC%A4%84%EB%B0%94%EA%BF%88%EC%9C%BC%EB%A1%9C%20%EA%B0%80%EB%8F%85%EC%84%B1%20%ED%96%A5%EC%83%81.md)를 지켜 작성한다.
* 매직넘버는 전부 [상수화](https://github.com/liveforone/study/blob/main/GoodCode/%EB%A7%A4%EC%A7%81%EB%84%98%EB%B2%84%20%EC%83%81%EC%88%98%EB%A1%9C%20%ED%95%B4%EA%B2%B0.md)해서 처리한다.
* 분기문은 반드시 [게이트웨이](https://github.com/liveforone/study/blob/main/GoodCode/%EB%8D%94%20%EC%A2%8B%EC%9D%80%20%EB%B6%84%EA%B8%B0%EB%AC%B8.md) 스타일로 한다.
* entity -> dto 변환 편의메소드는 리스트나 페이징이 아닌 경우 컨트롤러에서 사용한다.
* [HttpHeader 처리 함수](https://github.com/liveforone/study/blob/main/GoodCode/HttpHeaders%20%EC%83%9D%EC%84%B1%20%ED%95%A8%EC%88%98.md)
* 스프링 시큐리티에서 권한 체크 필요한것만 매핑하고 나머지(anyRequest)는 authenticated 로 설정해 코드를 줄이고 가독성 향상한다.

# 5. 상세 설명
## 대출
* 대출을 하려면 책을 먼저 검색한다.
* 검색된 책을 클릭하여 detail(책 한개 게시글)로 진입한다.
* 대출한다.(검증하는 함수들이 작동되며 성공시 대출된다.)
## 반납
* 사용자의 이름 혹은 책 제목으로 대출들을 검색한다. 일반적으로 사용자의 이름으로 검색하는것이 권장된다.
* 클릭하여 대출 디테일(대출 한개 게시글)로 진입한다.
* 이때 연체인지 아닌지 계산이 들어가고 borrowState가 반영된 상태로 detail이 화면에 뜬다.
* 반납한다.
## 연체 테스트 데이터
* 대출후 7일 뒤부터는 연체 처리되도록 전략을 세웠다.
* 아래는 테스트 데이터 이다.
```
insert into borrow(borrow_date, borrow_state, book_id, member_id) values("2022-11-18", "BORROW", 1, 1);
멤버 회원가입 후에 해당 값 db에 넣고 
/borrow/1에 접속하면 BORROW가 아니라 OVERDUE로, 즉 연체로 변경되는것을 확인 할 수 있다.
```

# 6. 나의 고민
## DB 미리 삽입
* 아무래도 도서는 여러개이니 하나씩 어드민으로 로그인해서 post를 던지기 귀찮았다.
* sql의 이름은 data, 즉 data.sql 로 파일을 만들어야한다. 
* 스프링이 시작될때 자동으로 data.sql 파일을 실행하여준다.(hibernate에 로그가 찍히진 않음)
* 아래는 필요한 설정이다.
```
spring:
  jpa:
    defer-datasource-initialization: true
  sql:
    init:
      mode: always
```
## Null 체크 함수 생성
* java8부터 optional을 지원하여 NPE를 방지하도록 도와주고 있다.
* 하지만 optional은 성능 상 좋지 않다는 단점을 가지고 있으며,
* 이에 따라 optional을 사용할때에는 무작정 사용하는 것이 아닌 꼭 필요할때 사용하는 주의를 기울여야한다.
* 이에따라 매번 null을 분기처리하는 코드가 반복됬고, 이를 optional처럼 함수화 하여 처리해보자는 생각이 들었다.
* CommonUtils 라는 클래스를 선언하고 isNull()이라는 함수를 만들었다.
* 이 함수는 list와 문자열, 객체(일반적으로 엔티티)의 null을 체크한다.
* [정리한 글](https://github.com/liveforone/study/blob/main/GoodCode/%EA%B0%9D%EC%B2%B4%20null%EC%B2%B4%ED%81%AC%EC%99%80%20%EC%A4%91%EB%B3%B5%EC%B2%B4%ED%81%AC.md)
* 아래는 코드이다.
```
public static boolean isNull(Object obj) {

    //== 일반 객체 체크 ==//
    if(obj == null) {
        return true;
    }

    //== 문자열 체크 ==//
    if ((obj instanceof String) && (((String)obj).trim().length() == 0)) {
        return true;
    }

    //== 리스트 체크 ==//
    if (obj instanceof List) {
        return ((List<?>)obj).isEmpty();
    }

    return false;
}
```
## HttpHeaders 생성 함수
* 매번 컨트롤러에서 리다이렉트를 할때마다 HttpHeaders를 직접 생성 하는것이 반복되고 귀찮았다.
* 이 또한 CommonUtils에 makeHeader() 라는 함수를 만들어서 사용하는 것으로 해결하였다.
* [정리한 글](https://github.com/liveforone/study/blob/main/GoodCode/HttpHeaders%20%EC%83%9D%EC%84%B1%20%ED%95%A8%EC%88%98.md)
* 아래는 코드이다.
```
public static HttpHeaders makeHeader(String uri) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(URI.create(uri));

    return httpHeaders;
}

//호출 방법
String url = "/book/" + id;
HttpHeaders httpHeaders = CommonUtils.makeHeader(url);
```
## lazyinitializationexception
* 이러한 에러가 /borrow/{id} 에서 터졌다.
* 서비스 레벨에서 @Transactional 어노테이션이 작성된 메소드가 종료되면 Hibernate의 Session도 종료된다.
* FetchType.LAZY가 설정된 필드가 포함된 엔티티에 대해, 
* 컨트롤러 레벨에서 해당 필드를 조회할 때 Getter 메써드를 호출하고 실제 조회 쿼리가 실행된다. 
* 하지만 이미 Session이 종료된 상태이기 때문에 LazyInitializationException 예외가 발생하게 되는 것이다.
* EAGER 로 바꾸면 해결이 되지만 더 좋은것은 fetch join이다.
* 페치조인으로 해결했다.(jpql 작성이 귀찮아도 꼭 페치조인은 사용하자. 여러모로 좋다.)