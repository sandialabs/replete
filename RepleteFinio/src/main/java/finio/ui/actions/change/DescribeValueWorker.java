package finio.ui.actions.change;

import finio.core.NonTerminal;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.view.ExpandRequest;
import finio.ui.view.SelectAction;
import finio.ui.view.SelectRequest;
import finio.ui.view.SelectionContext;
import finio.ui.worlds.WorldContext;

public class DescribeValueWorker extends FWorker<String, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public DescribeValueWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected Void background(String gathered) throws Exception {
        for(SelectionContext C : getValidSelected()) {
            NonTerminal Mparent = (NonTerminal) C.getParentV();
            Object K = C.getK();
            Mparent.describe(K);

            select(
                new SelectRequest()
                    .setContext(C)
                    .setAction(SelectAction.SIBLING)
                    .setArgs(K)
            );
            expand(
                new ExpandRequest()
                    .setContext(C)
                    .setAction(SelectAction.SIBLING)
                    .setArgs(K)
            );
        }

        return null;
    }

    @Override
    public String getActionVerb() {
        return "describing the value";
    }
}
