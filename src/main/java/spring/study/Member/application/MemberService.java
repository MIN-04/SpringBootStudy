package spring.study.Member.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import spring.study.Member.domain.aggregates.Member;
import spring.study.Member.domain.commands.MemberCommand;
import spring.study.Member.domain.services.MemberRepository;
import spring.study.common.exceptions.CustomException;

import static spring.study.common.enums.ErrorCode.DUPLICATED_MEMBER;

@Slf4j
@Service
public class MemberService {

    private MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * 회원 가입
     */
    public Member join(MemberCommand command) {
        Member member = Member.builder()
                .email(command.getEmail())
                .memberBasicInfo(command.getBasicInfo())
                .memberAddressInfo(command.getAddressInfo())
                .build();

        log.info("[join] member = {}", member);

        //아이디 중복체크 (이메일과 번호)
        memberRepository.findMemberByEmailOrMemberBasicInfo_MobileNum(member.getEmail(),
                member.getMemberBasicInfo().getMobileNum())
                .ifPresent(m -> {
                    throw new CustomException(DUPLICATED_MEMBER);
                });

        /* memberRepository.findMemberByEmailAndMemberBasicInfo_MobileNum(member.getEmail(),
                member.getMemberBasicInfo().getMobileNum())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
        memberRepository.findByEmail(member.getEmail())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
        //이메일 중복체크
        memberRepository.findByMobileNum(member.getMemberValueObject().getMobileNum())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });*/

        //회원 저장
        return memberRepository.save(member);
    }

    /**
     * 회원 수정
     */
    public Member modify(MemberCommand command) {
        Member member = Member.builder()
                .email(command.getEmail())
                .memberBasicInfo(command.getBasicInfo())
                .memberAddressInfo(command.getAddressInfo())
                .build();

        log.info("[modify] member = {}", member);

        return memberRepository.save(member);
    }

    /**
     * 회원 탈퇴
     */
    public void delete(String email) {
        Member member = Member.builder()
                .email(email)
                .build();

        log.info("[delete] member = {}", member);

        memberRepository.delete(member);
    }

    /**
     * 회원 목록 조회
     *
     * @param page      : 페이지 index
     * @param pageCount : 한 페이지당 데이터 수
     * @return
     */
    public Page<Member> findAll(int page, int pageCount) {
        PageRequest pageRequest = PageRequest.of(page, pageCount);
        log.info("[findAll] pageRequest = {}", pageRequest);

        return memberRepository.findAll(pageRequest);
    }

}
