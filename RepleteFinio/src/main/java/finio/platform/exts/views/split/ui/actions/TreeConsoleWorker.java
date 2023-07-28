package finio.platform.exts.views.split.ui.actions;

import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.worlds.WorldContext;

public class TreeConsoleWorker extends FWorker<Void, Void> {


    ///////////
    // FIELD //
    ///////////

    private boolean vert;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public TreeConsoleWorker(AppContext ac, WorldContext wc, String name, boolean vert) {
        super(ac, wc, name);
        this.vert = vert;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected void completeInner(Void result) {
        ac.getConfig().setSplitPaneState(vert ? 1 : 2);
    }
}
