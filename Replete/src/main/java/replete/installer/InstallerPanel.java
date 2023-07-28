package replete.installer;

import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import replete.ui.GuiUtil;
import replete.ui.windows.Dialogs;


/**
 * @author Derek Trumbo
 */

public abstract class InstallerPanel extends JPanel {
    protected InstallerDataModel dataModel;

    public InstallerPanel() {}
    public InstallerPanel(InstallerDataModel dm) {
        dataModel = dm;
    }

    public DefaultButtonConfiguration getButtonConfiguration() {
        return new DefaultButtonConfiguration();
    }
    public abstract String getTitle();
    public void saveToDataModel() {}
    public void doPanelShown() {}
    public boolean canCancel() {
        JFrame frame = GuiUtil.fra(this);
        return Dialogs.showConfirm(frame, "Do you wish to cancel the installation?");
    }
    public boolean canMoveBack() { return true; }
    public boolean canMoveNext() { return true; }

    protected ActionListener requestGoBackListener;
    protected ActionListener requestGoNextListener;
    protected ActionListener requestCloseInstallerListener;
}