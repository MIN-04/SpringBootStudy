package spring.study.Member.domain.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import spring.study.Member.domain.aggregates.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
//    Member save(Member member);
//    void deleteById(Long id);
//    Optional<Member> findById(Long id);
//
////    void delete(Member member);
//
//    //이거 안쓰고 해보기
//    @Transactional //https://breakcoding.tistory.com/333 오류내용 해결책
//    void deleteByEmail(String email);
////    void deleteById(String email);
//    //List<Member> findAll();
//
//    // return 값이 Page여도 사용 가능
//    Page<Member> findAll(Pageable pageable);
//    Optional<Member> findMemberByEmailOrMemberBasicInfo_MobileNum(String email, String mobileNum);
//    Optional<Member> findByEmail(String email);

//    Optional<Member> findMemberByMemberBasicInfo_MobileNum(String mobileNum);
//    Optional<Member> findByMobileNum(String mobileNum);
}
