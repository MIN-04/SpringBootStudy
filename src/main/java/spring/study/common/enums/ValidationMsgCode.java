package spring.study.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ValidationMsgCode {
    /**
     * 유효성 검사 디테일한 메시지
     */
    BLANK_VALIDATE("값을 입력해주세요."), //값이 없을 때
    EMAIL_NOT_MATCH("이메일 형식이 맞지 않습니다."),
    MOBILENUM_NOT_MATCH("휴대전화 형식이 맞지 않습니다."),
    NAME_NOT_MATCH("이름 형식이 맞지 않습니다."),
    PASSWORD_NOT_MATCH("패스워드 형식이 맞지 않습니다.");

    private final String validationMsg;
}
