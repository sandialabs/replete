package finio.plugins.extpoints;

import javax.swing.ImageIcon;

import finio.core.NonTerminal;
import finio.ui.app.AppContext;
import finio.ui.views.ViewsPanel;
import finio.ui.worlds.WorldContext;
import replete.plugins.ExtensionPoint;

public interface Worlds extends ExtensionPoint {
    public String getName();
    public ImageIcon getIcon();
    public ViewsPanel createPanel(AppContext ac, WorldContext wc, NonTerminal Mcontext);
}