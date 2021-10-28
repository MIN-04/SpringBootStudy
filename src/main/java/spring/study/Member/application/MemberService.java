package spring.study.Member.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spring.study.Member.domain.aggregates.Member;
import spring.study.Member.domain.commands.MemberCommand;
import spring.study.Member.domain.valueObjects.MemberBasicInfo;
import spring.study.Member.infraStructure.repository.MemberJPARepository;
import spring.study.common.exceptions.CustomException;

import java.util.Collections;
import java.util.List;

import static spring.study.common.enums.ErrorCode.DUPLICATED_MEMBER;
import static spring.study.common.enums.ErrorCode.NOT_EXIST_MEMBER;

@Slf4j
@Service
public class MemberService{

    private final MemberJPARepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MemberService(MemberJPARepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 회원 가입
     * 21.10.06 수정
     */
    public Member join(MemberCommand command) {
        log.info("[join - Service] command = {}", command);

        //MemberCommand -> Member
        Member member = Member.builder()
                .email(command.getEmail())
                .roles(Collections.singletonList("ROLE_MEMBER"))
                .provider(command.getProvider())
                .memberBasicInfo(MemberBasicInfo.builder()
                        .password(passwordEncoder.encode(command.getBasicInfo().getPassword()))
                        .name(command.getBasicInfo().getName())
                        .mobileNum(command.getBasicInfo().getMobileNum())
                        .gender(command.getBasicInfo().getGender())
                        .birth(command.getBasicInfo().getBirth())
                        .build())
                .memberAddressInfo(command.getAddressInfo())
                .build();
        log.info("[join - Service] member = {}", member);

        //아이디 중복체크 (이메일과 번호)
        List<Member> memberList = memberRepository.findByEmailOrMemberBasicInfo_MobileNum(member.getEmail(),
                member.getMemberBasicInfo().getMobileNum());

        //중복된 회원이 있으면 Exception 발생
        if (!memberList.isEmpty()) throw new CustomException(DUPLICATED_MEMBER);

        //회원 저장
        return memberRepository.save(member);
    }

    /**
     * 회원 수정
     * 21.10.15 피드백 (10.18 피드백 적용)
     * 복잡한 로직은 메서드로 빼기
     */
    public Member modify(MemberCommand command) {
        log.info("[modify - Service] command = {}", command);

        //MemberCommand -> Member
        Member member = Member.builder()
                .id(command.getId())
                .email(command.getEmail())
                .roles(command.getRoles())
                .memberBasicInfo(MemberBasicInfo.builder()
                        .password(passwordEncoder.encode(command.getBasicInfo().getPassword()))
                        .name(command.getBasicInfo().getName())
                        .mobileNum(command.getBasicInfo().getMobileNum())
                        .gender(command.getBasicInfo().getGender())
                        .birth(command.getBasicInfo().getBirth())
                        .build())
                .memberAddressInfo(command.getAddressInfo())
                .build();
        log.info("[modify - Service] member = {}", member);

        //수정할 회원이 있는지 찾기 -> 없을 경우 Exception 발생
        if(memberRepository.findById(member.getId()).isEmpty()){
            throw new CustomException(NOT_EXIST_MEMBER);
        }

        //수정할 이메일, 전화번호가 다른 멤버와 중복인지 확인 메서드
        // -> 수정할 Member Id와 찾은 Member Id 비교
        // -> 다르면 Exception 발생
        findDuplicatedMemberThrowExceptionFn(member);

        //회원 수정
        return memberRepository.save(member);
    }

    /**
     * 회원 탈퇴
     * 21.10.13 수정
     */
    public void delete(Long id) {

        log.info("[delete] id = {}", id);

        //삭제할 회원이 있는지 찾기
        if(memberRepository.findById(id).isEmpty()){
            throw new CustomException(NOT_EXIST_MEMBER);
        }

        //회원 삭제
        memberRepository.deleteById(id);
    }

    /**
     * 회원 목록 조회
     * @param page      : 페이지 index
     * @param pageCount : 한 페이지당 데이터 수
     * @return
     */
    public Page<Member> findAll(int page, int pageCount) {
        PageRequest pageRequest = PageRequest.of(page, pageCount);
        log.info("[findAll - Service] pageRequest = {}", pageRequest);

        //회원 목록 조회
        return memberRepository.findAll(pageRequest);
    }

    /**
     * 이메일 또는 전화번호 중복된 멤버 찾아, 아이디가 다르면 Exception 발생 Function
     * 21.10.27 피드백 (10.28 수정 완료)
     * filter 사용하기
     * @param member
     */
    public void findDuplicatedMemberThrowExceptionFn(Member member) {
        //중복된 멤버 찾기
        List<Member> memberList = memberRepository
                .findByEmailOrMemberBasicInfo_MobileNum(member.getEmail(),
                        member.getMemberBasicInfo().getMobileNum());
        log.info("[modify - Service] memberList = {}", memberList);

        memberList.stream().filter(m -> !m.getId().equals(member.getId()))
                .findAny().ifPresent(m -> {
            throw new CustomException(DUPLICATED_MEMBER);
        });

        /*Long memberId = member.getId();
        if(!memberList.isEmpty()) {
            memberList.forEach(m -> {
                if (!memberId.equals(m.getId())) {
                    throw new CustomException(DUPLICATED_MEMBER);
                }
            });
        }*/
    }
}
