package finio.platform.exts.views.tabs;

import javax.swing.ImageIcon;

import finio.core.NonTerminal;
import finio.plugins.extpoints.Views;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import finio.ui.views.ViewsPanel;
import finio.ui.worlds.WorldContext;
import replete.ui.images.concepts.ImageLib;

public class TabbedViews implements Views {

    @Override
    public String getName() {
        return "Tabbed Views";
    }

    @Override
    public ImageIcon getIcon() {
        return ImageLib.get(FinioImageModel.TREE_VIEW);
    }

    @Override
    public ViewsPanel createPanel(AppContext ac, WorldContext wc, NonTerminal Mcontext) {
        return new TabbedViewsPanel(ac, wc, this);
    }

}
