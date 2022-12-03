package librarysolution.library.book.repository;

import librarysolution.library.book.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("select b from Book b where b.id = :id")
    Book findOneById(@Param("id") Long id);

    @Query("select b from Book b where b.title like %:title%")
    Page<Book> searchByTitle(@Param("title") String title, Pageable pageable);

    @Query("select b from Book b where b.bookCode like %:bookCode%")
    Page<Book> searchByBookCode(@Param("bookCode") String bookCode, Pageable pageable);

    @Modifying
    @Query("update Book b set b.borrowCount = b.borrowCount + 1 where b.id = :id")
    void updateBorrowCount(@Param("id") Long id);
}
