package replete.pipeline.errors;

public class StageNotExecutedException extends RuntimeException {
    public StageNotExecutedException() {}
    public StageNotExecutedException(String message, Throwable cause) {
        super(message, cause);
    }
    public StageNotExecutedException(String message) {
        super(message);
    }
    public StageNotExecutedException(Throwable cause) {
        super(cause);
    }
    public StageNotExecutedException(String message, Throwable cause, boolean enableSuppression,
                                     boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
