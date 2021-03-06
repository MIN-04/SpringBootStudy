package spring.study.common.auth.providers;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import spring.study.Member.domain.services.CustomUserDetailsService;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {
    private String secretKey = "webfirewood";

    private long tokenValidMilisecond = 30 * 60 * 1000L; // 30분만 토큰 유효

    private final CustomUserDetailsService userDetailsService;

    @Autowired
    public JwtTokenProvider(CustomUserDetailsService customUserDetailsService) {
        this.userDetailsService = customUserDetailsService;
    }

    @PostConstruct // 객체 초기화, secretKey를 Base64로 인코딩한다.
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // Jwt 토큰 생성
    public String createToken(String userPk, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(userPk); // JWT payload 에 저장되는 정보단위
        claims.put("roles", roles); // 정보는 key / value 쌍으로 저장된다.
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 데이터 저장
                .setIssuedAt(now) // 토큰 발행일자
                .setExpiration(new Date(now.getTime() + tokenValidMilisecond)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey) // 사용할 암호화 알고리즘, signature 에 들어갈 secret값 세팅
                .compact();
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

    // Request의 Header에서 token 값을 가져옵니다. "X-AUTH-TOKEN" : "TOKEN값'
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("X-AUTH-TOKEN");
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

    // JWT 토큰에서 인증 정보 조회
    /*public Authentication getAuthentication(String accessToken) {
        Jws<Claims> jwsClaims = parseClaims(accessToken);
        Claims claims = jwsClaims.getBody();

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("roles").toString()
                        .replaceAll("\\[","")
                        .replaceAll("\\]","").split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
//
        System.out.println("authorities = " + claims.get("roles", ArrayList.class));
//
//        UserDetails userDetails = new User(claims.getSubject(), "", claims.get("roles", ArrayList.class));

        UserDetails userDetails = new User(claims.getSubject(), "", authorities);

        *//*UserDetails userDetails = Member.builder()
                .email(claims.getSubject())
                .roles(claims.get("roles", ArrayList.class))
                .build();*//*
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }*/

    /*public Jws<Claims> parseClaims(String accessToken) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken);
    }*/
}
