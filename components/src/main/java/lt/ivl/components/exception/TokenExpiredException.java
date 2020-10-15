package lt.ivl.components.exception;

public class TokenExpiredException extends Exception {
    public TokenExpiredException() {
        super("Tokenas negalioja!");
//        super("Token is expired!");
    }

    public TokenExpiredException(String message) {
        super(message);
    }
}
