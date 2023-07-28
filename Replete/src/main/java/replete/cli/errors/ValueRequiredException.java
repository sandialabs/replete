package replete.cli.errors;

import replete.cli.options.Option;

/**
 * @author Derek Trumbo
 */

// Represents arguments that do not have a required argument
// (e.g. '--source-dir').
public class ValueRequiredException extends IllegalOptionException {
    public ValueRequiredException(Option opt) {
        super(opt, "Illegal option '" + opt + "': requires value");
    }
    public ValueRequiredException(Option opt, String msg) {
        super(opt, msg);
    }
}
