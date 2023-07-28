package replete.ui.sdplus.events;

import replete.ui.sdplus.ScaleSetPanel;

/**
 * An event created whenever something happens
 * at the scale panel set level.  This could be
 * filter criteria changes or colors, etc.
 *
 * @author Derek Trumbo
 */

public class ScaleSetEvent {

    ///////////
    // Field //
    ///////////

    protected ScaleSetPanel pnlSet;     // Can be null if source panel autonomous.

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ScaleSetEvent(ScaleSetPanel s) {
        pnlSet = s;
    }

    ///////////////
    // ACCESSORS //
    ///////////////

    public ScaleSetPanel getScaleSetPanel() {
        return pnlSet;
    }

    //////////////
    // toString //
    //////////////

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + internalString() + "]";
    }

    protected String internalString() {
        if(pnlSet == null) {
            return "<no set>";
        }

        return pnlSet.toString();
    }
}
