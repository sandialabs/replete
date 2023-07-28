package finio.platform.exts.view.treeview.ui.actions;

import finio.platform.exts.view.treeview.ui.FTree;
import finio.platform.exts.view.treeview.ui.nodes.NodeWorld;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.worlds.WorldContext;
import replete.ui.tree.RTreeNode;
import replete.ui.tree.RTreePath;

public class CollapseAllWorker extends FWorker<Void, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public CollapseAllWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected void completeInner(Void result) {
        FTree tree = wc.getWorldPanel().getTreePanel().getTree();
        RTreePath[] pSels = tree.getSelPaths();                // Order Selected
        for(RTreePath pSel : pSels) {
            if(pSel.get() instanceof NodeWorld) {
                for(RTreeNode nWorldChild : pSel.getLast()) {
                    tree.collapseAll(nWorldChild);
                }
            } else {
                tree.collapseAll(pSel);
            }
        }
    }
}
