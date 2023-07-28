package replete.ui.tabbed;

import java.awt.LayoutManager;

import javax.swing.JPanel;

public abstract class RTabPanel extends JPanel {

    public RTabPanel() {
        super();
    }
    public RTabPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }
    public RTabPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }
    public RTabPanel(LayoutManager layout) {
        super(layout);
    }

    public abstract void tabDeactivated();
    public abstract void tabActivated();
}
