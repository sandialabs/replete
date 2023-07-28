package replete.progress;

/**
 * @author Derek Trumbo
 */

public class FractionProgressMessage extends ProgressMessage {


    ////////////
    // FIELDS //
    ////////////

    protected int value1;
    protected int value2;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public FractionProgressMessage(String title, int v1, int v2) {
        this(title, null, v1, v2);
    }
    public FractionProgressMessage(String title, String msg, int v1, int v2) {
        super(title, msg);
        value1 = v1;
        value2 = v2;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public int getValue1() {
        return value1;
    }
    public int getValue2() {
        return value2;
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
        return (int) (value1 * 100.0 / value2);
    }
    @Override
    public String renderNumericMessage() {
        return value1 + " / " + value2;
    }
}
