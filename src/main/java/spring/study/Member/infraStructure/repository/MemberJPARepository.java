package spring.study.Member.infraStructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.study.Member.domain.aggregates.Member;
import spring.study.Member.domain.services.MemberRepository;

public interface MemberJPARepository extends MemberRepository, JpaRepository<Member, Long> {
}
