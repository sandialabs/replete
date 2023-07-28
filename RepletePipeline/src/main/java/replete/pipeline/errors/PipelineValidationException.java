package replete.pipeline.errors;

public class PipelineValidationException extends RuntimeException {
    public PipelineValidationException() {}
    public PipelineValidationException(String message) {
        super(message);
    }
    public PipelineValidationException(Throwable cause) {
        super(cause);
    }
    public PipelineValidationException(String message, Throwable cause) {
        super(message, cause);
    }
    public PipelineValidationException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
