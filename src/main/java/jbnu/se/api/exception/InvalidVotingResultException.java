package jbnu.se.api.exception;

import org.springframework.http.HttpStatus;

public class InvalidVotingResultException extends ApplicationException {

    private static final String MESSAGE = "유효하지 않은 투표 결과 입니다.";

    public InvalidVotingResultException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
