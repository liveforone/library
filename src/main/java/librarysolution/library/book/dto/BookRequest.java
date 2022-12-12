package librarysolution.library.book.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookRequest {

    private Long id;
    private String bookCode;
    private String title;
    private String writer;
    private int count;
}
