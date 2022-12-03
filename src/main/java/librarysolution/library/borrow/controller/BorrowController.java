package librarysolution.library.borrow.controller;

import librarysolution.library.book.model.Book;
import librarysolution.library.book.service.BookService;
import librarysolution.library.borrow.dto.BorrowResponse;
import librarysolution.library.borrow.model.Borrow;
import librarysolution.library.borrow.service.BorrowService;
import librarysolution.library.member.dto.MemberResponse;
import librarysolution.library.member.model.Member;
import librarysolution.library.member.service.MemberService;
import librarysolution.library.utility.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BorrowController {

    private final BorrowService borrowService;
    private final MemberService memberService;
    private final BookService bookService;

    private static final int PASSWORD_MATCH = 1;
    private static final int CANT_BORROW = 10;
    private static final int LATE = 1;

    @GetMapping("/borrow/by-name/{name}")
    public ResponseEntity<?> borrowListByName(@PathVariable("name") String name) {
        List<BorrowResponse> borrowList = borrowService.getBorrowListByName(name);

        if (CommonUtils.isNull(borrowList)) {
            return ResponseEntity.ok("해당 이름의 회원이 없습니다.");
        }

        return ResponseEntity.ok(borrowList);
    }

    @GetMapping("/borrow/book-id/{bookId}")
    public ResponseEntity<?> borrowListByBookId(@PathVariable("bookId") Long bookId) {
        List<BorrowResponse> borrowList = borrowService.getBorrowListByBookId(bookId);

        if (CommonUtils.isNull(borrowList)) {
            return ResponseEntity.ok("해당 아이디의 책이 없습니다.");
        }

        return ResponseEntity.ok(borrowList);
    }

    @GetMapping("/borrow/{id}")
    public ResponseEntity<?> borrowDetailPage(
            @PathVariable("id") Long id,
            Principal principal
    ) {
        Member member = memberService.getMemberEntity(principal.getName());
        int checkLate = borrowService.checkBorrowState(id);

        if (checkLate == LATE) {  //연체일때
            memberService.updateLate(member.getId());  //연체횟수 증가
        }

        Borrow borrow = borrowService.getBorrowDetail(id);

        return ResponseEntity.ok(
                borrowService.entityToDtoDetail(borrow)
        );
    }

    @PostMapping("/borrow/post/{bookId}")
    public ResponseEntity<?> bookBorrow(
            @RequestBody String password,
            @PathVariable("bookId") Long bookId,
            Principal principal
    ) {
        Member member = memberService.getMemberEntity(principal.getName());
        Book book = bookService.getBookDetail(bookId);

        int checkPasswordMatching = memberService.checkPasswordMatching(
                password,
                member.getPassword()
        );

        if (checkPasswordMatching != PASSWORD_MATCH) {
            return ResponseEntity.ok("회원 비밀번호가 다릅니다.");
        }

        if (member.getLate() >= CANT_BORROW) {
            return ResponseEntity.ok("회원님은 연체가 잦아 더이상 대출이 불가능합니다.");
        }

        if (book.getBorrowCount() >= book.getCount()) {
            return ResponseEntity.ok("전 수량이 모두 대출되어 대출이 불가능합니다.");
        }

        Long borrowId = borrowService.saveBorrow(principal.getName(), bookId);
        bookService.updateBorrowCount(bookId);
        log.info("도서 대출 성공");


        String url = "/borrow/" + borrowId;
        HttpHeaders httpHeaders = CommonUtils.makeHeader(url);

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }

    @PostMapping("/borrow/return/{id}")
    public ResponseEntity<?> borrowReturn(@PathVariable("id") Long id) {
        borrowService.borrowReturn(id);
        log.info("반납 성공");

        String url = "/book";
        HttpHeaders httpHeaders = CommonUtils.makeHeader(url);

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }
}
