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
import spring.study.Member.application.MemberService;
import spring.study.Member.controller.dto.MemberRequestJoinDTO;
import spring.study.Member.domain.aggregates.Member;
import spring.study.Member.domain.valueObjects.MemberAddressInfo;
import spring.study.Member.domain.valueObjects.MemberBasicInfo;
import spring.study.common.enums.ValidationMsgCode;
import spring.study.common.responses.ResponseMessage;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static spring.study.common.enums.ErrorCode.FAIL_VALIDATE;
import static spring.study.common.enums.SuccessCode.SUCCESS_JOIN_MEMBER;
import static spring.study.common.enums.ValidationMsgCode.*;

@WebMvcTest(MemberController.class)
@ExtendWith(MockitoExtension.class)
@DisplayName("[Controller] 회원 가입 Test")
class MemberControllerJoinMockTest {

    @MockBean
    private MemberService memberService;

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    //회원가입 성공시 return 되는 결과값
    Member member = Member.builder()
            .id(1L)
                .email("hong@naver.com")
                .memberBasicInfo(MemberBasicInfo.builder()
                        .password("hong1!")
                        .name("홍길동")
                        .mobileNum("010-1111-2222")
                        .gender("M")
                        .birth("001122")
                        .build())
            .memberAddressInfo(MemberAddressInfo.builder()
                        .address("Seoul")
                        .build())
            .build();

