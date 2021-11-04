package spring.study.Member.application;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import spring.study.Member.domain.aggregates.Member;
import spring.study.Member.domain.services.SocialOauthService;
import spring.study.Member.infraStructure.repository.MemberJPARepository;
import spring.study.Member.infraStructure.rest.dto.GoogleOAuthResponseDTO;
import spring.study.Member.infraStructure.rest.dto.GoogleUserInfo;
import spring.study.common.auth.providers.JwtTokenProvider;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

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
        //when
        //then
    }

    @Test
    @DisplayName("Social RedirectURL 불러오기 성공")
    void successFindSocialRedirectUrl() {

    }

}