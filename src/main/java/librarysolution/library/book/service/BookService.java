package librarysolution.library.book.service;

import librarysolution.library.book.dto.BookRequest;
import librarysolution.library.book.dto.BookResponse;
import librarysolution.library.book.model.Book;
import librarysolution.library.book.repository.BookRepository;
import librarysolution.library.utility.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;

    //== 랜덤 bookCode 생성 함수 ==//
    public String makeBookCode() {
        return RandomStringUtils.randomAlphanumeric(8);
    }

    //== dto builder ==//
    public BookResponse dtoBuilder(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .bookCode(book.getBookCode())
                .title(book.getTitle())
                .writer(book.getWriter())
                .count(book.getCount())
                .borrowCount(book.getBorrowCount())
                .build();
    }

    //== dto -> entity ==//
    public Book dtoToEntity(BookRequest request) {
        return Book.builder()
                .id(request.getId())
                .bookCode(makeBookCode())
                .title(request.getTitle())
                .writer(request.getWriter())
                .count(request.getCount())
                .build();
    }

    //== entity ->  dto 편의메소드1 - 페이징 형식 ==//
    public Page<BookResponse> entityToDtoPage(Page<Book> bookList) {
        return bookList.map(this::dtoBuilder);
    }

    //== entity -> dto 편의메소드2 - 엔티티 하나 ==//
    public BookResponse entityToDtoDetail(Book book) {
        if (CommonUtils.isNull(book)) {
            return null;
        }
        return dtoBuilder(book);
    }

    public Book getBookDetail(Long id) {
        return bookRepository.findOneById(id);
    }

    public Page<BookResponse> getAllBook(Pageable pageable) {
        return entityToDtoPage(
                bookRepository.findAll(pageable)
        );
    }

    public Page<BookResponse> getSearchListByTitle(String title, Pageable pageable) {
        return entityToDtoPage(
                bookRepository.searchByTitle(title, pageable)
        );
    }

    public Page<BookResponse> getSearchListByBookCode(String bookCode, Pageable pageable) {
        return entityToDtoPage(
                bookRepository.searchByBookCode(bookCode, pageable)
        );
    }

    @Transactional
    public Long saveBook(BookRequest bookRequest) {
        return bookRepository.save(
                dtoToEntity(bookRequest)
        ).getId();
    }

    @Transactional
    public void editBook(BookRequest bookRequest, Long id) {
        bookRequest.setId(id);

        bookRepository.save(
                dtoToEntity(bookRequest)
        );
    }

    @Transactional
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}
