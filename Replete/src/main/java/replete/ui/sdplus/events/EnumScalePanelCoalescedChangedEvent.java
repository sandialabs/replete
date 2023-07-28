package replete.ui.sdplus.events;

import replete.ui.sdplus.ScaleSetPanel;
import replete.ui.sdplus.panels.EnumScaleMultiPanelModel;
import replete.ui.sdplus.panels.ScalePanel;
import replete.ui.sdplus.panels.ScalePanelModel;

/**
 * An event created whenever the filter criteria
 * in an enumerated scale panel changes.  This
 * coalesced event is used when enumerated scale
 * panel coalesced evets are on and the user chooses
 * Select All or Deselect All - and there's only
 * one event fired for this operation.
 *
 * @author Derek Trumbo
 */

public class EnumScalePanelCoalescedChangedEvent extends ScalePanelChangedEvent {

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public EnumScalePanelCoalescedChangedEvent(ScaleSetPanel s, String k,
                                               ScalePanel p, ScalePanelModel m) {
        super(s, k, p, m);
    }

    ///////////////
    // ACCESSORS //
    ///////////////

    @Override
    public EnumScaleMultiPanelModel getScalePanelModel() {
        return (EnumScaleMultiPanelModel) model;
    }
}
