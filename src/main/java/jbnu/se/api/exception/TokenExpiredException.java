package jbnu.se.api.exception;

import org.springframework.http.HttpStatus;

public class TokenExpiredException extends ApplicationException {

    private static final String MESSAGE = "만료된 토큰입니다.";

    public TokenExpiredException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.UNAUTHORIZED.value();  // 401 Unauthorized
    }
}
