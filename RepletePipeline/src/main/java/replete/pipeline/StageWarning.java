package replete.pipeline;

public class StageWarning {


    ////////////
    // FIELDS //
    ////////////

    private String parentName;    // Right now the "qualified name" of the stage that produced the warning
    private long when;
    private String message;       // Optional
    private Exception exception;  // Optional


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public StageWarning(String parentName, long when, String message, Exception exception) {
        this.parentName = parentName;
        this.when = when;
        this.message = message;
        this.exception = exception;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public String getParent() {
        return parentName;
    }
    public long getWhen() {
        return when;
    }
    public String getMessage() {
        return message;
    }
    public Exception getException() {
        return exception;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return
            "[" + parentName + "@" + when + "]" +
            (message != null ? " " + message : "") +
            (exception != null ? " " + exception.getMessage() : "");
    }
}
