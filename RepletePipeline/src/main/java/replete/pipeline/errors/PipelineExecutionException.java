package replete.pipeline.errors;

public class PipelineExecutionException extends RuntimeException {
    public PipelineExecutionException() {}
    public PipelineExecutionException(String message) {
        super(message);
    }
    public PipelineExecutionException(Throwable cause) {
        super(cause);
    }
    public PipelineExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
    public PipelineExecutionException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
