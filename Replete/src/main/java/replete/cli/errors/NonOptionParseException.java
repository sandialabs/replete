package replete.cli.errors;

/**
 * @author Derek Trumbo
 */

// Represents any problems with parsing the arguments.
public abstract class NonOptionParseException extends CommandLineParseException {
    public NonOptionParseException(String msg) {
        super(msg);
    }
}
