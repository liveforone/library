# Public library Info System
> 도서관 도서 종합 관리 시스템(솔루션)

# 1. 기술 스택

# 2. 설명

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
/member/search/nickname - get
/member/nickname-post - post
/member/change-email - post
/member/change-password - post
/member/withdraw - post
/admin - get
```
## 연관관계



회원은 도서 검색 가능함
회원은 스스로 대출 가능함(하드웨어와 연결되어있단 가정)
회원은 대출시 비밀번호 입력해야함 + 어드민도 동일(어드민이 대신해줄때)
어드민은 도서 등록, 수정, 삭제 가능

도서 : bookCode, count, title, borrowCount
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

어드민은 책 등록 가능(그 이외 불가능)
책은 미리 데이터 10개 생성해서 넣기
insert into 이름 values(값1, 값2..);

도서 관리(도서 종합관리 시스템)
도서코드로 검색(책 검색할때)
status 연체도 존재 -> 회원 불이익 넣기

@EntityListeners(AuditingEntityListener.class)
@Column(columnDefinition = "integer default 0")

북코드는 서비스 로직에서 무작위로 문자 + 숫자 8자리로생성하기
연체업데이트 하면서 회원 등급 조정하기(마이북 들어갔을때)
도서 데이터 미리 넣기(문서화 까지)