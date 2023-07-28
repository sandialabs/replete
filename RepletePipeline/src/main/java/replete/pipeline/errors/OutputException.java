package replete.pipeline.errors;

public class OutputException extends RuntimeException {
    public OutputException() {}
    public OutputException(String message) {
        super(message);
    }
    public OutputException(Throwable cause) {
        super(cause);
    }
    public OutputException(String message, Throwable cause) {
        super(message, cause);
    }
    public OutputException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
