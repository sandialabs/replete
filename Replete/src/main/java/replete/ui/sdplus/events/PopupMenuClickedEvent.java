package replete.ui.sdplus.events;

import replete.ui.sdplus.ScaleSetPanel;
import replete.ui.sdplus.panels.ScalePanel;
import replete.ui.sdplus.panels.ScalePanelModel;

/**
 * An event created whenever a scale panel's popup
 * menu item is clicked.
 *
 * @author Derek Trumbo
 */

public class PopupMenuClickedEvent extends ScalePanelEvent {

    ///////////
    // Field //
    ///////////

    protected String menuText;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public PopupMenuClickedEvent(ScaleSetPanel s, String k,
                                 ScalePanel p, ScalePanelModel m,
                                 String txt) {
        super(s, k, p, m);
        menuText = txt;
    }

    ///////////////
    // ACCESSORS //
    ///////////////

    public String getMenuText() {
        return menuText;
    }

    //////////////
    // toString //
    //////////////

    @Override
    protected String internalString() {
        return super.internalString() +
            ", menu=" + menuText;
    }
}
