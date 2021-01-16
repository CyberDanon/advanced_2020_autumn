package exception;

public class DBException extends Exception {
    public DBException(String message, Exception e) {
        super(message, e);
    }

    public DBException(String no_such_user) {
        super(no_such_user);
    }
}
