package jbnu.se.api.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends CustomAuthenticationException {

    private static final String MESSAGE = "유효하지 않은 아이디 또는 비밀번호입니다.";

    public UnauthorizedException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.UNAUTHORIZED.value();
    }
}
