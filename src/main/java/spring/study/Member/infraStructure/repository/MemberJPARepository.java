package spring.study.Member.infraStructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.study.Member.domain.aggregates.Member;
import spring.study.Member.domain.services.MemberRepository;

import java.util.Optional;

public interface MemberJPARepository extends MemberRepository, JpaRepository<Member, Long> {

    //이메일, 전화번호 중복체크
    Optional<Member> findMemberByEmailOrMemberBasicInfo_MobileNum(String email, String mobileNum);
}
