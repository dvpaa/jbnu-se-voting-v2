package jbnu.se.api.exception;

import org.springframework.http.HttpStatus;

public class UnmatchedElectionTypeException extends ApplicationException {

    private static final String MESSAGE = "선거 종류가 맞지 않습니다.";

    public UnmatchedElectionTypeException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
