package replete.cli.errors;

import replete.cli.options.Option;

/**
 * @author Derek Trumbo
 */

// Represents options that are required but which were not supplied.
public class RequiredOptionException extends IllegalOptionException {
    public RequiredOptionException(Option opt) {
        super(opt, "Required option '" + opt + "' not supplied");
    }
    public RequiredOptionException(Option opt, String msg) {
        super(opt, msg);
    }
}
