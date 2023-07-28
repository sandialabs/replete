package replete.errors;

// This is the kind of exception that should never, ever
// be thrown.
public class IntentionalDebugException extends RuntimeException {
    public IntentionalDebugException() {
    }
    public IntentionalDebugException(String message) {
        super(message);
    }
    public IntentionalDebugException(Throwable cause) {
        super(cause);
    }
    public IntentionalDebugException(String message, Throwable cause) {
        super(message, cause);
    }
}
