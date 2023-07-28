package replete.ui.validation;

import java.io.Serializable;

public class ValidationMessage implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    private MessageType type;      // INFO, WARN, ERROR, or custom (cannot be null).
    private String reason;         // Why the data is problematic, e.g. "Invalid Format", "Not enough vowels",
                                   // "Value too small" (cannot be null).
    private String evidence;       // Part of data that substantiates the reason, e.g. "-10", "Abe", "myname@"
                                   // (can be null).
    private Throwable exception;   // Used in situations where you aren't just statically checking the "current
                                   // state" of a component hierarchy, but rather are actively attempting some
                                   // task (a task other than the validation task itself), have a hierarchy of
                                   // calls that could all fail in their own way, and want to also record Java
                                   // exceptions/errors that pop up during that task (can be null).


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ValidationMessage() {

    }
    public ValidationMessage(MessageType type, String reason) {
        this.type = type;
        this.reason = reason;
    }
    public ValidationMessage(MessageType type, String reason, String evidence) {
        this.type = type;
        this.reason = reason;
        this.evidence = evidence;
    }
    public ValidationMessage(MessageType type, String reason, String evidence, Throwable exception) {
        this.type = type;
        this.reason = reason;
        this.evidence = evidence;
        this.exception = exception;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public MessageType getType() {
        return type;
    }
    public String getReason() {
        return reason;
    }
    public String getEvidence() {
        return evidence;
    }
    public Throwable getException() {
        return exception;
    }

    // Mutators

    public ValidationMessage setType(MessageType type) {
        this.type = type;
        return this;
    }
    public ValidationMessage setReason(String reason) {
        this.reason = reason;
        return this;
    }
    public ValidationMessage setEvidence(String evidence) {
        this.evidence = evidence;
        return this;
    }
    public ValidationMessage setException(Throwable exception) {
        this.exception = exception;
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return toString(false);
    }

    // Related Accessor (Computed)

    public String toString(boolean includeException) {
        return "[" + type + "] " + reason +
            (evidence != null ? " {" + evidence + "}" : "") +
            (includeException && exception != null ? " !" + exception : "");
    }
}
