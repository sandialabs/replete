package replete.pipeline.errors;

public class DescriptorException extends RuntimeException {

    public DescriptorException() {
    }

    public DescriptorException(String message) {
        super(message);
    }

    public DescriptorException(Throwable cause) {
        super(cause);
    }

    public DescriptorException(String message, Throwable cause) {
        super(message, cause);
    }

    public DescriptorException(String message, Throwable cause, boolean enableSuppression,
                          boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
