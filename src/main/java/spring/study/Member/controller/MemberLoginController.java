package spring.study.Member.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import spring.study.Member.application.MemberLoginService;
import spring.study.Member.application.MemberService;
import spring.study.Member.controller.dto.MemberRequestLoginDTO;
import spring.study.Member.controller.dto.mapper.MemberRequestMapper;
import spring.study.Member.domain.commands.MemberCommand;
import spring.study.common.enums.SuccessCode;
import spring.study.common.responses.ResponseMessage;

import static spring.study.common.enums.SuccessCode.SUCCESS_LOGIN;

@Slf4j
@RestController
public class MemberLoginController {

    private final MemberLoginService memberLoginService;
    private final MemberRequestMapper mapper;

    @Autowired
    public MemberLoginController(MemberLoginService memberLoginService) {
        this.memberLoginService = memberLoginService;
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
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseMessage> login(@RequestBody MemberRequestLoginDTO dto) {
        log.info("[login - Controller] dto = {}", dto);

        //MemberRequestJoinDTO -> Membercommand
        MemberCommand command = mapper.toCommand(dto);


        String result = memberLoginService.login(command);
        log.info("[login - Controller] result = {}", result);

        //응답 메시지 만드는 메서드 호출
        ResponseMessage rm = setResponseMessage(SUCCESS_LOGIN, result);

        return ResponseEntity.ok(rm);

    }
}
