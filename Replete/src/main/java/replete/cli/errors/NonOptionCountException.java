package replete.cli.errors;

/**
 * @author Derek Trumbo
 */

// Represents any problems with parsing the arguments.
public class NonOptionCountException extends NonOptionParseException {
    public NonOptionCountException(String msg) {
        super(msg);
    }
}
