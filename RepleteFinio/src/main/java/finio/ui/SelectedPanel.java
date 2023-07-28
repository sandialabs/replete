package finio.ui;

import javax.swing.Icon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import finio.core.FUtil;
import finio.ui.app.AppContext;
import finio.ui.fpanel.FPanel;
import finio.ui.images.FinioImageModel;
import finio.ui.view.SelectionContext;
import finio.ui.view.ViewContainerPanel;
import finio.ui.view.ViewPanel;
import finio.ui.world.RenameWorldEvent;
import finio.ui.world.RenameWorldListener;
import finio.ui.worlds.WorldContext;
import finio.ui.worlds.WorldSelectedEvent;
import finio.ui.worlds.WorldSelectedListener;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;
import replete.ui.text.RLabel;
import replete.ui.tree.NodeBase;
import replete.ui.tree.RTree;
import replete.ui.tree.RTreeNode;

public class SelectedPanel extends FPanel {


    ////////////
    // FIELDS //
    ////////////

    private AppContext ac;
    private RLabel lblWorld;
    private RLabel lblView;
    private RLabel lblSep;
    private RTree tre;
    private RTreeNode nRoot;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public SelectedPanel(final AppContext ac) {
        this.ac = ac;

        Lay.BLtg(this,
            "N", Lay.FL("L",
                lblWorld = Lay.lb(),
                lblSep = Lay.lb(FinioImageModel.LIST_SEPARATOR, "eb=5lr"),
                lblView = Lay.lb()
            ),
            "C", Lay.p(Lay.sp(tre = new RTree(nRoot = new RTreeNode())), "eb=5lrb")
        );
        tre.setRootVisible(false);

        ac.addSelectedValuesListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateSelectedValues();
            }
        });

        ac.addSelectedWorldListener(new WorldSelectedListener() {
            @Override
            public void stateChanged(WorldSelectedEvent e) {
                updateWorldLabel();
            }
        });
        ac.addRenameWorldListener(new RenameWorldListener() {
            @Override
            public void stateChanged(RenameWorldEvent e) {
                if(ac.getSelectedWorld() == e.getWorldContext()) {
                    updateWorldLabel();
                }
            }
        });
        ac.addSelectedViewListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateViewLabels();
            }
        });
    }

    private void updateWorldLabel() {
        WorldContext wc = ac.getSelectedWorld();
        if(wc == null) {
            lblWorld.setText("<html><i>(nothing selected)</i></html>");
            lblWorld.setIcon(CommonConcepts.CLOSE);
        } else {
            lblWorld.setText(wc.createTabTitle());
            lblWorld.setIcon(CommonConcepts.WORLD);
        }
        updateSelectedValues();
    }

    private void updateViewLabels() {
        ViewContainerPanel pnlViewCont = ac.getSelectedViewContainerPanel();
        if(pnlViewCont == null) {
            lblSep.setVisible(false);   // Still has borders
            lblView.setText(null);
            lblView.setIcon((Icon) null);
        } else {
            lblSep.setVisible(true);
            lblView.setText(pnlViewCont.createTabTitle());
            lblView.setIcon(FinioImageModel.VIEW);
        }
        updateSelectedValues();
    }

    private void updateSelectedValues() {
        ViewPanel pnlView = ac.getSelectedViewPanel();
        nRoot.removeAllChildren();
        if(pnlView != null) {
            SelectionContext[] Cs = pnlView.getSelectionValues();
            if(Cs != null) {
                for(SelectionContext C : Cs) {
                    nRoot.add(new NodeSelected(C));
                }
            }
        }
        tre.getModel().nodeStructureChanged(nRoot);
    }


    /////////////////
    // INNER CLASS //
    /////////////////

    private class NodeSelected extends NodeBase {
        private SelectionContext C;

        public NodeSelected(SelectionContext C) {
            this.C = C;
        }

        ////////////////
        // OVERRIDDEN //
        ////////////////

        @Override
        public Icon getIcon(boolean expanded) {
            return ImageLib.get(FinioImageModel.SELECTED);
        }

        @Override
        public String toString() {
            if(FUtil.isNull(C.getV())) {
                return C.getK() + " = " + "(NULL)";
            }
            return C.getK() + " = " + C.getV().toString();
        }
    }
}
