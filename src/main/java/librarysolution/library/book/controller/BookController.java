package librarysolution.library.book.controller;

import librarysolution.library.book.dto.BookRequest;
import librarysolution.library.book.dto.BookResponse;
import librarysolution.library.book.model.Book;
import librarysolution.library.book.service.BookService;
import librarysolution.library.utility.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;

    @GetMapping("/book")
    public ResponseEntity<?> allBookList(
            @PageableDefault(page = 0, size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) Pageable pageable
    ) {
        Page<BookResponse> bookList = bookService.getAllBook(pageable);

        return ResponseEntity.ok(bookList);
    }

    @GetMapping("/book/search/title")
    public ResponseEntity<?> bookSearchByTitlePage(
            @PageableDefault(page = 0, size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) Pageable pageable,
            @RequestParam("title") String title
    ) {
        Page<BookResponse> searchList =
                bookService.getSearchListByTitle(title, pageable);

        return ResponseEntity.ok(searchList);
    }

    @GetMapping("/book/search/book-code")
    public ResponseEntity<?> bookSearchByBookCodePage(
            @PageableDefault(page = 0, size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) Pageable pageable,
            @RequestParam("bookCode") String bookCode
    ) {
        Page<BookResponse> searchList =
                bookService.getSearchListByBookCode(bookCode, pageable);

        return ResponseEntity.ok(searchList);
    }

    @GetMapping("/book/post")
    public ResponseEntity<?> bookPostPage() {
        return ResponseEntity.ok("책 등록 페이지 입니다.");
    }

    @PostMapping("/book/post")
    public ResponseEntity<?> bookPost(
            @RequestBody BookRequest bookRequest
    ) {
        Long bookId = bookService.saveBook(bookRequest);
        log.info("책 등록 성공");

        String url = "/book/" + bookId;
        HttpHeaders httpHeaders = CommonUtils.makeHeader(url);

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }

    @GetMapping("/book/{id}")
    public ResponseEntity<?> bookDetailPage(@PathVariable("id") Long id) {
        BookResponse book = bookService.getBookResponse(id);

        if (CommonUtils.isNull(book)) {
            return ResponseEntity.ok("책이 없어 조회가 불가능합니다.");
        }

        return ResponseEntity.ok(book);
    }

    @GetMapping("/book/edit/{id}")
    public ResponseEntity<?> bookEditPage(@PathVariable("id") Long id) {
        BookResponse book = bookService.getBookResponse(id);

        if (CommonUtils.isNull(book)) {
            return ResponseEntity.ok("책이 없어서 수정이 불가능합니다.");
        }

        return ResponseEntity.ok(book);
    }

    @PostMapping("/book/edit/{id}")
    public ResponseEntity<?> bookEdit(
            @PathVariable("id") Long id,
            @RequestBody BookRequest bookRequest
    ) {
        Book book = bookService.getBookDetail(id);

        if (CommonUtils.isNull(book)) {
            return ResponseEntity.ok("책이 없어서 수정이 불가능합니다.");
        }

        bookService.editBook(bookRequest, id);
        log.info("도서 수정 성공");

        String url = "/book/" + id;
        HttpHeaders httpHeaders = CommonUtils.makeHeader(url);

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }

    @PostMapping("/book/delete/{id}")
    public ResponseEntity<?> bookDelete(@PathVariable("id") Long id) {
        bookService.deleteBook(id);
        log.info("도서 삭제 성공");

        String url = "/book";
        HttpHeaders httpHeaders = CommonUtils.makeHeader(url);

        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .headers(httpHeaders)
                .build();
    }
}
