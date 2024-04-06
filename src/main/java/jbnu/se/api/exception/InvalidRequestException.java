package jbnu.se.api.exception;

import org.springframework.http.HttpStatus;

public class InvalidRequestException extends ApplicationException {
    private static final String MESSAGE = "지원되지 않는 요청: ";

    public InvalidRequestException(String method) {
        super(MESSAGE + method);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.METHOD_NOT_ALLOWED.value();
    }
}
