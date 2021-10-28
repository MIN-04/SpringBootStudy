package spring.study.Member.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spring.study.Member.domain.aggregates.Member;
import spring.study.Member.domain.commands.MemberCommand;
import spring.study.Member.infraStructure.repository.MemberJPARepository;
import spring.study.Member.infraStructure.rest.OAuthToken;
import spring.study.common.auth.SocialOauth;
import spring.study.common.auth.SocialOauthFactory;
import spring.study.common.auth.providers.JwtTokenProvider;
import spring.study.common.auth.providers.SnsTokenProvider;
import spring.study.common.enums.SocialLoginType;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberLoginService {

    private final MemberJPARepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final SnsTokenProvider snsTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final SocialOauthFactory socialOauthFactory;

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

    /**
     * Social RedirectURL 불러오는 메서드
     */
    public String findSocialRedirectUrl(String type) {
        SocialLoginType socialLoginType = SocialLoginType.valueOf(type);

        SocialOauth socialOauth = socialOauthFactory.findSocialOauthType(socialLoginType);
        return socialOauth.getOauthRedirectUrl();
    }
}
