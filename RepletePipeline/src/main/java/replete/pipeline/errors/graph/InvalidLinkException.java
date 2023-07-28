package replete.pipeline.errors.graph;

public class InvalidLinkException extends GraphException {
    public InvalidLinkException() {
        super();
    }
    public InvalidLinkException(String message, Throwable cause) {
        super(message, cause);
    }
    public InvalidLinkException(String message) {
        super(message);
    }
    public InvalidLinkException(Throwable cause) {
        super(cause);
    }
}
