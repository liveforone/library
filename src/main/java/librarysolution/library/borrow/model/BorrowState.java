package librarysolution.library.borrow.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BorrowState {

    BORROW("STATE_BORROW"),
    RETURN("STATE_RETURN"),
    OVERDUE("STATE_OVERDUE");

    private String value;
}
