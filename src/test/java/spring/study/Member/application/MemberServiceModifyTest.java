package spring.study.Member.application;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spring.study.Member.domain.aggregates.Member;
import spring.study.Member.domain.commands.MemberCommand;
import spring.study.Member.domain.services.MemberRepository;
import spring.study.Member.domain.valueObjects.MemberAddressInfo;
import spring.study.Member.domain.valueObjects.MemberBasicInfo;
import spring.study.Member.infraStructure.repository.MemberJPARepository;
import spring.study.common.enums.ErrorCode;
import spring.study.common.exceptions.CustomException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static spring.study.common.enums.ErrorCode.DUPLICATED_MEMBER;
import static spring.study.common.enums.ErrorCode.NOT_EXIST_MEMBER;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Service] 회원 수정 Test")
class MemberServiceModifyTest {

    @InjectMocks
    MemberService memberService;

    @Mock
    MemberJPARepository memberRepository;

    @Test
    @DisplayName("회원 수정 성공")
    void modifySuccess() {
        //given
        //수정 전 member
        Member oldMember = Member.builder()
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
        //수정 후 정보
        Member updateMember = Member.builder()
                .id(1L)
                .email("hong1@naver.com")
                .memberBasicInfo(MemberBasicInfo.builder()
                        .password("abcde1!")
                        .name("홍길동1")
                        .mobileNum("010-1112-1112")
                        .gender("M")
                        .birth("001122")
                        .build())
                .memberAddressInfo(MemberAddressInfo.builder()
                        .address("Seoul")
                        .build())
                .build();
        //수정할 정보 command
        MemberCommand updateCommand = MemberCommand.builder()
                .id(1L)
                .email("hong1@naver.com")
                .basicInfo(MemberBasicInfo.builder()
                        .password("abcde1!")
                        .name("홍길동1")
                        .mobileNum("010-1112-1112")
                        .gender("M")
                        .birth("001122")
                        .build())
                .addressInfo(MemberAddressInfo.builder()
                        .address("Seoul")
                        .build())
                .build();

        //수정할 회원이 있다.
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(oldMember));
        //수정 정보중 다른 회원과 중복된 이메일, 전화번호가 없다.
        given(memberRepository.findByEmailOrMemberBasicInfo_MobileNum(anyString(), anyString()))
                .willReturn(new ArrayList<>());
        //수정하면 member를 리턴한다.
        given(memberRepository.save(any())).willReturn(updateMember);


