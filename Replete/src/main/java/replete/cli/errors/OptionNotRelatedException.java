package replete.cli.errors;

import replete.cli.options.Option;

/**
 * @author Derek Trumbo
 */

// Represents errors that occur when methods invoked on one
// parser are passed Option objects created by some other
// parser.
public class OptionNotRelatedException extends OptionAccessException {
    public OptionNotRelatedException(Option option) {
        super("The option '" + option + "' is not related to this parser.");
    }
}
