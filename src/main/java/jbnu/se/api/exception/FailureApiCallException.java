package jbnu.se.api.exception;

import org.springframework.http.HttpStatus;

public class FailureApiCallException extends ApplicationException {

    private static final String MESSAGE = "인증에 실패하였습니다.";

    public FailureApiCallException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
}
