package replete.cli.options;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import replete.cli.CommandLineParser;
import replete.cli.errors.IllegalOptionException;
import replete.cli.errors.IllegalOptionValueException;
import replete.cli.errors.IllegalOptionValueValidationException;
import replete.cli.errors.InvalidOptionNameException;
import replete.cli.errors.ValueRequiredException;
import replete.cli.errors.ValueRequiredSuboptionException;
import replete.cli.validator.OptionValueValidator;


/**
 * Representation of a command-line option that has either
 * a short form, a long form, or both.
 *
 * @author Derek Trumbo
 */

public abstract class Option<T> implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    // Constants

    public static final boolean DEFAULT_WANTS_VALUE = false;
    public static final boolean DEFAULT_REQUIRED    = false;
    public static final boolean DEFAULT_ALLOW_MULTI = true;
    public static final boolean DEFAULT_HAS_X_ARGS  = false;

    // Core

    private String  shortForm    = null;
    private String  longForm     = null;
    private boolean wantsValue   = DEFAULT_WANTS_VALUE;
    private boolean required     = DEFAULT_REQUIRED;
    private boolean allowMulti   = DEFAULT_ALLOW_MULTI;
    private boolean hasXArgs     = DEFAULT_HAS_X_ARGS;
    private String  helpDesc     = null;
    private String  helpParam    = null;
    private String  defaultLabel = null;
    private Object  defaultValue = null;
    private List<OptionValueValidator<T>> validators = new ArrayList<>();

    // Stores Character objects (for short-form aliases) and String
    // objects (for long-form aliases).  Each Option object only
    // needs to know its aliases directly for the usage message.
    private List<Object> aliases = new ArrayList<>();


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    protected Option(String shortForm, String longForm, boolean wantsValue, boolean required, boolean allowMulti) {
        if(shortForm == null && longForm == null) {
            throw new InvalidOptionNameException("Options must have either a short or long form or both.");
        }
        if(shortForm != null) {
            shortForm = shortForm.trim();
            validateShortForm(shortForm);
        }
        if(longForm != null) {
            longForm = longForm.trim();
            validateLongForm(longForm);
        }
        this.shortForm  = shortForm;
        this.longForm   = longForm;
        this.wantsValue = wantsValue;
        this.required   = required;
        this.allowMulti = allowMulti;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getShortForm()       { return shortForm;    }
    public String getLongForm()        { return longForm;     }
    public boolean wantsValue()        { return wantsValue;   }
    public boolean isRequired()        { return required;     }
    public boolean isAllowMulti()      { return allowMulti;   }
    public boolean hasXArgs()          { return hasXArgs;     }
    public String getHelpDescription() { return helpDesc;     }
    public String getHelpParamName()   { return helpParam;    }
    public String getDefaultLabel()    { return defaultLabel; }
    public Object getDefaultValue()    { return defaultValue; }
    public List<Object> getAliases()   { return aliases;      }

    // Accessors (Computed)

    // Returns a value as interpreted by this option given
    // the string of characters supplied.  The second argument
    // is here only to make the parser code a little cleaner.
    public final T getValue(String arg, String group, ParseContext context) throws IllegalOptionException {

        if(arg == null) {
            if(wantsValue) {
                if(group != null) {
                    throw new ValueRequiredSuboptionException(this, group);
                }
                throw new ValueRequiredException(this);
            }
        }

        T value = parseValue(arg, context);

        checkCustomValidation(arg, value);

        return value;
    }

    // Mutators

    public Option setRequired(boolean required) {       // In case you don't want to use ctor
        this.required = required;
        return this;
    }
    public Option setAllowMulti(boolean allowMulti) {   // In case you don't want to use ctor
        this.allowMulti = allowMulti;
        return this;
    }
    public Option setHasXArgs(boolean hasXArgs) {
        this.hasXArgs = hasXArgs;
        return this;
    }
    public Option setHelpDescription(String desc) {
        helpDesc = desc;
        return this;
    }
    public Option setHelpParamName(String name) {
        helpParam = name;
        return this;
    }
    public Option setDefaultLabel(String defaultLabel) {
        this.defaultLabel = defaultLabel;
        return this;
    }
    public Option setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
    public Option addValidator(OptionValueValidator<T> validator) {
        validators.add(validator);
        return this;
    }

    // To be called only by CommandLineParser.addAlias(*).  Do not call these
    // yourself.  These exist only for the purpose of generating the usage message.
    public Option addAlias(char shortForm) {
        aliases.add(shortForm);
        return this;
    }
    public Option addAlias(String longForm) {
        aliases.add(longForm);
        return this;
    }


    //////////
    // MISC //
    //////////

    public static void validateShortForm(String shortForm) {
        if(shortForm.length() != 1) {
            throw new InvalidOptionNameException("Short form options must be a single non-space character.");
        }
        if(shortForm.equals(CommandLineParser.DASH)) {
            throw new InvalidOptionNameException("Short form options cannot be a dash.");
        }
    }
    public static void validateLongForm(String longForm) {
        if(longForm.startsWith(CommandLineParser.DASH)) {
            throw new InvalidOptionNameException("Long form options cannot begin with a dash.");
        }
        if(longForm.contains("=")) {
            throw new InvalidOptionNameException("Long form options cannot contain an equal sign.");
        }
    }
    private void checkCustomValidation(String arg, T value) throws IllegalOptionValueValidationException {
        for(OptionValueValidator<T> validator : validators) {
            String msg = validator.validate(this, value);
            if(msg != null) {
                throw new IllegalOptionValueValidationException(this, arg, msg);
            }
        }
    }


    //////////////
    // ABSTRACT //
    //////////////

    public Class<?> getValueType() {        // Probably a better design possible
        return Object.class;                // Meant to be overridden
    }
    protected abstract T parseValue(String arg, ParseContext context) throws IllegalOptionValueException;


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        String ret = "";
        if(shortForm != null) {
            ret += CommandLineParser.DASH + shortForm;
            if(longForm != null) {
                ret += "/";
            }
        }
        if(longForm != null) {
            ret += CommandLineParser.DASH_DASH + longForm;
        }
        return ret;
    }
}
