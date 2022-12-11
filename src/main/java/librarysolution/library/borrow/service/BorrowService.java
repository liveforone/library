package librarysolution.library.borrow.service;

import librarysolution.library.book.model.Book;
import librarysolution.library.book.repository.BookRepository;
import librarysolution.library.borrow.dto.BorrowResponse;
import librarysolution.library.borrow.model.Borrow;
import librarysolution.library.borrow.model.BorrowState;
import librarysolution.library.borrow.repository.BorrowRepository;
import librarysolution.library.member.model.Member;
import librarysolution.library.member.repository.MemberRepository;
import librarysolution.library.utility.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BorrowService {

    private final BorrowRepository borrowRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;

    //== BorrowResponse builder method ==//
    public BorrowResponse dtoBuilder(Borrow borrow) {
        return BorrowResponse.builder()
                .id(borrow.getId())
                .member(borrow.getMember().getNickname())
                .book(borrow.getBook().getTitle())
                .borrowState(borrow.getBorrowState())
                .borrowDate(borrow.getBorrowDate())
                .build();
    }

    //== entity ->  dto 편의메소드1 - 리스트 형식 ==//
    public List<BorrowResponse> entityToDtoList(List<Borrow> borrowList) {
        return borrowList
                .stream()
                .map(this::dtoBuilder)
                .collect(Collectors.toList());
    }

    //== entity ->  dto 편의메소드2 - detail 형식 ==//
    public BorrowResponse entityToDtoDetail(Borrow borrow) {
        if (CommonUtils.isNull(borrow)) {
            return null;
        }
        return dtoBuilder(borrow);
    }

    public Borrow getBorrowDetail(Long id) {
        return borrowRepository.findOneById(id);
    }

    public List<BorrowResponse> getBorrowListByBookId(Long bookId) {
        return entityToDtoList(
                borrowRepository.findBorrowListByBookId(bookId)
        );
    }

    public List<BorrowResponse> getBorrowListByName(String name) {
        return entityToDtoList(
                borrowRepository.findBorrowListByMemberName(name)
        );
    }

    @Transactional
    public Long saveBorrow(String email, Long bookId) {
        Member member = memberRepository.findByEmail(email);
        Book book = bookRepository.findOneById(bookId);

        Borrow borrow = Borrow.builder()
                .borrowState(BorrowState.BORROW)
                .member(member)
                .book(book)
                .build();

        return borrowRepository.save(borrow).getId();
    }

    @Transactional
    public int checkBorrowState(Long id) {
        Borrow borrow = borrowRepository.findOneById(id);

        LocalDate lateDay = borrow.getBorrowDate().plusDays(7);
        LocalDate now = LocalDate.now();

        if (now.isAfter(lateDay)) {
            borrowRepository.updateBorrowState(BorrowState.OVERDUE, id);
            return 1;
        }
        return 0;
    }

    @Transactional
    public void borrowReturn(Long id) {
        borrowRepository.updateBorrowState(BorrowState.RETURN, id);
    }
}
