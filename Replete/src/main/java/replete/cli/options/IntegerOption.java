package replete.cli.options;

import replete.cli.errors.IllegalOptionValueException;

/**
 * An option that expects an integer value
 *
 * @author Derek Trumbo
 */

public class IntegerOption extends Option<Integer> {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public IntegerOption(char shortForm) {
        this("" + shortForm, null, DEFAULT_REQUIRED, DEFAULT_ALLOW_MULTI);
    }
    public IntegerOption(String longForm) {
        this(null, longForm, DEFAULT_REQUIRED, DEFAULT_ALLOW_MULTI);
    }
    public IntegerOption(char shortForm, String longForm) {
        this("" + shortForm, longForm, DEFAULT_REQUIRED, DEFAULT_ALLOW_MULTI);
    }
    public IntegerOption(char shortForm, boolean required) {
        this("" + shortForm, null, required, DEFAULT_ALLOW_MULTI);
    }
    public IntegerOption(String longForm, boolean required) {
        this(null, longForm, required, DEFAULT_ALLOW_MULTI);
    }
    public IntegerOption(char shortForm, String longForm, boolean required, boolean allowMulti) {
        this("" + shortForm, longForm, required, allowMulti);
    }
    public IntegerOption(String shortForm, String longForm, boolean required, boolean allowMulti) {
        super(shortForm, longForm, true, required, allowMulti);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getValueType() {
        return Integer.class;
    }
    @Override
    protected Integer parseValue(String arg, ParseContext context) throws IllegalOptionValueException {
        try {
            return new Integer(arg);
        } catch(NumberFormatException e) {
            throw new IllegalOptionValueException(this, arg);
        }
    }
}
