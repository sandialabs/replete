package replete.pipeline.errors;

public class UnrecognizedInputDescriptorException extends InputDescriptorException {
    public UnrecognizedInputDescriptorException() {}
    public UnrecognizedInputDescriptorException(String message) {
        super(message);
    }
    public UnrecognizedInputDescriptorException(Throwable cause) {
        super(cause);
    }
    public UnrecognizedInputDescriptorException(String message, Throwable cause) {
        super(message, cause);
    }
    public UnrecognizedInputDescriptorException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
