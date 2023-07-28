package finio.ui.actions.transform;

import finio.core.NonTerminal;
import finio.extractors.StringAsCharArrayExtractor;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.view.ExpandRequest;
import finio.ui.view.SelectAction;
import finio.ui.view.SelectionContext;
import finio.ui.worlds.WorldContext;

public class ExpandStringWorker extends FWorker<Void, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ExpandStringWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected Void background(Void gathered) throws Exception {

        for(SelectionContext C : getValidSelected()) {
            NonTerminal Mparent = (NonTerminal) C.getParentV();
            Object V = C.getV();

            StringAsCharArrayExtractor X =
                new StringAsCharArrayExtractor(V.toString());
            NonTerminal VselNew = X.extract();

            Object K = C.getK();
            Mparent.put(C.getK(), VselNew);

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
        return "expanding the string";
    }
}
