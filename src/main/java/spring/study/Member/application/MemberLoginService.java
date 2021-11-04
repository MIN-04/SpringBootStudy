package spring.study.Member.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spring.study.Member.domain.aggregates.Member;
import spring.study.Member.domain.commands.MemberCommand;
import spring.study.Member.domain.services.SocialOauthService;
import spring.study.Member.infraStructure.repository.MemberJPARepository;
import spring.study.Member.infraStructure.rest.dto.GoogleOAuthResponseDTO;
import spring.study.Member.infraStructure.rest.dto.GoogleUserInfo;
import spring.study.common.auth.providers.JwtTokenProvider;
import spring.study.common.exceptions.CustomException;

import static spring.study.common.enums.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberLoginService {

    private final MemberJPARepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final SocialOauthService socialOauthService;

    /**
     * 로컬 회원 로그인
     */
    public String loginLocal(MemberCommand command) {
        Member member = Member.builder()
                .email(command.getEmail())
                .memberBasicInfo(command.getBasicInfo())
                .build();

        Member result = memberRepository.findByEmail(member.getEmail())
                .orElseThrow(() -> new CustomException(NOT_EXIST_MEMBER)); //회원이 존재하지 않습니다.

        if (!passwordEncoder.matches(member.getPassword(), result.getPassword()))
            throw new CustomException(WRONG_PASSWORD); //잘못된 비밀번호 입니다.

        return jwtTokenProvider.createToken(member.getEmail(), member.getRoles());
    }

    /**
     * SNS 회원 로그인
     * 21.10.27 피드백 (11.04 수정 완료)
     * if의 else 부분은 실패인데 Controller는 항상 성공코드를 넣는다.
     * -> 실패 시 예외처리
     */
    public String loginSNS(String socialLoginType, String code) {

        //AccessToken 요청
        ResponseEntity<GoogleOAuthResponseDTO> accessTokenResponse = socialOauthService.getAccessToken(socialLoginType, code);

        GoogleOAuthResponseDTO accessTokenDto;

        if(accessTokenResponse.getStatusCode() == HttpStatus.OK) {
            accessTokenDto = accessTokenResponse.getBody();
        }else {
            throw new CustomException(FAIL_LOGIN);
        }
        log.info("[MemberLoginService - loginSNS()] Access Token = {}", accessTokenDto.getAccessToken());

        //AccessToken으로 회원 정보 요청
        ResponseEntity<GoogleUserInfo> userInfoResponse = socialOauthService.getUserInfo(socialLoginType, accessTokenDto);

        GoogleUserInfo googleUserInfo;
        if(accessTokenResponse.getStatusCode() == HttpStatus.OK) {
            googleUserInfo = userInfoResponse.getBody();
        }else {
            throw new CustomException(FAIL_LOGIN);
        }
        log.info("[MemberLoginService - loginSNS()] Google User Email = {}", googleUserInfo.getEmail());

        Member member = memberRepository.findByEmail(googleUserInfo.getEmail())
                .orElseThrow(() -> new CustomException(NOT_EXIST_MEMBER)); //회원이 존재하지 않습니다.

        return jwtTokenProvider.createToken(member.getEmail(), member.getRoles());
    }

    /**
     * Social RedirectURL 불러오는 메서드
     */
    public String findSocialRedirectUrl(String type) {
        return socialOauthService.getRedirectUrl(type);
    }
}
