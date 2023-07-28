package replete.ui.sdplus.events;

import replete.ui.sdplus.ScaleSetPanel;

/**
 * An event created whenever the filter criteria
 * changes in the scale set panel.  Used whenever
 * the scale set panel's coalesced events is
 * set to true and there is only one event fired
 * each time the user chooses Select All or
 * Deselect All from the options pane.
 *
 * @author Derek Trumbo
 */

public class ScaleSetChangedCoalescedEvent extends ScaleSetEvent
        implements ValueChangedEvent {

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ScaleSetChangedCoalescedEvent(ScaleSetPanel pnl) {
        super(pnl);
    }
}
