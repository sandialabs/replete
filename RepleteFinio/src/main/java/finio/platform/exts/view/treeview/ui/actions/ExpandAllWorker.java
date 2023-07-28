package finio.platform.exts.view.treeview.ui.actions;

import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.view.ExpandRequest;
import finio.ui.view.SelectAction;
import finio.ui.view.SelectionContext;
import finio.ui.worlds.WorldContext;

public class ExpandAllWorker extends FWorker<Void, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ExpandAllWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected void completeInner(Void result) {
        for(SelectionContext C : getValidSelected()) {
            expand(
                new ExpandRequest()
                    .setContext(C)
                    .setAction(SelectAction.SELF)
                    .setAll(true)
            );
        }
    }
}
