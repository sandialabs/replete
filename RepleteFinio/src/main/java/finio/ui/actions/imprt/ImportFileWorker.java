package finio.ui.actions.imprt;

import java.io.File;

import finio.core.NonTerminal;
import finio.extractors.FileSystemExtractor;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.view.ExpandRequest;
import finio.ui.view.SelectAction;
import finio.ui.view.SelectRequest;
import finio.ui.view.SelectionContext;
import finio.ui.worlds.WorldContext;
import replete.ui.fc.RFileChooser;
import replete.util.DateUtil;

public class ImportFileWorker extends FWorker<File, NonTerminal> {


    ///////////
    // FIELD //
    ///////////

    private SelectionContext C;
    private NonTerminal Mcontext;
    private String K;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ImportFileWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected File gather() {

        // Choose the file path to be imported
        RFileChooser fc = RFileChooser.getChooser(
            getName() + ": Select File");
        if(fc.showOpen(ac.getWindow())) {

            // Grab appropriate context from the tree.
            C = wc.getWorldPanel().getSelectedView().getSelectedValue();
            Mcontext = (NonTerminal) C.getV();
            String Kproposed = (String) Mcontext.getNextAvailableKey(getName() + "-");
            Kproposed = getDesiredKeyName(Kproposed, Mcontext);

            // If the process was not canceled, move forward with the action.
            if(Kproposed != null) {
                K = Kproposed;
                return fc.getSelectedFile();
            }
        }

        return null;
    }

    @Override
    protected boolean proceed(File gathered) {
        return gathered != null;
    }

    @Override
    protected NonTerminal background(File gathered) throws Exception {
        NonTerminal M = new FileSystemExtractor(gathered, false).extract();
        M.putSysMeta("time", DateUtil.toLongString(System.currentTimeMillis()));
        M.putSysMeta("source", "File Path");
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
        return "importing file path";
    }
}
