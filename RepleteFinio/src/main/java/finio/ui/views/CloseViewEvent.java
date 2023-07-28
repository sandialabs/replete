package finio.ui.views;

import finio.ui.view.ViewContainerPanel;

public class CloseViewEvent extends ViewEvent {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public CloseViewEvent(ViewContainerPanel pnlViewCont, int index) {
        super(pnlViewCont, index);
    }
}
