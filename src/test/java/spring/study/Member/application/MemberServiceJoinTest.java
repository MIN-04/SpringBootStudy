package spring.study.Member.application;

import org.junit.jupiter.api.Disabled;
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
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static spring.study.common.enums.ErrorCode.DUPLICATED_MEMBER;

@ExtendWith(MockitoExtension.class)
//@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("[Service] 회원 가입 Test")
class MemberServiceJoinTest {

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

    // MemberCommand
    MemberCommand memberCommand = MemberCommand.builder()
            .email("hong@naver.com")
            .basicInfo(memberBasicInfo)
            .addressInfo(memberAddressInfo)
            .build();

    // Member
    Member member = Member.builder()
            .id(1L)
            .email("hong@naver.com")
            .memberBasicInfo(memberBasicInfo)
            .memberAddressInfo(memberAddressInfo)
            .build();

    @Test
    @DisplayName("회원 가입 성공")
    void joinSuccess() {
        //given
        //빈 List
        List<Member> testList = new ArrayList<>();
        given(memberRepository.findByEmailOrMemberBasicInfo_MobileNum(anyString(), anyString()))
                .willReturn(testList);
        given(memberRepository.save(any())).willReturn(member);

        //아래는 실패 코드 (왤까?)
        //given(memberRepository.save(member)).willReturn(member);
        //given(memberRepository.save(eq(member))).willReturn(member);

        //then
        List<Member> memberList = memberRepository.findByEmailOrMemberBasicInfo_MobileNum(member.getEmail(),
                member.getMemberBasicInfo().getMobileNum());
        Member result = memberService.join(memberCommand);

        //when
        assertTrue(memberList.isEmpty());
        assertThat(result).usingRecursiveComparison().isEqualTo(member);
    }

    @Test
    @DisplayName("회원 가입 실패 - 이메일 중복일 때")
    void joinFailureEmail() {
        //given
        given(memberRepository
                .findByEmailOrMemberBasicInfo_MobileNum(eq(member.getEmail()), anyString()))
                .willThrow(new CustomException(DUPLICATED_MEMBER));

        //when
        //then
        CustomException exception = assertThrows(CustomException.class,
                () -> memberService.join(memberCommand));
        ErrorCode errorCode = exception.getErrorCode();
        assertThat(errorCode).isEqualTo(DUPLICATED_MEMBER);
    }

    @Test
    @DisplayName("회원 가입 실패 - 전화번호 중복일 때")
    void joinFailureMobileNum() {
        //given
        given(memberRepository
                .findByEmailOrMemberBasicInfo_MobileNum(anyString(),
                        eq(member.getMemberBasicInfo().getMobileNum())))
                .willThrow(new CustomException(DUPLICATED_MEMBER));

        //when
        //then
        CustomException exception = assertThrows(CustomException.class,
                () -> memberService.join(memberCommand));
        ErrorCode errorCode = exception.getErrorCode();
        assertThat(errorCode).isEqualTo(DUPLICATED_MEMBER);

    }

    @Test
    @DisplayName("회원 가입 실패 - 이메일, 전화번호 모두 중복일 때")
    void joinFailureAll() {
        //given
        given(memberRepository
                .findByEmailOrMemberBasicInfo_MobileNum(
                        eq(member.getEmail()), eq(member.getMemberBasicInfo().getMobileNum())))
                .willThrow(new CustomException(DUPLICATED_MEMBER));

        //when
        //then
        CustomException exception = assertThrows(CustomException.class,
                () -> memberService.join(memberCommand));
        ErrorCode errorCode = exception.getErrorCode();
        assertThat(errorCode).isEqualTo(DUPLICATED_MEMBER);
    }

    /**
     * 중복된 메서드 사용이라 @ParameterizedTest 어노테이션을 사용했다.
     * 그런데 예외 발생 : Unexpected exception type thrown ==>
     * expected: <spring.study.common.exceptions.CustomException>
     * but was: <org.mockito.exceptions.misusing.PotentialStubbingProblem>
     * given을 사용하여 동일한 메서드를 여러번 호출하면 발생 (동일한 메서드를 여러번 스텁해서 발생)
     * 이유 : 엄격한 스텁 규칙 때문 - https://www.javadoc.io/doc/org.mockito/mockito-core/2.6.5/org/mockito/exceptions/misusing/PotentialStubbingProblem.html
     * 이걸 느슨하게 해줄려면 클래스에 @MockitoSettings(strictness = Strictness.LENIENT) 붙이기 https://stackoverflow.com/questions/52139619/simulation-of-service-using-mockito-2-leads-to-stubbing-error
     * 하지만 이걸 붙여주면
     * Expected spring.study.common.exceptions.CustomException to be thrown, but nothing was thrown.
     * 이런식으로 오류 발생
     * 해결 실패 -> 나중에 더 찾아보기
     * */
    @Disabled
    @DisplayName("회원가입 실패 - 이메일 또는 전화번호가 중복일 때")
    @ParameterizedTest(name = "{index}: {2}")
    @MethodSource("invalidParameters")
    void joinFailure(String email, String mobileNum, String testName) {
        //given
        given(memberRepository
                .findByEmailOrMemberBasicInfo_MobileNum(email, mobileNum))
                .willThrow(new CustomException(DUPLICATED_MEMBER));
        //다른 방법 (성공)
        //given(memberRepository
        //        .findMemberByEmailOrMemberBasicInfo_MobileNum(eq(member.getEmail()), anyString()))
        //        .willThrow(new CustomException(DUPLICATED_MEMBER));

        //when
        //then
        assertThrows(CustomException.class, () ->memberService.join(memberCommand));

    }

    static Stream<Arguments> invalidParameters() {
        final String EMAIL = "hong@naver.com";
        final String MOBILENUM = "010-1111-1111";

        return Stream.of(
                Arguments.of(EMAIL, "010-2222-1111", "이메일만 중복일 때"),
                Arguments.of("hongAAA@naver.com", MOBILENUM, "전화번호만 중복일 때"),
                Arguments.of(EMAIL, MOBILENUM, "이메일, 전화번호 모두 중복일 때")
        );
    }
}