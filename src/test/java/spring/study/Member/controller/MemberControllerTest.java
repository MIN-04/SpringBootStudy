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

import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static spring.study.common.enums.SuccessCode.SUCCESS_JOIN_MEMBER;

//@WebMvcTest(MemberController.class)
//@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper = new ObjectMapper();

    // DTO
    final MemberRequestDTO joinRequestDTO = MemberRequestDTO.builder()
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
//        MemberCommand command = toCommandMapper.toCommand(joinRequestDTO);
//        given(memberService.join(any())).willReturn(member);

        //given
        ResponseMessage message = ResponseMessage.builder()
                .httpStatus(SUCCESS_JOIN_MEMBER.getHttpStatus())
                .message(SUCCESS_JOIN_MEMBER.getSuccessMsg())
                .resultData(member)
                .build();

        //when
        //then
        mockMvc.perform(post("/member/new")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(joinRequestDTO))
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


}