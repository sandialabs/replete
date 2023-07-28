package finio.ui.actions.mark;

import finio.platform.exts.view.treeview.ui.FNode;
import finio.platform.exts.view.treeview.ui.FTree;
import finio.platform.exts.view.treeview.ui.nodes.NodeFTree;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.worlds.WorldContext;

public class ToggleAnchorWorker extends FWorker<Void, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ToggleAnchorWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected Void background(Void gathered) throws Exception {
        FTree tree = wc.getWorldPanel().getTreePanel().getTree();
        boolean anchor = !tree.shouldUnanchor();
        for(FNode nSel : tree.getASelectionNodes()) {
            NodeFTree uSel = nSel.getObject();
            if(anchor) {
                tree.getAnchorNodes().add(nSel);
            } else {
                tree.getAnchorNodes().remove(nSel);
            }
            uSel.setAnchor(anchor);
            tree.getModel().nodeChanged(nSel);                 // UI Thread :(
        }
        return null;
    }

    @Override
    public String getActionVerb() {
        return "toggling the anchor state";
    }
}
