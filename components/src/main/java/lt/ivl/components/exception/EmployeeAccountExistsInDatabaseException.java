package lt.ivl.components.exception;

public class EmployeeAccountExistsInDatabaseException extends Exception {
    public EmployeeAccountExistsInDatabaseException() {
        super("Darbuotojo paskyra su tokiu el. pašto adresu jau užregistruota.");
//        super("Username exists in DB!");
    }

    public EmployeeAccountExistsInDatabaseException(String message) {
        super(message);
    }
}
