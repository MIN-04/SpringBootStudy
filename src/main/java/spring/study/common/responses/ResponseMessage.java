package spring.study.common.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class ResponseMessage {
    private final HttpStatus httpStatus;
    private final String message;
    private final String detailMsg;
    private Object resultData;

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
