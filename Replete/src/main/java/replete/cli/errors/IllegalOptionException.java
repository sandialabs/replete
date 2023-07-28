package replete.cli.errors;

import replete.cli.options.Option;

/**
 * @author Derek Trumbo
 */

// Represents known but invalid arguments.
public class IllegalOptionException extends OptionParseException {
    private Option option;

    public IllegalOptionException(Option opt) {
        this(opt, "Illegal option '" + opt + "'");
    }
    public IllegalOptionException(Option opt, String msg) {
        super(msg);
        option = opt;
    }

    public Option getOption() { return option; }
}
