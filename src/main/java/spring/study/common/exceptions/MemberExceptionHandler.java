package spring.study.common.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import spring.study.common.enums.ErrorCode;
import spring.study.common.responses.ResponseMessage;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import static spring.study.common.enums.ErrorCode.FAIL_VALIDATE;

@Slf4j
@RestControllerAdvice
public class MemberExceptionHandler extends ResponseEntityExceptionHandler {

    private ResponseMessage setResponseMessage(ErrorCode errorCode, String detailMsg) {
        return ResponseMessage.builder()
                .httpStatus(errorCode.getHttpStatus())
                .message(errorCode.getErrorMsg())
                .detailMsg(detailMsg)
                .build();
    }

    /**
     * @RequestBody validation 검사 예외처리
     * @return
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e, HttpHeaders headers, HttpStatus status, WebRequest request) {

        log.error("handleValidException throw MethodArgumentNotValidException : {}",
                e.getBindingResult().getAllErrors().get(0).getDefaultMessage());

        String detailMsg = e.getAllErrors().get(0).getDefaultMessage();
        ResponseMessage rm = setResponseMessage(FAIL_VALIDATE, detailMsg);

        return new ResponseEntity<>(rm, HttpStatus.BAD_REQUEST);
    }

    /**
     * 회원 CustomException
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> handleDuplicatedUserException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();

        log.error("MemberExceptionHandler throw DuplicatedUserException : {}, message = {}, HttpStatus = {}",
                errorCode, errorCode.getErrorMsg(), errorCode.getHttpStatus());

        ResponseMessage rm = setResponseMessage(errorCode, null);

        return new ResponseEntity<>(rm, errorCode.getHttpStatus());

    }

    /**
     * @PathVariable validation 검사 예외처리
     * @return
     */
    //참고
    //https://kapentaz.github.io/spring/Spring-Boo-Bean-Validation-%EC%A0%9C%EB%8C%80%EB%A1%9C-%EC%95%8C%EA%B3%A0-%EC%93%B0%EC%9E%90/#
//    @ExceptionHandler(ConstraintViolationException.class)
//    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e) {
//
//        System.out.println(e.getConstraintViolations());
//        ResponseMessage rm = setResponseMessage(FAIL_VALIDATE, null);
//        return new ResponseEntity<>(rm, HttpStatus.BAD_REQUEST);
////        return new ResponseEntity<>(e., HttpStatus.BAD_REQUEST);
//    }

//    @ExceptionHandler(ValidationException.class)
//    protected String handleCustomException(ValidationException e) {
//        log.error("handleCustomException throw ValidationException : {}", e.getMessage());
//        return e.getMessage();
//    }

    /**
     * return 모양
     */
//        return new ResponseEntity<>(errorCode.getErrorMsg(), errorCode.getHttpStatus());
//        return new ResponseEntity<>(errorCode.getErrorMsg(), errorCode.getHttpStatus());
//        return ResponseEntity
//                .status(errorCode.getHttpStatus())
//                .body(errorCode.getDetailMsg());
//        return ResponseEntity
//                .status(errorCode.getHttpStatus())
//                .body(ErrorResponse.builder()
//                        .status(errorCode.getHttpStatus().value())
//                        .message(errorCode.getDetail())
//                        .timeStamp(LocalDateTime.now())
//                        .build()
//                );
//        return ErrorResponse.toResponseEntity(e.getErrorCode());
}
