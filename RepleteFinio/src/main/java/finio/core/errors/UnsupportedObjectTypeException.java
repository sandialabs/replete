package finio.core.errors;

public class UnsupportedObjectTypeException extends RuntimeException {
    private Object Ooffender;
    public UnsupportedObjectTypeException(String message, Object O) {
        super(message);
        Ooffender = O;
    }
}
