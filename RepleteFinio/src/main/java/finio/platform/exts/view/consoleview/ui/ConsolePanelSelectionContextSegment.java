package finio.platform.exts.view.consoleview.ui;

import finio.core.KeyPath;
import finio.ui.view.SelectionContextSegment;

public class ConsolePanelSelectionContextSegment extends SelectionContextSegment {


    ////////////
    // FIELDS //
    ////////////

    protected KeyPath P;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ConsolePanelSelectionContextSegment(Object K, Object V, KeyPath P) {
        super(K, V);
        this.P = P;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public KeyPath getP() {
        return P;
    }
}
