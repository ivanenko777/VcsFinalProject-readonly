package lt.ivl.webExternalApp.exception;

public class ItemNotFoundException extends Exception {
    public ItemNotFoundException() {
        super("Elementas nerastas!");
//        super("Item not found!");
    }

    public ItemNotFoundException(String message) {
        super(message);
    }
}
