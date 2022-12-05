package librarysolution.library.member.service;

import librarysolution.library.member.model.Member;
import librarysolution.library.member.model.Role;
import librarysolution.library.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return createUserDetails(memberRepository.findByEmail(email));
    }

    private UserDetails createUserDetails(Member member) {
        if (member.getAuth() == Role.ADMIN) {
            return User.builder()
                    .username(member.getUsername())
                    .password(member.getPassword())
                    .roles("ADMIN")
                    .build();
        } else {
            return User.builder()
                    .username(member.getUsername())
                    .password(member.getPassword())
                    .roles("MEMBER")
                    .build();
        }
    }
}
