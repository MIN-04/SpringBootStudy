package spring.study.Member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import spring.study.Member.application.MemberService;
import spring.study.Member.controller.dto.MemberJoinRequestDTO;
import spring.study.Member.controller.dto.mapper.MemberRequestMapper;
import spring.study.Member.domain.aggregates.Member;
import spring.study.Member.domain.commands.MemberCommand;
import spring.study.Member.domain.valueObjects.MemberAddressInfo;
import spring.study.Member.domain.valueObjects.MemberBasicInfo;
import spring.study.common.enums.SuccessCode;
import spring.study.common.responses.ResponseMessage;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static spring.study.common.enums.SuccessCode.SUCCESS_JOIN_MEMBER;

@WebMvcTest(MemberController.class)
@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    @Mock
    MemberService memberService;

    @Mock
    MemberRequestMapper toCommandMapper;

    // DTO
    MemberJoinRequestDTO joinRequestDTO = MemberJoinRequestDTO.builder()
            .email("hong@naver.com")
            .password("hong1!")
            .name("홍길동")
            .address("Seoul")
            .mobileNum("010-1111-1111")
            .gender("M")
            .birthday("001122")
            .build();

    // return Member 값
    Member member = Member.builder()
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

    @Test
    @DisplayName("회원 가입")
    void join() throws Exception {
        //given
        MemberCommand command = toCommandMapper.toCommand(joinRequestDTO);
        given(memberService.join(command)).willReturn(member);

        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.post("/member/new")
                .content(mapper.writeValueAsString(joinRequestDTO))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(
                        ResponseMessage.builder()
                                .httpStatus(SUCCESS_JOIN_MEMBER.getHttpStatus())
                                .message(SUCCESS_JOIN_MEMBER.getSuccessMsg())
                                .resultData(member)
                                .build()))
                );

//                .andExpect(mapper.writeValueAsString(setResponseMessage(SUCCESS_JOIN_MEMBER, member)));

    }


}