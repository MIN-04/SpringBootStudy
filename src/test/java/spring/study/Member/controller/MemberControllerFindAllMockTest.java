package spring.study.Member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import spring.study.Member.application.MemberService;
import spring.study.Member.domain.aggregates.Member;
import spring.study.Member.domain.services.CustomUserDetailsService;
import spring.study.Member.domain.valueObjects.MemberAddressInfo;
import spring.study.Member.domain.valueObjects.MemberBasicInfo;
import spring.study.common.auth.providers.JwtTokenProvider;
import spring.study.common.responses.ResponseMessage;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.BDDMockito.willReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static spring.study.common.enums.SuccessCode.SUCCESS_FINDALL_MEMBER;
import static spring.study.common.paths.MemberUrl.MEMBERS_PATH;
import static spring.study.common.paths.MemberUrl.MEMBER_ROOT_PATH;

@WebMvcTest(MemberController.class)
@ExtendWith(MockitoExtension.class)
@DisplayName("[Controller] 회원 목록 조회 Test")
class MemberControllerFindAllMockTest {

    @MockBean
    MemberService memberService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    static Member member1;
    static Member member2;

    @BeforeAll
    static void setUp() {
        member1 = Member.builder()
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

        member2 = Member.builder()
                .id(2L)
                .email("park@naver.com")
                .password("park1!")
                .roles(Collections.singletonList("ROLE_MEMBER"))
                .memberBasicInfo(MemberBasicInfo.builder()
                        .name("박길동")
                        .mobileNum("010-1111-3333")
                        .gender("M")
                        .birth("000229")
                        .build())
                .memberAddressInfo(MemberAddressInfo.builder()
                        .address("Seoul")
                        .build())
                .build();
    }

    @Test
    @DisplayName("회원 목록 조회 성공")
    @WithMockUser(roles = "MEMBER")
    void successFindAll() throws Exception {
        //given
        int page = 0;
        int pageCount = 10;

        Page<Member> testPage = new PageImpl<>(Arrays.asList(member1, member2));

        ResponseMessage message = ResponseMessage.builder()
                .httpStatus(SUCCESS_FINDALL_MEMBER.getHttpStatus())
                .message(SUCCESS_FINDALL_MEMBER.getSuccessMsg())
                .resultData(testPage.getContent())
                .build();

        willReturn(testPage).given(memberService).findAll(page, pageCount);

        //when
        //then
        mockMvc.perform(get(MEMBER_ROOT_PATH + MEMBERS_PATH +"?page="+page+"&pageCount="+pageCount)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(message)))
                .andDo(print());
    }

}
