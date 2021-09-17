package spring.study.common.exceptions;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import spring.study.common.enums.ErrorCode;
import spring.study.common.responses.ResponseMessage;

import javax.validation.ConstraintViolationException;

import static spring.study.common.enums.ErrorCode.FAIL_VALIDATE;

@Slf4j
@RestControllerAdvice
public class MemberExceptionHandler extends ResponseEntityExceptionHandler {

    private ResponseMessage setResponseMessage(ErrorCode errorCode, String detailMsg) {
//        return new ResponseMessage(errorCode.getHttpStatus(), errorCode.getErrorMsg(), detailMsg);
        return ResponseMessage.builder()
                .httpStatus(errorCode.getHttpStatus())
                .message(errorCode.getErrorMsg())
                .detailMsg(detailMsg)
                .build();
    }

    /**
     * validation 검사 예외
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

//    @ExceptionHandler(ConstraintViolationException.class)
//    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e) {
//
//        ResponseMessage rm = setResponseMessage(FAIL_VALIDATE);
//        return new ResponseEntity<>(rm, HttpStatus.BAD_REQUEST);
//    }

    /**
     * 회원 중복 예외
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<String> handleDuplicatedUserException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();

        log.error("MemberExceptionHandler throw DuplicatedUserException : {}, message = {}, HttpStatus = {}",
                errorCode, errorCode.getErrorMsg(), errorCode.getHttpStatus());

        ResponseMessage rm = setResponseMessage(errorCode, null);

        return new ResponseEntity<>(errorCode.getErrorMsg(), errorCode.getHttpStatus());
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
}
