package replete.cli.errors;

import replete.cli.options.Option;

/**
 * @author Derek Trumbo
 */

// Represents arguments that are given their required values but which
// are given values that are inappropriate for the data type
// (e.g. '--count=abc').
public class IllegalOptionValueException extends IllegalOptionException {
    private String value;

    public IllegalOptionValueException(Option opt, String val) {
        super(opt, "Illegal option '" + opt + "': invalid value '" + val + "'");
        value = val;
    }
    public IllegalOptionValueException(Option opt, String val, String msg) {
        super(opt, msg);
        value = val;
    }

    public String getValue() { return value; }
}
