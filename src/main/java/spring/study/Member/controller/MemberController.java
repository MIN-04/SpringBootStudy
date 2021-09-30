package spring.study.Member.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import spring.study.Member.application.MemberService;
import spring.study.Member.controller.dto.MemberJoinRequestDTO;
import spring.study.Member.controller.dto.MemberModifyRequestDTO;
import spring.study.Member.controller.dto.mapper.MemberRequestMapper;
import spring.study.Member.controller.validations.EmailValidation;
import spring.study.Member.domain.aggregates.Member;
import spring.study.Member.domain.commands.MemberCommand;
import spring.study.common.enums.SuccessCode;
import spring.study.common.responses.ResponseMessage;

import javax.validation.Valid;

import static spring.study.common.enums.SuccessCode.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/member")
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
//        return new ResponseMessage(sc.getHttpStatus(), sc.getSuccessMsg(), null, result);
        return ResponseMessage.builder()
                .httpStatus(sc.getHttpStatus())
                .message(sc.getSuccessMsg())
                .resultData(result)
                .build();
    }

    /**
     * 회원 가입
     * @param dto
     * @return
     */
    @PostMapping("/new")
    public ResponseEntity join(@RequestBody @Valid MemberJoinRequestDTO dto) {

        log.info("[join] dto = {}", dto);

        MemberCommand command = mapper.toCommand(dto);
        log.info("[join] command = {}", command);

        Member result = memberService.join(command);
        log.info("[join] result = {}", result.toString());

        ResponseMessage rm = setResponseMessage(SUCCESS_JOIN_MEMBER, result);

        return ResponseEntity.ok(rm);
//        return new ResponseEntity(rm, HttpStatus.OK);
//        return new ResponseEntity<>(SUCCESS_JOIN_MEMBER.getDetail(), SUCCESS_JOIN_MEMBER.getHttpStatus());
//        return SuccessResponse.toResponseEntity(SUCCESS_JOIN_MEMBER);

    }

    /**
     * 회원 수정
     * @param dto
     * @return
     */
    @PostMapping("/modify")
    public ResponseEntity modify(@RequestBody @Valid MemberModifyRequestDTO dto) {

        log.info("[modify] dto = {}", dto);

        MemberCommand command = mapper.toCommand(dto);
        log.info("[modify] command = {}", command);

        Member result = memberService.modify(command);
        log.info("[modify] result = {}", result.toString());

        ResponseMessage rm = setResponseMessage(SUCCESS_MODIFY_MEMBER, result);

        return ResponseEntity.ok(rm);
//        return new ResponseEntity(rm, HttpStatus.OK);
    }

    /**
     * 회원 삭제
     * @return
     */
    @GetMapping("/delete/{email}")
    @Validated
    public ResponseEntity delete(@PathVariable @EmailValidation String email) {
        log.info("[delete] email = {}", email);
        memberService.delete(email);
        ResponseMessage rm = setResponseMessage(SUCCESS_DELETE_MEMBER, null);

        return ResponseEntity.ok(rm);
//        return new ResponseEntity(rm, HttpStatus.OK);
    }

    /**
     * 회원 목록 조회
     * @param page : 페이지 index
     * @param pageCount : 한 페이지당 데이터 수
     * @return
     */
    @GetMapping("/findAll/{page}/{pageCount}")
    public ResponseEntity findAll(@PathVariable int page, @PathVariable int pageCount){

        log.info("[findAll] page = {}, pageCount = {}", page, pageCount);

        // page(현재 페이지)는 0에서 부터 시작하여 받은 page 값-1을 해준다.
        Page<Member> memberList = memberService.findAll(page-1, pageCount);
        ResponseMessage rm = setResponseMessage(SUCCESS_FINDALL_MEMBER, memberList);
        return ResponseEntity.ok(rm);
//        return new ResponseEntity(rm, HttpStatus.OK);
    }

}
