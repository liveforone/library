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
* 연체 횟수 5번마다 회원 bad등급이 증가하는 구조이다.
* 대출 엔티티의 status는 총 3가지가 존재 : 대출, 연체, 반납
* 도서 검색으로 대출 수량을 확인 가능하며 대출수량이 재고 수량을 넘어가면 대출이 불가능함.

# 3. 설계
## ERD Diagram

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
## 연관관계

# 4. 스타일 가이드
* 유저를 제외한 모든 객체의 Null체크 + 중복 체크, [널체크 링크](https://github.com/liveforone/study/blob/main/GoodCode/%EA%B0%9D%EC%B2%B4%20null%EC%B2%B4%ED%81%AC%EC%99%80%20%EC%A4%91%EB%B3%B5%EC%B2%B4%ED%81%AC.md)
* 함수와 긴 변수의 경우 [줄바꿈 가이드](https://github.com/liveforone/study/blob/main/GoodCode/%EC%A4%84%EB%B0%94%EA%BF%88%EC%9C%BC%EB%A1%9C%20%EA%B0%80%EB%8F%85%EC%84%B1%20%ED%96%A5%EC%83%81.md)를 지켜 작성한다.
* 매직넘버는 전부 상수화해서 처리한다.[상수화](https://github.com/liveforone/study/blob/main/GoodCode/%EB%A7%A4%EC%A7%81%EB%84%98%EB%B2%84%20%EC%83%81%EC%88%98%EB%A1%9C%20%ED%95%B4%EA%B2%B0.md)
* 분기문은 반드시 gate-way 스타일로 한다. [게이트웨이](https://github.com/liveforone/study/blob/main/GoodCode/%EB%8D%94%20%EC%A2%8B%EC%9D%80%20%EB%B6%84%EA%B8%B0%EB%AC%B8.md)
* entity -> dto 변환 편의메소드는 리스트나 페이징이 아닌 경우 컨트롤러에서 사용한다.
* [httpheader 처리 함수](https://github.com/liveforone/study/blob/main/GoodCode/HttpHeaders%20%EC%83%9D%EC%84%B1%20%ED%95%A8%EC%88%98.md)

# 5. 나의 고민
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


회원은 스스로 대출 가능함(하드웨어와 연결되어있단 가정)
회원은 대출시 비밀번호 입력해야함 + 어드민도 동일(어드민이 대신해줄때)
어드민은 도서 등록, 수정, 삭제 가능

대출 : 도서id, 멤버id, 상태(대출, 반납, 연체), 날짜

대출 갯수에는 대출과 연체가 영향을 미침
반남하면 대출에는 기록이 남지만 대출갯수 마이너스하는 함수 호출해서
대출갯수 줄이기

연체는 7일을 기준으로 함. 7일을 넘기면 status 연체로 변경
날짜는 localdate 사용, localdatetime 안쓰는 이유는 몇시에 
대출하던지 대출한 날짜(date)기준으로 + 7일임.
연체시 멤버의 연체 횟수 플러스 카운트 하고
멤버 검색시 회원 연체횟수 출력
연체횟수 5회마다 멤버 bad등급 증가
bad 등급 최고점 일경우 대출 불가능
함수를 매번 호출하는게아니라 
10이 아니면 리턴값 없애서 매번 update 쿼리 내보내지 않고
처리함.(매번 업데이트 쿼리 내보는 짓 하지 않음)

status 연체도 존재 -> 회원 불이익 넣기

@EntityListeners(AuditingEntityListener.class)
@Column(columnDefinition = "integer default 0")

@PageableDefault(page = 0, size = 10)
@SortDefault.SortDefaults({
@SortDefault(sort = "id", direction = Sort.Direction.DESC)
}) Pageable pageable


책 대출하기
책 반납하기
연체업데이트 하면서 회원 등급 조정하기(마이북 들어갔을때)