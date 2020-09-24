package lt.ivl.webExternalApp.exception;

public class PasswordDontMatchException extends Exception {
    public PasswordDontMatchException(String message) {
        super(message);
    }

    public PasswordDontMatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
