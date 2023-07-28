package finio.core.errors;

public class KeyPathException extends RuntimeException {


    //////////
    // ENUM //
    //////////

    public enum Type {
        SOURCE,
        DESTINATION,
        OTHER
    }


    ///////////
    // FIELD //
    ///////////

    private Type type = Type.OTHER;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public KeyPathException() {
        super();
    }
    public KeyPathException(String message, Throwable cause) {
        super(message, cause);
    }
    public KeyPathException(String message) {
        super(message);
    }
    public KeyPathException(Throwable cause) {
        super(cause);
    }
    public KeyPathException(Type type) {
        super();
        this.type = type;
    }
    public KeyPathException(String message, Throwable cause, Type type) {
        super(message, cause);
        this.type = type;
    }
    public KeyPathException(String message, Type type) {
        super(message);
        this.type = type;
    }
    public KeyPathException(Throwable cause, Type type) {
        super(cause);
        this.type = type;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public Type getType() {
        return type;
    }
}
