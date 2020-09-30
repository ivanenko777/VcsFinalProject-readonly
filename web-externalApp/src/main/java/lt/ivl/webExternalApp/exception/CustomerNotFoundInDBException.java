package lt.ivl.webExternalApp.exception;

public class CustomerNotFoundInDBException extends Exception {
    public CustomerNotFoundInDBException(String message) {
        super(message);
    }

    public CustomerNotFoundInDBException(String message, Throwable cause) {
        super(message, cause);
    }
}
