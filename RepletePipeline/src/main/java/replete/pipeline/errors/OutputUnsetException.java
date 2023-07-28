package replete.pipeline.errors;

public class OutputUnsetException extends OutputException {
    public OutputUnsetException() {}
    public OutputUnsetException(String message) {
        super(message);
    }
    public OutputUnsetException(Throwable cause) {
        super(cause);
    }
    public OutputUnsetException(String message, Throwable cause) {
        super(message, cause);
    }
    public OutputUnsetException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
