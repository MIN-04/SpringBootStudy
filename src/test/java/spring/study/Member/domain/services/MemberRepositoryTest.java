package spring.study.Member.domain.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import spring.study.Member.domain.aggregates.Member;
import spring.study.Member.domain.valueObjects.MemberAddressInfo;
import spring.study.Member.domain.valueObjects.MemberBasicInfo;

import static org.assertj.core.api.Assertions.assertThat;

//@DataJpaTest
@SpringBootTest
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    Member member;

    @BeforeEach
    void setUp() {

        MemberBasicInfo memberBasicInfo = MemberBasicInfo.builder()
                .password("mandy1!")
                .name("mandy")
                .mobileNum("010-1111-1111")
                .gender("F")
                .birthday("950424")
                .build();

        MemberAddressInfo memberAddressInfo = MemberAddressInfo.builder()
                .address("강서구")
                .build();

        member = Member.builder()
                .email("mandy@plgrim.com")
                .memberBasicInfo(memberBasicInfo)
                .memberAddressInfo(memberAddressInfo)
                .build();
    }

    @Test
    @DisplayName("회원 가입")
    void save(){
        //given
        //when
        Member result = memberRepository.save(member);

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(member); //객체 안에 객체 비교
    }

    @Test
    @DisplayName("이메일 또는 전화번호 중복 찾기")
    void findMemberByEmailOrMemberBasicInfo_MobileNum() {
    //given
        //when
        //then
    }

    @Test
    @DisplayName("회원 목록 검색")
    void findAll() {
        //given
        //when
        //then
    }

    @Test
    @DisplayName("회원 삭제")
    void delete() {
        //given
        //when
        //then
    }
}