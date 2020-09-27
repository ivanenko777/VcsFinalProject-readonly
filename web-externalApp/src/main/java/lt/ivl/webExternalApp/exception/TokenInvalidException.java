package lt.ivl.webExternalApp.exception;

public class TokenInvalidException extends Exception {
    public TokenInvalidException(String message) {
        super(message);
    }

    public TokenInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}
