package finio.plugins.extpoints;

import javax.swing.ImageIcon;

import finio.ui.app.AppContext;
import finio.ui.view.ViewPanel;
import finio.ui.worlds.WorldContext;
import replete.plugins.ExtensionPoint;

public interface View extends ExtensionPoint {
    public String getName();
    public ImageIcon getIcon();
    public boolean canView(Object V);
    public ViewPanel createPanel(AppContext ac, WorldContext wc, Object K, Object V);
}