package spring.study.Member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import spring.study.Member.application.MemberService;
import spring.study.common.responses.ResponseMessage;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static spring.study.common.enums.ErrorCode.FAIL_VALIDATE;
import static spring.study.common.enums.SuccessCode.SUCCESS_DELETE_MEMBER;

@WebMvcTest(MemberController.class)
@ExtendWith(MockitoExtension.class)
class MemberControllerDeleteMockTest {

    @MockBean
    MemberService memberService;

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("회원 탈퇴 성공")
    void successDelete() throws Exception {
        //given
        Long id = 1L;
        //응답 메시지
        ResponseMessage message = ResponseMessage.builder()
                .httpStatus(SUCCESS_DELETE_MEMBER.getHttpStatus())
                .message(SUCCESS_DELETE_MEMBER.getSuccessMsg())
                .build();

        //when
        //then
        mockMvc.perform(delete("/member/members/{id}", id)
        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(message)))
                .andDo(print());
        verify(memberService, times(1)).delete(1L);
    }

    @Disabled
    @Test
    @DisplayName("회원 탈퇴 실패 - validation 통과 X")
    void failDeleteValidation() throws Exception {
        //given
        //case 1. email이 빈칸일 때
        String deleteNull = "";
        ResponseMessage messageNull = ResponseMessage.builder()
                .httpStatus(FAIL_VALIDATE.getHttpStatus())
                .message(FAIL_VALIDATE.getErrorMsg())
//                .detailMsg(BLANK_VALIDATE.getValidationMsg())
                .build();

        //case 2. email의 형식이 안맞을 때
        String deleteMissMatch = "ddd";
        ResponseMessage messageMissMatch = ResponseMessage.builder()
                .httpStatus(FAIL_VALIDATE.getHttpStatus())
                .message(FAIL_VALIDATE.getErrorMsg())
//                .detailMsg(EMAIL_NOT_MATCH.getValidationMsg())
                .build();

        //when
        //then
        //case 1. email이 빈칸일 때
        mockMvc.perform(get("/member/delete/{email}", deleteNull)
        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(mapper.writeValueAsString(messageNull)))
                .andDo(print());

//        //case 2. email의 형식이 안맞을 때
//        mockMvc.perform(get("/member/delete/{email}", deleteMissMatch)
//        .accept(MediaType.APPLICATION_JSON)
//        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(content().string(mapper.writeValueAsString(messageMissMatch)))
//                .andDo(print());

    }

}
