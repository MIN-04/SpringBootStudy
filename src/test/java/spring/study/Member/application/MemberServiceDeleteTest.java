package spring.study.Member.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spring.study.Member.domain.aggregates.Member;
import spring.study.Member.domain.valueObjects.MemberAddressInfo;
import spring.study.Member.domain.valueObjects.MemberBasicInfo;
import spring.study.Member.infraStructure.repository.MemberJPARepository;
import spring.study.common.exceptions.CustomException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static spring.study.common.enums.ErrorCode.FAIL_DELETE_MEMBER;
import static spring.study.common.enums.ErrorCode.NOT_EXIST_MEMBER;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Service] 회원 탈퇴 Test")
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

    @Test
    @DisplayName("회원 탈퇴 실패 - 탈퇴할 회원이 없을 때")
    void failDeleteNotExist() {
        //given
        Long id = 1L;
        given(memberRepository.findById(anyLong())).willThrow(new CustomException(NOT_EXIST_MEMBER));

        //when
        //then
        CustomException exception = assertThrows(CustomException.class,
                () -> memberService.delete(id));
        assertThat(exception.getErrorCode()).isEqualTo(NOT_EXIST_MEMBER);
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 삭제가 제대로 안됐을 때")
    void failDelete() {
        //given
        Long id = 1L;
        //given(memberRepository.findById(eq(id))).willReturn(Optional.of(member));
        given(memberRepository.findById(eq(id))).willThrow(new CustomException(FAIL_DELETE_MEMBER));

        //when
        //then
        CustomException exception = assertThrows(CustomException.class,
                () -> memberService.delete(id));
        assertThat(exception.getErrorCode()).isEqualTo(FAIL_DELETE_MEMBER);

    }

}