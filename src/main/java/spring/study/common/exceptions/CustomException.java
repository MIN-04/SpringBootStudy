package spring.study.common.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import spring.study.common.enums.ErrorCode;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException{
    private final ErrorCode errorCode;
}
