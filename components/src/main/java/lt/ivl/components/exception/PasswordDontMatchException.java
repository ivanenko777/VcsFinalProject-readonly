package lt.ivl.components.exception;

public class PasswordDontMatchException extends Exception {
    public PasswordDontMatchException() {
        super("Slaptažodžiai nesutampa!");
//        super("Passwords are not match!");
    }

    public PasswordDontMatchException(String message) {
        super(message);
    }
}
