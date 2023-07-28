package replete.pipeline.errors;

public class MissingRequiredInputException extends InputException {
    public MissingRequiredInputException() {
    }
    public MissingRequiredInputException(String message) {
        super(message);
    }
    public MissingRequiredInputException(Throwable cause) {
        super(cause);
    }
    public MissingRequiredInputException(String message, Throwable cause) {
        super(message, cause);
    }
    public MissingRequiredInputException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
