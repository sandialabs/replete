package finio.ui.actions.view;

import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.world.WorldPanel;
import finio.ui.worlds.WorldContext;
import replete.ui.windows.Dialogs;

public class RenameViewWorker extends FWorker<String, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public RenameViewWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected String gather() {
        String current = wc.getWorldPanel().getSelectedViewContainerPanel().getViewName();
        return Dialogs.showInput(ac.getWindow(),
            "Enter a new name for this view:", "Rename View", current);
    }

    @Override
    protected boolean proceed(String gathered) {
        return gathered != null;
    }

    @Override
    public String getActionVerb() {
        return "renaming this view";
    }

    @Override
    protected void completeInner(Void result) {
        String input = gathered.trim();
        WorldPanel pnlWorld = wc.getWorldPanel();
        pnlWorld.renameSelectedView(input);
    }
}
