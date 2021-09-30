package spring.study.Member.application;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceDeleteTest {

    @InjectMocks
    MemberService memberService;

    @Mock
    MemberRepository memberRepository;

    MemberBasicInfo memberBasicInfo = MemberBasicInfo.builder()
            .password("abcd1!")
            .name("홍길동")
            .mobileNum("010-1111-1111")
            .gender("F")
            .birthday("001122")
            .build();

    MemberAddressInfo memberAddressInfo = MemberAddressInfo.builder()
            .address("Seoul")
            .build();

    // MemberCommand
    MemberCommand memberCommand = MemberCommand.builder()
            .email("hong@naver.com")
            .basicInfo(memberBasicInfo)
            .addressInfo(memberAddressInfo)
            .build();

    // Member
    Member member = Member.builder()
            .email("hong@naver.com")
            .memberBasicInfo(memberBasicInfo)
            .memberAddressInfo(memberAddressInfo)
            .build();

    @Test
    @DisplayName("회원 탈퇴 성공")
    void successDelete() {
        //given

        //when
        memberService.delete(memberCommand.getEmail());

        //then
        verify(memberRepository, times(1)).deleteByEmail(memberCommand.getEmail());
    }


}