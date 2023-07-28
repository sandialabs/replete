package replete.cli.options;

import replete.cli.errors.IllegalOptionValueException;

/**
 * An option that expects a double floating-point value
 *
 * @author Derek Trumbo
 */

public class DoubleOption extends Option<Double> {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public DoubleOption(char shortForm) {
        this("" + shortForm, null, DEFAULT_REQUIRED, DEFAULT_ALLOW_MULTI);
    }
    public DoubleOption(String longForm) {
        this(null, longForm, DEFAULT_REQUIRED, DEFAULT_ALLOW_MULTI);
    }
    public DoubleOption(char shortForm, String longForm) {
        this("" + shortForm, longForm, DEFAULT_REQUIRED, DEFAULT_ALLOW_MULTI);
    }
    public DoubleOption(char shortForm, boolean required) {
        this("" + shortForm, null, required, DEFAULT_ALLOW_MULTI);
    }
    public DoubleOption(String longForm, boolean required) {
        this(null, longForm, required, DEFAULT_ALLOW_MULTI);
    }
    public DoubleOption(char shortForm, String longForm, boolean required, boolean allowMulti) {
        this("" + shortForm, longForm, required, allowMulti);
    }
    public DoubleOption(String shortForm, String longForm, boolean required, boolean allowMulti) {
        super(shortForm, longForm, true, required, allowMulti);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getValueType() {
        return Double.class;
    }
    @Override
    protected Double parseValue(String arg, ParseContext context) throws IllegalOptionValueException {
        try {
            return new Double(arg);
        } catch(NumberFormatException e) {
            throw new IllegalOptionValueException(this, arg);
        }
    }
}
