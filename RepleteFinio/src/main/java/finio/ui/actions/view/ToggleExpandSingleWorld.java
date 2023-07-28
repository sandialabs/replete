package finio.ui.actions.view;

import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.worlds.WorldContext;

public class ToggleExpandSingleWorld extends FWorker<Void, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ToggleExpandSingleWorld(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getActionVerb() {
        return "toggling world expansion";
    }

    @Override
    protected void completeInner(Void result) {
        ac.getConfig().setWorldsExpandSingleWorld(
            !ac.getConfig().isWorldsExpandSingleWorld());
    }
}
