package jbnu.se.api.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class ApplicationException extends RuntimeException {

    private final Map<String, String> validation = new HashMap<>();

    protected ApplicationException(String message) {
        super(message);
    }

    protected ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract int getStatusCode();

    public void addValidation(String fieldName, String message) {
        this.validation.put(fieldName, message);
    }
}