    @Test
    @DisplayName("회원 가입 성공")
    void successJoin() throws Exception {

        //given
        //DTO
        MemberRequestJoinDTO dto = MemberRequestJoinDTO.builder()
                .email("hong@naver.com")
                .password("hong1!")
                .name("홍길동")
                .address("Seoul")
                .mobileNum("010-1111-2222")
                .gender("M")
                .birth("001122")
                .build();

        //return response message
        ResponseMessage message = ResponseMessage.builder()
                .httpStatus(SUCCESS_JOIN_MEMBER.getHttpStatus())
                .message(SUCCESS_JOIN_MEMBER.getSuccessMsg())
                .resultData(member)
                .build();

        given(memberService.join(any())).willReturn(member);

        //when
        //then
        mockMvc.perform(post("/member/members/new")
        .contentType(APPLICATION_JSON)
        .content(mapper.writeValueAsString(dto))
        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(message)))
                .andDo(print());
    }

    @DisplayName("회원 가입 실패 - 이메일 validation 통과 X")
    @ParameterizedTest(name = "{index}: {2}")
    @MethodSource("invalidEmailParameters")
    void failureEmailNull(String email, ValidationMsgCode msgCode, String title) throws Exception {
        //given
        MemberRequestJoinDTO dto = MemberRequestJoinDTO.builder()
                .email(email)
                .password("hong1!")
                .name("홍길동")
                .address("Seoul")
                .mobileNum("010-1111-2222")
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
        mockMvc.perform(post("/member/members/new")
                .accept(APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(mapper.writeValueAsString(message)))
                .andDo(print());

    }

    //email 파라미터
    static Stream<Arguments> invalidEmailParameters() {
        return Stream.of(
                Arguments.of(null, BLANK_VALIDATE, "이메일이 null일 때"),
                Arguments.of(" ", BLANK_VALIDATE, "이메일이 빈칸일 때"),
                Arguments.of("hong", EMAIL_NOT_MATCH, "이메일 형식이 안맞을 때")
        );
    }

    @DisplayName("회원 가입 실패 - 패스워드 validation 통과 X")
    @ParameterizedTest(name = "{index}: {2}")
    @MethodSource("invalidPasswordParameters")
    void failJoinValidationPassword(String password, ValidationMsgCode msgCode, String title) throws Exception {
        //given
        MemberRequestJoinDTO dto = MemberRequestJoinDTO.builder()
                .email("hong@naver.com")
                .password(password)
                .name("홍길동")
                .address("Seoul")
                .mobileNum("010-1111-1111")
                .gender("M")
                .birth("001122")
                .build();

        ResponseMessage message = ResponseMessage.builder()
                .httpStatus(FAIL_VALIDATE.getHttpStatus())
                .message(FAIL_VALIDATE.getErrorMsg())
                .detailMsg(msgCode.getValidationMsg())
                .build();

        //when
        //then
        mockMvc.perform(post("/member/members/new")
                .accept(APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(mapper.writeValueAsString(message)))
                .andDo(print());
    }

    //password 파라미터
    static Stream<Arguments> invalidPasswordParameters() {
        return Stream.of(
                Arguments.of(null, BLANK_VALIDATE, "password가 null일 때"),
                Arguments.of(" ", BLANK_VALIDATE, "password가 빈칸일 때"),
                Arguments.of("c1!", PASSWORD_NOT_MATCH, "password 길이가 min(=4) 보다 짧을 때"),
                Arguments.of("hhhhhhhh1!", PASSWORD_NOT_MATCH, "password 길이가 max(=8) 보다 길 때"),
                Arguments.of("hhhhhh", PASSWORD_NOT_MATCH, "password 형식이 안맞을 때")
        );
    }


    @DisplayName("회원 가입 실패 - 이름 validation 통과 X")
    @ParameterizedTest(name = "{index}: {2}")
    @MethodSource("invalidNameParameters")
    void failJoinValidationName(String name, ValidationMsgCode msgCode, String title) throws Exception {
        //given
        MemberRequestJoinDTO dto = MemberRequestJoinDTO.builder()
                .email("hong@naver.com")
                .password("pass1!")
                .name(name)
                .address("Seoul")
                .mobileNum("010-1111-1111")
                .gender("M")
                .birth("001122")
                .build();

        ResponseMessage message = ResponseMessage.builder()
                .httpStatus(FAIL_VALIDATE.getHttpStatus())
                .message(FAIL_VALIDATE.getErrorMsg())
                .detailMsg(msgCode.getValidationMsg())
                .build();

        //when
        //then
        mockMvc.perform(post("/member/members/new")
                .accept(APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(mapper.writeValueAsString(message)))
                .andDo(print());
    }

    //name 파라미터
    static Stream<Arguments> invalidNameParameters() {
        return Stream.of(
                Arguments.of(null, BLANK_VALIDATE, "name이 null일 때"),
                Arguments.of(" ", BLANK_VALIDATE, "name이 빈칸일 때"),
                Arguments.of("honggildong", NAME_NOT_MATCH, "name 형식이 안맞을 때")
        );
    }

    @DisplayName("회원 가입 실패 - 전화번호 validation 통과 X")
    @ParameterizedTest(name = "{index}: {2}")
    @MethodSource("invalidMobileNumParameters")
    void failJoinValidationMobileNum(String mobileNum, ValidationMsgCode msgCode, String title) throws Exception {
        //given
        MemberRequestJoinDTO dto = MemberRequestJoinDTO.builder()
                .email("hong@naver.com")
                .password("pass1!")
                .name("홍길동")
                .address("Seoul")
                .mobileNum(mobileNum)
                .gender("M")
                .birth("001122")
                .build();

        ResponseMessage message = ResponseMessage.builder()
                .httpStatus(FAIL_VALIDATE.getHttpStatus())
                .message(FAIL_VALIDATE.getErrorMsg())
                .detailMsg(msgCode.getValidationMsg())
                .build();

        //when
        //then
        mockMvc.perform(post("/member/members/new")
                .accept(APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(mapper.writeValueAsString(message)))
                .andDo(print());
    }

    //mobileNum 파라미터
    static Stream<Arguments> invalidMobileNumParameters() {
        return Stream.of(
                Arguments.of(null, BLANK_VALIDATE, "전화번호가 null일 때"),
                Arguments.of(" ", BLANK_VALIDATE, "전화번호가 빈칸일 때"),
                Arguments.of("010-1111-11", MOBILENUM_NOT_MATCH, "전화번호 길이가 짧을 때"),
                Arguments.of("015-1111-1111", MOBILENUM_NOT_MATCH, "전화번호 형식(010등 외의 번호)이 안맞을 때"),
                Arguments.of("01011111111", MOBILENUM_NOT_MATCH, "전화번호 형식(-)이 안맞을 때")
        );
    }

}