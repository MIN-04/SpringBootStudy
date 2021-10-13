package spring.study.Member.application;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import spring.study.Member.domain.aggregates.Member;
import spring.study.Member.domain.valueObjects.MemberAddressInfo;
import spring.study.Member.domain.valueObjects.MemberBasicInfo;
import spring.study.Member.infraStructure.repository.MemberJPARepository;

import java.util.Arrays;

import static org.mockito.BDDMockito.willReturn;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Service] 회원 목록 조회 Test")
class MemberServiceFindAllTest {

    @InjectMocks
    MemberService memberService;

    @Mock
    MemberJPARepository memberRepository;

    static Member member1;
    static Member member2;

    @BeforeAll
    static void setUp() {
        member1 = Member.builder()
                .id(1L)
                .email("hong@naver.com")
                .memberBasicInfo(MemberBasicInfo.builder()
                        .password("hong1!")
                        .name("홍길동")
                        .mobileNum("010-1111-2222")
                        .gender("M")
                        .birth("001122")
                        .build())
                .memberAddressInfo(MemberAddressInfo.builder()
                        .address("Seoul")
                        .build())
                .build();

        member2 = Member.builder()
                .id(2L)
                .email("park@naver.com")
                .memberBasicInfo(MemberBasicInfo.builder()
                        .password("park1!")
                        .name("박길동")
                        .mobileNum("010-1111-3333")
                        .gender("M")
                        .birth("000229")
                        .build())
                .memberAddressInfo(MemberAddressInfo.builder()
                        .address("Seoul")
                        .build())
                .build();
    }

    @Test
    @DisplayName("회원 목록 조회 성공")
    void findAll() {
        //given
        int page = 0, pageCount = 10;
        PageRequest pageRequest = PageRequest.of(page, pageCount);

        Page<Member> testPage = new PageImpl<>(Arrays.asList(member1, member2));

        willReturn(testPage)
                .given(memberRepository).findAll(pageRequest);

        //when
        Page<Member> result = memberService.findAll(page, pageCount);

        //then
        Assertions.assertThat(result).isEqualTo(testPage);
    }

}