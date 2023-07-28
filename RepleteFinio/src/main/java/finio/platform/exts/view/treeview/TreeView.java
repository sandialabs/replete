package finio.platform.exts.view.treeview;

import javax.swing.ImageIcon;

import finio.core.FUtil;
import finio.platform.exts.view.treeview.ui.FTreePanel;
import finio.plugins.extpoints.View;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import finio.ui.view.ViewPanel;
import finio.ui.worlds.WorldContext;
import replete.ui.images.concepts.ImageLib;

public class TreeView implements View {

    @Override
    public String getName() {
        return "Tree";
    }

    @Override
    public ImageIcon getIcon() {
        return ImageLib.get(FinioImageModel.TREE_VIEW);
    }

    @Override
    public boolean canView(Object V) {
        return FUtil.isNonTerminal(V);
    }

    @Override
    public ViewPanel createPanel(AppContext ac, WorldContext wc, Object K, Object V) {
        return new FTreePanel(ac, wc, K, V, this);
    }

}
