package replete.errors;

import java.io.Serializable;

import replete.text.StringUtil;

// This class exists to help client code NEVER have to
// explicitly serialize an exception object to an
// external destination.  Serializing any arbitrary
// throwable to disk or across a socket risks errors
// in itself since 3rd-party exception objects can
// contain unexpected fields (unserializable fields or
// fields that contain a reference to a large object
// tree either accidentally or on purpose).

// In this class we've chosen to duplicate the class name
// and message information to remove any ambiguity that
// might come attempting to parse those things out of
// the complete text.

// Other implementation options include only saving the
// complete text and parsing out the class name and message
// whenever they are needed and retaining StackTraceElement's
// directly instead of the complete text.  This implementation
// strikes the balance between simplicity and retaining all
// the core information, but changes can be made as needs
// evolve.

public class ThrowableSnapshot implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    private String className;
    private String message;       // Can be null
    private String completeText;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ThrowableSnapshot(Throwable throwable) {
        className = throwable.getClass().getName();
        message = throwable.getMessage();              // Can be null
        completeText = ExceptionUtil.toCompleteString(throwable, 4);
        // ^This implementation strategy also has the downside of
        //  locking in the exact formatting (e.g. 4 spaces) into
        //  the complete text.
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getClassName() {
        return className;
    }
    public String getMessage() {
        return message;
    }
    public String getCompleteText() {
        return completeText;
    }

    // Accessors (Computed)

    public String getClassMessageLine() {
        if(StringUtil.isBlank(message)) {   // Not *exactly* how Throwable works, but close enough
            return className;
        }
        return className + ": " + message;
    }

    // Mutators

    public ThrowableSnapshot setClassName(String className) {
        this.className = className;
        return this;
    }
    public ThrowableSnapshot setMessage(String message) {
        this.message = message;
        return this;
    }
    public ThrowableSnapshot setCompleteText(String completeText) {
        this.completeText = completeText;
        return this;
    }
}
