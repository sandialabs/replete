package finio.core.errors;


public class KeyPathExistenceException extends KeyPathException {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public KeyPathExistenceException() {
        super();
    }
    public KeyPathExistenceException(String message, Throwable cause, Type type) {
        super(message, cause, type);
    }
    public KeyPathExistenceException(String message, Throwable cause) {
        super(message, cause);
    }
    public KeyPathExistenceException(String message, Type type) {
        super(message, type);
    }
    public KeyPathExistenceException(String message) {
        super(message);
    }
    public KeyPathExistenceException(Throwable cause, Type type) {
        super(cause, type);
    }
    public KeyPathExistenceException(Throwable cause) {
        super(cause);
    }
    public KeyPathExistenceException(Type type) {
        super(type);
    }
}
