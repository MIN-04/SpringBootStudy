package spring.study.Member.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import spring.study.Member.application.MemberService;
import spring.study.Member.controller.dto.MemberRequestJoinDTO;
import spring.study.Member.controller.dto.MemberRequestModifyDTO;
import spring.study.Member.controller.dto.mapper.MemberRequestMapper;
import spring.study.Member.domain.aggregates.Member;
import spring.study.Member.domain.commands.MemberCommand;
import spring.study.common.enums.SuccessCode;
import spring.study.common.responses.ResponseMessage;

import javax.validation.Valid;
import java.util.List;

import static spring.study.common.enums.SuccessCode.*;
import static spring.study.common.paths.MemberUrl.*;

@Slf4j
@Validated
@RestController
@RequestMapping(MEMBER_ROOT_PATH)
public class MemberController {

    private final MemberService memberService;
    private final MemberRequestMapper mapper;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
        this.mapper = new MemberRequestMapper();
    }

    /**
     * 응답 메시지 만드는 메서드
     */
    private ResponseMessage setResponseMessage(SuccessCode sc, Object result) {
        return ResponseMessage.builder()
                .httpStatus(sc.getHttpStatus())
                .message(sc.getSuccessMsg())
                .resultData(result)
                .build();
    }

    /**
     * 회원 가입
     * 21.10.15 피드백 (10.18 적용 완료)
     * 반환값인 ResponseEntity에 <> 사용해야 한다.
     */
    @PostMapping(MEMBERS_NEW)
    public ResponseEntity<ResponseMessage> join(@RequestBody @Valid MemberRequestJoinDTO dto) {

        log.info("[join - Controller] dto = {}", dto);

        //MemberRequestJoinDTO -> Membercommand
        MemberCommand command = mapper.toCommand(dto);
        log.info("[join - Controller] command = {}", command);

        //서비스의 회원 저장 메서드 호출 - return Member
        Member result = memberService.join(command);
        log.info("[join - Controller] result = {}", result.toString());

        //응답 메시지 만드는 메서드 호출
        ResponseMessage rm = setResponseMessage(SUCCESS_JOIN_MEMBER, result);

        return ResponseEntity.ok(rm);
        //return new ResponseEntity(rm, HttpStatus.OK);
        //return new ResponseEntity<>(SUCCESS_JOIN_MEMBER.getDetail(), SUCCESS_JOIN_MEMBER.getHttpStatus());
        //return SuccessResponse.toResponseEntity(SUCCESS_JOIN_MEMBER);

    }

    /**
     * 회원 수정
     * 21.10.15 피드백 (10.18 적용 완료)
     * 반환값인 ResponseEntity에 <> 사용해야 한다.
     */
    @PostMapping(MEMBERS_PATH)
    public ResponseEntity<ResponseMessage> modify(@RequestBody @Valid MemberRequestModifyDTO dto) {

        log.info("[modify] dto = {}", dto);

        //MemberRequestModifyDTO -> Membercommand
        MemberCommand command = mapper.toCommand(dto);
        log.info("[modify] command = {}", command);

        //서비스의 회원 수정 메서드 호출 - return Member
        Member result = memberService.modify(command);
        log.info("[modify] result = {}", result.toString());

        //응답 메시지 만드는 메서드 호출
        ResponseMessage rm = setResponseMessage(SUCCESS_MODIFY_MEMBER, result);

        return ResponseEntity.ok(rm);
        //return new ResponseEntity(rm, HttpStatus.OK);
    }

    /**
     * 회원 삭제
     * 21.10.01 피드백 (10.05 수정 완료)
     * 회원 삭제 같은 경우 @GetMapping 말고 @DeleteMapping 사용 → HTTP delete 메서드 사용
     * url에 delete를 사용하지 않는다.
     * 21.10.15 피드백 (10.18 적용 완료)
     * 반환값인 ResponseEntity에 <> 사용해야 한다.
     */
    @DeleteMapping(MEMBERS_DELETE)
    public ResponseEntity<ResponseMessage> delete(@PathVariable Long id) {

        log.info("[delete - Controller] id = {}", id);

        //서비스의 회원 삭제 메서드 호출 - void
        memberService.delete(id);

        //응답 메시지 만드는 메서드 호출
        ResponseMessage rm = setResponseMessage(SUCCESS_DELETE_MEMBER, null);

        return ResponseEntity.ok(rm);
        //return new ResponseEntity(rm, HttpStatus.OK);
    }

    /**
     * 회원 목록 조회
     * @param page : 페이지 index
     * @param pageCount : 한 페이지당 데이터 수
     * 21.10.01 피드백 (10.05 수정 완료)
     * 회원 목록 조회 같은 건 @Pathvariable {page}/{pageCount}로 하지 않는다. → queryString 으로 받기
     * member/members?page=1&pageCount=10
     * 21.10.15 피드백 (10.18 적용 완료)
     * 반환값인 ResponseEntity에 <> 사용해야 한다.
     */
    @GetMapping(MEMBERS_PATH)
    public ResponseEntity<ResponseMessage> findAll(@RequestParam int page, @RequestParam int pageCount){

        log.info("[findAll - Controller] page = {}, pageCount = {}", page, pageCount);

        //서비스의 회원 목록 조회 메서드 호출 - return Page
        Page<Member> members = memberService.findAll(page, pageCount);
        //Page 객체에서 회원 목록만 memberList에 담기기
        List<Member> memberList = members.getContent();
        log.info("[findAll - Controller] memberList = {}", memberList);

        //응답 메시지 만드는 메서드 호출
        ResponseMessage rm = setResponseMessage(SUCCESS_FINDALL_MEMBER, memberList);

        return ResponseEntity.ok(rm);
        //return new ResponseEntity(rm, HttpStatus.OK);
    }

}
