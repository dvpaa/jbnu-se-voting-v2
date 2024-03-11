package jbnu.se.api.exception;

import org.springframework.http.HttpStatus;

public class AlreadyVotedException extends ApplicationException {

    private static final String MESSAGE = "이미 투표를 하였습니다.";

    public AlreadyVotedException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.FORBIDDEN.value();
    }
}
