package replete.installer;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import replete.ui.windows.escape.EscapeFrame;


/**
 * @author Derek Trumbo
 */

public class InstallerFrame extends EscapeFrame {
    protected InstallerPackage installerPackage;

    protected JButton btnCancel;
    protected JButton btnBack;
    protected JButton btnNext;
    protected JLabel lblInstallerPanelTitle;
    protected CardLayout cLayout;
    protected JPanel pnlInstallerPanels;
    protected int currentInstallerPanel = 0;

    public InstallerFrame(InstallerPackage pkg) {
        installerPackage = pkg;
        initGUI();
    }

    protected void initGUI() {

        // Set title.

        setTitle("Installer for " + installerPackage.appName + " " + installerPackage.appVersion);

        if(installerPackage.getFrameIcon() != null) {
            setIconImage(installerPackage.getFrameIcon().getImage());
        }

        // Make the three buttons.

        btnCancel = new JButton();
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                attemptClose();
            }
        });

        btnBack = new JButton();
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                goBack();
            }
        });

        btnNext = new JButton();
        btnNext.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                goNext();
            }
        });

        getRootPane().setDefaultButton(btnNext);

        // Construct the button panels.

        JPanel pnlCancel = new JPanel(new FlowLayout());
        pnlCancel.add(btnCancel);

        JPanel pnlBackNext = new JPanel(new FlowLayout());
        pnlBackNext.add(btnBack);
        pnlBackNext.add(btnNext);

        JPanel pnlButtons = new JPanel(new BorderLayout());
        pnlButtons.add(pnlCancel, BorderLayout.WEST);
        pnlButtons.add(pnlBackNext, BorderLayout.EAST);

        // Create the panel title label.

        lblInstallerPanelTitle = new JLabel(" ");
        lblInstallerPanelTitle.setBackground(new Color(175, 175, 175));
        lblInstallerPanelTitle.setOpaque(true);
        lblInstallerPanelTitle.setFont(lblInstallerPanelTitle.getFont().deriveFont(16.0F));

        // Set up the installer panels.

        pnlInstallerPanels = new JPanel(cLayout = new CardLayout(20, 20));
        for(InstallerPanel ipnl : installerPackage.installerPanels) {
            JPanel pp = new JPanel();
            BoxLayout bb = new BoxLayout(pp, BoxLayout.Y_AXIS);
            pp.setLayout(bb);
            pp.add(ipnl);
            pp.add(Box.createVerticalGlue());
            pnlInstallerPanels.add(pp, ipnl.getTitle());
        }

        JPanel pnlDivider = new JPanel();
        pnlDivider.setBackground(new Color(175, 175, 175));
        pnlDivider.setPreferredSize(new Dimension(10000, 5));
        pnlDivider.setMinimumSize(new Dimension(10000, 5));
        pnlDivider.setMaximumSize(new Dimension(10000, 5));

        // Create the panel for the installer panels.

        JPanel pnlContent = new JPanel(new BorderLayout());
        pnlContent.add(lblInstallerPanelTitle, BorderLayout.NORTH);
        pnlContent.add(pnlInstallerPanels, BorderLayout.CENTER);
        pnlContent.add(pnlDivider, BorderLayout.SOUTH);

        // Set the main dialog content area.

        setLayout(new BorderLayout());
        add(installerPackage.titlePanel, BorderLayout.NORTH);
        add(pnlContent, BorderLayout.CENTER);
        add(pnlButtons, BorderLayout.SOUTH);

        // Show the first panel.

        showInstallerPanel();

        btnNext.requestFocusInWindow();        // ?? not working

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                attemptClose();
            }
        });

        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    protected void showInstallerPanel() {
        InstallerPanel ipnl = installerPackage.installerPanels.get(currentInstallerPanel);
        cLayout.show(pnlInstallerPanels, ipnl.getTitle());
        lblInstallerPanelTitle.setText("  " + ipnl.getTitle());
        DefaultButtonConfiguration cfg = ipnl.getButtonConfiguration();

        btnCancel.setVisible(cfg.isCancelVisible());
        btnBack.setVisible(cfg.isBackVisible());
        btnNext.setVisible(cfg.isNextVisible());

        btnCancel.setText(cfg.getCancelText());
        btnBack.setText(cfg.getBackText());
        btnNext.setText(cfg.getNextText());

        btnCancel.setMnemonic(cfg.getCancelMnemonic());
        btnBack.setMnemonic(cfg.getBackMnemonic());
        btnNext.setMnemonic(cfg.getNextMnemonic());

        btnCancel.setIcon(cfg.getCancelIcon());
        btnBack.setIcon(cfg.getBackIcon());
        btnNext.setIcon(cfg.getNextIcon());

        btnCancel.setHorizontalTextPosition(cfg.getCancelTextPosition());
        btnBack.setHorizontalTextPosition(cfg.getBackTextPosition());
        btnNext.setHorizontalTextPosition(cfg.getNextTextPosition());

        ipnl.requestGoBackListener = requestGoBackListener;
        ipnl.requestGoNextListener = requestGoNextListener;
        ipnl.requestCloseInstallerListener = requestCloseInstallerListener;

        if(!btnNext.isVisible()) {
            getRootPane().setDefaultButton(null);
        } else {
            getRootPane().setDefaultButton(btnNext);
        }

        ipnl.doPanelShown();
    }

    protected void goBack() {
        InstallerPanel ipnl = installerPackage.installerPanels.get(currentInstallerPanel);
        if(ipnl.canMoveBack()) {
            ipnl.saveToDataModel();
            currentInstallerPanel = Math.max(currentInstallerPanel - 1, 0);
            showInstallerPanel();
        }
    }

    protected ActionListener requestGoBackListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            goBack();
        }
    };

    protected ActionListener requestGoNextListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            goNext();
        }
    };

    protected ActionListener requestCloseInstallerListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            dispose();
            System.exit(0);
        }
    };

    protected void goNext() {
        InstallerPanel ipnl = installerPackage.installerPanels.get(currentInstallerPanel);
        if(ipnl.canMoveNext()) {
            ipnl.saveToDataModel();
            currentInstallerPanel = Math.min(currentInstallerPanel + 1, installerPackage.installerPanels.size() - 1);
            showInstallerPanel();
        }
    }

    public void attemptClose() {
        InstallerPanel ipnl = installerPackage.installerPanels.get(currentInstallerPanel);
        if(ipnl.canCancel()) {
            dispose();
            System.exit(0);
        }
    }

    @Override
    public void escapePressed() {
        attemptClose();
    }

    @Override
    public void waitOn() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
    @Override
    public void waitOff() {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}