package replete.logging;

import java.io.Serializable;

public class LogCode implements Comparable<LogCode>, Serializable {


    ////////////
    // FIELDS //
    ////////////

    private String category;
    private String loggerName;
    private String code;
    private String description;
    private boolean important;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    LogCode(String category, Class<?> clazz, String code, String description, boolean important) {
        this.category = category;
        loggerName = clazz.getName();
        this.code = code;
        this.description = description;
        this.important = important;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public String getCategory() {
        return category;
    }
    public String getLoggerName() {
        return loggerName;
    }
    public String getCode() {
        return code;
    }
    public String getDescription() {
        return description;
    }
    public boolean isImportant() {
        return important;
    }

    // Computed

    public String toLongString() {
        return
            "(" + category + ") [" + loggerName + "] {" +
            code + "} " + (important ? "!!! " : "") + description;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int compareTo(LogCode o) {
        int v = category.compareTo(o.category);
        if(v != 0) {
            return v;
        }
        v = loggerName.compareTo(o.loggerName);
        if(v != 0) {
            return v;
        }
        v = code.compareTo(o.code);
        if(v != 0) {
            return v;
        }
        return description.compareTo(o.description);
    }
    @Override
    public String toString() {
        return "{" + code + "}";
    }
}
