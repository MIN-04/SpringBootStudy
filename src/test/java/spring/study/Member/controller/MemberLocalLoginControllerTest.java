package spring.study.Member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import spring.study.Member.application.MemberLoginService;
import spring.study.Member.controller.dto.MemberRequestLoginDTO;
import spring.study.Member.domain.services.CustomUserDetailsService;
import spring.study.common.auth.providers.JwtTokenProvider;
import spring.study.common.enums.ErrorCode;
import spring.study.common.exceptions.CustomException;
import spring.study.common.responses.ResponseMessage;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static spring.study.common.enums.ErrorCode.NOT_EXIST_MEMBER;
import static spring.study.common.enums.ErrorCode.WRONG_PASSWORD;
import static spring.study.common.enums.SuccessCode.SUCCESS_LOGIN;
import static spring.study.common.paths.LoginUrl.LOGIN_ROOT_PATH;

@WebMvcTest(MemberLoginController.class)
@ExtendWith(MockitoExtension.class)
@DisplayName("[Controller - login] 로컬 로그인 Test")
class MemberLocalLoginControllerTest {
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
    @DisplayName("로컬 로그인 성공")
    void successLocalLogin() throws Exception {
        //given
        MemberRequestLoginDTO loginDTO = MemberRequestLoginDTO.builder()
                .email("hong@ddd.com")
                .password("hong1!")
                .build();

        ResponseMessage rm = ResponseMessage.builder()
                .httpStatus(SUCCESS_LOGIN.getHttpStatus())
                .message(SUCCESS_LOGIN.getSuccessMsg())
                .resultData("token")
                .build();

        given(memberLoginService.loginLocal(any())).willReturn("token");

        //when
        //then
        mockMvc.perform(post(LOGIN_ROOT_PATH)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(rm)))
                .andDo(print());
    }

    @DisplayName("로컬 로그인 실패 - 예외 발생")
    @ParameterizedTest(name = "{index}: {3}")
    @MethodSource("exceptionError")
    void failLocalLogin(String email, String password, ErrorCode errorCode, String testTitle) throws Exception {
        //given
        MemberRequestLoginDTO loginDTO = MemberRequestLoginDTO.builder()
                .email(email)
                .password(password)
                .build();

        ResponseMessage rm = ResponseMessage.builder()
                .httpStatus(errorCode.getHttpStatus())
                .message(errorCode.getErrorMsg())
                .build();

        given(memberLoginService.loginLocal(any())).willThrow(new CustomException(errorCode));

        //when
        //then
        mockMvc.perform(post(LOGIN_ROOT_PATH)
        .accept(APPLICATION_JSON)
        .contentType(APPLICATION_JSON)
        .content(mapper.writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(mapper.writeValueAsString(rm)))
                .andDo(print());

    }

    static Stream<Arguments> exceptionError() {
        final String EMAIL = "hong@ddd.com";
        final String PASSWORD = "hong1!";

        return Stream.of(
                Arguments.of("park@ddd.com", PASSWORD, NOT_EXIST_MEMBER, "회원이 존재하지 않을 때"),
                Arguments.of(EMAIL, "park1!", WRONG_PASSWORD, "비밀번호가 일치하지 않을 때")
        );
    }
}