package librarysolution.library.borrow.dto;

import librarysolution.library.borrow.model.BorrowState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowResponse {

    private Long id;
    private String member;
    private String book;
    private BorrowState borrowState;
    private LocalDate borrowDate;
}
