package replete.cli.options;

import replete.cli.errors.IllegalOptionValueException;

/**
 * An option that expects a floating-point value
 *
 * @author Derek Trumbo
 */

public class FloatOption extends Option<Float> {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public FloatOption(char shortForm) {
        this("" + shortForm, null, DEFAULT_REQUIRED, DEFAULT_ALLOW_MULTI);
    }
    public FloatOption(String longForm) {
        this(null, longForm, DEFAULT_REQUIRED, DEFAULT_ALLOW_MULTI);
    }
    public FloatOption(char shortForm, String longForm) {
        this("" + shortForm, longForm, DEFAULT_REQUIRED, DEFAULT_ALLOW_MULTI);
    }
    public FloatOption(char shortForm, boolean required) {
        this("" + shortForm, null, required, DEFAULT_ALLOW_MULTI);
    }
    public FloatOption(String longForm, boolean required) {
        this(null, longForm, required, DEFAULT_ALLOW_MULTI);
    }
    public FloatOption(char shortForm, String longForm, boolean required, boolean allowMulti) {
        this("" + shortForm, longForm, required, allowMulti);
    }
    public FloatOption(String shortForm, String longForm, boolean required, boolean allowMulti) {
        super(shortForm, longForm, true, required, allowMulti);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getValueType() {
        return Float.class;
    }
    @Override
    protected Float parseValue(String arg, ParseContext context) throws IllegalOptionValueException {
        try {
            return new Float(arg);
        } catch(NumberFormatException e) {
            throw new IllegalOptionValueException(this, arg);
        }
    }
}
