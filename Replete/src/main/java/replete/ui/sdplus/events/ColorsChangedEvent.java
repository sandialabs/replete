package replete.ui.sdplus.events;

import replete.ui.sdplus.ScaleSetPanel;
import replete.ui.sdplus.color.ColorMap;
import replete.ui.sdplus.panels.ScalePanel;
import replete.ui.sdplus.panels.ScalePanelModel;

/**
 * An event created whenever a scale panel model's override
 * colors change (regardless of whether or not that scale panel
 * is selected for Color visualization).
 *
 * @author Derek Trumbo
 */

public class ColorsChangedEvent extends ScalePanelEvent {

    ///////////
    // Field //
    ///////////

    protected ColorMap overrideColors;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ColorsChangedEvent(ScaleSetPanel s, String k,
                              ScalePanel p, ScalePanelModel m,
                              ColorMap colors) {
        super(s, k, p, m);
        overrideColors = colors;
    }

    ///////////////
    // ACCESSORS //
    ///////////////

    public ColorMap getOverrideColors() {
        return overrideColors;
    }

    //////////////
    // toString //
    //////////////

    @Override
    protected String internalString() {
        return super.internalString() +
            ", colors=" + (overrideColors == null ? "no" : "yes");
    }
}
