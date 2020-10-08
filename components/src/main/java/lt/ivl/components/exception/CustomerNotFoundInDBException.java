package lt.ivl.components.exception;

public class CustomerNotFoundInDBException extends Exception {
    public CustomerNotFoundInDBException() {
        super("Vartotojo paskyra nerasta!");
//        super("Customer not found in DB!");
    }

    public CustomerNotFoundInDBException(String message) {
        super(message);
    }
}
