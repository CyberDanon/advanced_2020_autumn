package exception;

public class ServerException extends Exception {
    public ServerException(String message, Exception e) {
        super(message, e);
    }
}
