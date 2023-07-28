package replete.ui.sdplus.events;

import replete.ui.sdplus.ScaleSetPanel;
import replete.ui.sdplus.panels.ScalePanel;
import replete.ui.sdplus.panels.ScalePanelModel;

/**
 * An event created whenever a scale's corresponding
 * column in the table is shown or hidden.
 *
 * @author Derek Trumbo
 */

public class ListedChangedEvent extends ScalePanelEvent {

    ///////////
    // Field //
    ///////////

    protected boolean newState;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ListedChangedEvent(ScaleSetPanel s, String k,
                              ScalePanel p, ScalePanelModel m,
                              boolean state) {
        super(s, k, p, m);
        newState = state;
    }

    ///////////////
    // ACCESSORS //
    ///////////////

    public boolean getNewState() {
        return newState;
    }

    //////////////
    // toString //
    //////////////

    @Override
    protected String internalString() {
        return super.internalString() +
            ", newState=" + newState;
    }
}
