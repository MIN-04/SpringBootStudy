package spring.study.Member.application;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spring.study.Member.domain.aggregates.Member;
import spring.study.Member.domain.commands.MemberCommand;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static spring.study.common.enums.ErrorCode.DUPLICATED_MEMBER;
import static spring.study.common.enums.ErrorCode.NOT_EXIST_MEMBER;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Service] 회원 수정 Test")
class MemberServiceModifyTest {

    @InjectMocks
    MemberService memberService;

    @Mock
    MemberJPARepository memberRepository;

    //수정 전 member
    static Member oldMember;

    //수정 할 command
    static MemberCommand command;

    //수정 후 정보
    static Member updateMember;

    @BeforeAll
    static void setUp() {
        //수정 전 member
        oldMember = Member.builder()
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

        //수정 할 command
        command = MemberCommand.builder()
                .id(1L)
                .email("hong1@naver.com")
                .basicInfo(MemberBasicInfo.builder()
                        .password("abcde1!")
                        .name("홍길동1")
                        .mobileNum("010-1111-2222")
                        .gender("M")
                        .birth("001122")
                        .build())
                .addressInfo(MemberAddressInfo.builder()
                        .address("Seoul")
                        .build())
                .build();

        //수정 후 정보
        updateMember = Member.builder()
                .id(1L)
                .email("hong1@naver.com")
                .memberBasicInfo(MemberBasicInfo.builder()
                        .password("abcde1!")
                        .name("홍길동1")
                        .mobileNum("010-1111-2222")
                        .gender("M")
                        .birth("001122")
                        .build())
                .memberAddressInfo(MemberAddressInfo.builder()
                        .address("Seoul")
                        .build())
                .build();
    }

    @Test
    @DisplayName("회원 수정 성공")
    void modifySuccess() {
        //given

        //수정할 회원이 있다.
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(oldMember));
        //수정 정보중 다른 회원과 중복된 이메일, 전화번호가 없다.
        given(memberRepository.findByEmailOrMemberBasicInfo_MobileNum(anyString(), anyString()))
                .willReturn(new ArrayList<>());
        //수정하면 member를 리턴한다.
        given(memberRepository.save(any())).willReturn(updateMember);


        //when
        Member result = memberService.modify(command);

        //then
        assertTrue(result.getId().equals(command.getId()));
        assertThat(result).usingRecursiveComparison().isEqualTo(updateMember);
        assertThat(result).usingRecursiveComparison().isNotEqualTo(oldMember);
    }

    @Test
    @DisplayName("회원 수정 실패 - 수정할 회원이 없을 때")
    void notExistMember() {
        //given
        willReturn(Optional.empty())
                .willThrow(new CustomException(NOT_EXIST_MEMBER))
                .given(memberRepository).findById(anyLong());

        //when
        //then
        CustomException exception = assertThrows(CustomException.class,
                () -> memberService.modify(command));
        ErrorCode errorCode = exception.getErrorCode();
        assertThat(errorCode).isEqualTo(NOT_EXIST_MEMBER);
    }

    @DisplayName("회원수정 실패 - 이메일 또는 전화번호가 중복인 회원이 있을 때")
    @ParameterizedTest(name = "{index}: {2}")
    @MethodSource("invalidParameters")
    void duplicatedFailure(String email, String mobileNum, String testName) {
        //given
        Member duplicatedMember = Member.builder()
                .id(2L)
                .email(email)
                .memberBasicInfo(MemberBasicInfo.builder()
                        .mobileNum(mobileNum)
                        .build())
                .build();

        List<Member> duplicatedList = new ArrayList<>();
        duplicatedList.add(duplicatedMember);

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(new Member()));

        willReturn(duplicatedList)
                .willThrow(new CustomException(DUPLICATED_MEMBER))
                .given(memberRepository).findByEmailOrMemberBasicInfo_MobileNum(
                        updateMember.getEmail(),
                        updateMember.getMemberBasicInfo().getMobileNum()
        );

        //when
        //then
        CustomException exception = assertThrows(CustomException.class,
                () -> memberService.modify(command));
        assertThat(exception.getErrorCode()).isEqualTo(DUPLICATED_MEMBER);
    }

    static Stream<Arguments> invalidParameters() {
        final String EMAIL = "hong1@naver.com";
        final String MOBILENUM = "010-1111-2222";

        return Stream.of(
                Arguments.of(EMAIL, "010-2222-1111", "이메일만 중복일 때"),
                Arguments.of("hongAAA@naver.com", MOBILENUM, "전화번호만 중복일 때"),
                Arguments.of(EMAIL, MOBILENUM, "이메일, 전화번호 모두 중복일 때")
        );
    }

}