package finio.ui.actions.imprt;

import java.net.URL;

import finio.core.NonTerminal;
import finio.extractors.UrlExtractor;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.view.ExpandRequest;
import finio.ui.view.SelectAction;
import finio.ui.view.SelectRequest;
import finio.ui.view.SelectionContext;
import finio.ui.worlds.WorldContext;
import replete.ui.windows.Dialogs;
import replete.util.DateUtil;

public class ImportUrlWorker extends FWorker<String, NonTerminal> {


    ////////////
    // FIELDS //
    ////////////

    private SelectionContext C;
    private NonTerminal Mcontext;
    private String K;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ImportUrlWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected String gather() {
        String input = Dialogs.showInput(ac.getWindow(),
            "Enter URL:", getName() + ": URL",
            "http://127.9.23.1/pages");

        if(input != null) {

            // Grab appropriate context from the tree.
            C = wc.getWorldPanel().getSelectedView().getSelectedValue();
            Mcontext = (NonTerminal) C.getV();
            String Kproposed = (String) Mcontext.getNextAvailableKey(getName() + "-");
            K = getDesiredKeyName(Kproposed, Mcontext);
            if(K != null) {
                return input;
            }
        }

        return null;
    }

    @Override
    protected boolean proceed(String gathered) {
        return gathered != null;
    }

    @Override
    protected NonTerminal background(String gathered) throws Exception {
        URL U = new URL(gathered);
        NonTerminal M = new UrlExtractor(U).extract();
        M.putSysMeta("time", DateUtil.toLongString(System.currentTimeMillis()));
        M.putSysMeta("source", "URL");
        Mcontext.put(K, M);
        select(
            new SelectRequest()
                .setContext(C)
                .setAction(SelectAction.CHILD)
                .setArgs(K)
        );
        expand(
            new ExpandRequest()
                .setContext(C)
                .setAction(SelectAction.CHILD)
                .setArgs(K)
        );
        return M;
    }

    @Override
    public String getActionVerb() {
        return "importing URL";
    }
}
