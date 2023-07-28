package replete.pipeline.errors.graph;

public class DuplicateLinkException extends InvalidLinkException {
    public DuplicateLinkException() {
        super();
    }
    public DuplicateLinkException(String message, Throwable cause) {
        super(message, cause);
    }
    public DuplicateLinkException(String message) {
        super(message);
    }
    public DuplicateLinkException(Throwable cause) {
        super(cause);
    }
}
