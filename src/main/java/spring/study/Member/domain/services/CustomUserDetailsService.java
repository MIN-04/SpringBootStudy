package spring.study.Member.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import spring.study.Member.infraStructure.repository.MemberJPARepository;
import spring.study.common.exceptions.CustomException;

import static spring.study.common.enums.ErrorCode.NOT_EXIST_MEMBER;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberJPARepository memberRepository;

    @Autowired
    public CustomUserDetailsService(MemberJPARepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * 상세 정보를 조회하는 메서드, 사용자의 계정 정보와 권한을 갖는 UserDetails 인터페이스를 반환해야 한다.
     * @param username : 로그인 시 입력한 아이디, Spring Security에서는 username이라는 이름으로 사용
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Optional<Member> memberWrapper = memberRepository.findByEmail(username);
//
//        //유저 검증에 실패했다면 예외 발생
//        if (memberWrapper.isEmpty()) throw new UsernameNotFoundException(username);
//
//        Member member = memberWrapper.get();

        //SpringSecurity에서 제공하는 UserDetails를 구현한 User을 반환
        //생성자의 각 매개변수는 순서대로 아이디, 비밀번호, 권한리스트
        return memberRepository.findByEmail(username)
                .orElseThrow(() -> new CustomException(NOT_EXIST_MEMBER));
//                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }
}
