package spring.study.Member.domain.services;

import org.springframework.http.ResponseEntity;
import spring.study.Member.infraStructure.rest.dto.GoogleOAuthResponseDTO;
import spring.study.Member.infraStructure.rest.dto.GoogleUserInfo;
import spring.study.common.enums.SocialLoginType;

public interface SocialOauth {

    /**
     * Social Enum타입의 값을 반환
     */
    SocialLoginType getSocialOauthName();
    
    /**
     * 각 Social Login 페이지로 Redirect 처리할 URL Build
     * 사용자로부터 로그인 요청을 받아 Social Login Server 인증용 code 요청
     */
    String getOauthRedirectUrl();

    /**
     * Token 요청을 하는 메서드
     */
    ResponseEntity<GoogleOAuthResponseDTO> requestAccessToken(String code);

    /**
     * 사용자 정보를 요청하는 메서드
     */
    ResponseEntity<GoogleUserInfo> requestUserInfo(GoogleOAuthResponseDTO dto);
}
