package librarysolution.library.borrow.repository;

import librarysolution.library.borrow.model.Borrow;
import librarysolution.library.borrow.model.BorrowState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BorrowRepository extends JpaRepository<Borrow, Long> {

    @Query("select b from Borrow b join fetch b.book join fetch b.member where b.id = :id")
    Borrow findOneById(@Param("id") Long id);

    @Query("select b from Borrow b join fetch b.member join fetch b.book o where o.id = :id")
    List<Borrow> findBorrowListByBookId(@Param("id") Long id);

    @Query("select b from Borrow b join fetch b.book join fetch b.member m where m.nickname = :nickname")
    List<Borrow> findBorrowListByMemberName(@Param("nickname") String nickname);

    @Modifying
    @Query("update Borrow b set b.borrowState = :borrowState where b.id = :id")
    void updateBorrowState(@Param("borrowState") BorrowState borrowState, @Param("id") Long id);
}
