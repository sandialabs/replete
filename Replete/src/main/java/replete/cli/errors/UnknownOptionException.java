package replete.cli.errors;

/**
 * @author Derek Trumbo
 */

// Represents unknown arguments (e.g. '-x', '--xyz').
public class UnknownOptionException extends OptionParseException {
    private String optionName = null;

    public UnknownOptionException(String optName) {
        this(optName, "Unknown option '" + optName + "'");
    }
    public UnknownOptionException(String optName, String msg) {
        super(msg);
        optionName = optName;
    }

    public String getOptionName() { return optionName; }
}
