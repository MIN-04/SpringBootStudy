package spring.study.Member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import spring.study.Member.application.MemberService;
import spring.study.Member.controller.dto.MemberRequestJoinDTO;
import spring.study.Member.domain.aggregates.Member;
import spring.study.Member.domain.services.CustomUserDetailsService;
import spring.study.Member.domain.valueObjects.MemberAddressInfo;
import spring.study.Member.domain.valueObjects.MemberBasicInfo;
import spring.study.common.auth.providers.JwtTokenProvider;
import spring.study.common.enums.ValidationMsgCode;
import spring.study.common.exceptions.CustomException;
import spring.study.common.responses.ResponseMessage;

import java.util.Collections;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static spring.study.common.enums.ErrorCode.DUPLICATED_MEMBER;
import static spring.study.common.enums.ErrorCode.FAIL_VALIDATE;
import static spring.study.common.enums.SuccessCode.SUCCESS_JOIN_MEMBER;
import static spring.study.common.enums.ValidationMsgCode.*;
import static spring.study.common.paths.MemberUrl.MEMBERS_NEW;
import static spring.study.common.paths.MemberUrl.MEMBER_ROOT_PATH;

@WebMvcTest(MemberController.class)
@ExtendWith(MockitoExtension.class)
@DisplayName("[Controller] ?????? ?????? Test")
class MemberControllerJoinMockTest {

    @MockBean
    private MemberService memberService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    //???????????? ????????? return ?????? ?????????
    static Member member;

    static MemberRequestJoinDTO dto;

    @BeforeAll
    static void setUp() {
        member = Member.builder()
                .id(1L)
                .email("hong@naver.com")
                .password("hong1!")
                .roles(Collections.singletonList("ROLE_MEMBER"))
                .memberBasicInfo(MemberBasicInfo.builder()
                        .name("?????????")
                        .mobileNum("010-1111-2222")
                        .gender("M")
                        .birth("001122")
                        .build())
                .memberAddressInfo(MemberAddressInfo.builder()
                        .address("Seoul")
                        .build())
                .build();

        dto = MemberRequestJoinDTO.builder()
                .email("hong@naver.com")
                .password("hong1!")
                .name("?????????")
                .address("Seoul")
                .mobileNum("010-1111-2222")
                .gender("M")
                .birth("001122")
                .build();
    }

    @Test
    @DisplayName("?????? ?????? ??????")
    void successJoin() throws Exception {

        //given
        //return response message
        ResponseMessage message = ResponseMessage.builder()
                .httpStatus(SUCCESS_JOIN_MEMBER.getHttpStatus())
                .message(SUCCESS_JOIN_MEMBER.getSuccessMsg())
                .resultData(member)
                .build();

        given(memberService.join(any())).willReturn(member);

        //when
        //then
        mockMvc.perform(post(MEMBER_ROOT_PATH + MEMBERS_NEW)
        .contentType(APPLICATION_JSON)
        .content(mapper.writeValueAsString(dto))
        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(message)))
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ????????? ?????? ???????????? ????????? ???")
    void duplicatedEmail() throws Exception {
        //given
        //return response message
        ResponseMessage message = ResponseMessage.builder()
                .httpStatus(DUPLICATED_MEMBER.getHttpStatus())
                .message(DUPLICATED_MEMBER.getErrorMsg())
                .build();

        given(memberService.join(any())).willThrow(new CustomException(DUPLICATED_MEMBER));

        //when
        //then
        mockMvc.perform(post(MEMBER_ROOT_PATH + MEMBERS_NEW)
                .accept(APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().string(mapper.writeValueAsString(message)))
                .andDo(print());

    }


    @DisplayName("?????? ?????? ?????? - validation ?????? X")
    @ParameterizedTest(name = "{index}: {5}")
    @MethodSource("invalidParameters")
    void failureValidation(String email, String password, String name, String mobile, ValidationMsgCode msgCode, String title) throws Exception {
        //given
        MemberRequestJoinDTO dtoVal = MemberRequestJoinDTO.builder()
                .email(email)
                .password(password)
                .name(name)
                .address("Seoul")
                .mobileNum(mobile)
                .gender("M")
                .birth("001122")
                .build();

        //return response message
        ResponseMessage message = ResponseMessage.builder()
                .httpStatus(FAIL_VALIDATE.getHttpStatus())
                .message(FAIL_VALIDATE.getErrorMsg())
                .detailMsg(msgCode.getValidationMsg())
                .build();

        //when
        //then
        mockMvc.perform(post(MEMBER_ROOT_PATH + MEMBERS_NEW)
                .accept(APPLICATION_JSON)
                .content(mapper.writeValueAsString(dtoVal))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(mapper.writeValueAsString(message)))
                .andDo(print());

    }

    static Stream<Arguments> invalidParameters() {
        final String EMAIL = "hong@naver.com";
        final String PASSWORD = "hong1!";
        final String NAME = "?????????";
        final String MOBILE = "010-1111-2222";

        return Stream.of(
                Arguments.of(null, PASSWORD, NAME, MOBILE, BLANK_VALIDATE, "???????????? null??? ???"),
                Arguments.of(" ", PASSWORD, NAME, MOBILE, BLANK_VALIDATE, "???????????? ????????? ???"),
                Arguments.of("hong", PASSWORD, NAME, MOBILE, EMAIL_NOT_MATCH, "????????? ????????? ????????? ???"),
                Arguments.of(EMAIL, null, NAME, MOBILE, BLANK_VALIDATE, "password??? null??? ???"),
                Arguments.of(EMAIL, " ", NAME, MOBILE, BLANK_VALIDATE, "password??? ????????? ???"),
                Arguments.of(EMAIL, "c1!", NAME, MOBILE, PASSWORD_NOT_MATCH, "password ????????? min(=4) ?????? ?????? ???"),
                Arguments.of(EMAIL, "hhhhhhhh1!", NAME, MOBILE, PASSWORD_NOT_MATCH, "password ????????? max(=8) ?????? ??? ???"),
                Arguments.of(EMAIL, "hhhhhh", NAME, MOBILE, PASSWORD_NOT_MATCH, "password ????????? ????????? ???"),
                Arguments.of(EMAIL, PASSWORD, null, MOBILE, BLANK_VALIDATE, "name??? null??? ???"),
                Arguments.of(EMAIL, PASSWORD, " ", MOBILE, BLANK_VALIDATE, "name??? ????????? ???"),
                Arguments.of(EMAIL, PASSWORD, "honggildong", MOBILE, NAME_NOT_MATCH, "name ????????? ????????? ???"),
                Arguments.of(EMAIL, PASSWORD, NAME, null, BLANK_VALIDATE, "??????????????? null??? ???"),
                Arguments.of(EMAIL, PASSWORD, NAME, " ", BLANK_VALIDATE, "??????????????? ????????? ???"),
                Arguments.of(EMAIL, PASSWORD, NAME, "010-1111-11", MOBILENUM_NOT_MATCH, "???????????? ????????? ?????? ???"),
                Arguments.of(EMAIL, PASSWORD, NAME, "015-1111-1111", MOBILENUM_NOT_MATCH, "???????????? ??????(010??? ?????? ??????)??? ????????? ???"),
                Arguments.of(EMAIL, PASSWORD, NAME, "01011111111", MOBILENUM_NOT_MATCH, "???????????? ??????(-)??? ????????? ???")
        );
    }

}