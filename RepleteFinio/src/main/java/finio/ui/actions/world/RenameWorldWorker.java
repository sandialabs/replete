package finio.ui.actions.world;

import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.worlds.WorldContext;
import replete.ui.windows.Dialogs;

public class RenameWorldWorker extends FWorker<String, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public RenameWorldWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected String gather() {
        String current = wc.getName();
        return Dialogs.showInput(ac.getWindow(),
            "Enter a new name for this world:", "Rename World", current);
    }

    @Override
    protected boolean proceed(String gathered) {
        return gathered != null;
    }

    @Override
    public String getActionVerb() {
        return "renaming this world";
    }

    @Override
    protected void completeInner(Void result) {
        String input = gathered.trim();
        if(input.isEmpty()) {
            Dialogs.showMessage(ac.getWindow(),
                "World name cannot be blank.", "Rename World");
        } else {
            ac.renameWorld(ac.getSelectedWorldIndex(), input);
        }
    }
}
