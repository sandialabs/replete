package replete.cli.errors;

/**
 * @author Derek Trumbo
 */

// Represents errors that occur when methods are called on the
// parser before or after a parse when either the parser is
// in an invalid state or the supplied arguments are invalid.
public abstract class OptionAccessException extends RuntimeException {
    public OptionAccessException(String msg) {
        super(msg);
    }
}
