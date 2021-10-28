package spring.study.Member.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import spring.study.Member.application.MemberLoginService;
import spring.study.Member.infraStructure.rest.OauthService;
import spring.study.Member.controller.dto.MemberRequestLoginDTO;
import spring.study.Member.controller.dto.mapper.MemberRequestMapper;
import spring.study.Member.domain.commands.MemberCommand;
import spring.study.Member.infraStructure.rest.OAuthToken;
import spring.study.common.enums.SuccessCode;
import spring.study.common.responses.ResponseMessage;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static spring.study.common.client_gitignore.ClientProfile.GOOGLE_SNS_CLINET_ID;
import static spring.study.common.enums.SocialLoginType.GOOGLE;
import static spring.study.common.enums.SuccessCode.SUCCESS_LOGIN;
import static spring.study.common.paths.LoginUrl.*;

@Slf4j
@RestController
@RequestMapping(LOGIN_ROOT_PATH)
public class MemberLoginController {

    private final MemberLoginService memberLoginService;
    private final OauthService oauthService;
    private final MemberRequestMapper mapper;

    @Autowired
    public MemberLoginController(MemberLoginService memberLoginService, OauthService oauthService) {
        this.memberLoginService = memberLoginService;
        this.oauthService = oauthService;
        this.mapper = new MemberRequestMapper();
    }

    /**
     * 응답 메시지 만드는 메서드
     * 파라미터 sc 이부분 노란박스 수정하기
     */
    private ResponseMessage setResponseMessage(SuccessCode sc, Object result) {
        return ResponseMessage.builder()
                .httpStatus(sc.getHttpStatus())
                .message(sc.getSuccessMsg())
                .resultData(result)
                .build();
    }

    /**
     * 로컬 로그인
     */
    @PostMapping
    public ResponseEntity<ResponseMessage> login(@RequestBody MemberRequestLoginDTO dto) {
        log.info("[MemberLoginController - login()] dto = {}", dto);

        //MemberRequestJoinDTO -> Membercommand
        MemberCommand command = mapper.toCommand(dto);


        String result = memberLoginService.loginLocal(command);
        log.info("[login - Controller] result = {}", result);

        //응답 메시지 만드는 메서드 호출
        ResponseMessage rm = setResponseMessage(SUCCESS_LOGIN, result);

        return ResponseEntity.ok(rm);

    }

    /**
     * 사용자로부터 SNS 로그인 요청을 Social Login Type을 받아 처리
     * 예를 들어 [구글 아이디로 로그인]을 클릭하면 이 컨트롤러로 요청 받는 것
    * @param socialLoginType (GOOGLE, NAVER, KAKAO)
     */
    @GetMapping(LOGIN_SOCIAL_TYPE)
    public void socialLoginType(@PathVariable String socialLoginType, HttpServletResponse response) throws IOException {
        log.info("[MemberLoginController - socialLoginType()] 사용자로부터 SNS 로그인 요청을 받음 :: socialLoginType = {}", socialLoginType);

        //redirect URL 분기처리 할 Service 호출
        //redirect URL : 로그인 하는 화면을 나타내는 거라고 생각하면 된다.
        //String redirectionUrl = oauthService.request(socialLoginType);
        String redirectionUrl = "";
        switch (socialLoginType) {
            case "google": {
                MultiValueMap<String, String> query = new LinkedMultiValueMap<>() {{
                    add("scope", "profile");
                    add("response_type", "code");
                    add("client_id", GOOGLE_SNS_CLINET_ID);
                    add("redirect_uri", GOOGLE.getRedirectionUrl());
                    add("access_type", "offline");
                }};

                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(GOOGLE.getBaseUrl())
                        .queryParams(query);
                redirectionUrl = builder.toUriString();
                break;
            }
            case "kakao":
                break;
            default:
                throw new IllegalStateException("알 수 없는 소셜 로그인 형식입니다.");
        }

        response.sendRedirect(redirectionUrl);
        //response.sendRedirect(redirectionUrl);
    }

    @GetMapping(LOGIN_SOCIAL_CALLBACK)
    public ResponseEntity<ResponseMessage> callBack(@PathVariable String socialLoginType, @RequestParam String code) {
        log.info("[MemberLoginController - callBack()] 소셜 로그인 API 서버로부터 받은 code :: socialLoginType = {}, code = {}", socialLoginType, code);

        OAuthToken result = memberLoginService.loginSNS(code);

        //응답 메시지 만드는 메서드 호출
        ResponseMessage rm = setResponseMessage(SUCCESS_LOGIN, result);

        return ResponseEntity.ok(rm);
        //return oauthService.requestAccessToken(code);
    }
}
