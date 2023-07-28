package replete.errors;

// This is the kind of exception that should never, ever
// be thrown.
public class UnicornException extends RuntimeException {
    public UnicornException() {
    }
    public UnicornException(String message) {
        super(message);
    }
    public UnicornException(Throwable cause) {
        super(cause);
    }
    public UnicornException(String message, Throwable cause) {
        super(message, cause);
    }
}
