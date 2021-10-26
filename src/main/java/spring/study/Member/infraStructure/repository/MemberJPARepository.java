package spring.study.Member.infraStructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.study.Member.domain.aggregates.Member;
import spring.study.Member.domain.services.MemberRepository;

import java.util.List;
import java.util.Optional;

public interface MemberJPARepository extends MemberRepository, JpaRepository<Member, Long> {

    //이메일, 전화번호가 같은 Member 찾기
    List<Member> findByEmailOrMemberBasicInfo_MobileNum(String email, String mobileNum);

    //이메일로 회원 찾기 (로그인에 사용)
    Optional<Member> findByEmail(String email);

    //전화번호 중복체크 (modify에 사용된다.)
    //Optional<Member> findIdByMemberBasicInfo_MobileNum(String mobilNum);

}
