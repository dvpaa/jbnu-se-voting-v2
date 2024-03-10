package jbnu.se.api.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class InvalidCsvFormatException extends ApplicationException {

    private static final String MESSAGE = "csv 형식이 올바르지 않습니다.";

    public InvalidCsvFormatException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return BAD_REQUEST.value();
    }
}
