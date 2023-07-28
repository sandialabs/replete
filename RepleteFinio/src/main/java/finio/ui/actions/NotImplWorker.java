package finio.ui.actions;

import finio.ui.app.AppContext;
import finio.ui.worlds.WorldContext;

public class NotImplWorker extends FWorker<Void, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public NotImplWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected Void gather() {
        ac.notImpl(getName());
        return null;
    }
}
