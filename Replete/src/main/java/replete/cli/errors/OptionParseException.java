package replete.cli.errors;

/**
 * @author Derek Trumbo
 */

// Represents any problems with parsing the arguments.
public abstract class OptionParseException extends CommandLineParseException {
    public OptionParseException(String msg) {
        super(msg);
    }
}
