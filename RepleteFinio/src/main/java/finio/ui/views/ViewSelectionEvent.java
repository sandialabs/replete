package finio.ui.views;

import finio.ui.view.ViewContainerPanel;

public class ViewSelectionEvent extends ViewEvent {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ViewSelectionEvent(ViewContainerPanel pnlViewCont, int index) {
        super(pnlViewCont, index);
    }
}
