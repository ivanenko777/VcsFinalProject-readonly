package lt.ivl.webExternalApp.exception;

public class ItemNotFoundException extends Throwable {
    public ItemNotFoundException(String message) {
        super(message);
    }

    public ItemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
