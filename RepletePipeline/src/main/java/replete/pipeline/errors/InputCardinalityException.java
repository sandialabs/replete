package replete.pipeline.errors;

public class InputCardinalityException extends InputException {
    public InputCardinalityException() {
    }
    public InputCardinalityException(String message) {
        super(message);
    }
    public InputCardinalityException(Throwable cause) {
        super(cause);
    }
    public InputCardinalityException(String message, Throwable cause) {
        super(message, cause);
    }
    public InputCardinalityException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
