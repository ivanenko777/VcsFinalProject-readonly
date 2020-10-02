package lt.ivl.webExternalApp.exception;

public class UsernameExistsInDatabaseException extends Exception {
    public UsernameExistsInDatabaseException() {
        super("Vartotojo paskyra su tokiu el. pašto adresu jau užsiregistravęs.");
//        super("Username exists in DB!");
    }

    public UsernameExistsInDatabaseException(String message) {
        super(message);
    }
}
