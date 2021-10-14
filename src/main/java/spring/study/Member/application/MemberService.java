package spring.study.Member.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import spring.study.Member.domain.aggregates.Member;
import spring.study.Member.domain.commands.MemberCommand;
import spring.study.Member.infraStructure.repository.MemberJPARepository;
import spring.study.common.exceptions.CustomException;

import java.util.List;

import static spring.study.common.enums.ErrorCode.DUPLICATED_MEMBER;
import static spring.study.common.enums.ErrorCode.NOT_EXIST_MEMBER;

@Slf4j
@Service
public class MemberService {

    private MemberJPARepository memberRepository;

    @Autowired
    public MemberService(MemberJPARepository memberRepository) {
        this.memberRepository = memberRepository;
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
                .memberBasicInfo(command.getBasicInfo())
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
     * 21.10.06 수정
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

        //수정할 회원이 있는지 찾기 -> 없을 경우 Exception 발생
        if(memberRepository.findById(member.getId()).isEmpty()){
            throw new CustomException(NOT_EXIST_MEMBER);
        }

        //수정할 이메일, 전화번호가 다른 멤버와 중복인지 확인 -> 수정할 Member Id와 찾은 Member Id 비교
        //member의 mobileNum
        String mobileNum = member.getMemberBasicInfo().getMobileNum(); 
        //중복된 멤버 찾기
        List<Member> memberList = memberRepository.findByEmailOrMemberBasicInfo_MobileNum(member.getEmail(), mobileNum);
        log.info("[modify - Service] memberList = {}", memberList);

        Long memberId = member.getId();
        if(!memberList.isEmpty()) {
            memberList.forEach(m -> {
                if (!memberId.equals(m.getId())) {
                    throw new CustomException(DUPLICATED_MEMBER);
                }
            });
        }

        //email 중복 찾기
        //Optional<Member> emailMember = memberRepository.findByEmail(member.getEmail());
        //log.info("[modify - Service] emailMember = {}", emailMember);

        //mobileNum 중복 찾기
        //String mobileNum = member.getMemberBasicInfo().getMobileNum(); //member의 mobileNum
        //Optional<Member> mobileMember = memberRepository.findIdByMemberBasicInfo_MobileNum(mobileNum);
        //log.info("[modify - Service] mobileMember = {}", mobileMember);

        //수정할 Member Id와 찾은 Member Id 비교할 메서드
        //findDuplicatedMember(member.getId(), emailMember, mobileMember);

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
     * 수정할 Member Id와 찾은 Member Id 비교 메서드
     * @param memberId
     * @param emailMember
     * @param mobileMember
     */
    /*public void findDuplicatedMember(Long memberId, Optional<Member> emailMember, Optional<Member> mobileMember) {
        //중복된 이메일을 가진 회원이 존재할 때
        if (emailMember.isPresent()) {
            //수정할 member와 찾은 member id가 다를 때 -> Exception 발생
            if (!memberId.equals(emailMember.get().getId())) {
                throw new CustomException(DUPLICATED_EMAIL);
            }
        }
        //중복된 전화번호를 가진 회원이 존재할 때
        if (mobileMember.isPresent()) {
            //수정할 member와 찾은 member id가 다를 때 -> Exception 발생
            if (!memberId.equals(mobileMember.get().getId())) {
                throw new CustomException(DUPLICATED_MOBILENUM);
            }
        }

        *//** 궁금한 점!
         * 이메일과 전화번호 중복체크 할 때 repository 메서드를 하나로 합쳐줬었다.
         * 그런데 수정에서 어떤 것이 중복인지 안알려주면 안된다고 생각했다.
         * 이메일과 전화번호 중복체크를 나누는게 맞을지 하나로 합치는게 맞을지 궁금
         * 아래는 합친 코드
         * *//*
        *//* if (emailMember.isPresent() || mobileMember.isPresent()) {
            //수정할 member와 찾은 member id가 다를 때 -> Exception 발생
            if (!memberId.equals(emailMember.get().getId()) || !memberId.equals(mobileMember.get().getId())) {
                throw new CustomException(DUPLICATED_MEMBER);
            }
        }*//*
    }*/
}
