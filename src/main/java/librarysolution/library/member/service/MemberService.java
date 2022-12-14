package librarysolution.library.member.service;

import librarysolution.library.jwt.JwtTokenProvider;
import librarysolution.library.jwt.TokenInfo;
import librarysolution.library.member.dto.MemberRequest;
import librarysolution.library.member.dto.MemberResponse;
import librarysolution.library.member.model.Member;
import librarysolution.library.member.model.Role;
import librarysolution.library.member.repository.MemberRepository;
import librarysolution.library.utility.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

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
        return memberList
                .stream()
                .map(this::dtoBuilder)
                .collect(Collectors.toList());
    }

    //== ????????? ????????? ?????? - ?????? + ?????? ==//
    public String makeRandomNickname() {
        return RandomStringUtils.randomAlphanumeric(5);
    }

    //== ????????? ?????? ?????? ==//
    public int checkDuplicateEmail(String email) {
        Member member = memberRepository.findByEmail(email);

        if (CommonUtils.isNull(member)) {
            return NOT_DUPLICATE;
        }
        return DUPLICATE;
    }

    //== ????????? ?????? ?????? ==//
    public int checkDuplicateNickname(String nickname) {
        Member member = memberRepository.findByNickname(nickname);

        if (CommonUtils.isNull(member)) {
            return NOT_DUPLICATE;
        }
        return DUPLICATE;
    }

    //== ???????????? ????????? ==//
    public int checkPasswordMatching(String inputPassword, String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if(encoder.matches(inputPassword, password)) {
            return PASSWORD_MATCH;
        }
        return PASSWORD_NOT_MATCH;
    }

    public Member getMemberEntity(String email) {
        return memberRepository.findByEmail(email);
    }

    public MemberResponse getMemberByEmail(String email) {
        return entityToDtoDetail(
                memberRepository.findByEmail(email)
        );
    }

    //== ?????? ?????? ?????? for admin ==//
    public List<Member> getAllMemberForAdmin() {
        return memberRepository.findAll();
    }

    //== ???????????? ?????? ?????? ==//
    public List<MemberResponse> getSearchByEmail(String email) {
        return entityToDtoList(
                memberRepository.searchByEmail(email)
        );
    }

    //== ???????????? ?????? ?????? ==//
    public List<MemberResponse> getSearchByNickname(String nickname) {
        return entityToDtoList(
                memberRepository.searchByNickName(nickname)
        );
    }

    //== ?????? ?????? ?????? ==//
    @Transactional
    public void joinUser(MemberRequest memberRequest) {
        //???????????? ?????????
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        memberRequest.setPassword(passwordEncoder.encode(memberRequest.getPassword()));

        if (Objects.equals(memberRequest.getEmail(), "admin@library.com")) {
            memberRequest.setAuth(Role.ADMIN);
        } else {
            memberRequest.setAuth(Role.MEMBER);
        }
        memberRequest.setNickname(makeRandomNickname());  //????????? ????????? ??????

        memberRepository.save(
                dtoToEntity(memberRequest)
        );
    }

    //== ????????? - ????????? ?????????????????? ?????? ==//
    @Transactional
    public TokenInfo login(MemberRequest memberRequest) {

        String email = memberRequest.getEmail();
        String password = memberRequest.getPassword();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                email,
                password
        );
        Authentication authentication = authenticationManagerBuilder
                .getObject()
                .authenticate(authenticationToken);

        return jwtTokenProvider.generateToken(authentication);
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
        //pw ?????????
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
