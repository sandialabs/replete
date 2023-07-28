package finio.ui.actions.change;

import finio.core.NonTerminal;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.view.SelectAction;
import finio.ui.view.SelectRequest;
import finio.ui.view.SelectionContext;
import finio.ui.worlds.WorldContext;

public class ClearWorker extends FWorker<Void, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ClearWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "Clear";
    }

    @Override
    protected Void background(Void gathered) throws Exception {
        for(SelectionContext C : getValidSelected()) {
            NonTerminal M = (NonTerminal) C.getV();
            M.clear();
            select(
                new SelectRequest()
                    .setContext(C)
                    .setAction(SelectAction.SELF)
            );
        }
        return null;
    }

    @Override
    public String getActionVerb() {
        return "clearing the map";
    }
}
