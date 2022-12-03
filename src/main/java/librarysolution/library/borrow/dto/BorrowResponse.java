package librarysolution.library.borrow.dto;

import librarysolution.library.borrow.model.BorrowState;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class BorrowResponse {

    private Long id;
    private String member;
    private String book;
    private BorrowState borrowState;
    private LocalDate borrowDate;

    @Builder
    public BorrowResponse(
            Long id,
            String member,
            String book,
            BorrowState borrowState,
            LocalDate borrowDate
    ) {
        this.id = id;
        this.member = member;
        this.book = book;
        this.borrowState = borrowState;
        this.borrowDate = borrowDate;
    }
}
