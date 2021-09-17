package spring.study.common.responses;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
@Builder
public class ResponseMessage {
    private final HttpStatus httpStatus;
    private final String message;
    private final String detailMsg;
    private final Object resultData;

//    public ResponseMessage(HttpStatus status, String message) {
//        this.httpStatus = status;
//        this.message = message;
//    }
//
//    public ResponseMessage(HttpStatus status, String message, Object resultData) {
//        this.httpStatus = status;
//        this.message = message;
//        this.resultData = resultData;
//    }
}
