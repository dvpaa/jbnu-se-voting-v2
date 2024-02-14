package jbnu.se.api.exception;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends ApplicationException {

    private static final String MESSAGE = "유효하지 않은 토큰입니다.";

    public InvalidTokenException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.UNAUTHORIZED.value();  // 401 Unauthorized
    }
}
