package finio.ui.actions.mgmt;

import finio.core.managed.ManagedNonTerminal;
import finio.manager.ManagedParameters;
import finio.plugins.extpoints.NonTerminalManager;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.manager.ManagedParametersDialog;
import finio.ui.manager.ManagedParametersPanel;
import finio.ui.view.SelectionContext;
import finio.ui.worlds.WorldContext;

public class EditManagedParametersDialogWorker extends FWorker<ManagedParameters, Void> {


    ///////////
    // FIELD //
    ///////////

    private ManagedNonTerminal G;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public EditManagedParametersDialogWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected ManagedParameters gather() {
        SelectionContext C = getValidSelected().get(0);
        G = (ManagedNonTerminal) C.getV();
        NonTerminalManager manager = G.getManager();
        ManagedParametersPanel pnlParams = manager.createParametersPanel();
        ManagedParametersDialog dlg = new ManagedParametersDialog(
            ac.getWindow(), G.getParams(), pnlParams);
        dlg.setVisible(true);
        return dlg.getResult() == ManagedParametersDialog.SET ? dlg.getParams() : null;
    }

    @Override
    protected boolean proceed(ManagedParameters gathered) {
        return gathered != null;
    }

    @Override
    protected Void background(ManagedParameters params) throws Exception {
        G.setParams(params);
        return null;
    }
}
