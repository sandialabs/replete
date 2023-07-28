package replete.ui.sdplus.events;

import replete.ui.sdplus.ScaleSetPanel;
import replete.ui.sdplus.panels.ScalePanel;
import replete.ui.sdplus.panels.ScalePanelModel;

/**
 * An event created whenever something happens
 * at the scale panel level.  This could be
 * filter criteria changes or colors, etc.
 *
 * @author Derek Trumbo
 */

public class ScalePanelEvent extends ScaleSetEvent {

    ////////////
    // Fields //
    ////////////

    protected String key;
    protected ScalePanel pnlScale;
    protected ScalePanelModel model;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ScalePanelEvent(ScaleSetPanel s, String k,
                           ScalePanel p, ScalePanelModel m) {
        super(s);
        key = k;
        pnlScale = p;
        model = m;
    }

    ///////////////
    // Accessors //
    ///////////////

    public String getKey() {
        return key;
    }

    public ScalePanel getScalePanel() {
        return pnlScale;
    }

    public ScalePanelModel getScalePanelModel() {
        return model;
    }

    //////////////
    // toString //
    //////////////

    @Override
    protected String internalString() {
        return super.internalString() +
            ", key=" + key +
            ", model=" + model;
    }
}
