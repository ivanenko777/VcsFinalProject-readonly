package lt.ivl.webExternalApp.exception;

public class PasswordDontMatchException extends Exception {
    public PasswordDontMatchException() {
        super("Slaptažodiai nesutampa!");
//        super("Passwords are not match!");
    }

    public PasswordDontMatchException(String message) {
        super(message);
    }
}
