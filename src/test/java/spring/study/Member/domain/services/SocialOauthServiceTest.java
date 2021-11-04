package spring.study.Member.domain.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import spring.study.Member.infraStructure.rest.dto.GoogleOAuthResponseDTO;
import spring.study.Member.infraStructure.rest.dto.GoogleUserInfo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Domain Service] SNS Service Test")
class SocialOauthServiceTest {

    @InjectMocks
    SocialOauthService socialOauthService;

    @Mock
    SocialOauthFactory socialOauthFactory;

    @Mock
    SocialOauth socialOauth;

    @Test
    @DisplayName("AccessToken 요청 성공")
    void successRequestAccessToken() {
        //given
        String type = "google";
        String code = "4ldwdt";

        //getAccessToken() 예상 반환값
        GoogleOAuthResponseDTO googleDto = GoogleOAuthResponseDTO.builder()
                .accessToken("accessToken")
                .build();
        ResponseEntity<GoogleOAuthResponseDTO> responseEntity = ResponseEntity.ok(googleDto);

        given(socialOauthFactory.findSocialOauthType(any())).willReturn(socialOauth);
        given(socialOauth.requestAccessToken(any())).willReturn(responseEntity);

        //when
        ResponseEntity<GoogleOAuthResponseDTO> resultResponse = 
                socialOauthService.getAccessToken(type, code);

        //then
        assertThat(resultResponse).isEqualTo(responseEntity);
    }

    @Test
    @DisplayName("SNS User 정보 요청 성공")
    void successGetUserInfo() {
        //given
        String type = "google";

        //getUserInfo()의 파라미터
        GoogleOAuthResponseDTO dto = GoogleOAuthResponseDTO.builder()
                .accessToken("accessToken")
                .build();

        //getUserInfo()의 예상 반환값
        GoogleUserInfo userInfo = GoogleUserInfo.builder()
                .email("hong@google.com")
                .build();
        ResponseEntity<GoogleUserInfo> responseEntity = ResponseEntity.ok(userInfo);

        given(socialOauthFactory.findSocialOauthType(any())).willReturn(socialOauth);
        given(socialOauth.requestUserInfo(any())).willReturn(responseEntity);

        //when
        ResponseEntity<GoogleUserInfo> resultResponse =
                socialOauthService.getUserInfo(type, dto);

        //then
        assertThat(resultResponse).isEqualTo(responseEntity);
    }

    @Test
    @DisplayName("Social RedirectURL 요청 성공")
    void successRequestRedirectUrl() {
        //given
        String redirectUrl = "https://accounts.google.com/o/oauth2/v2/auth?scope=email";

        given(socialOauthFactory.findSocialOauthType(any())).willReturn(socialOauth);
        given(socialOauth.getOauthRedirectUrl()).willReturn(redirectUrl);

        //when
        String resultUrl = socialOauthService.getRedirectUrl("google");

        //then
        assertThat(resultUrl).isEqualTo(redirectUrl);
    }

}