package replete.pipeline.errors;

public class InputNullException extends InputException {
    public InputNullException() {
    }
    public InputNullException(String message) {
        super(message);
    }
    public InputNullException(Throwable cause) {
        super(cause);
    }
    public InputNullException(String message, Throwable cause) {
        super(message, cause);
    }
    public InputNullException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
