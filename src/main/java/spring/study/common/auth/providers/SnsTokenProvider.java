package spring.study.common.auth.providers;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import spring.study.Member.domain.services.CustomUserDetailsService;
import spring.study.Member.infraStructure.rest.OauthService;
import spring.study.Member.infraStructure.rest.OAuthToken;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@Component
public class SnsTokenProvider {
    private String secretKey = "webfirewood";

    private long tokenValidMilisecond = 30 * 60 * 1000L; // 30분만 토큰 유효

    private final UserDetailsService userDetailsService;

    private final OauthService oauthService;

    @Autowired
    public SnsTokenProvider(CustomUserDetailsService customUserDetailsService, OauthService oauthService) {
        this.userDetailsService = customUserDetailsService;
        this.oauthService = oauthService;
    }

    @PostConstruct // 객체 초기화, secretKey를 Base64로 인코딩한다.
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // SNS API 토큰 생성
    public OAuthToken createToken(String code) {
        return oauthService.requestAccessToken(code);
    }

    // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 정보 추출
    public String getUserPk(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    // Request의 Header에서 token 값을 가져옵니다. "SNS-AUTH-TOKEN" : "TOKEN값'
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("SNS-AUTH-TOKEN");
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
