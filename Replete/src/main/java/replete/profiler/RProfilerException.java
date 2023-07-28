package replete.profiler;

public class RProfilerException extends RuntimeException {
    public RProfilerException() {}
    public RProfilerException(String message) {
        super(message);
    }
    public RProfilerException(Throwable cause) {
        super(cause);
    }
    public RProfilerException(String message, Throwable cause) {
        super(message, cause);
    }
    public RProfilerException(String message, Throwable cause, boolean enableSuppression,
                              boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
