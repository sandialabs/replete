package replete.cli.errors;

/**
 * @author Derek Trumbo
 */

// Represents any problems with parsing the arguments.
public abstract class CommandLineParseException extends Exception {
    public CommandLineParseException(String msg) {
        super(msg);
    }
}
