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
import spring.study.Member.controller.dto.MemberRequestModifyDTO;
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
import static spring.study.common.enums.ErrorCode.*;
import static spring.study.common.enums.SuccessCode.SUCCESS_MODIFY_MEMBER;
import static spring.study.common.enums.ValidationMsgCode.*;
import static spring.study.common.paths.MemberUrl.MEMBERS_PATH;
import static spring.study.common.paths.MemberUrl.MEMBER_ROOT_PATH;

@WebMvcTest(MemberController.class)
@ExtendWith(MockitoExtension.class)
@DisplayName("[Controller] 회원 수정 Test")
class MemberControllerModifyMockTest {

    @MockBean
    private MemberService memberService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    //회원 수정 성공시 return 되는 결과값
    static Member member;

    static MemberRequestModifyDTO dto;

    @BeforeAll
    static void setUp() {
        member = Member.builder()
                .id(1L)
                .email("hong@naver.com")
                .password("hong1!")
                .roles(Collections.singletonList("ROLE_MEMBER"))
                .memberBasicInfo(MemberBasicInfo.builder()
                        .name("홍길동")
                        .mobileNum("010-1111-2222")
                        .gender("M")
                        .birth("001122")
                        .build())
                .memberAddressInfo(MemberAddressInfo.builder()
                        .address("Seoul")
                        .build())
                .build();

        dto = MemberRequestModifyDTO.builder()
                .id(1L)
                .email("hong@naver.com")
                .password("hong1!")
                .name("홍길동")
                .address("Seoul")
                .mobileNum("010-1111-2222")
                .gender("M")
                .birth("001122")
                .build();
    }

    @Test
    @DisplayName("회원 수정 성공")
    @WithMockUser(roles = "MEMBER")
    void successModify() throws Exception {

        //given
        //return response message
        ResponseMessage message = ResponseMessage.builder()
                .httpStatus(SUCCESS_MODIFY_MEMBER.getHttpStatus())
                .message(SUCCESS_MODIFY_MEMBER.getSuccessMsg())
                .resultData(member)
                .build();

        given(memberService.modify(any())).willReturn(member);

        //when
        //then
        mockMvc.perform(post(MEMBER_ROOT_PATH + MEMBERS_PATH)
        .contentType(APPLICATION_JSON)
        .content(mapper.writeValueAsString(dto))
        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(message)))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 수정 실패 - 수정할 회원이 없을 경우")
    @WithMockUser(roles = "MEMBER")
    void notFoundMember() throws Exception {
        //given
        ResponseMessage message = ResponseMessage.builder()
                .httpStatus(NOT_EXIST_MEMBER.getHttpStatus())
                .message(NOT_EXIST_MEMBER.getErrorMsg())
                .build();

        given(memberService.modify(any())).willThrow(new CustomException(NOT_EXIST_MEMBER));

        //when
        //then
        mockMvc.perform(post(MEMBER_ROOT_PATH + MEMBERS_PATH)
                .accept(APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(mapper.writeValueAsString(message)))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 수정 실패 - 이메일 또는 전화번호 중복일 때")
    @WithMockUser(roles = "MEMBER")
    void failureDuplicated() throws Exception {
        //given
        //return response message
        ResponseMessage message = ResponseMessage.builder()
                .httpStatus(DUPLICATED_MEMBER.getHttpStatus())
                .message(DUPLICATED_MEMBER.getErrorMsg())
                .build();

        given(memberService.modify(any())).willThrow(new CustomException(DUPLICATED_MEMBER));

        //when
        //then
        mockMvc.perform(post(MEMBER_ROOT_PATH + MEMBERS_PATH)
                .accept(APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().string(mapper.writeValueAsString(message)))
                .andDo(print());

    }


    @DisplayName("회원 수정 실패 - validation 통과 X")
    @WithMockUser(roles = "MEMBER")
    @ParameterizedTest(name = "{index}: {6}")
    @MethodSource("invalidParameters")
    void failureValidation(Long id, String email, String password, String name, String mobile, ValidationMsgCode msgCode, String title) throws Exception {
        //given
        MemberRequestModifyDTO dtoVal = MemberRequestModifyDTO.builder()
                .id(id)
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
        mockMvc.perform(post(MEMBER_ROOT_PATH + MEMBERS_PATH)
                .accept(APPLICATION_JSON)
                .content(mapper.writeValueAsString(dtoVal))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(mapper.writeValueAsString(message)))
                .andDo(print());

    }

    static Stream<Arguments> invalidParameters() {
        final Long ID = 1L;
        final String EMAIL = "hong@naver.com";
        final String PASSWORD = "hong1!";
        final String NAME = "홍길동";
        final String MOBILE = "010-1111-2222";

        return Stream.of(
                Arguments.of(null, EMAIL, PASSWORD, NAME, MOBILE, BLANK_VALIDATE, "id가 null일 때"),
                Arguments.of(ID, null, PASSWORD, NAME, MOBILE, BLANK_VALIDATE, "이메일이 null일 때"),
                Arguments.of(ID, " ", PASSWORD, NAME, MOBILE, BLANK_VALIDATE, "이메일이 빈칸일 때"),
                Arguments.of(ID, "hong", PASSWORD, NAME, MOBILE, EMAIL_NOT_MATCH, "이메일 형식이 안맞을 때"),
                Arguments.of(ID, EMAIL, null, NAME, MOBILE, BLANK_VALIDATE, "password가 null일 때"),
                Arguments.of(ID, EMAIL, " ", NAME, MOBILE, BLANK_VALIDATE, "password가 빈칸일 때"),
                Arguments.of(ID, EMAIL, "c1!", NAME, MOBILE, PASSWORD_NOT_MATCH, "password 길이가 min(=4) 보다 짧을 때"),
                Arguments.of(ID, EMAIL, "hhhhhhhh1!", NAME, MOBILE, PASSWORD_NOT_MATCH, "password 길이가 max(=8) 보다 길 때"),
                Arguments.of(ID, EMAIL, "hhhhhh", NAME, MOBILE, PASSWORD_NOT_MATCH, "password 형식이 안맞을 때"),
                Arguments.of(ID, EMAIL, PASSWORD, null, MOBILE, BLANK_VALIDATE, "name이 null일 때"),
                Arguments.of(ID, EMAIL, PASSWORD, " ", MOBILE, BLANK_VALIDATE, "name이 빈칸일 때"),
                Arguments.of(ID, EMAIL, PASSWORD, "honggildong", MOBILE, NAME_NOT_MATCH, "name 형식이 안맞을 때"),
                Arguments.of(ID, EMAIL, PASSWORD, NAME, null, BLANK_VALIDATE, "전화번호가 null일 때"),
                Arguments.of(ID, EMAIL, PASSWORD, NAME, " ", BLANK_VALIDATE, "전화번호가 빈칸일 때"),
                Arguments.of(ID, EMAIL, PASSWORD, NAME, "010-1111-11", MOBILENUM_NOT_MATCH, "전화번호 길이가 짧을 때"),
                Arguments.of(ID, EMAIL, PASSWORD, NAME, "015-1111-1111", MOBILENUM_NOT_MATCH, "전화번호 형식(010등 외의 번호)이 안맞을 때"),
                Arguments.of(ID, EMAIL, PASSWORD, NAME, "01011111111", MOBILENUM_NOT_MATCH, "전화번호 형식(-)이 안맞을 때")
        );
    }

}