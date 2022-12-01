package librarysolution.library.book.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bookCode;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int count;  //재고 수량

    @Column(columnDefinition = "integer default 0")
    private int borrowCount;  //대출 수

    @Builder
    public Book(
            Long id,
            String bookCode,
            String title,
            int count,
            int borrowCount
    ) {
        this.id = id;
        this.bookCode = bookCode;
        this.title = title;
        this.count = count;
        this.borrowCount = borrowCount;
    }
}
