package finio.platform.exts.view.treeview.ui.actions;

import finio.core.syntax.FMapSyntax;
import finio.platform.exts.view.treeview.ui.FNode;
import finio.platform.exts.view.treeview.ui.TreePanelSelectionContextSegment;
import finio.renderers.map.StandardAMapRenderer;
import finio.ui.SyntaxSelectionDialog;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.view.ExpandRequest;
import finio.ui.view.SelectAction;
import finio.ui.view.SelectionContext;
import finio.ui.view.SelectionContextSegment;
import finio.ui.worlds.WorldContext;

public class ToggleSimplifiedWorker extends FWorker<FMapSyntax, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ToggleSimplifiedWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected FMapSyntax gather() {
        SyntaxSelectionDialog dlg = new SyntaxSelectionDialog(ac.getWindow());
        dlg.setVisible(true);
        return dlg.getResult() == SyntaxSelectionDialog.SELECT ? dlg.getSyntax() : null;
    }

    @Override
    protected boolean proceed(FMapSyntax gathered) {
        return gathered != null;
    }

    @Override
    protected Void background(FMapSyntax syntax) throws Exception {
        StandardAMapRenderer renderer = new StandardAMapRenderer(syntax);
//        FTree tree = wc.getWorldPanel().getTreePanel().getTree();  Maybe need for whether to simp/desimp?
        for(SelectionContext C : getValidSelected()) {
            SelectionContextSegment S = C.getSegment(0);
            if(S instanceof TreePanelSelectionContextSegment) {
                FNode nSel = ((TreePanelSelectionContextSegment) S).getNode();

                if(nSel.getSimplified() == null) {
                    String s = renderer.renderValue(C.getV());
                    nSel.setSimplified(s);
                } else {
                    nSel.setSimplified(null);
                }

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
