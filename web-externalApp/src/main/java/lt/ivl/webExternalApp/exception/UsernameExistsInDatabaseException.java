package lt.ivl.webExternalApp.exception;

public class UsernameExistsInDatabaseException extends Exception {
    public UsernameExistsInDatabaseException() {
    }

    public UsernameExistsInDatabaseException(String message) {
        super(message);
    }

    public UsernameExistsInDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
