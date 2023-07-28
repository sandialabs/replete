package replete.pstate2;

public class PersistentStateSaveException extends RuntimeException {
    public PersistentStateSaveException() {
    }
    public PersistentStateSaveException(String message) {
        super(message);
    }
    public PersistentStateSaveException(Throwable cause) {
        super(cause);
    }
    public PersistentStateSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
