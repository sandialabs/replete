package replete.ui.sdplus.events;

import replete.ui.sdplus.ScaleSetPanel;
import replete.ui.sdplus.panels.EnumScaleBasePanelModel;
import replete.ui.sdplus.panels.ScalePanel;
import replete.ui.sdplus.panels.ScalePanelModel;

/**
 * An event created whenever the filter criteria
 * in an enumerated scale panel changes.  This event
 * is used when enumerated scale panel coalesced
 * events are off and the user chooses Select All
 * or Deselect All -- one event will be fired for
 * each value that changes.
 *
 * @author Derek Trumbo
 */

public class EnumScalePanelIndvChangedEvent extends ScalePanelChangedEvent {

    ////////////
    // Fields //
    ////////////

    protected Object value;
    protected boolean newState;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public EnumScalePanelIndvChangedEvent(ScaleSetPanel s, String k,
                                          ScalePanel p, ScalePanelModel m,
                                          Object val, boolean state) {
        super(s, k, p, m);
        value = val;
        newState = state;
    }

    ///////////////
    // Accessors //
    ///////////////

    public Object getValue() {
        return value;
    }

    public boolean getNewState() {
        return newState;
    }

    @Override
    public EnumScaleBasePanelModel getScalePanelModel() {
        return (EnumScaleBasePanelModel) model;
    }

    //////////////
    // toString //
    //////////////

    @Override
    protected String internalString() {
        return super.internalString() +
            ", value=" + value +
            ", newState=" + newState +
            ", selValues=" +
                ((EnumScaleBasePanelModel) model).getSelectedValues();
    }
}
