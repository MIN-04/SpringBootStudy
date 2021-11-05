package spring.study.common.auth.providers;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import spring.study.Member.domain.aggregates.Member;
import spring.study.Member.domain.services.CustomUserDetailsService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("jwt Provider 테스트")
class JwtTokenProviderTest {

    @InjectMocks
    JwtTokenProvider jwtTokenProvider;

    @Mock
    CustomUserDetailsService userDetailsService;

    static Member member;
    static UserDetails userDetails;
    
    @BeforeAll
    static void setUp() {
        member = Member.builder()
                .email("hong@ddd.com")
                .roles(Collections.singletonList("MEMBER"))
                .build();
        
        userDetails = member;
    }
    
    @Test
    @DisplayName("Jwt 토큰 생성 성공")
    void successCreateToken() {
        //given
        String secretKey = "webfirewood";

        List<String> roles = new ArrayList<>();
        roles.add("MEMBER");

        //when
        String resultToken = jwtTokenProvider.createToken(member.getEmail(), roles);

        //then
        String tokenEmail = Jwts.parser().setSigningKey(secretKey)
                .parseClaimsJws(resultToken)
                .getBody().getSubject();

        assertThat(tokenEmail).isEqualTo(member.getEmail());
    }

    @Test
    @DisplayName("Jwt 토큰에서 인증 정보 조회 성공")
    void successGetAuthentication() {
        //given
        String testToken = jwtTokenProvider.createToken(member.getEmail(), member.getRoles());

        given(userDetailsService.loadUserByUsername(anyString())).willReturn(userDetails);
        
        //when
        Authentication authentication = jwtTokenProvider.getAuthentication(testToken);
        
        //then
        assertThat(authentication.getPrincipal()).isEqualTo(userDetails);
    }

    @Test
    @DisplayName("Request Header의 Token값 가져오기 성공")
    void successResolveToken() {
        //given
        String testToken = jwtTokenProvider.createToken(member.getEmail(), member.getRoles());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-AUTH-TOKEN", testToken);

        //when
        String resultToken = jwtTokenProvider.resolveToken(request);

        //then
        assertThat(resultToken).isEqualTo(testToken);
    }

    @Test
    @DisplayName("토큰의 유효성 + 만료일자 확인 성공")
    void successValidateToken() {
        //given
        String testToken = jwtTokenProvider.createToken(member.getEmail(), member.getRoles());

        //when
        boolean result = jwtTokenProvider.validateToken(testToken);

        //then
        assertTrue(result);
    }

    @Test
    @DisplayName("토큰의 유효성 + 만료일자 확인 실패")
    void failValidateToken() {
        //given
        //when
        boolean result = jwtTokenProvider.validateToken("token");

        //then
        assertFalse(result);
    }

}