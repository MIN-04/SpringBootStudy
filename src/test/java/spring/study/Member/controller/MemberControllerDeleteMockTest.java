package spring.study.Member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import spring.study.Member.application.MemberService;
import spring.study.Member.domain.services.CustomUserDetailsService;
import spring.study.common.auth.providers.JwtTokenProvider;
import spring.study.common.exceptions.CustomException;
import spring.study.common.responses.ResponseMessage;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static spring.study.common.enums.ErrorCode.NOT_EXIST_MEMBER;
import static spring.study.common.enums.SuccessCode.SUCCESS_DELETE_MEMBER;
import static spring.study.common.paths.MemberUrl.*;

@WebMvcTest(MemberController.class)
@ExtendWith(MockitoExtension.class)
@DisplayName("[Controller] 회원 탈퇴 Test")
@WithMockUser(roles = "MEMBER")
class MemberControllerDeleteMockTest {

    @MockBean
    MemberService memberService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

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

        willDoNothing().given(memberService).delete(anyLong());

        //when
        //then
        mockMvc.perform(delete(MEMBER_ROOT_PATH + MEMBERS_DELETE, id)
        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(message)))
                .andDo(print());

        verify(memberService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 삭제할 회원이 없을 때")
    void notFound() throws Exception {
        //given
        Long id = 1L;
        //응답 메시지
        ResponseMessage message = ResponseMessage.builder()
                .httpStatus(NOT_EXIST_MEMBER.getHttpStatus())
                .message(NOT_EXIST_MEMBER.getErrorMsg())
                .build();

        willThrow(new CustomException(NOT_EXIST_MEMBER))
                .given(memberService).delete(anyLong());

        //when
        //then
        mockMvc.perform(delete(MEMBER_ROOT_PATH + MEMBERS_DELETE, id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(mapper.writeValueAsString(message)))
                .andDo(print());

        verify(memberService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 권한 없음")
    @WithAnonymousUser
    void unauthorized() throws Exception {
        //given
        Long id = 1L;

        //when
        //then
        mockMvc.perform(delete(MEMBER_ROOT_PATH + MEMBERS_DELETE, id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }
}
