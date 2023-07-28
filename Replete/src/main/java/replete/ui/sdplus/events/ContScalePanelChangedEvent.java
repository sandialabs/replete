package replete.ui.sdplus.events;

import replete.ui.sdplus.ScaleSetPanel;
import replete.ui.sdplus.panels.ContScalePanelModel;
import replete.ui.sdplus.panels.ScalePanel;
import replete.ui.sdplus.panels.ScalePanelModel;

/**
 * An event created whenever the filter criteria in
 * a continuous scale panel changes.
 *
 * @author Derek Trumbo
 */

public class ContScalePanelChangedEvent extends ScalePanelChangedEvent {

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ContScalePanelChangedEvent(ScaleSetPanel s, String k,
                                      ScalePanel p, ScalePanelModel m) {
        super(s, k, p, m);
    }

    ///////////////
    // ACCESSORS //
    ///////////////

    @Override
    public ContScalePanelModel getScalePanelModel() {
        return (ContScalePanelModel) model;
    }
}
