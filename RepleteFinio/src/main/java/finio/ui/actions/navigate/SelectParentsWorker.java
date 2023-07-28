package finio.ui.actions.navigate;

import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.view.SelectAction;
import finio.ui.view.SelectRequest;
import finio.ui.view.SelectionContext;
import finio.ui.worlds.WorldContext;

public class SelectParentsWorker extends FWorker<Void, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public SelectParentsWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getActionVerb() {
        return "selecting the parent";
    }

    @Override
    protected void completeInner(Void result) {
        for(SelectionContext C : getValidSelected()) {
            select(
                new SelectRequest()
                    .setContext(C)
                    .setAction(SelectAction.PARENT)
            );
        }
    }
}
