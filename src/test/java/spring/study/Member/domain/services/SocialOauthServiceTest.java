package spring.study.Member.domain.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import spring.study.Member.infraStructure.rest.dto.GoogleOAuthResponseDTO;
import spring.study.Member.infraStructure.rest.dto.GoogleUserInfo;
import spring.study.common.exceptions.CustomException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static spring.study.common.enums.ErrorCode.FAIL_LOGIN;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Domain Service] SNS Service Test")
class SocialOauthServiceTest {

    @InjectMocks
    SocialOauthService socialOauthService;

    @Mock
    SocialOauthFactory socialOauthFactory;

    @Mock
    SocialOauth socialOauth;

    static String type;
    static String code;
    static GoogleOAuthResponseDTO googleDto;

    @BeforeAll
    static void setUp() {
        type = "google";
        code = "4ldwdt";

        googleDto = GoogleOAuthResponseDTO.builder()
                .accessToken("accessToken")
                .build();
    }

    @Test
    @DisplayName("AccessToken 요청 성공")
    void successRequestAccessToken() {
        //given
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
    @DisplayName("AccessToken 요청 실패")
    void failRequestAccessToken() {
        //given
        ResponseEntity<GoogleOAuthResponseDTO> failResponse =
                new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); //500error

        given(socialOauthFactory.findSocialOauthType(any())).willReturn(socialOauth);
        given(socialOauth.requestAccessToken(code)).willReturn(failResponse);

        //when
        //then
        CustomException exception = assertThrows(CustomException.class,
                () -> socialOauthService.getAccessToken(type,  code));
        assertThat(exception.getErrorCode()).isEqualTo(FAIL_LOGIN);
        assertThat(exception.getErrorCode().getHttpStatus()).isEqualTo(failResponse.getStatusCode());
    }

    @Test
    @DisplayName("SNS User 정보 요청 성공")
    void successGetUserInfo() {
        //given
        //getUserInfo()의 예상 반환값
        GoogleUserInfo userInfo = GoogleUserInfo.builder()
                .email("hong@google.com")
                .build();
        ResponseEntity<GoogleUserInfo> responseEntity = ResponseEntity.ok(userInfo);

        given(socialOauthFactory.findSocialOauthType(any())).willReturn(socialOauth);
        given(socialOauth.requestUserInfo(any())).willReturn(responseEntity);

        //when
        ResponseEntity<GoogleUserInfo> resultResponse =
                socialOauthService.getUserInfo(type, googleDto);

        //then
        assertThat(resultResponse).isEqualTo(responseEntity);
    }

    @Test
    @DisplayName("SNS User 정보 요청 실패")
    void failGetUserInfo() {
        //given
        ResponseEntity<GoogleUserInfo> failResponse = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        given(socialOauthFactory.findSocialOauthType(any())).willReturn(socialOauth);
        given(socialOauth.requestUserInfo(any())).willReturn(failResponse);

        //when
        //then
        CustomException exception = assertThrows(CustomException.class,
                () -> socialOauthService.getUserInfo(type, googleDto));
        assertThat(exception.getErrorCode()).isEqualTo(FAIL_LOGIN);
        assertThat(exception.getErrorCode().getHttpStatus()).isEqualTo(failResponse.getStatusCode());
    }

    @Test
    @DisplayName("Social RedirectURL 요청 성공")
    void successRequestRedirectUrl() {
        //given
        String redirectUrl = "https://accounts.google.com/o/oauth2/v2/auth?scope=email";

        given(socialOauthFactory.findSocialOauthType(any())).willReturn(socialOauth);
        given(socialOauth.getOauthRedirectUrl()).willReturn(redirectUrl);

        //when
        String resultUrl = socialOauthService.getRedirectUrl(type);

        //then
        assertThat(resultUrl).isEqualTo(redirectUrl);
    }

}