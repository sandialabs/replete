package finio.ui.actions.transform;

import finio.core.NonTerminal;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.view.ExpandRequest;
import finio.ui.view.SelectAction;
import finio.ui.view.SelectionContext;
import finio.ui.worlds.WorldContext;
import replete.ui.windows.Dialogs;

public class ConvertBytesToStringWorker extends FWorker<String, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ConvertBytesToStringWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected String gather() {
        String enc = Dialogs.showInput(ac.getWindow(), "Enc?", "Enc?", "UTF-8");
        if(enc == null) {
            return null;
        }
        return enc;
    }

    @Override
    protected boolean proceed(String gathered) {
        return gathered != null;
    }

    @Override
    protected Void background(String gathered) throws Exception {

        for(SelectionContext C : getValidSelected()) {
            NonTerminal Mparent = (NonTerminal) C.getParentV();
            Object V = C.getV();

            byte[] b = (byte[]) V;

            String S = new String(b, gathered);

            Object K = C.getK();
            Mparent.put(C.getK(), S);

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
        return "extracting the bytes";
    }
}
