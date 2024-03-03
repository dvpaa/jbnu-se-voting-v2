package jbnu.se.api.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public class ElectionNotFound extends ApplicationException {

    private static final String MESSAGE = "존재하지 않는 선거입니다.";

    public ElectionNotFound() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return NOT_FOUND.value();
    }
}
