package spring.study.Member.application;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import spring.study.common.exceptions.CustomException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static spring.study.common.enums.ErrorCode.DUPLICATED_MEMBER;

@ExtendWith(MockitoExtension.class)
class MemberServiceJoinTest {

    @InjectMocks
    MemberService memberService;

    @Mock
    MemberRepository memberRepository;

    MemberCommand memberCommand;
    Member member;

    @BeforeEach
    void setUp() {
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
        memberCommand = MemberCommand.builder()
                .email("hong@naver.com")
                .basicInfo(memberBasicInfo)
                .addressInfo(memberAddressInfo)
                .build();

        // Member
        member = Member.builder()
                .email("hong@naver.com")
                .memberBasicInfo(memberBasicInfo)
                .memberAddressInfo(memberAddressInfo)
                .build();
    }

    @Test
    @DisplayName("회원 가입 성공")
    void joinSuccess() {
        //given
        given(memberRepository.save(any())).willReturn(member);
        //아래는 실패 코드 (왤까?)
        //given(memberRepository.save(member)).willReturn(member);
        //given(memberRepository.save(eq(member))).willReturn(member);

        //then
        Member result = memberService.join(memberCommand);

        //when
        Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(member);
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void joinFailureEmail() {
        //given
        given(memberRepository
                .findMemberByEmailOrMemberBasicInfo_MobileNum(eq("hong@naver.com"), anyString()))
                .willReturn(Optional.of(member))
                .willThrow(new CustomException(DUPLICATED_MEMBER));
        //다른 방법 (성공)
//        given(memberRepository
//                .findMemberByEmailOrMemberBasicInfo_MobileNum(eq(member.getEmail()), anyString()))
//                .willThrow(new CustomException(DUPLICATED_MEMBER));

        //when
        //then
        assertThrows(CustomException.class, () ->memberService.join(memberCommand));

    }

    @Test
    @DisplayName("회원가입 실패 - 전화번호 중복")
    void joinFailureMobileNum() {
        //given
        given(memberRepository
                .findMemberByEmailOrMemberBasicInfo_MobileNum(anyString(),
                        eq("010-1111-1111")))
                .willReturn(Optional.of(member))
                .willThrow(new CustomException(DUPLICATED_MEMBER));

        //when
        //then
        assertThrows(CustomException.class, () -> memberService.join(memberCommand));
    }

}