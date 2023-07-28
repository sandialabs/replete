package replete.pipeline.errors;

public class InputDescriptorException extends DescriptorException {
    public InputDescriptorException() {
    }
    public InputDescriptorException(String message) {
        super(message);
    }
    public InputDescriptorException(Throwable cause) {
        super(cause);
    }
    public InputDescriptorException(String message, Throwable cause) {
        super(message, cause);
    }
    public InputDescriptorException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
