package spring.study.Member.infraStructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.study.Member.domain.aggregates.Member;
import spring.study.Member.domain.services.MemberRepository;

import java.util.List;
import java.util.Optional;

public interface MemberJPARepository extends MemberRepository, JpaRepository<Member, Long> {

    //이메일, 전화번호 중복체크
    Optional<Member> findMemberByEmailOrMemberBasicInfo_MobileNum(String email, String mobileNum);

    List<Member> findMembersByEmailOrMemberBasicInfo_MobileNum(String email, String mobileNum);

    //이메일 중복체크 (modify에 사용된다.)
    //Optional<Member> findByEmail(String email);

    //전화번호 중복체크 (modify에 사용된다.)
    //Optional<Member> findIdByMemberBasicInfo_MobileNum(String mobilNum);

}
