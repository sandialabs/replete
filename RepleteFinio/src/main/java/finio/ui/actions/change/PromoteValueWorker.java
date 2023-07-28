package finio.ui.actions.change;

import finio.core.NonTerminal;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.view.SelectAction;
import finio.ui.view.SelectRequest;
import finio.ui.view.SelectionContext;
import finio.ui.worlds.WorldContext;

public class PromoteValueWorker extends FWorker<String, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public PromoteValueWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected Void background(String gathered) throws Exception {

        for(SelectionContext C : getValidSelected()) {
            NonTerminal Mgrandparent = (NonTerminal) C.getGrandparentV();
            Object Kparent = C.getParentK();
            Mgrandparent.promote(Kparent);

            select(
                new SelectRequest()
                    .setContext(C)
                    .setAction(SelectAction.PARENT)
                    .setArgs(Kparent)
            );
        }

        return null;
    }

    @Override
    public String getActionVerb() {
        return "promoting the value";
    }
}
