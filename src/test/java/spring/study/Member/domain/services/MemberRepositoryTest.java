package spring.study.Member.domain.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import spring.study.Member.domain.aggregates.Member;
import spring.study.Member.domain.valueObjects.MemberAddressInfo;
import spring.study.Member.domain.valueObjects.MemberBasicInfo;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@DataJpaTest
@SpringBootTest
@Transactional
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    Member member1;
    Member member2;
    Member member3;

    @BeforeEach
    void setUp() {
        // member1
        MemberBasicInfo memberBasicInfo1 = MemberBasicInfo.builder()
                .password("abcd1!")
                .name("홍길동")
                .mobileNum("010-1111-1111")
                .gender("F")
                .birthday("001122")
                .build();

        MemberAddressInfo memberAddressInfo1 = MemberAddressInfo.builder()
                .address("Seoul")
                .build();

        member1 = Member.builder()
                .email("hong@naver.com")
                .memberBasicInfo(memberBasicInfo1)
                .memberAddressInfo(memberAddressInfo1)
                .build();


        // member2
        MemberBasicInfo memberBasicInfo2 = MemberBasicInfo.builder()
                .password("abcd1!")
                .name("박땡땡")
                .mobileNum("010-1111-2222")
                .gender("M")
                .birthday("991203")
                .build();

        MemberAddressInfo memberAddressInfo2 = MemberAddressInfo.builder()
                .address("Seoul")
                .build();

        member2 = Member.builder()
                .email("park@naver.com")
                .memberBasicInfo(memberBasicInfo2)
                .memberAddressInfo(memberAddressInfo2)
                .build();

        // member3
        MemberBasicInfo memberBasicInfo3 = MemberBasicInfo.builder()
                .password("abcd1!")
                .name("최영영")
                .mobileNum("010-1111-3333")
                .gender("F")
                .birthday("010302")
                .build();

        MemberAddressInfo memberAddressInfo3 = MemberAddressInfo.builder()
                .address("Seoul")
                .build();

        member3 = Member.builder()
                .email("choi@naver.com")
                .memberBasicInfo(memberBasicInfo3)
                .memberAddressInfo(memberAddressInfo3)
                .build();
    }

    @Test
    @DisplayName("회원 가입")
    void save(){
        //given
        //when
        Member result = memberRepository.save(member1);

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(member1); //객체 안에 있는 객체 비교
    }

    @Test
    @DisplayName("이메일 또는 전화번호 중복 찾기")
    void findMemberByEmailOrMemberBasicInfo_MobileNum() {
        //given
        memberRepository.save(member1);

        //when
        //case 1. 이메일만 같을 때
        Optional<Member> result1 = memberRepository
                .findMemberByEmailOrMemberBasicInfo_MobileNum("hong@naver.com", "010-2222-1111");
        //case 2. 전화번호만 같을 때
        Optional<Member> result2 = memberRepository
                .findMemberByEmailOrMemberBasicInfo_MobileNum("hongAAA@naver.com", "010-1111-1111");
        //case 3. 이메일과 전화번호 둘 다 같을 때
        Optional<Member> result3 = memberRepository
                .findMemberByEmailOrMemberBasicInfo_MobileNum("hong@naver.com", "010-1111-1111");

        //then
        //case 1
        assertFalse(result1.isEmpty()); //false여야 통과
        assertThat(result1.get()).usingRecursiveComparison().isEqualTo(member1);
        //case 2
        assertFalse(result2.isEmpty());
        assertThat(result2.get()).usingRecursiveComparison().isEqualTo(member1);
        //case 3
        assertFalse(result3.isEmpty());
        assertThat(result3.get()).usingRecursiveComparison().isEqualTo(member1);

    }

    @Test
    @DisplayName("회원 목록 검색")
    void findAll() {
        //given
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        //when
        int page = 1;
        int pageCount = 2;
        PageRequest pageRequest = PageRequest.of(page, pageCount);
        Page<Member> members = memberRepository.findAll(pageRequest);

        //then
        List<Member> contents = members.getContent();
        assertFalse(contents.isEmpty());

        int dataSize = members.getContent().size();
        assertThat(dataSize <= pageCount).isTrue();

//        assertThat(memberList).usingRecursiveComparison()(member, member2);
        //https://tecoble.techcourse.co.kr/post/2020-11-03-assertJ_methods/
        // containsExactly : 순서를 포함해서 정확히 일치
        // assertThat(memberList).containsExactly(member, member2);
        // assertThat(memberList).contains(member, member2);

    }

    @Test
    @DisplayName("회원 삭제")
    void delete() {
        //given
        memberRepository.save(member1);

        //when
        memberRepository.deleteByEmail(member1.getEmail());

        //then
        Optional<Member> findMember = memberRepository.findByEmail(member1.getEmail());
        assertTrue(findMember.isEmpty());
    }
}