package spring.study.Member.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spring.study.Member.domain.aggregates.Member;
import spring.study.Member.domain.commands.MemberCommand;
import spring.study.Member.infraStructure.repository.MemberJPARepository;
import spring.study.common.security.JwtTokenProvider;

@Slf4j
@Service
public class MemberLoginService {

    private final MemberJPARepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MemberLoginService(MemberJPARepository memberRepository, JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 회원 로그인
     */
    public String login(MemberCommand command) {
        Member member = Member.builder()
                .email(command.getEmail())
                .memberBasicInfo(command.getBasicInfo())
                .build();

        Member result = memberRepository.findByEmail(member.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));

        if (!passwordEncoder.matches(member.getMemberBasicInfo().getPassword(), result.getPassword()))
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");

        return jwtTokenProvider.createToken(member.getUsername(), member.getRoles());
    }

}
