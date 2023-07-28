package finio.ui.actions.window;

import finio.ui.FFrame;
import finio.ui.FFrameManager;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.worlds.WorldContext;

public class NewWindowWorker extends FWorker<Void, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public NewWindowWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getActionVerb() {
        return "opening a new window";
    }

    @Override
    protected void completeInner(Void result) {
        FFrame F = FFrameManager.getInstance().create();
        F.setVisible(true);
    }
}
