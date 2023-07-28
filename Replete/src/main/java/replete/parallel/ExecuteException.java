package replete.parallel;

public class ExecuteException extends RuntimeException {
    public ExecuteException() {
        super();
    }
    public ExecuteException(String message, Throwable cause) {
        super(message, cause);
    }
    public ExecuteException(String message) {
        super(message);
    }
    public ExecuteException(Throwable cause) {
        super(cause);
    }
}
