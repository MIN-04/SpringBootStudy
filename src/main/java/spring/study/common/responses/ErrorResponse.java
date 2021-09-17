package spring.study.common.responses;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import spring.study.common.enums.ErrorCode;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponse {

    private final LocalDateTime timeStamp;
    private final int status; //HTTP 상태 값 저장
    private final String message; //예외 메시지 저장

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .status(errorCode.getHttpStatus().value())
                        .message(errorCode.getErrorMsg())
                        .timeStamp(LocalDateTime.now())
                        .build()
                );
    }

}
