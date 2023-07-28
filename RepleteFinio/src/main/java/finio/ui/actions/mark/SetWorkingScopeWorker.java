package finio.ui.actions.mark;

import javax.swing.tree.TreePath;

import finio.core.KeyPath;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.view.SelectionContext;
import finio.ui.view.ViewPanel;
import finio.ui.worlds.WorldContext;

public class SetWorkingScopeWorker extends FWorker<TreePath, KeyPath> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public SetWorkingScopeWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getActionVerb() {
        return "setting the working scope";
    }

    @Override
    protected void completeInner(KeyPath P) {
        ViewPanel pnlView = ac.getSelectedViewPanel();
        if(pnlView != null) {
            SelectionContext C = pnlView.getSelectedValue(Integer.MAX_VALUE);
            pnlView.setWorkingScope(C.getP());
        }
    }
}
