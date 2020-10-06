package lt.ivl.components.exception;

public class EmployeeNotFoundInDbException extends Exception {
    public EmployeeNotFoundInDbException() {
        super("Darbuotojo paskyra nerasta!");
//        super("Employee not found in DB!");
    }

    public EmployeeNotFoundInDbException(String message) {
        super(message);
    }
}
