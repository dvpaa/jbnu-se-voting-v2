package jbnu.se.api.exception;

import org.springframework.http.HttpStatus;

public class InvalidElectionTypeException extends ApplicationException {

    private static final String MESSAGE = "유효하지 않은 선거 종류 입니다.";

    public InvalidElectionTypeException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
