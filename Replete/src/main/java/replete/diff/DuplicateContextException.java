package replete.diff;

public class DuplicateContextException extends RuntimeException {

    public DuplicateContextException() {
        super();
    }

    public DuplicateContextException(String message, Throwable cause, boolean enableSuppression,
                                     boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public DuplicateContextException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateContextException(String message) {
        super(message, null, false, true);
    }

    public DuplicateContextException(Throwable cause) {
        super(cause);
    }

}
