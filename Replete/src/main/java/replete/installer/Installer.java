package replete.installer;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author Derek Trumbo
 */

public class Installer {

    protected InstallerPackage installerPackage;

    public Installer(InstallerPackage pkg) {
        installerPackage = pkg;
    }

    public void start() {

        if(installerPackage.installerPanels == null ||
                        installerPackage.installerPanels.size() == 0) {
            throw new IllegalArgumentException("Cannot start installer with zero content panels.");
        }

        InstallerFrame win = new InstallerFrame(installerPackage);
        win.setVisible(true);
    }

    ///////////////
    // Test Main //
    ///////////////

    public static void main(String[] args) {
        InstallerPackage ipkg = new InstallerPackage();
        ipkg.setAppName("SampleApp");
        ipkg.setAppVersion("1.0.0");

        JPanel pnl = new JPanel();
        pnl.add(new JLabel("SampleApp"));
        ipkg.setTitlePanel(pnl);

        InstallerPanel pnl2 = new InstallerPanel() {
            @Override
            public String getTitle() {
                return "Parameters";
            }
        };
        pnl2.add(new JTextField("Parameter", 20));

        List<InstallerPanel> ipnls = new ArrayList<>();
        ipnls.add(pnl2);
        ipkg.setInstallerPanels(ipnls);

        Installer installer = new Installer(ipkg);
        installer.start();
    }
}

