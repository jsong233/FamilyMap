package dataaccess;

public class DataAccessException extends Exception{

        DataAccessException() {
            super();
        }
        DataAccessException(String message) {
        super(message);
    }
}
