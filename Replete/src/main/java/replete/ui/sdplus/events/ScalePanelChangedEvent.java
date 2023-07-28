package replete.ui.sdplus.events;

import replete.ui.sdplus.ScaleSetPanel;
import replete.ui.sdplus.panels.ScalePanel;
import replete.ui.sdplus.panels.ScalePanelModel;

/**
 * An event created whenever the filter criteria
 * in one of the scale panels changes.
 *
 * @author Derek Trumbo
 */

public class ScalePanelChangedEvent extends ScalePanelEvent
        implements ValueChangedEvent {

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ScalePanelChangedEvent(ScaleSetPanel s, String k,
                                  ScalePanel p, ScalePanelModel m) {
        super(s, k, p, m);
    }
}
