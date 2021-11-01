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
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;

@ExtendWith(MockitoExtension.class)
//@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("[Service] 회원 가입 Test")
class MemberServiceJoinTest {

    @InjectMocks
    MemberService memberService;

    @Mock
    MemberJPARepository memberRepository;

    // MemberCommand
    static MemberCommand memberCommand;

    // Member
    static Member member;

    @BeforeAll
    static void setUp() {
        MemberBasicInfo memberBasicInfo = MemberBasicInfo.builder()
                .name("홍길동")
                .mobileNum("010-1111-1111")
                .gender("F")
                .birth("001122")
                .build();

        MemberAddressInfo memberAddressInfo = MemberAddressInfo.builder()
                .address("Seoul")
                .build();

        memberCommand = MemberCommand.builder()
                .email("hong@naver.com")
                .password("abcd1!")
                .basicInfo(memberBasicInfo)
                .addressInfo(memberAddressInfo)
                .build();

        member = Member.builder()
                .id(1L)
                .email("hong@naver.com")
                .password("abcd1!")
                .memberBasicInfo(memberBasicInfo)
                .memberAddressInfo(memberAddressInfo)
                .build();
    }

    /**
     * 21.10.15 피드백 (10.18 피드백 적용 완료)
     * 수정 전
     * List<Member> memberList = memberRepository.findByEmailOrMemberBasicInfo_MobileNum(member.getEmail(),
     *                 member.getMemberBasicInfo().getMobileNum());
     * 이게 then에 있었다. 이걸 사용하면 안된다. memberRepository는 mocking 처리로 끝내야 한다.
     */
    @Test
    @DisplayName("회원 가입 성공")
    void joinSuccess() {
        //given
        //이메일과 번호가 중복된 회원은 없다.
        willReturn(new ArrayList<>()).given(memberRepository)
                .findByEmailOrMemberBasicInfo_MobileNum(anyString(), anyString());

        given(memberRepository.save(any())).willReturn(member);

        //아래는 실패 코드 (왤까?)
        //given(memberRepository.save(member)).willReturn(member);
        //given(memberRepository.save(eq(member))).willReturn(member);

        //then
        Member result = memberService.join(memberCommand);

        //when
        assertThat(result).usingRecursiveComparison().isEqualTo(member);
    }

    /**
     * 21.10.15 피드백 (10.18 피드백 적용 완료)
     * willThrow는 mocking처리에 대한 리턴 값이 아니기 때문에
     * given에 willReturn과 willThrow를 같이 사용하지 않는다.
     */
    @DisplayName("회원가입 실패 - 이메일 또는 전화번호가 중복일 때")
    @ParameterizedTest(name = "{index}: {2}")
    @MethodSource("invalidParameters")
    void joinFailure(String email, String mobileNum, String testName) {

        //given
        //중복된 회원
        Member duplicatedMember = Member.builder()
                .email(email)
                .memberBasicInfo(MemberBasicInfo.builder()
                        .mobileNum(mobileNum)
                        .build())
                .build();

        List<Member> duplicatedList = new ArrayList<>();
        duplicatedList.add(duplicatedMember);

        willReturn(duplicatedList)
                .given(memberRepository).findByEmailOrMemberBasicInfo_MobileNum(
                        member.getEmail(),
                        member.getMemberBasicInfo().getMobileNum()
        );

        //when
        //then
        CustomException exception = assertThrows(CustomException.class, () ->memberService.join(memberCommand));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.DUPLICATED_MEMBER);


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