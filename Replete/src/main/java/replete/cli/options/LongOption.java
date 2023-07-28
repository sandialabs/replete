package replete.cli.options;

import replete.cli.errors.IllegalOptionValueException;

/**
 * An option that expects a long integer value
 *
 * @author Derek Trumbo
 */

public class LongOption extends Option<Long> {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public LongOption(char shortForm) {
        this("" + shortForm, null, DEFAULT_REQUIRED, DEFAULT_ALLOW_MULTI);
    }
    public LongOption(String longForm) {
        this(null, longForm, DEFAULT_REQUIRED, DEFAULT_ALLOW_MULTI);
    }
    public LongOption(char shortForm, String longForm) {
        this("" + shortForm, longForm, DEFAULT_REQUIRED, DEFAULT_ALLOW_MULTI);
    }
    public LongOption(char shortForm, boolean required) {
        this("" + shortForm, null, required, DEFAULT_ALLOW_MULTI);
    }
    public LongOption(String longForm, boolean required) {
        this(null, longForm, required, DEFAULT_ALLOW_MULTI);
    }
    public LongOption(char shortForm, String longForm, boolean required, boolean allowMulti) {
        this("" + shortForm, longForm, required, allowMulti);
    }
    public LongOption(String shortForm, String longForm, boolean required, boolean allowMulti) {
        super(shortForm, longForm, true, required, allowMulti);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getValueType() {
        return Long.class;
    }
    @Override
    protected Long parseValue(String arg, ParseContext context) throws IllegalOptionValueException {
        try {
            return new Long(arg);
        } catch(NumberFormatException e) {
            throw new IllegalOptionValueException(this, arg);
        }
    }
}
