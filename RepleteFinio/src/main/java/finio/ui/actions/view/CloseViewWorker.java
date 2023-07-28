package finio.ui.actions.view;

import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.world.WorldPanel;
import finio.ui.worlds.WorldContext;

public class CloseViewWorker extends FWorker<Void, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public CloseViewWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected Void gather() {
        WorldContext wc = ac.getSelectedWorld();
        WorldPanel pnlWorld = wc.getWorldPanel();
        pnlWorld.closeView(pnlWorld.getSelectedViewIndex());
        return null;
    }
}
