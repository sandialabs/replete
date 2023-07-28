package replete.cli.errors;

import replete.cli.options.Option;

/**
 * @author Derek Trumbo
 */

// Represents arguments that are given their required values but which
// are given values that are inappropriate for the data type
// (e.g. '--count=abc').
public class IllegalOptionValueValidationException extends IllegalOptionException {
    private String value;
    private String validationMessage;

    public IllegalOptionValueValidationException(Option opt, String val, String msg) {
        super(opt, "Illegal option '" + opt + "': invalid value '" + val + "': " + msg);
        value = val;
        validationMessage = msg;
    }

    public String getValue() {
        return value;
    }
    public String getValidationMessage() {
        return validationMessage;
    }
}
