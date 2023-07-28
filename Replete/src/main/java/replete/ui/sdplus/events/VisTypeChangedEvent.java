package replete.ui.sdplus.events;

import replete.ui.sdplus.ScaleSetPanel;
import replete.ui.sdplus.panels.ScalePanel;
import replete.ui.sdplus.panels.ScalePanelModel;
import replete.ui.sdplus.panels.VisualizationType;

/**
 * An event created whenever the visualization
 * type changes for a given scale.
 *
 * @author Derek Trumbo
 */

public class VisTypeChangedEvent extends ScalePanelEvent {

    ///////////
    // Field //
    ///////////

    protected VisualizationType newVisType;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public VisTypeChangedEvent(ScaleSetPanel s, String k,
                               ScalePanel p, ScalePanelModel m,
                               VisualizationType type) {
        super(s, k, p, m);
        newVisType = type;
    }

    ///////////////
    // ACCESSORS //
    ///////////////

    public VisualizationType getNewVisType() {
        return newVisType;
    }

    //////////////
    // toString //
    //////////////

    @Override
    protected String internalString() {
        return super.internalString() +
            ", newVisType=" + newVisType;
    }
}
