package replete.diff.generic;

public class InvalidTypeException extends RuntimeException {
    public InvalidTypeException() {
        super();
    }
    public InvalidTypeException(String info) {
        super(info);
    }
}
