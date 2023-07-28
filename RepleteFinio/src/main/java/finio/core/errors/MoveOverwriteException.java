package finio.core.errors;

public class MoveOverwriteException extends RuntimeException {
    private Object key;

    public MoveOverwriteException(String message, Object key) {
        super(message);
        this.key = key;
    }

    public Object getKey() {
        return key;
    }
}
