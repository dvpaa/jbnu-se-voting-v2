package jbnu.se.api.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public class HeadquarterNotFoundException extends ApplicationException {

    private static final String MESSAGE = "존재하지 않는 선본입니다.";

    public HeadquarterNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return NOT_FOUND.value();
    }
}
