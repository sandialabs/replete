package replete.progress;

import replete.text.StringUtil;

/**
 * @author Derek Trumbo
 */

public abstract class ProgressMessage {


    ////////////
    // FIELDS //
    ////////////

    protected String majorMessage;   // Could be source process if context needed
    protected String minorMessage;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ProgressMessage(String title, String msg) {
        majorMessage = title;
        minorMessage = msg;
    }


    //////////////
    // ABSTRACT //
    //////////////

    public abstract boolean isIndeterminate();
    public abstract int calculatePercentDone();
    public abstract String renderNumericMessage();


    ///////////////
    // ACCESSORS //
    ///////////////

    public String getMajorMessage() {
        return majorMessage;
    }
    public String getMinorMessage() {
        return minorMessage;
    }


    //////////
    // MISC //
    //////////

    public String renderTextualMessage() {
        String textual = "";
        if(!StringUtil.isBlank(majorMessage)) {
            textual = majorMessage;
            if(!StringUtil.isBlank(minorMessage)) {
                textual += ": ";
            }
        }
        if(!StringUtil.isBlank(minorMessage)) {
            textual += minorMessage;
        }
        return textual;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        if(isIndeterminate()) {
            return "[~] " + renderTextualMessage();
        }
        return "[" + calculatePercentDone() + "% : " + renderNumericMessage() + "] " + renderTextualMessage();
    }
}
