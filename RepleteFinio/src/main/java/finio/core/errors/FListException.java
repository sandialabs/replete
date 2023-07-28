package finio.core.errors;

public class FListException extends RuntimeException {
    public FListException() {
        super();
    }
    public FListException(String message, Throwable cause) {
        super(message, cause);
    }
    public FListException(String message) {
        super(message);
    }
    public FListException(Throwable cause) {
        super(cause);
    }
}
