package librarysolution.library.member.service;

import jakarta.servlet.http.HttpSession;
import librarysolution.library.member.dto.MemberRequest;
import librarysolution.library.member.dto.MemberResponse;
import librarysolution.library.member.model.Member;
import librarysolution.library.member.model.Role;
import librarysolution.library.member.repository.MemberRepository;
import librarysolution.library.utility.CommonUtils;
import org.apache.commons.lang3.RandomStringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;

    private static final int DUPLICATE = 0;
    private static final int NOT_DUPLICATE = 1;
    private static final int PASSWORD_MATCH = 1;
    private static final int PASSWORD_NOT_MATCH = 0;

    //== UserResponse builder method ==//
    public MemberResponse dtoBuilder(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build();
    }

    //== dto -> entity ==//
    public Member dtoToEntity(MemberRequest member) {
        return Member.builder()
                .id(member.getId())
                .email(member.getEmail())
                .password(member.getPassword())
                .auth(member.getAuth())
                .nickname(member.getNickname())
                .build();
    }

    //== entity -> dto1 - detail ==//
    public MemberResponse entityToDtoDetail(Member member) {

        if (CommonUtils.isNull(member)) {
            return null;
        }
        return dtoBuilder(member);
    }

    //== entity -> dto2 - list ==//
    public List<MemberResponse> entityToDtoList(List<Member> memberList) {
        List<MemberResponse> dtoList = new ArrayList<>();

        for (Member member : memberList) {
            dtoList.add(dtoBuilder(member));
        }

        return dtoList;
    }

    //== 무작위 닉네임 생성 - 숫자 + 문자 ==//
    public String makeRandomNickname() {
        return RandomStringUtils.randomAlphanumeric(5);
    }

    //== 이메일 중복 검증 ==//
    public int checkDuplicateEmail(String email) {
        Member member = memberRepository.findByEmail(email);

        if (CommonUtils.isNull(member)) {
            return NOT_DUPLICATE;
        }
        return DUPLICATE;
    }

    //== 닉네임 중복 검증 ==//
    public int checkDuplicateNickname(String nickname) {
        Member member = memberRepository.findByNickname(nickname);

        if (CommonUtils.isNull(member)) {
            return NOT_DUPLICATE;
        }
        return DUPLICATE;
    }

    //== 비밀번호 복호화 ==//
    public int checkPasswordMatching(String inputPassword, String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if(encoder.matches(inputPassword, password)) {
            return PASSWORD_MATCH;
        }
        return PASSWORD_NOT_MATCH;
    }

    //== spring context 반환 메소드(필수) ==//
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email);

        List<GrantedAuthority> authorities = new ArrayList<>();

        if (member.getAuth() == Role.ADMIN) {  //어드민 아이디 지정됨, 비밀번호는 회원가입해야함
            authorities.add(new SimpleGrantedAuthority(Role.ADMIN.getValue()));
        }
        authorities.add(new SimpleGrantedAuthority(Role.MEMBER.getValue()));

        return new User(
                member.getEmail(),
                member.getPassword(),
                authorities
        );
    }

    public Member getMemberEntity(String email) {
        return memberRepository.findByEmail(email);
    }

    public MemberResponse getMemberByEmail(String email) {
        return entityToDtoDetail(
                memberRepository.findByEmail(email)
        );
    }

    //== 전체 유저 리턴 for admin ==//
    public List<Member> getAllMemberForAdmin() {
        return memberRepository.findAll();
    }

    //== 이메일로 회원 검색 ==//
    public List<MemberResponse> getSearchByEmail(String email) {
        return entityToDtoList(
                memberRepository.searchByEmail(email)
        );
    }

    //== 이름으로 회원 검색 ==//
    public List<MemberResponse> getSearchByNickname(String nickname) {
        return entityToDtoList(memberRepository.searchByNickName(nickname));
    }

    //== 회원 가입 로직 ==//
    @Transactional
    public void joinUser(MemberRequest memberRequest) {
        //비밀번호 암호화
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        memberRequest.setPassword(passwordEncoder.encode(memberRequest.getPassword()));
        memberRequest.setAuth(Role.MEMBER);  //기본 권한 매핑
        memberRequest.setNickname(makeRandomNickname());  //무작위 닉네임 생성

        memberRepository.save(
                dtoToEntity(memberRequest)
        );
    }

    //== 로그인 - 세션과 컨텍스트홀더 사용 ==//
    @Transactional
    public void login(MemberRequest memberRequest, HttpSession httpSession)
            throws UsernameNotFoundException
    {
        String email = memberRequest.getEmail();
        String password = memberRequest.getPassword();
        Member member = memberRepository.findByEmail(email);

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(email, password);
        SecurityContextHolder.getContext().setAuthentication(token);
        httpSession.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );

        List<GrantedAuthority> authorities = new ArrayList<>();
        /*
        처음 어드민이 로그인을 하는경우 이메일로 판별해서 권한을 admin 으로 변경해주고
        그 다음부터 어드민이 업데이트 할때에는 auth 칼럼으로 판별해서 db 업데이트 하지않고,
        GrantedAuthority 만 업데이트 해준다.
         */
        if (member.getAuth() != Role.ADMIN && ("admin@library.com").equals(email)) {
            authorities.add(new SimpleGrantedAuthority(Role.ADMIN.getValue()));
            memberRepository.updateAuth(Role.ADMIN, memberRequest.getEmail());
        }

        if (member.getAuth() == Role.ADMIN) {
            authorities.add(new SimpleGrantedAuthority(Role.ADMIN.getValue()));
        }
        authorities.add(new SimpleGrantedAuthority(Role.MEMBER.getValue()));

        new User(
                member.getEmail(),
                member.getPassword(),
                authorities
        );
    }

    @Transactional
    public void updateNickname(String nickname, String email) {
        memberRepository.updateNickname(nickname, email);
    }

    @Transactional
    public void updateEmail(String oldEmail, String newEmail) {
        memberRepository.updateEmail(oldEmail, newEmail);
    }

    @Transactional
    public void updatePassword(Long id, String inputPassword) {
        //pw 암호화
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String newPassword =  passwordEncoder.encode(inputPassword);

        memberRepository.updatePassword(id, newPassword);
    }

    @Transactional
    public void updateLate(Long id) {
        memberRepository.updateLate(id);
    }

    @Transactional
    public void deleteUser(Long userId) {
        memberRepository.deleteById(userId);
    }
}
