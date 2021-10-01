package spring.study.Member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import spring.study.Member.controller.dto.MemberRequestDTO;
import spring.study.Member.domain.aggregates.Member;
import spring.study.Member.domain.valueObjects.MemberAddressInfo;
import spring.study.Member.domain.valueObjects.MemberBasicInfo;
import spring.study.common.responses.ResponseMessage;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static spring.study.common.enums.ErrorCode.FAIL_VALIDATE;
import static spring.study.common.enums.SuccessCode.SUCCESS_JOIN_MEMBER;
import static spring.study.common.enums.ValidationMsgCode.*;

//@WebMvcTest(MemberController.class)
//@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    //회원가입 성공시 return 되는 결과값
    Member member;

    @Test
    @DisplayName("회원 가입 성공")
    void successJoin() throws Exception {
//        MemberCommand command = toCommandMapper.toCommand(joinRequestDTO);
//        given(memberService.join(any())).willReturn(member);

        //given
        //DTO
        MemberRequestDTO dto = MemberRequestDTO.builder()
                .email("hong@naver.com")
                .password("hong1!")
                .name("홍길동")
                .address("Seoul")
                .mobileNum("010-1111-1111")
                .gender("M")
                .birthday("001122")
                .build();

        //return Member 값
        member = Member.builder()
                .email("hong@naver.com")
                .memberBasicInfo(MemberBasicInfo.builder()
                        .password("hong1!")
                        .name("홍길동")
                        .mobileNum("010-1111-1111")
                        .gender("M")
                        .birthday("001122")
                        .build())
                .memberAddressInfo(MemberAddressInfo.builder()
                        .address("Seoul")
                        .build())
                .build();

        //return response message
        ResponseMessage message = ResponseMessage.builder()
                .httpStatus(SUCCESS_JOIN_MEMBER.getHttpStatus())
                .message(SUCCESS_JOIN_MEMBER.getSuccessMsg())
                .resultData(member)
                .build();

        //when
        //then
        mockMvc.perform(post("/member/new")
        .contentType(APPLICATION_JSON)
        .content(mapper.writeValueAsString(dto))
        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(message)))
                .andDo(print());

        //json path로 시도해본 것
        //        .andExpect(jsonPath("$.httpStatus").value(SUCCESS_JOIN_MEMBER.getHttpStatus()))
        //        .andExpect(jsonPath("$.message").value(SUCCESS_JOIN_MEMBER.getSuccessMsg()))
        //        .andExpect(jsonPath("$.detailMsg").value(IsNull.nullValue()))
        //        .andExpect(jsonPath("$.resultData").value(mapper.writeValueAsString(member)));

    }

    @Test
    @DisplayName("회원 가입 실패 - email validation 통과 X")
    void failJoinValidationEmail() throws Exception {
        //given
        //case 1. email이 null일 때
        MemberRequestDTO dtoNull = MemberRequestDTO.builder()
                .email(" ")
                .password("hong1!")
                .name("홍길동")
                .address("Seoul")
                .mobileNum("010-1111-1111")
                .gender("M")
                .birthday("001122")
                .build();
        //return response message
        ResponseMessage messageNull = ResponseMessage.builder()
                .httpStatus(FAIL_VALIDATE.getHttpStatus())
                .message(FAIL_VALIDATE.getErrorMsg())
                .detailMsg(BLANK_VALIDATE.getValidationMsg())
                .build();

        //case 2. email 형식이 안맞을 때
        MemberRequestDTO dtoMissMatch = MemberRequestDTO.builder()
                .email("hong")
                .password("hong1!")
                .name("홍길동")
                .address("Seoul")
                .mobileNum("010-1111-1111")
                .gender("M")
                .birthday("001122")
                .build();
        //return response message
        ResponseMessage messageMissMatch = ResponseMessage.builder()
                .httpStatus(FAIL_VALIDATE.getHttpStatus())
                .message(FAIL_VALIDATE.getErrorMsg())
                .detailMsg(EMAIL_NOT_MATCH.getValidationMsg())
                .build();

        //when
        //then
        //case 1. email이 null일 때
        mockMvc.perform(post("/member/new")
        .accept(APPLICATION_JSON)
        .content(mapper.writeValueAsString(dtoNull))
        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(mapper.writeValueAsString(messageNull)))
                .andDo(print());

        //case 2. email 형식이 안맞을 때
        mockMvc.perform(post("/member/new")
        .accept(APPLICATION_JSON)
        .content(mapper.writeValueAsString(dtoMissMatch))
        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(mapper.writeValueAsString(messageMissMatch)))
                .andDo(print());

    }

    @Test
    @DisplayName("회원 가입 실패 - password validation 통과 X")
    void failJoinValidationPassword() throws Exception {
        //given
        //case 1. password가 빈칸일 때
        MemberRequestDTO dtoNull = MemberRequestDTO.builder()
                .email("hong@naver.com")
                .password("")
                .name("홍길동")
                .address("Seoul")
                .mobileNum("010-1111-1111")
                .gender("M")
                .birthday("001122")
                .build();

        ResponseMessage messageNull = ResponseMessage.builder()
                .httpStatus(FAIL_VALIDATE.getHttpStatus())
                .message(FAIL_VALIDATE.getErrorMsg())
                .detailMsg(BLANK_VALIDATE.getValidationMsg())
                .build();

        //case 2. password 길이가 min(=4) 보다 짧을 때
        MemberRequestDTO dtoMin = MemberRequestDTO.builder()
                .email("hong@naver.com")
                .password("c1!")
                .name("홍길동")
                .address("Seoul")
                .mobileNum("010-1111-1111")
                .gender("M")
                .birthday("001122")
                .build();

        //case 3. password 길이가 max(=8) 보다 길 때
        MemberRequestDTO dtoMax = MemberRequestDTO.builder()
                .email("hong@naver.com")
                .password("hhhhhhhh1!")
                .name("홍길동")
                .address("Seoul")
                .mobileNum("010-1111-1111")
                .gender("M")
                .birthday("001122")
                .build();

        //case 4. password 형식이 안맞을 때
        MemberRequestDTO dtoMissMatch = MemberRequestDTO.builder()
                .email("hong@naver.com")
                .password("hhhhhh")
                .name("홍길동")
                .address("Seoul")
                .mobileNum("010-1111-1111")
                .gender("M")
                .birthday("001122")
                .build();

        //case 2,3,4는 response message가 똑같다.
        ResponseMessage messageMissMatch = ResponseMessage.builder()
                .httpStatus(FAIL_VALIDATE.getHttpStatus())
                .message(FAIL_VALIDATE.getErrorMsg())
                .detailMsg(PASSWORD_NOT_MATCH.getValidationMsg())
                .build();

        //when
        //then
        //case 1. password가 빈칸일 때
        mockMvc.perform(post("/member/new")
        .accept(APPLICATION_JSON)
        .content(mapper.writeValueAsString(dtoNull))
        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(mapper.writeValueAsString(messageNull)))
                .andDo(print());

        //case 2. password 길이가 min(=4) 보다 짧을 때
        mockMvc.perform(post("/member/new")
        .accept(APPLICATION_JSON)
        .content(mapper.writeValueAsString(dtoMin))
        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(mapper.writeValueAsString(messageMissMatch)))
                .andDo(print());

        //case 3. password 길이가 max(=8) 보다 길 때
        mockMvc.perform(post("/member/new")
        .accept(APPLICATION_JSON)
        .content(mapper.writeValueAsString(dtoMax))
        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(mapper.writeValueAsString(messageMissMatch)))
                .andDo(print());

        //case 4. password 형식이 안맞을 때
        mockMvc.perform(post("/member/new")
                .accept(APPLICATION_JSON)
                .content(mapper.writeValueAsString(dtoMissMatch))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(mapper.writeValueAsString(messageMissMatch)))
                .andDo(print());


    }

}