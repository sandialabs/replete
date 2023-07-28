package finio.ui.actions.transform;

import static finio.core.impl.FMap.A;

import finio.core.NonTerminal;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.view.ExpandRequest;
import finio.ui.view.SelectAction;
import finio.ui.view.SelectionContext;
import finio.ui.worlds.WorldContext;
import replete.ui.windows.Dialogs;

public class SplitStringWorker extends FWorker<String, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public SplitStringWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected String gather() {
        return Dialogs.showInput(
            ac.getWindow(),
            "Enter the regular expression with which to split the string (e.g. \\s*,\\s*)",
            "Split String"
        );
    }

    @Override
    protected boolean proceed(String gathered) {
        return gathered != null;
    }

    @Override
    protected Void background(String gathered) throws Exception {

        for(SelectionContext C : getValidSelected()) {
            Object V = C.getV();
            String S = V.toString();
            String[] parts = S.split(gathered);
            NonTerminal Mparent = (NonTerminal) C.getParentV();
            NonTerminal Mnew = A();
            for(int p = 0; p < parts.length; p++) {
                Mnew.put(p, parts[p]);
            }
            Object K = C.getK();
            Mparent.put(K, Mnew);

            expand(
                new ExpandRequest()
                    .setContext(C)
                    .setAction(SelectAction.SIBLING)
                    .setArgs(K)
            );
            // Selection unchanged.
        }

        return null;
    }

    @Override
    public String getActionVerb() {
        return "splitting the string";
    }
}
