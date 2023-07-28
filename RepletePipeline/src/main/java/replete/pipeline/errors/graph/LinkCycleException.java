package replete.pipeline.errors.graph;

public class LinkCycleException extends InvalidLinkException {
    public LinkCycleException() {
        super();
    }
    public LinkCycleException(String message, Throwable cause) {
        super(message, cause);
    }
    public LinkCycleException(String message) {
        super(message);
    }
    public LinkCycleException(Throwable cause) {
        super(cause);
    }
}
