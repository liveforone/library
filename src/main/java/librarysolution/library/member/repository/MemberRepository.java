package librarysolution.library.member.repository;

import librarysolution.library.member.model.Member;
import librarysolution.library.member.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByEmail(String email);

    @Query("select m from Member m where m.nickname = :nickname")
    Member findByNickname(@Param("nickname") String nickname);

    @Query("select m from Member m where m.email like %:email%")
    List<Member> searchByEmail(@Param("email") String email);

    @Query("select m from Member m where m.nickname like %:nickname%")
    List<Member> searchByNickName(@Param("nickname") String nickname);

    @Modifying
    @Query("update Member m set m.auth = :auth where m.email = :email")
    void updateAuth(@Param("auth") Role auth, @Param("email") String email);

    @Modifying
    @Query("update Member m set m.nickname = :nickname where m.email = :email")
    void updateNickname(@Param("nickname") String nickname, @Param("email") String email);

    @Modifying
    @Query("update Member m set m.email = :newEmail where m.email = :oldEmail")
    void updateEmail(@Param("oldEmail") String oldEmail, @Param("newEmail") String newEmail);

    @Modifying
    @Query("update Member m set m.password = :password where m.id = :id")
    void updatePassword(@Param("id") Long id, @Param("password") String password);

    @Modifying
    @Query("update Member m set m.late = m.late + 1 where m.id = :id")
    void updateLate(@Param("id") Long id);
}
