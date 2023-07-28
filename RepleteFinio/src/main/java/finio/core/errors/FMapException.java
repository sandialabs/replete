package finio.core.errors;

public class FMapException extends RuntimeException {
    public FMapException() {
        super();
    }
    public FMapException(String message, Throwable cause) {
        super(message, cause);
    }
    public FMapException(String message) {
        super(message);
    }
    public FMapException(Throwable cause) {
        super(cause);
    }
}
