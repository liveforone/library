package librarysolution.library.borrow.model;

import jakarta.persistence.*;
import librarysolution.library.book.model.Book;
import librarysolution.library.member.model.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Borrow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private BorrowState borrowState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @CreatedDate
    @Column(updatable = false)
    private LocalDate borrowDate;

    @Builder
    public Borrow(
            Long id,
            BorrowState borrowState,
            Book book,
            Member member,
            LocalDate borrowDate
    ) {
        this.id = id;
        this.borrowState = borrowState;
        this.book = book;
        this.member = member;
        this.borrowDate = borrowDate;
    }
}
