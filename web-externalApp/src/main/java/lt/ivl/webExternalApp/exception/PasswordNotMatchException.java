package lt.ivl.webExternalApp.exception;

public class PasswordNotMatchException extends Exception {
    public PasswordNotMatchException(String message) {
        super(message);
    }

    public PasswordNotMatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
