package replete.installer;

import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * @author Derek Trumbo
 */

public class InstallerPackage {
    protected String appName;
    protected String appVersion;
    protected JPanel titlePanel;
    protected List<InstallerPanel> installerPanels;
    protected ImageIcon frameIcon;

    public String getAppName() {
        return appName;
    }
    public void setAppName(String appName) {
        this.appName = appName;
    }
    public String getAppVersion() {
        return appVersion;
    }
    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }
    public JPanel getTitlePanel() {
        return titlePanel;
    }
    public void setTitlePanel(JPanel titlePanel) {
        this.titlePanel = titlePanel;
    }
    public List<InstallerPanel> getInstallerPanels() {
        return installerPanels;
    }
    public void setInstallerPanels(List<InstallerPanel> installerPanels) {
        this.installerPanels = installerPanels;
    }
    public ImageIcon getFrameIcon() {
        return frameIcon;
    }
    public void setFrameIcon(ImageIcon frameIcon) {
        this.frameIcon = frameIcon;
    }
}