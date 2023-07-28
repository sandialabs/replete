package finio.ui.actions.transform;

import finio.core.FConst;
import finio.core.FUtil;
import finio.core.NonTerminal;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.view.ExpandRequest;
import finio.ui.view.SelectAction;
import finio.ui.view.SelectRequest;
import finio.ui.view.SelectionContext;
import finio.ui.worlds.WorldContext;

public class CreateAlternatesMapWorker extends FWorker<Void, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public CreateAlternatesMapWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected Void background(Void gathered) throws Exception {
        for(SelectionContext C : getValidSelected()) {
            Object K = C.getK();
            Object V = C.getV();

            if(!FUtil.isSysMetaKey(K) || !FUtil.isNonTerminal(V)) {
                if(FUtil.isNonTerminal(V)) {
                    NonTerminal M = (NonTerminal) V;
                    M.createAlternatesMap();

                    select(
                        new SelectRequest()
                            .setContext(C)
                            .setAction(SelectAction.CHILD)
                            .setArgs(FConst.SYS_ALT_KEY)
                    );
                    expand(
                        new ExpandRequest()
                            .setContext(C)
                            .setAction(SelectAction.CHILD)
                            .setArgs(FConst.SYS_ALT_KEY)
                    );

                } else {
                    NonTerminal Mparent = (NonTerminal) C.getParentV();
                    Mparent.createAlternatesMap(K);
//TODO not done
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
            }
        }

        return null;
    }

    @Override
    public String getActionVerb() {
        return "creating an alternates map";
    }
}
