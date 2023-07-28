package replete.threads;

public class InterruptedRuntimeException extends RuntimeException {

    public InterruptedRuntimeException() {
        super();
    }

    public InterruptedRuntimeException(String message, Throwable cause, boolean enableSuppression,
                                       boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InterruptedRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InterruptedRuntimeException(String message) {
        super(message);
    }

    public InterruptedRuntimeException(Throwable cause) {
        super(cause);
    }

}
