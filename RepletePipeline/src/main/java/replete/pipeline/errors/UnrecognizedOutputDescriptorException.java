package replete.pipeline.errors;

public class UnrecognizedOutputDescriptorException extends OutputDescriptorException {
    public UnrecognizedOutputDescriptorException() {}
    public UnrecognizedOutputDescriptorException(String message) {
        super(message);
    }
    public UnrecognizedOutputDescriptorException(Throwable cause) {
        super(cause);
    }
    public UnrecognizedOutputDescriptorException(String message, Throwable cause) {
        super(message, cause);
    }
    public UnrecognizedOutputDescriptorException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
