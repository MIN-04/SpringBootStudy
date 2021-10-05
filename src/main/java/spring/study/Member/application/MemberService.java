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

import static spring.study.common.enums.ErrorCode.*;

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
        log.info("[join - Service] command = {}", command);

        //MemberCommand -> Member
        Member member = Member.builder()
                .email(command.getEmail())
                .memberBasicInfo(command.getBasicInfo())
                .memberAddressInfo(command.getAddressInfo())
                .build();
        log.info("[join - Service] member = {}", member);

        //아이디 중복체크 (이메일과 번호)
        memberRepository.findMemberByEmailOrMemberBasicInfo_MobileNum(member.getEmail(),
                member.getMemberBasicInfo().getMobileNum())
                .ifPresent(m -> {
                    throw new CustomException(DUPLICATED_MEMBER);
                });

        //회원 저장
        return memberRepository.save(member);
    }

    /**
     * 회원 수정
     */
    public Member modify(MemberCommand command) {
        log.info("[modify - Service] command = {}", command);

        //MemberCommand -> Member
        Member member = Member.builder()
                .id(command.getId())
                .email(command.getEmail())
                .memberBasicInfo(command.getBasicInfo())
                .memberAddressInfo(command.getAddressInfo())
                .build();
        log.info("[modify - Service] member = {}", member);

        //수정할 회원이 있는지 찾기
        if(memberRepository.findById(member.getId()).isEmpty()){
            throw new CustomException(NOT_EXIST_MEMBER);
        }

        //회원 수정
        return memberRepository.save(member);
    }

    /**
     * 회원 탈퇴
     */
    public void delete(String email) {

        log.info("[delete] email = {}", email);

        //삭제할 회원이 있는지 찾기
        if(memberRepository.findByEmail(email).isEmpty()){
            throw new CustomException(NOT_EXIST_MEMBER);
        }

        memberRepository.deleteByEmail(email);

        //회원 삭제가 되었는지 확인 (회원 찾기)
        memberRepository.findByEmail(email)
                .ifPresent(m -> {
                    throw new CustomException(FAIL_DELETE_MEMBER);
                });
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
