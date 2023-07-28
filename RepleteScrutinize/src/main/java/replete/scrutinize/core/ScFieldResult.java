package replete.scrutinize.core;

import java.io.Serializable;

public class ScFieldResult implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    private Object value;
    private String className;       // What the generic field/method return type should have been (useful for knowing value possibilities and for null)
    private Exception exception;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public Object getValue() {
        return value;
    }
    public String getClassName() {
        return className;
    }
    public Exception getException() {
        return exception;
    }

    // Mutators

    public void setValue(Object value) {
        this.value = value;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    public void setException(Exception exception) {
        this.exception = exception;
    }
}
