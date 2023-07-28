package finio.ui.actions.imprt;

import finio.core.NonTerminal;
import finio.extractors.LocalScrutinizationExtractor;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.view.ExpandRequest;
import finio.ui.view.SelectAction;
import finio.ui.view.SelectRequest;
import finio.ui.view.SelectionContext;
import finio.ui.worlds.WorldContext;
import replete.util.DateUtil;

public class ImportScrutinizationWorker extends FWorker<Void, NonTerminal> {


    ////////////
    // FIELDS //
    ////////////

    private SelectionContext C;
    private NonTerminal Mcontext;
    private String K;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ImportScrutinizationWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected Void gather() {
        C = wc.getWorldPanel().getSelectedView().getSelectedValue();
        Mcontext = (NonTerminal) C.getV();
        String Kproposed = (String) Mcontext.getNextAvailableKey(getName() + "-");
        K = getDesiredKeyName(Kproposed, Mcontext);
        return null;
    }

    @Override
    protected boolean proceed(Void gathered) {
        return K != null;
    }

    @Override
    protected NonTerminal background(Void gathered) throws Exception {
        LocalScrutinizationExtractor X = new LocalScrutinizationExtractor();
        NonTerminal M = X.extract();
        M.putSysMeta("time", DateUtil.toLongString(System.currentTimeMillis()));
        M.putSysMeta("source", "Local Scrutinization");

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
        return "importing local scrutinization";
    }
}
