package finio.platform.exts.worlds.tabs.ui.actions;

import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.worlds.WorldContext;

public class SetWorldTabPlacementWorker extends FWorker<Void, Void> {


    ///////////
    // FIELD //
    ///////////

    private int placement;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public SetWorldTabPlacementWorker(AppContext ac, WorldContext wc, String name, int placement) {
        super(ac, wc, name);
        this.placement = placement;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected void completeInner(Void result) {
        ac.getConfig().setWorldsTabPlacement(placement);
    }
}
