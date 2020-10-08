package lt.ivl.webExternalApp.exception;

public class CustomerExistsInDatabaseException extends Exception {
    public CustomerExistsInDatabaseException() {
        super("Vartotojo paskyra su tokiu el. pašto adresu jau užregistruota.");
//        super("Username exists in DB!");
    }

    public CustomerExistsInDatabaseException(String message) {
        super(message);
    }
}
