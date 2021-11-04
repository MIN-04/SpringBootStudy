package spring.study.Member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import spring.study.Member.application.MemberLoginService;
import spring.study.Member.domain.services.CustomUserDetailsService;
import spring.study.common.auth.providers.JwtTokenProvider;
import spring.study.common.exceptions.CustomException;
import spring.study.common.responses.ResponseMessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static spring.study.common.enums.ErrorCode.FAIL_LOGIN;
import static spring.study.common.enums.ErrorCode.NOT_EXIST_MEMBER;
import static spring.study.common.enums.SuccessCode.SUCCESS_LOGIN;
import static spring.study.common.paths.LoginUrl.*;

@WebMvcTest(MemberLoginController.class)
@ExtendWith(MockitoExtension.class)
@DisplayName("[Controller - login] SNS 로그인 Test")
class MemberSNSLoginControllerTest {
    @MockBean
    MemberLoginService memberLoginService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("[socialLoginType()] SNS redirect url 찾기 성공")
    void successFindRedirectUrl() throws Exception {
        //given
        String redirectUrl = "https://accounts.google.com/o/oauth2/v2/auth?scope=email";

        given(memberLoginService.findSocialRedirectUrl(any())).willReturn(redirectUrl);

        //when
        String resultUrl = mockMvc.perform(get(LOGIN_ROOT_PATH + LOGIN_SOCIAL_TYPE, "google")
                .contentType(APPLICATION_JSON))
                .andReturn().getResponse().getRedirectedUrl();

        //then
        assertThat(resultUrl).isEqualTo(redirectUrl);

    }

    @Test
    @DisplayName("[callBack()] SNS 로그인으로 jwt token 생성 성공")
    void successCreateToken() throws Exception {
        //given
        String code = "4ldwdt";
        String jwtToken = "jwtToken";

        ResponseMessage rm = ResponseMessage.builder()
                .httpStatus(SUCCESS_LOGIN.getHttpStatus())
                .message(SUCCESS_LOGIN.getSuccessMsg())
                .resultData(jwtToken)
                .build();

        given(memberLoginService.loginSNS(any(), any())).willReturn(jwtToken);

        //when
        //then
        mockMvc.perform(get(LOGIN_ROOT_PATH + LOGIN_SOCIAL_CALLBACK + "?code=" + code, "google")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(rm)))
                .andDo(print());
    }

    @Test
    @DisplayName("SNS 로그인 실패 - FAIL_LOGIN 예외 발생")
    void failLoginException() throws Exception {
        //given
        String code = "4ldwdt";

        ResponseMessage rm = ResponseMessage.builder()
                .httpStatus(FAIL_LOGIN.getHttpStatus())
                .message(FAIL_LOGIN.getErrorMsg())
                .build();

        given(memberLoginService.loginSNS(any(), any())).willThrow(new CustomException(FAIL_LOGIN));

        //when
        //then
        mockMvc.perform(get(LOGIN_ROOT_PATH + LOGIN_SOCIAL_CALLBACK + "?code=" + code, "google"))
                .andExpect(status().is5xxServerError())
                .andExpect(content().string(mapper.writeValueAsString(rm)))
                .andDo(print());
    }

    @Test
    @DisplayName("SNS 로그인 실패 - 회원이 존재하지 않을 때")
    void notExistMember() throws Exception {
        //given
        String code = "4ldwdt";

        ResponseMessage rm = ResponseMessage.builder()
                .httpStatus(NOT_EXIST_MEMBER.getHttpStatus())
                .message(NOT_EXIST_MEMBER.getErrorMsg())
                .build();

        given(memberLoginService.loginSNS(any(), any())).willThrow(new CustomException(NOT_EXIST_MEMBER));

        //when
        //then
        mockMvc.perform(get(LOGIN_ROOT_PATH + LOGIN_SOCIAL_CALLBACK + "?code=" + code, "google"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(mapper.writeValueAsString(rm)))
                .andDo(print());
    }


}