package replete.pipeline.errors;

public class UnrecognizedOutputException extends OutputException {
    public UnrecognizedOutputException() {}
    public UnrecognizedOutputException(String message) {
        super(message);
    }
    public UnrecognizedOutputException(Throwable cause) {
        super(cause);
    }
    public UnrecognizedOutputException(String message, Throwable cause) {
        super(message, cause);
    }
    public UnrecognizedOutputException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
