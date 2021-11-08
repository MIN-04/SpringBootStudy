package spring.study.Member.domain.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import spring.study.Member.infraStructure.rest.dto.GoogleOAuthResponseDTO;
import spring.study.Member.infraStructure.rest.dto.GoogleUserInfo;
import spring.study.common.enums.SocialLoginType;
import spring.study.common.exceptions.CustomException;

import static spring.study.common.enums.ErrorCode.FAIL_LOGIN;

@Service
@RequiredArgsConstructor
public class SocialOauthService {

    private final SocialOauthFactory socialOauthFactory;

    /**
     * AccessToken을 요청하는 메서드
     * 21.11.05 피드백 (11.08 수정완료)
     * MemberLoginService 서비스 단의 로직이 길면 의심해볼만 하다. 분리하기 (예외처리)
     * -> SocialOauthService로 로직 분리했다.
     */
    public ResponseEntity<GoogleOAuthResponseDTO> getAccessToken(String type, String code) {
        SocialLoginType socialLoginType = SocialLoginType.valueOf(type.toUpperCase());

        SocialOauth socialOauth = socialOauthFactory.findSocialOauthType(socialLoginType);

        ResponseEntity<GoogleOAuthResponseDTO> accessTokenResponse = socialOauth.requestAccessToken(code);

        if(accessTokenResponse.getStatusCode() == HttpStatus.OK) {
            return accessTokenResponse;
        }else {
            throw new CustomException(FAIL_LOGIN);
        }
    }

    /**
     * SNS User 정보를 요청하는 메서드
     * 21.11.05 피드백 (11.08 수정완료)
     * MemberLoginService 서비스 단의 로직이 길면 의심해볼만 하다. 분리하기 (예외처리)
     * -> SocialOauthService로 로직 분리했다.
     */
    public ResponseEntity<GoogleUserInfo> getUserInfo(String type, GoogleOAuthResponseDTO dto){
        SocialLoginType socialLoginType = SocialLoginType.valueOf(type.toUpperCase());

        SocialOauth socialOauth = socialOauthFactory.findSocialOauthType(socialLoginType);

        ResponseEntity<GoogleUserInfo> userInfoResponse = socialOauth.requestUserInfo(dto);

        if(userInfoResponse.getStatusCode() == HttpStatus.OK) {
            return userInfoResponse;
        }else {
            throw new CustomException(FAIL_LOGIN);
        }
    }

    /**
     * Social RedirectURL 불러오는 메서드
     */
    public String getRedirectUrl(String type) {
        SocialLoginType socialLoginType = SocialLoginType.valueOf(type.toUpperCase());

        SocialOauth socialOauth = socialOauthFactory.findSocialOauthType(socialLoginType);
        return socialOauth.getOauthRedirectUrl();
    }

}
