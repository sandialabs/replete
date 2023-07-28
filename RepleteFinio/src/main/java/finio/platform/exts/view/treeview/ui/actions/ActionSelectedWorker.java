package finio.platform.exts.view.treeview.ui.actions;

import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.worlds.WorldContext;

public class ActionSelectedWorker extends FWorker<Void, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ActionSelectedWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected Void gather() {
        ac.notImpl(getName());
//        uiController.getParentFrame().transferSelectedPaths(true);
        return null;
    }
}
