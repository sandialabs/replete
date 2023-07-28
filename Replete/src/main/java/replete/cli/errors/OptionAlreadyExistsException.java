package replete.cli.errors;

/**
 * @author Derek Trumbo
 */

// Represents errors that occur when an option is added that already
// exists in the parser (in either short or long form).
public class OptionAlreadyExistsException extends OptionAddException {
    public OptionAlreadyExistsException(String optionName) {
        super("The option '" + optionName + "' has already been added to the parser.");
    }
}
