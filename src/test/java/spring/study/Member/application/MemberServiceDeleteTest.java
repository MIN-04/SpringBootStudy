package spring.study.Member.application;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spring.study.Member.domain.aggregates.Member;
import spring.study.Member.domain.commands.MemberCommand;
import spring.study.Member.domain.services.MemberRepository;
import spring.study.Member.domain.valueObjects.MemberAddressInfo;
import spring.study.Member.domain.valueObjects.MemberBasicInfo;
import spring.study.Member.infraStructure.repository.MemberJPARepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceDeleteTest {

    @InjectMocks
    MemberService memberService;

    @Mock
    MemberJPARepository memberRepository;

    // Member
    Member member = Member.builder()
            .id(1L)
            .email("hong@naver.com")
            .memberBasicInfo(MemberBasicInfo.builder()
                    .password("abcd1!")
                    .name("홍길동")
                    .mobileNum("010-1111-1111")
                    .gender("F")
                    .birth("001122")
                    .build())
            .memberAddressInfo(MemberAddressInfo.builder()
                    .address("Seoul")
                    .build())
            .build();

    @Test
    @DisplayName("회원 탈퇴 성공")
    void successDelete() {
        //given
        Long id = 1L;
        given(memberRepository.findById(eq(id))).willReturn(Optional.of(member), Optional.empty());
        //willDoNothing().given(memberRepository.deleteById(id));

        //when
        memberService.delete(id);

        //then
        verify(memberRepository, times(1)).deleteById(id);
        verify(memberRepository, times(2)).findById(id);
    }

    @Disabled
    @Test
    @DisplayName("회원 탈퇴 실패 - 탈퇴할 회원이 없을 때")
    void failDeleteNotExist() {

    }

    @Disabled
    @Test
    @DisplayName("회원 탈퇴 실패 - 삭제가 제대로 안이루어졌을 때")
    void failDelete() {

    }

}