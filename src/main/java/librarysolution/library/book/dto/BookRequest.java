package librarysolution.library.book.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookRequest {

    private Long id;
    private String bookCode;
    private String title;
    private String writer;
    private int count;
}
