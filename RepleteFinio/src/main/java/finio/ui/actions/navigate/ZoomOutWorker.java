package finio.ui.actions.navigate;

import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.worlds.WorldContext;

public class ZoomOutWorker extends FWorker<Void, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ZoomOutWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected Void gather() {
        ac.notImpl("Zoom Out");
        return null;
    }
}
