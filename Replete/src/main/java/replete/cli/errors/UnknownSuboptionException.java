package replete.cli.errors;

/**
 * @author Derek Trumbo
 */

// Represents unknown arguments embedded within a group of arguments
// (e.g. '-axb').
public class UnknownSuboptionException extends UnknownOptionException {
    private String optionGroup;

    public UnknownSuboptionException(String optName, String optGroup) {
        super(optName, "Unknown option '" + optName + "' in '" + optGroup + "'");
        optionGroup = optGroup;
    }

    public String getOptionGroup() { return optionGroup; }
}
