package replete.cli.errors;

/**
 * @author Derek Trumbo
 */

// Represents errors that occur when options with invalid short or
// long forms are added to the parser.
public class InvalidOptionNameException extends OptionAddException {
    public InvalidOptionNameException(String msg) {
        super(msg);
    }
}
