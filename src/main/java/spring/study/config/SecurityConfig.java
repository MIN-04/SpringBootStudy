package spring.study.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import spring.study.Member.domain.services.CustomUserDetailsService;
import spring.study.common.auth.filters.JwtAuthenticationFilter;
import spring.study.common.auth.providers.JwtTokenProvider;
import spring.study.common.exceptions.CustomAuthenticationEntryPoint;

@EnableWebSecurity(debug = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public SecurityConfig(CustomUserDetailsService customUserDetailsService, JwtTokenProvider jwtTokenProvider) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * HTTP 요청에 대한 웹 기반 보안을 구성
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().disable()	// security에서 기본으로 생성하는 login페이지 사용 안 함
                .csrf().disable()	// csrf 사용 안 함 == REST API 사용하기 때문에
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)	// JWT인증사용하므로 세션 사용  함
                .and()
                .authorizeRequests() // 다음 리퀘스트에 대한 사용권한 체크
                .antMatchers("/login/**", "/member/members/new").permitAll() // 가입 및 인증 주소는 누구나 접근가능
                .anyRequest().hasRole("MEMBER") // 그외 나머지 요청은 모두 인증된 회원만 접근 가능
                .and()
                .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        // JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 전에 넣는다
    }

    /**
     * Spring Security에서 모든 인증은 AuthenticationManager를 통해 이루어진다.
     * 이것을 생성하기 위해 AuthenticationManagerBuilder 사용
     */
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(customUserDetailsService);
////        auth.userDetailsService(memberService).passwordEncoder(passwordEncoder());
//    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(customUserDetailsService);
//    }
}
