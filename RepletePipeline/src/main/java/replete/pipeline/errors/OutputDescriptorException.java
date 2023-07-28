package replete.pipeline.errors;

public class OutputDescriptorException extends DescriptorException {

    public OutputDescriptorException() {
    }

    public OutputDescriptorException(String message) {
        super(message);
    }

    public OutputDescriptorException(Throwable cause) {
        super(cause);
    }

    public OutputDescriptorException(String message, Throwable cause) {
        super(message, cause);
    }

    public OutputDescriptorException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
