package replete.cli.errors;

/**
 * @author Derek Trumbo
 */

// Represents errors that occur when options are initially added to the
// parser.
public abstract class OptionAddException extends RuntimeException {
    public OptionAddException(String msg) {
        super(msg);
    }
}
