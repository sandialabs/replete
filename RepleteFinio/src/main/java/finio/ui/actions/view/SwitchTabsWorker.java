package finio.ui.actions.view;

import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.worlds.WorldContext;

public class SwitchTabsWorker extends FWorker<Void, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public SwitchTabsWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getActionVerb() {
        return "switching to tabs mode";
    }

    @Override
    protected void completeInner(Void result) {
        ac.getConfig().setWorldsUseDesktopPane(false);
    }
}
