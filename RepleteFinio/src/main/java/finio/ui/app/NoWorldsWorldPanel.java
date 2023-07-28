package finio.ui.app;

import finio.ui.actions.FActionMap;
import finio.ui.fpanel.FPanel;
import replete.ui.lay.Lay;

public class NoWorldsWorldPanel extends FPanel {


    ////////////
    // FIELDS //
    ////////////

    private FActionMap actionMap;
    private AppContext ac;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public NoWorldsWorldPanel(AppContext ac) {
        this.ac = ac;
        actionMap = ac.getActionMap();

        Lay.BLtg(this,
            "C", new LoadInitialWorldPanel(ac)   // Currently can't @Override paintComponent so need to compose
        );
    }
}
