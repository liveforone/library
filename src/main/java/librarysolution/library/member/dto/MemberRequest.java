package librarysolution.library.member.dto;

import librarysolution.library.member.model.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberRequest {

    private Long id;
    private String email;
    private String password;
    private Role auth;
    private String nickname;
}
