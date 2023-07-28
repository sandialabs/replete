package replete.cli.errors;

import replete.cli.options.Option;

public class TooManyValuesForOptionException extends IllegalOptionException {
    public TooManyValuesForOptionException(Option opt) {
        super(opt, "Option '" + opt + "' cannot have multiple values.");
    }
}
