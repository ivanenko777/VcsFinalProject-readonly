package lt.ivl.components.exception;

public class TokenInvalidException extends Exception {
    public TokenInvalidException() {
        super("Neteisingas tokenas!");
//        super("Token is invalid!");
    }

    public TokenInvalidException(String message) {
        super(message);
    }
}
