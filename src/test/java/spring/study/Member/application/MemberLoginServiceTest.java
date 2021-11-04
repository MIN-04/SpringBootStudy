package spring.study.Member.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import spring.study.Member.domain.aggregates.Member;
import spring.study.Member.domain.commands.MemberCommand;
import spring.study.Member.infraStructure.repository.MemberJPARepository;
import spring.study.common.auth.providers.JwtTokenProvider;
import spring.study.common.exceptions.CustomException;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static spring.study.common.enums.ErrorCode.NOT_EXIST_MEMBER;
import static spring.study.common.enums.ErrorCode.WRONG_PASSWORD;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Service - login] 로컬 로그인")
class MemberLocalLoginServiceTest {

    @InjectMocks
    MemberLoginService memberLoginService;

    @Mock
    MemberJPARepository memberRepository;
    @Mock
    JwtTokenProvider jwtTokenProvider;
    @Mock
    PasswordEncoder passwordEncoder;

    MemberCommand command;
    Member result;

    @BeforeEach
    void setUp() {
        //loginLocal Service에 들어갈 command
        command = MemberCommand.builder()
                .email("hong@ddd.com")
                .password("hong1!")
                .build();
        //회원 조회할 때 나오는 결과 member
        result = Member.builder()
                .id(1L)
                .email("hong@ddd.com")
                .password(passwordEncoder.encode("hong1!"))
                .roles(Collections.singletonList("MEMBER"))
                .build();
    }

    @Test
    @DisplayName("로컬 회원 로그인 성공")
    void successLocalLogin() {
        //given
        String encodedPw = passwordEncoder.encode(command.getPassword());

        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(result));

        given(passwordEncoder.matches(encodedPw, result.getPassword())).willReturn(true);

        given(jwtTokenProvider.createToken(any(), any())).willReturn("token");

        //when
        String resultToken = memberLoginService.loginLocal(command);

        //then
        assertThat(resultToken).isEqualTo("token");
    }

    @Test
    @DisplayName("로컬 회원 로그인 실패 - 회원이 없을 때")
    void notFoundMember() {
        //given
        given(memberRepository.findByEmail(anyString())).willThrow(new CustomException(NOT_EXIST_MEMBER));

        //when
        //then
        CustomException exception = assertThrows(CustomException.class,
                () -> memberLoginService.loginLocal(command));
        assertThat(exception.getErrorCode()).isEqualTo(NOT_EXIST_MEMBER);
    }

    @Test
    @DisplayName("로컬 회원 로그인 실패 - 비밀번호가 다를 때")
    void notMatchPassword() {
        //given
        String encodedPw = passwordEncoder.encode("park1!");

        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(result));

        given(passwordEncoder.matches(encodedPw, result.getPassword())).willReturn(false);

        //when
        //then
        assertFalse(passwordEncoder.matches(encodedPw, result.getPassword()));
        CustomException exception = assertThrows(CustomException.class,
                () -> memberLoginService.loginLocal(command));
        assertThat(exception.getErrorCode()).isEqualTo(WRONG_PASSWORD);
    }

}