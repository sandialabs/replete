package finio.platform.exts.views.split.ui.actions;

import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.worlds.WorldContext;

public class ConsoleOnlyWorker extends FWorker<Void, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ConsoleOnlyWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected void completeInner(Void result) {
        ac.getConfig().setSplitPaneState(3);
    }
}
