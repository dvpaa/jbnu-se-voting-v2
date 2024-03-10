package jbnu.se.api.exception;

public class CSVException extends ApplicationException {

    private static final String MESSAGE = "IOException 발생";

    public CSVException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 0;
    }
}
