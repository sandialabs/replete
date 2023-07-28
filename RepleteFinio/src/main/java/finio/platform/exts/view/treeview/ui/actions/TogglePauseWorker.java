package finio.platform.exts.view.treeview.ui.actions;

import finio.platform.exts.view.treeview.ui.FNode;
import finio.platform.exts.view.treeview.ui.FTree;
import finio.platform.exts.view.treeview.ui.TreePanelSelectionContextSegment;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.view.ExpandRequest;
import finio.ui.view.SelectAction;
import finio.ui.view.SelectionContext;
import finio.ui.view.SelectionContextSegment;
import finio.ui.worlds.WorldContext;

public class TogglePauseWorker extends FWorker<Void, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public TogglePauseWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected boolean proceed(Void gathered) {
        FTree tree = wc.getWorldPanel().getTreePanel().getTree();
        return tree.isSelection();
    }

    @Override
    protected Void background(Void gathered) throws Exception {
        FTree tree = wc.getWorldPanel().getTreePanel().getTree();
        boolean pause = !tree.shouldUnpause();
        for(SelectionContext C : getValidSelected()) {
            SelectionContextSegment segment = C.getSegment(0);
            if(segment instanceof TreePanelSelectionContextSegment) {
                FNode nSel = ((TreePanelSelectionContextSegment) segment).getNode();
                nSel.setPaused(pause);

                expand(
                    new ExpandRequest()
                        .setContext(C)
                        .setAction(SelectAction.SELF)
                );
            }
        }
        return null;
    }
}
