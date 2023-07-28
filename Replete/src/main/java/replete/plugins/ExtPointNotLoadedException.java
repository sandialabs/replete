package replete.plugins;

public class ExtPointNotLoadedException extends RuntimeException {
    public ExtPointNotLoadedException() {
        super();
    }
    public ExtPointNotLoadedException(String message, Throwable cause) {
        super(message, cause);
    }
    public ExtPointNotLoadedException(String message) {
        super(message);
    }
    public ExtPointNotLoadedException(Throwable cause) {
        super(cause);
    }
}
