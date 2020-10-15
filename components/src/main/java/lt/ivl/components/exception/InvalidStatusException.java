package lt.ivl.components.exception;

public class InvalidStatusException extends Exception {
    public InvalidStatusException() {
        super("Statuso klaida! Grįžkite atgal ir bandykite iš naujo.");
    }

    public InvalidStatusException(String message) {
        super(message);
    }
}
