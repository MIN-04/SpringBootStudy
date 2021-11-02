package spring.study.Member.domain.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import spring.study.Member.infraStructure.rest.dto.GoogleOAuthResponseDTO;
import spring.study.Member.infraStructure.rest.dto.GoogleUserInfo;
import spring.study.common.enums.SocialLoginType;

@Service
@RequiredArgsConstructor
public class SocialOauthService {

    private final SocialOauthFactory socialOauthFactory;

    /**
     * AccessToken을 요청하는 메서드
     */
    public ResponseEntity<GoogleOAuthResponseDTO> getAccessToken(String type, String code) {
        SocialLoginType socialLoginType = SocialLoginType.valueOf(type.toUpperCase());

        SocialOauth socialOauth = socialOauthFactory.findSocialOauthType(socialLoginType);

        return socialOauth.requestAccessToken(code);
    }

    /**
     * SNS User 정보를 요청하는 메서드
     */
    public ResponseEntity<GoogleUserInfo> getUserInfo(String type, GoogleOAuthResponseDTO dto){
        SocialLoginType socialLoginType = SocialLoginType.valueOf(type.toUpperCase());

        SocialOauth socialOauth = socialOauthFactory.findSocialOauthType(socialLoginType);

        return socialOauth.requestUserInfo(dto);
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
