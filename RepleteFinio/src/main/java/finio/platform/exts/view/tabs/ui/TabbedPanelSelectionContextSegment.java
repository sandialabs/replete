package finio.platform.exts.view.tabs.ui;

import finio.ui.view.SelectionContextSegment;

public class TabbedPanelSelectionContextSegment extends SelectionContextSegment {


    ////////////
    // FIELDS //
    ////////////

    protected int index;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public TabbedPanelSelectionContextSegment(Object K, Object V, int index) {
        super(K, V);
        this.index = index;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public int getIndex() {
        return index;
    }
}
