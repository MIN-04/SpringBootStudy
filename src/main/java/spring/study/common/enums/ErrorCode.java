package spring.study.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    DUPLICATED_MEMBER(CONFLICT, "이미 존재하는 회원입니다."),
    DUPLICATED_EMAIL(CONFLICT, "중복된 이메일입니다."),
    DUPLICATED_MOBILENUM(CONFLICT, "중복된 전화번호입니다."),
    FAIL_VALIDATE(BAD_REQUEST, "유효성 통과 실패"),
    NOT_EXIST_MEMBER(BAD_REQUEST, "회원이 존재하지 않습니다."),
    WRONG_PASSWORD(BAD_REQUEST, "잘못된 비밀번호입니다."),
    FAIL_LOGIN(INTERNAL_SERVER_ERROR, "로그인 실패");

    private final HttpStatus httpStatus;
    private final String errorMsg;

}
