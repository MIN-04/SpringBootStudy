package spring.study.Member.application;

import org.assertj.core.api.Assertions;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static spring.study.common.enums.ErrorCode.DUPLICATED_MEMBER;

@ExtendWith(MockitoExtension.class)
class MemberServiceModifyTest {

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
    @DisplayName("회원 수정 성공")
    void joinSuccess() {
        //given
        MemberCommand testCommand = MemberCommand.builder()
                .email("hong@naver.com")
                .basicInfo(MemberBasicInfo.builder()
                        .password("abcd1!")
                        .name("홍길동1")
                        .mobileNum("010-1111-2222")
                        .gender("F")
                        .birthday("001122")
                        .build())
                .addressInfo(MemberAddressInfo.builder()
                        .address("Seoul")
                        .build())
                .build();

        given(memberRepository.save(any())).willReturn(member);


        //then
        memberService.join(memberCommand);
        Member result = memberService.modify(testCommand);

        //when
        assertThat(result.getEmail()).isEqualTo(member.getEmail());
        assertThat(result).usingRecursiveComparison().isNotEqualTo(member);
    }


}