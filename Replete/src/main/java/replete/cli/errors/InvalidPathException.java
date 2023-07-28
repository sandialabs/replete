package replete.cli.errors;

import replete.cli.options.Option;

/**
 * @author Derek Trumbo
 */

// Represents arguments that are given their required values but which
// are given values that are inappropriate for the data type
// (e.g. '--count=abc').
public class InvalidPathException extends IllegalOptionValueException {
    public InvalidPathException(Option opt, String val, String msg) {
        super(opt, val, "Illegal option '" + opt + "': invalid value '" + val + "': " + msg);
    }
}
