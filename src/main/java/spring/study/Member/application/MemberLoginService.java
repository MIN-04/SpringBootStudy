package spring.study.Member.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spring.study.Member.domain.aggregates.Member;
import spring.study.Member.domain.commands.MemberCommand;
import spring.study.Member.infraStructure.repository.MemberJPARepository;
import spring.study.Member.infraStructure.rest.OAuthToken;
import spring.study.common.auth.providers.JwtTokenProvider;
import spring.study.common.auth.providers.SnsTokenProvider;

@Slf4j
@Service
public class MemberLoginService {

    private final MemberJPARepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final SnsTokenProvider snsTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MemberLoginService(MemberJPARepository memberRepository, JwtTokenProvider jwtTokenProvider, SnsTokenProvider snsTokenProvider, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.snsTokenProvider = snsTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 로컬 회원 로그인
     */
    public String loginLocal(MemberCommand command) {
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

    /**
     * SNS 회원 로그인
     */
    public OAuthToken loginSNS(String code) {
        return snsTokenProvider.createToken(code);
    }

}
