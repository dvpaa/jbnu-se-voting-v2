package jbnu.se.api.exception;

import org.springframework.http.HttpStatus;

public class ElectoralRollNotFoundException extends ApplicationException {

    private static final String MESSAGE = "명부에 존재하지 않습니다.";

    public ElectoralRollNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.NOT_FOUND.value();
    }
}
