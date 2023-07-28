package replete.logging;

import java.io.Serializable;

public class ExceptionMetaInfo implements Comparable<ExceptionMetaInfo>, Serializable {


    ////////////
    // FIELDS //
    ////////////

    private String className;
    private String description;
    private boolean important;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    ExceptionMetaInfo(String className, String description, boolean important) {
        this.className = className;
        this.description = description;
        this.important = important;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public String getClassName() {
        return className;
    }
    public String getDescription() {
        return description;
    }
    public boolean isImportant() {
        return important;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int compareTo(ExceptionMetaInfo o) {
        int v = className.compareTo(o.className);
        if(v != 0) {
            return v;
        }
        return description.compareTo(o.description);
    }

    @Override
    public String toString() {
        return "{" + className + "} " + (important ? "!!! " : "") + description;
    }
}
