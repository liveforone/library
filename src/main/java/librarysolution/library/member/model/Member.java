package librarysolution.library.member.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(length = 100, nullable = false)
    private String password;

    @Enumerated(value = EnumType.STRING)
    private Role auth;

    private String nickname;

    @Column(columnDefinition = "integer default 0")
    private int late;

    @Builder
    public Member(
            Long id,
            String email,
            String password,
            Role auth,
            String nickname,
            int late
    ) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.auth = auth;
        this.nickname = nickname;
        this.late = late;
    }
}
