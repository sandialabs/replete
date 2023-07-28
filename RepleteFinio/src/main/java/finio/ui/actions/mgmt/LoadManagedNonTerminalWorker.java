package finio.ui.actions.mgmt;

import finio.core.managed.ManagedNonTerminal;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.view.ExpandRequest;
import finio.ui.view.SelectAction;
import finio.ui.view.SelectionContext;
import finio.ui.worlds.WorldContext;

public class LoadManagedNonTerminalWorker extends FWorker<Void, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public LoadManagedNonTerminalWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected Void background(Void gathered) throws Exception {

        for(SelectionContext C : getValidSelected()) {
            Object V = C.getV();
            ManagedNonTerminal G = (ManagedNonTerminal) V;
            if(!G.isLoaded()) {
                G.load();
                expand(
                    new ExpandRequest()
                        .setContext(C)
                        .setAction(SelectAction.SELF)
                );
            }
        }

        return null;
    }

    @Override
    public String getActionVerb() {
        return "loading managed non-terminal";
    }

    @Override
    protected void completeInner(Void result) {
        ac.getActionMap().validate();
    }
}
