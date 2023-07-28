package finio.platform.exts.view.treeview.ui.actions;

import finio.platform.exts.view.treeview.ui.FTree;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.worlds.WorldContext;
import replete.ui.tree.RTreePath;

public class CollapseWorker extends FWorker<Void, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public CollapseWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    // Collapse currently has a bug in it.  When you "collapse" a node...
    // for some reason, JTree also makes it "viewable"... in order to do
    // that... it has to "expand" all parent nodes!  So if you wanted to
    // collapse a node and its children using multi-select, if you don't
    // have the children preceding the parent in the selection order,
    // this will not work period.

    @Override
    protected void completeInner(Void result) {
        FTree tree = wc.getWorldPanel().getTreePanel().getTree();
        RTreePath[] pSels = tree.getSelPaths();                // Order Selected
        for(RTreePath pSel : pSels) {
            tree.collapse(pSel);
        }
    }
}
