package luceo.actions;

// Akin to a Log4J log message

public class ExecuteMessage {


    ////////////
    // FIELDS //
    ////////////

    private ExecuteMessageLevel level;
    private String message;
    private Exception error;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ExecuteMessage(ExecuteMessageLevel level, String message, Exception error) {
        this.level   = level;
        this.message = message;
        this.error   = error;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public ExecuteMessageLevel getLevel() {
        return level;
    }
    public String getMessage() {
        return message;
    }
    public Exception getError() {
        return error;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return
            "[" + level + "]" +
            (message == null ? "" : " " + message) +
            (error == null ? "" : " {" + error + "}")
        ;
    }
}
