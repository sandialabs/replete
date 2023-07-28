package replete.errors;

public class RuntimeConvertedException extends RuntimeException {
    public RuntimeConvertedException(Throwable cause) {
        super(cause);
    }
    public RuntimeConvertedException(String message, Throwable cause) {
        super(message, cause);
    }
}
