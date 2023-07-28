package replete.cli.options;

/**
 * An option that expects a string value
 *
 * @author Derek Trumbo
 */

public class StringOption extends Option<String> {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public StringOption(char shortForm) {
        this("" + shortForm, null, DEFAULT_REQUIRED, DEFAULT_ALLOW_MULTI);
    }
    public StringOption(String longForm) {
        this(null, longForm, DEFAULT_REQUIRED, DEFAULT_ALLOW_MULTI);
    }
    public StringOption(char shortForm, String longForm) {
        this("" + shortForm, longForm, DEFAULT_REQUIRED, DEFAULT_ALLOW_MULTI);
    }
    public StringOption(char shortForm, boolean required) {
        this("" + shortForm, null, required, DEFAULT_ALLOW_MULTI);
    }
    public StringOption(String longForm, boolean required) {
        this(null, longForm, required, DEFAULT_ALLOW_MULTI);
    }
    public StringOption(char shortForm, String longForm, boolean required, boolean allowMulti) {
        this("" + shortForm, longForm, required, allowMulti);
    }
    public StringOption(String shortForm, String longForm, boolean required, boolean allowMulti) {
        super(shortForm, longForm, true, required, allowMulti);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getValueType() {
        return String.class;
    }
    @Override
    protected String parseValue(String arg, ParseContext context) {
        return arg;
    }
}
