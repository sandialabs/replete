package finio.ui.views;

import finio.ui.view.ViewContainerPanel;

public class ViewEvent {


    ////////////
    // FIELDS //
    ////////////

    private ViewContainerPanel pnlViewCont;
    private int index;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ViewEvent(ViewContainerPanel pnlViewCont, int index) {
        this.pnlViewCont = pnlViewCont;
        this.index = index;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public ViewContainerPanel getViewContainerPanel() {
        return pnlViewCont;
    }
    public int getIndex() {
        return index;
    }
}
