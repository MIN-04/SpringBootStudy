package spring.study.common.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
@Builder
public class ResponseMessage {
    private final HttpStatus httpStatus;
    private final String message; //ErrorCode message
    private final String detailMsg; //ValidationMsgCode message
    private final Object resultData;
}
