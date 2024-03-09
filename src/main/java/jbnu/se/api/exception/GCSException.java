package jbnu.se.api.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class GCSException extends ApplicationException {

    public static final String MESSAGE = "gcs 오류";

    public GCSException(Throwable cause) {
        super(MESSAGE, cause);
    }

    @Override
    public int getStatusCode() {
        return INTERNAL_SERVER_ERROR.value();
    }
}
