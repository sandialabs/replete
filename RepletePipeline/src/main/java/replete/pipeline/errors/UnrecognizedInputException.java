package replete.pipeline.errors;

public class UnrecognizedInputException extends InputException {

    public UnrecognizedInputException() {
    }

    public UnrecognizedInputException(String message) {
        super(message);
    }

    public UnrecognizedInputException(Throwable cause) {
        super(cause);
    }

    public UnrecognizedInputException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnrecognizedInputException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