        //when
        Member result = memberService.modify(updateCommand);

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(updateMember);
        assertThat(result).usingRecursiveComparison().isNotEqualTo(oldMember);
    }

    @Test
    @DisplayName("회원 수정 실패 - 수정할 회원이 없을 때")
    void notExistMember() {
        //given
        //수정할 정보 command
        MemberCommand updateCommand = MemberCommand.builder()
                .id(1L)
                .email("hong1@naver.com")
                .basicInfo(MemberBasicInfo.builder()
                        .password("abcde1!")
                        .name("홍길동1")
                        .mobileNum("010-1112-1112")
                        .gender("M")
                        .birth("001122")
                        .build())
                .addressInfo(MemberAddressInfo.builder()
                        .address("Seoul")
                        .build())
                .build();

        given(memberRepository.findById(anyLong())).willThrow(new CustomException(NOT_EXIST_MEMBER));

        //when
        //then
        CustomException exception = assertThrows(CustomException.class,
                () -> memberService.modify(updateCommand));
        ErrorCode errorCode = exception.getErrorCode();
        assertThat(errorCode).isEqualTo(NOT_EXIST_MEMBER);
    }

    @Test
    @DisplayName("회원 수정 실패 - 이메일 중복일 때")
    void failureMail() {
        //given
        MemberCommand command = MemberCommand.builder()
                .id(2L)
                .email("hong@naver.com")
                .basicInfo(MemberBasicInfo.builder()
                        .password("abcde1!")
                        .name("홍길동")
                        .mobileNum("010-1111-2222")
                        .gender("M")
                        .birth("001122")
                        .build())
                .addressInfo(MemberAddressInfo.builder()
                        .address("Seoul")
                        .build())
                .build();
        //수정 전 member
        Member oldMember = Member.builder()
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
        List<Member> list = new ArrayList<>();
        list.add(oldMember);

        given(memberRepository.findById(eq(2L))).willReturn(Optional.of(new Member()));
        given(memberRepository
                .findByEmailOrMemberBasicInfo_MobileNum(eq("hong@naver.com"), anyString()))
                .willReturn(list);

        //when
        //then
        CustomException exception = assertThrows(CustomException.class,
                () -> memberService.modify(command));
        assertThat(exception.getErrorCode()).isEqualTo(DUPLICATED_MEMBER);
    }

    @Test
    @DisplayName("회원 수정 실패 - 전화번호가 중복일 때")
    void failureMobile() {
        //given
        MemberCommand command = MemberCommand.builder()
                .id(2L)
                .email("hong@naver.com")
                .basicInfo(MemberBasicInfo.builder()
                        .password("abcde1!")
                        .name("홍길동")
                        .mobileNum("010-1111-2222")
                        .gender("M")
                        .birth("001122")
                        .build())
                .addressInfo(MemberAddressInfo.builder()
                        .address("Seoul")
                        .build())
                .build();
        //수정 전 member
        Member oldMember = Member.builder()
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
        List<Member> list = new ArrayList<>();
        list.add(oldMember);

        given(memberRepository.findById(eq(2L))).willReturn(Optional.of(new Member()));
        given(memberRepository
                .findByEmailOrMemberBasicInfo_MobileNum(anyString(), eq("010-1111-2222")))
                .willReturn(list);

        //when
        //then
        CustomException exception = assertThrows(CustomException.class,
                () -> memberService.modify(command));
        assertThat(exception.getErrorCode()).isEqualTo(DUPLICATED_MEMBER);
    }

    @Test
    @DisplayName("회원 수정 실패 - 이메일, 전화번호 모두 중복일 때")
    void failureAll() {
        //given
        MemberCommand command = MemberCommand.builder()
                .id(1L)
                .email("hong@naver.com")
                .basicInfo(MemberBasicInfo.builder()
                        .password("abcde1!")
                        .name("홍길동")
                        .mobileNum("010-1111-2222")
                        .gender("M")
                        .birth("001122")
                        .build())
                .addressInfo(MemberAddressInfo.builder()
                        .address("Seoul")
                        .build())
                .build();
        List<Member> list = new ArrayList<>();

        //같은 이메일을 가진 회원
        Member sameEmail = Member.builder()
                .id(2L)
                .email("hong@naver.com")
                .memberBasicInfo(MemberBasicInfo.builder()
                        .password("abcde1!")
                        .name("홍길동")
                        .mobileNum("010-1111-1111")
                        .gender("M")
                        .birth("001122")
                        .build())
                .memberAddressInfo(MemberAddressInfo.builder()
                        .address("Seoul")
                        .build())
                .build();
        //같은 전화번호를 가진 회원
        Member sameMobileNum = Member.builder()
                .id(3L)
                .email("park@naver.com")
                .memberBasicInfo(MemberBasicInfo.builder()
                        .password("abcde1!")
                        .name("홍길동")
                        .mobileNum("010-1111-2222")
                        .gender("M")
                        .birth("001122")
                        .build())
                .memberAddressInfo(MemberAddressInfo.builder()
                        .address("Seoul")
                        .build())
                .build();

        list.add(sameEmail);
        list.add(sameMobileNum);

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(new Member()));
        given(memberRepository
                .findByEmailOrMemberBasicInfo_MobileNum(eq("hong@naver.com"), eq("010-1111-2222")))
                .willReturn(list);

        //when
        //then
        CustomException exception = assertThrows(CustomException.class,
                () -> memberService.modify(command));
        assertThat(exception.getErrorCode()).isEqualTo(DUPLICATED_MEMBER);
    }


}