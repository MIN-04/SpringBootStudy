package spring.study.Member.infraStructure.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@DataJpaTest
@SpringBootTest
@Transactional
@DisplayName("[Repository] Jpa Repository 테스트")
class MemberJPARepositoryTest {
    @Autowired
    MemberJPARepository memberRepository;

    Member member;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .email("hong@naver.com")
                .memberBasicInfo(MemberBasicInfo.builder()
                        .password("abcd1!")
                        .name("홍길동")
                        .mobileNum("010-1111-1112")
                        .gender("F")
                        .birth("001122")
                        .build())
                .memberAddressInfo(MemberAddressInfo.builder()
                        .address("Seoul")
                        .build())
                .build();
    }

    @Test
    @DisplayName("회원 가입")
    void save(){
        //given
        //when
        Member result = memberRepository.save(member);

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(member); //객체 안에 있는 객체 비교
    }

    @Test
    @DisplayName("회원 수정")
    void modify() {
        //given
        Member saveMember = memberRepository.save(member);

        //변경할 정보
        Member updateMember = Member.builder()
                .id(saveMember.getId())
                .email("hongUpdate@naver.com")
                .memberBasicInfo(MemberBasicInfo.builder()
                        .password("abcd1!")
                        .name("홍동길")
                        .mobileNum("010-1111-1113")
                        .gender("F")
                        .birth("001122")
                        .build())
                .memberAddressInfo(MemberAddressInfo.builder()
                        .address("Seoul")
                        .build())
                .build();

        //when
        Member updateResult = memberRepository.save(updateMember);

        //then
        assertThat(updateResult.getId()).isEqualTo(saveMember.getId());
        assertThat(updateResult).usingRecursiveComparison().isEqualTo(updateMember);
    }

    //21.10.01 피드백 (10.06 수정 완료)
    //테스트에서 조건이 다 다른데 같은 동작을 반복해야 할 경우 → @ParaeterizedTest 사용
    @DisplayName("이메일 또는 전화번호 중복 찾기")
    @ParameterizedTest(name = "{index}: {2}")
    @MethodSource("invalidParameters")
    void findMemberByEmailOrMemberBasicInfo_MobileNum(String email, String mobileNum, String testName) {
        //given
        memberRepository.save(member);

        //when
        List<Member> resultList = memberRepository
                .findByEmailOrMemberBasicInfo_MobileNum(email, mobileNum);

        //then
        assertFalse(resultList.isEmpty()); //false여야 통과
        resultList.forEach(m -> {
            assertThat(m).usingRecursiveComparison().isEqualTo(member);
        });

    }

    static Stream<Arguments> invalidParameters() {
        final String EMAIL = "hong@naver.com"; //같은 이메일
        final String MOBILENUM = "010-1111-1112"; //같은 전화번호

        return Stream.of(
                Arguments.of(EMAIL, "010-2222-1111", "이메일만 중복일 때"),
                Arguments.of("hongAAA@naver.com", MOBILENUM, "전화번호만 중복일 때"),
                Arguments.of(EMAIL, MOBILENUM, "이메일, 전화번호 모두 중복일 때")
        );
    }

    @Test
    @DisplayName("회원 목록 검색")
    void findAll() {
        //given
        // member2
        Member member2 = Member.builder()
                .email("park@naver.com")
                .memberBasicInfo(MemberBasicInfo.builder()
                        .password("abcd1!")
                        .name("박땡땡")
                        .mobileNum("010-1111-1113")
                        .gender("M")
                        .birth("991203")
                        .build())
                .memberAddressInfo(MemberAddressInfo.builder()
                        .address("Seoul")
                        .build())
                .build();

        // member3
        Member member3 = Member.builder()
                .email("choi@naver.com")
                .memberBasicInfo(MemberBasicInfo.builder()
                        .password("abcd1!")
                        .name("최영영")
                        .mobileNum("010-1111-1114")
                        .gender("F")
                        .birth("010302")
                        .build())
                .memberAddressInfo(MemberAddressInfo.builder()
                        .address("Seoul")
                        .build())
                .build();

        memberRepository.save(member);
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
    }

    @Test
    @DisplayName("회원 삭제")
    void deleteById() {
        //given
        memberRepository.save(member);

        //when
        memberRepository.deleteById(member.getId());

        //then
        Optional<Member> findOne = memberRepository.findById(member.getId());
        assertTrue(findOne.isEmpty());
    }
}