package spring.study.Member.application;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import spring.study.Member.domain.aggregates.Member;
import spring.study.Member.domain.services.SocialOauthService;
import spring.study.Member.infraStructure.repository.MemberJPARepository;
import spring.study.Member.infraStructure.rest.dto.GoogleOAuthResponseDTO;
import spring.study.Member.infraStructure.rest.dto.GoogleUserInfo;
import spring.study.common.auth.providers.JwtTokenProvider;
import spring.study.common.exceptions.CustomException;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static spring.study.common.enums.ErrorCode.FAIL_LOGIN;
import static spring.study.common.enums.ErrorCode.NOT_EXIST_MEMBER;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Service - login] SNS 로그인")
class MemberSNSLoginServiceTest {

    @InjectMocks
    MemberLoginService memberLoginService;

    @Mock
    MemberJPARepository memberRepository;
    @Mock
    JwtTokenProvider jwtTokenProvider;
    @Mock
    SocialOauthService socialOauthService;

    static String socialLoginType;
    static String code;
    static GoogleOAuthResponseDTO googleDTO;
    static ResponseEntity<GoogleOAuthResponseDTO> accessTokenResponse;
    static GoogleUserInfo googleUserInfo;
    static ResponseEntity<GoogleUserInfo> userInfoResponse;
    static Member findedMember;

    @BeforeAll
    static void setUp() {
        socialLoginType = "google";
        code = "4ldwdt";

        googleDTO = GoogleOAuthResponseDTO.builder()
                .accessToken("accessToken")
                .build();

        accessTokenResponse = ResponseEntity.ok(googleDTO);

        googleUserInfo = GoogleUserInfo.builder()
                .email("hong@google.com")
                .build();

        userInfoResponse = ResponseEntity.ok(googleUserInfo);

        findedMember = Member.builder()
                .email("hong@google.com")
                .roles(Collections.singletonList("MEMBER"))
                .build();
    }

    @Test
    @DisplayName("SNS 회원 로그인 성공")
    void successLoginSNS() {
        //given
        given(socialOauthService.getAccessToken(anyString(), anyString())).willReturn(accessTokenResponse);
        given(socialOauthService.getUserInfo(anyString(), any())).willReturn(userInfoResponse);
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(findedMember));
        given(jwtTokenProvider.createToken(any(), any())).willReturn("token");

        //when
        String resultToken = memberLoginService.loginSNS(socialLoginType, code);

        //then
        assertThat(resultToken).isEqualTo("token");
    }

    @Test
    @DisplayName("SNS 회원 로그인 실패 - AccessToken 요청 실패")
    void failRequestAccessToken() {
        //given
        ResponseEntity<GoogleOAuthResponseDTO> failResponse =
                new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); //500error

        given(socialOauthService.getAccessToken(anyString(), anyString())).willReturn(failResponse);

        //when
        //then
        assertThat(failResponse.getStatusCode()).isNotEqualTo(HttpStatus.OK);
        CustomException exception = assertThrows(CustomException.class,
                () -> memberLoginService.loginSNS(socialLoginType, code));
        assertThat(exception.getErrorCode()).isEqualTo(FAIL_LOGIN);
    }

    @Test
    @DisplayName("SNS 회원 로그인 실패 - 회원 정보 요청 실패")
    void failRequestUserInfo() {
        //given
        ResponseEntity<GoogleUserInfo> failResponse =
                new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); //500error

        given(socialOauthService.getAccessToken(anyString(), anyString())).willReturn(accessTokenResponse);
        given(socialOauthService.getUserInfo(anyString(), any())).willReturn(failResponse);

        //when
        //then
        assertThat(failResponse.getStatusCode()).isNotEqualTo(HttpStatus.OK);
        CustomException exception = assertThrows(CustomException.class,
                () -> memberLoginService.loginSNS(socialLoginType, code));
        assertThat(exception.getErrorCode()).isEqualTo(FAIL_LOGIN);
    }

    @Test
    @DisplayName("SNS 회원 로그인 실패 - 회원이 로컬 DB에 존재하지 않을 때")
    void notExistMember() {
        //given
        given(socialOauthService.getAccessToken(anyString(), anyString())).willReturn(accessTokenResponse);
        given(socialOauthService.getUserInfo(anyString(), any())).willReturn(userInfoResponse);
        given(memberRepository.findByEmail(anyString())).willThrow(new CustomException(NOT_EXIST_MEMBER));

        //when
        //then
        CustomException exception = assertThrows(CustomException.class,
                () -> memberLoginService.loginSNS(socialLoginType, code));
        assertThat(exception.getErrorCode()).isEqualTo(NOT_EXIST_MEMBER);
    }

    @Test
    @DisplayName("Social RedirectURL 불러오기 성공")
    void successFindSocialRedirectUrl() {
        //given
        String redirectUrl = "https://accounts.google.com/o/oauth2/v2/auth?scope=email";

        given(socialOauthService.getRedirectUrl(anyString())).willReturn(redirectUrl);

        //when
        String resultUrl = memberLoginService.findSocialRedirectUrl(socialLoginType);

        //then
        assertThat(resultUrl).isEqualTo(redirectUrl);
    }

}