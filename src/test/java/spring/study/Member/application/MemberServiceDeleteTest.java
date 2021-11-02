package spring.study.Member.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spring.study.Member.domain.aggregates.Member;
import spring.study.Member.infraStructure.repository.MemberJPARepository;
import spring.study.common.exceptions.CustomException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static spring.study.common.enums.ErrorCode.NOT_EXIST_MEMBER;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Service] 회원 탈퇴 Test")
class MemberServiceDeleteTest {

    @InjectMocks
    MemberService memberService;

    @Mock
    MemberJPARepository memberRepository;

    @Test
    @DisplayName("회원 탈퇴 성공")
    void successDelete() {
        //given
        Long id = 1L;
        //삭제할 회원이 있는지 찾기
        given(memberRepository.findById(eq(id))).willReturn(Optional.of(new Member()));
        willDoNothing().given(memberRepository).deleteById(id);

        //when
        memberService.delete(id);

        //then
        verify(memberRepository, times(1)).findById(id);
        verify(memberRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 탈퇴할 회원이 없을 때")
    void failDeleteNotExist() {
        //given
        Long id = 1L;
        willReturn(Optional.empty())
                .willThrow(new CustomException(NOT_EXIST_MEMBER))
                .given(memberRepository)
                .findById(anyLong());

        //when
        //then
        CustomException exception = assertThrows(CustomException.class,
                () -> memberService.delete(id));
        assertThat(exception.getErrorCode()).isEqualTo(NOT_EXIST_MEMBER);
    }

}