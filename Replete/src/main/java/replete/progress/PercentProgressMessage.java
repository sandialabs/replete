
package replete.progress;

/**
 * @author Derek Trumbo
 */

public class PercentProgressMessage extends ProgressMessage {


    ///////////
    // FIELD //
    ///////////

    protected int value1;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public PercentProgressMessage(String title, int v1) {
        this(title, null, v1);
    }
    public PercentProgressMessage(String title, String msg, int v1) {
        super(title, msg);
        value1 = v1;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public boolean isIndeterminate() {
        return false;
    }
    @Override
    public int calculatePercentDone() {
        return value1;
    }
    @Override
    public String renderNumericMessage() {
        return value1 + "%";
    }
}
