package finio.ui.views;

import replete.ui.panels.RPanel;

public abstract class ViewsPanel extends RPanel {
    public void init() {}
    public abstract void addViewSelectionListener(ViewSelectionListener listener);
    public abstract void removeViewSelectionListener(ViewSelectionListener listener);
}
