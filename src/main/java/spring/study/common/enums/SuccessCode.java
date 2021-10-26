package spring.study.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.OK;

@Getter
@AllArgsConstructor
public enum SuccessCode {

    SUCCESS_JOIN_MEMBER(OK, "회원 가입 성공"),
    SUCCESS_MODIFY_MEMBER(OK, "회원 수정 성공"),
    SUCCESS_DELETE_MEMBER(OK, "회원 삭제 성공"),
    SUCCESS_FINDALL_MEMBER(OK, "회원 목록 조회 성공"),
    SUCCESS_SAVE_PRODUCT(OK, "상품 등록 성공"),
    SUCCESS_FINDALL_PRODUCT(OK, "상품 목록 조회 성공"),
    SUCCESS_LOGIN(OK, "로그인 성공");

    private final HttpStatus httpStatus;
    private final String successMsg;
}
