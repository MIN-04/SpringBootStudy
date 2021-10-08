package spring.study.Member.application;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    MemberBasicInfo memberBasicInfo = MemberBasicInfo.builder()
            .password("abcd1!")
            .name("홍길동")
            .mobileNum("010-1111-1111")
            .gender("F")
            .birth("001122")
            .build();

    MemberAddressInfo memberAddressInfo = MemberAddressInfo.builder()
            .address("Seoul")
            .build();

    //MemberCommand (수정 전 정보)
    MemberCommand memberCommand = MemberCommand.builder()
            .email("hong@naver.com")
            .basicInfo(memberBasicInfo)
            .addressInfo(memberAddressInfo)
            .build();

    //Member (수정 전)
    Member member = Member.builder()
            .id(1L)
            .email("hong@naver.com")
            .memberBasicInfo(memberBasicInfo)
            .memberAddressInfo(memberAddressInfo)
            .build();

    @Test
    @DisplayName("회원 수정 성공")
    void modifySuccess() {
        //given
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
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        //수정 정보중 다른 회원과 중복된 이메일, 전화번호가 없다.
        given(memberRepository.findByEmailOrMemberBasicInfo_MobileNum(anyString(), anyString()))
                .willReturn(new ArrayList<>());
        //수정하면 member를 리턴한다.
        given(memberRepository.save(any())).willReturn(updateMember);


        //when
        Member result = memberService.modify(updateCommand);

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(updateMember);
        assertThat(result).usingRecursiveComparison().isNotEqualTo(member);
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
    @DisplayName("회원 수정 실패 - 이메일 중복")
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
        List<Member> list = new ArrayList<>();
        list.add(member);
        given(memberRepository.findById(command.getId())).willReturn(Optional.of(new Member()));
        given(memberRepository
                .findByEmailOrMemberBasicInfo_MobileNum(eq(command.getEmail()), anyString()))
                .willReturn(list);

        //when
        //then
        CustomException exception = assertThrows(CustomException.class,
                () -> memberService.modify(command));
        assertThat(exception.getErrorCode()).isEqualTo(DUPLICATED_MEMBER);
    }

    @Disabled
    @Test
    @DisplayName("회원 수정 실패 - 이메일 중복")
    void duplicatedMail() {
        //given
        MemberCommand command = MemberCommand.builder()
                .email("hong@naver.com")
                .basicInfo(memberBasicInfo)
                .addressInfo(memberAddressInfo)
                .build();
        //중복된 정보
        MemberCommand dupliatedCommand = MemberCommand.builder()
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

//        List<Member> memberList = new ArrayList<>();
//        memberList.add(member);
//        given(memberRepository
//                .findByEmailOrMemberBasicInfo_MobileNum(eq(updateCommand.getEmail()), anyString()))
//                .willReturn(memberList);
//        given(memberList.get(0).getId().equals(updateCommand.getId()))
//                .willThrow(new CustomException(DUPLICATED_MEMBER));
//        given(memberRepository
//                .findByEmailOrMemberBasicInfo_MobileNum(eq(updateCommand.getEmail()), anyString()))
//                .willThrow(new CustomException(DUPLICATED_MEMBER));

        //when
        Member member1 = memberService.join(command); //수정할 정보와 같은 이메일을 가진 command
        Member member2 = memberService.join(dupliatedCommand); //수정할 정보와 같은 이메일을 가진 command

        System.out.println(member1.getId());
        System.out.println(member2.getId());

//        //then
//        CustomException exception = assertThrows(CustomException.class,
//                () -> memberService.modify(updateCommand));
//        ErrorCode errorCode = exception.getErrorCode();
//        assertThat(errorCode).isEqualTo(DUPLICATED_MEMBER);
    }

}