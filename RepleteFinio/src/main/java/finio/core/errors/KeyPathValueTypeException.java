package finio.core.errors;


public class KeyPathValueTypeException extends KeyPathException {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public KeyPathValueTypeException() {
        super();
    }
    public KeyPathValueTypeException(String message, Throwable cause, Type type) {
        super(message, cause, type);
    }
    public KeyPathValueTypeException(String message, Throwable cause) {
        super(message, cause);
    }
    public KeyPathValueTypeException(String message, Type type) {
        super(message, type);
    }
    public KeyPathValueTypeException(String message) {
        super(message);
    }
    public KeyPathValueTypeException(Throwable cause, Type type) {
        super(cause, type);
    }
    public KeyPathValueTypeException(Throwable cause) {
        super(cause);
    }
    public KeyPathValueTypeException(Type type) {
        super(type);
    }
}
