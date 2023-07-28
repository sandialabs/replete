package replete.cli.options;

import replete.cli.errors.IllegalOptionValueException;

/**
 * An option that expects a boolean value, or whose existence
 * indicates a true value.
 *
 * @author Derek Trumbo
 */

public class BooleanOption extends Option<Boolean> {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public BooleanOption(char shortForm) {
        this("" + shortForm, null, DEFAULT_REQUIRED, DEFAULT_ALLOW_MULTI);
    }
    public BooleanOption(String longForm) {
        this(null, longForm, DEFAULT_REQUIRED, DEFAULT_ALLOW_MULTI);
    }
    public BooleanOption(char shortForm, String longForm) {
        this("" + shortForm, longForm, DEFAULT_REQUIRED, DEFAULT_ALLOW_MULTI);
    }
    public BooleanOption(char shortForm, boolean required) {
        this("" + shortForm, null, required, DEFAULT_ALLOW_MULTI);
    }
    public BooleanOption(String longForm, boolean required) {
        this(null, longForm, required, DEFAULT_ALLOW_MULTI);
    }
    public BooleanOption(char shortForm, String longForm, boolean required, boolean allowMulti) {
        this("" + shortForm, longForm, required, allowMulti);
    }
    public BooleanOption(String shortForm, String longForm, boolean required, boolean allowMulti) {
        super(shortForm, longForm, DEFAULT_WANTS_VALUE, required, allowMulti);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getValueType() {
        return Boolean.class;
    }
    @Override
    protected Boolean parseValue(String arg, ParseContext context) throws IllegalOptionValueException {
        if(arg == null) {
            return true;
        }
        if(!arg.equalsIgnoreCase("true") && !arg.equalsIgnoreCase("false")) {
            throw new IllegalOptionValueException(this, arg);
        }
        return arg.equalsIgnoreCase("true");
    }
}
