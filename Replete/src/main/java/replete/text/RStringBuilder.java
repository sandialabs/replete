package replete.text;

// This extension of StringBuilder uses composition
// StringBuilder is marked as final for some reason.

public class RStringBuilder {


    ////////////
    // FIELDS //
    ////////////

    private static final String DEFAULT_NEWLINE = "\n";

    private StringBuilder builder;
    private String newLine = DEFAULT_NEWLINE;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RStringBuilder() {
        builder = new StringBuilder();
    }
    public RStringBuilder(int len) {
        builder = new StringBuilder(len);
    }
    public RStringBuilder(String key) {
        builder = new StringBuilder(key);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getNewLine() {
        return newLine;
    }

    // Mutators

    public RStringBuilder setNewLine(String newLine) {
        this.newLine = newLine;
        return this;
    }


    //////////////
    // WRAPPERS //
    //////////////

    public RStringBuilder appendln() {
        builder.append(newLine);
        return this;
    }
    public RStringBuilder appendln(Object obj) {
        return appendln(String.valueOf(obj));
    }
    public RStringBuilder appendln(String str) {
        builder.append(str);
        builder.append(newLine);
        return this;
    }

    public RStringBuilder append(Object obj) {
        return append(String.valueOf(obj));
    }
    public RStringBuilder append(String str) {
        builder.append(str);
        return this;
    }

    public RStringBuilder appendf(String format, Object ... args) {
        builder.append(String.format(format, args));
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return builder.toString();
    }
}
