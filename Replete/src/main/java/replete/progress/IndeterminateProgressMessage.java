package replete.progress;

/**
 * @author Derek Trumbo
 */

public class IndeterminateProgressMessage extends ProgressMessage {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public IndeterminateProgressMessage(String title) {
        this(title, null);
    }
    public IndeterminateProgressMessage(String title, String msg) {
        super(title, msg);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public boolean isIndeterminate() {
        return true;
    }
    @Override
    public int calculatePercentDone() {
        return -1;
    }
    @Override
    public String renderNumericMessage() {
        return "";
    }
}
