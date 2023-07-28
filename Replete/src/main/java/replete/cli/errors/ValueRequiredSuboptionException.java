package replete.cli.errors;

import replete.cli.options.Option;

/**
 * @author Derek Trumbo
 */

// Represents arguments within a group that do not have a required argument
// (e.g. '-asb').
public class ValueRequiredSuboptionException extends ValueRequiredException {
    private String optionGroup;

    public ValueRequiredSuboptionException(Option opt, String optGroup) {
        super(opt, "Illegal option '" + opt + "' in '" + optGroup + "': requires value");
        optionGroup = optGroup;
    }

    public String getOptionGroup() { return optionGroup; }
}
