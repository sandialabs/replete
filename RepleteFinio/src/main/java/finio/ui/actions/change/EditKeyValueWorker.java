package finio.ui.actions.change;

import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.worlds.WorldContext;

public class EditKeyValueWorker extends FWorker<String, Void> {


    ///////////
    // FIELD //
    ///////////

    private boolean shift;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public EditKeyValueWorker(AppContext ac, WorldContext wc, String name, boolean shift) {
        super(ac, wc, name);
        this.shift = shift;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected Void background(String gathered) throws Exception {
        setShouldEdit(shift);
        return null;
    }
}
