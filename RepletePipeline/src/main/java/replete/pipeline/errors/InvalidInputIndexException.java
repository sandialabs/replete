package replete.pipeline.errors;

public class InvalidInputIndexException extends InputException {
    public InvalidInputIndexException() {
    }
    public InvalidInputIndexException(String message) {
        super(message);
    }
    public InvalidInputIndexException(Throwable cause) {
        super(cause);
    }
    public InvalidInputIndexException(String message, Throwable cause) {
        super(message, cause);
    }
    public InvalidInputIndexException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
