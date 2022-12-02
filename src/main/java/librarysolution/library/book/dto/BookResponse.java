package librarysolution.library.book.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookResponse {

    private Long id;
    private String bookCode;
    private String title;
    private String writer;
    private int count;
    private int borrowCount;

    @Builder
    public BookResponse(
            Long id,
            String bookCode,
            String title,
            String writer,
            int count,
            int borrowCount
    ) {
        this.id = id;
        this.bookCode = bookCode;
        this.title = title;
        this.writer = writer;
        this.count = count;
        this.borrowCount = borrowCount;
    }
}