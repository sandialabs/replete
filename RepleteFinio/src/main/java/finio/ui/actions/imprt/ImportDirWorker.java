package finio.ui.actions.imprt;

import java.io.File;

import javax.swing.JFileChooser;

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
import replete.ui.windows.Dialogs;
import replete.util.DateUtil;

public class ImportDirWorker extends FWorker<Void, NonTerminal> {


    ////////////
    // FIELDS //
    ////////////

    private File file;
    private boolean recurse;
    private SelectionContext C;
    private NonTerminal Mcontext;
    private String K;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ImportDirWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected Void gather() {

        // Choose the directory path to be imported
        RFileChooser fc = RFileChooser.getChooser(
            getName() + ": Select Directory",
            JFileChooser.DIRECTORIES_ONLY);

        if(fc.showOpen(ac.getWindow())) {
            recurse = Dialogs.showConfirm(
                ac.getWindow(), "Should this directory path be expanded recursively?",
                getName() + ": Recursive?", true);

            // Grab appropriate context from the tree.
            C = wc.getWorldPanel().getSelectedView().getSelectedValue();
            Mcontext = (NonTerminal) C.getV();
            String Kproposed = (String) Mcontext.getNextAvailableKey(getName() + "-");
            Kproposed = getDesiredKeyName(Kproposed, Mcontext);

            // If the process was not canceled, move forward with the action.
            if(Kproposed != null) {
                K = Kproposed;
                file = fc.getSelectedFile();
            }
        }

        return null;
    }

    @Override
    protected boolean proceed(Void gathered) {
        return file != null;
    }

    @Override
    protected NonTerminal background(Void gathered) throws Exception {
        NonTerminal M = new FileSystemExtractor(file, recurse).extract();
        M.putSysMeta("time", DateUtil.toLongString(System.currentTimeMillis()));
        M.putSysMeta("source", "Directory Path");
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
        return "importing directory path";
    }
}
