package replete.ui;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import replete.ui.progress.WarningProgressBar;
import replete.ui.windows.escape.EscapeDialog;


/**
 * Window to display the memory usage for the current VM.
 *
 * @author Derek Trumbo
 */

public class MemoryUsageDialog extends EscapeDialog {

    ////////////
    // FIELDS //
    ////////////

    public static final int UPDATE_INTERVAL = 1000;
    public static final int BYTES_PER_MEGABYTE = 1024 * 1024;

    protected JLabel lblUsedMemoryDesc;
    protected JLabel lblTotalMemoryDesc;
    protected JLabel lblMaxMemoryDesc;

    protected JLabel lblUsedMemory;
    protected JLabel lblTotalMemory;
    protected JLabel lblMaxMemory;

    protected WarningProgressBar pgbMemoryUsage;
    protected JButton btnGC;
    protected JButton btnClose;

    protected Timer tmrUpdate;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public MemoryUsageDialog(JDialog parent) {
        super(parent, "Memory Usage", false);
        init();
    }
    public MemoryUsageDialog(JFrame parent) {
        super(parent, "Memory Usage", false);
        init();
    }

    private void init() {

        // Labels

        lblUsedMemoryDesc = new JLabel("Used:");
        lblTotalMemoryDesc = new JLabel("Allocated:");
        lblMaxMemoryDesc = new JLabel("Max:");

        lblUsedMemory = new JLabel("", SwingConstants.RIGHT);
        lblTotalMemory = new JLabel("", SwingConstants.RIGHT);
        lblMaxMemory = new JLabel("", SwingConstants.RIGHT);

        JPanel pnlLabels = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(5, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 0;
        pnlLabels.add(lblUsedMemoryDesc, c);
        c.gridx = 0;
        c.gridy = 1;
        pnlLabels.add(lblTotalMemoryDesc, c);
        c.gridx = 0;
        c.gridy = 2;
        pnlLabels.add(lblMaxMemoryDesc, c);

        c.anchor = GridBagConstraints.LINE_END;
        c.gridx = 1;
        c.gridy = 0;
        pnlLabels.add(lblUsedMemory, c);
        c.gridx = 1;
        c.gridy = 1;
        pnlLabels.add(lblTotalMemory, c);
        c.gridx = 1;
        c.gridy = 2;
        pnlLabels.add(lblMaxMemory, c);
        c.gridx = 1;
        c.gridy = 3;

        // This panel is used just to make the second column a minimum width.
        Dimension d = new Dimension(70, 0);
        JPanel p = new JPanel();
        p.setMinimumSize(d);
        p.setPreferredSize(d);
        p.setMaximumSize(d);
        pnlLabels.add(p, c);

        // Memory bar

        pgbMemoryUsage = new WarningProgressBar(90);
        pgbMemoryUsage.setBackground(Color.white);
        pgbMemoryUsage.setStringPainted(true);

        JPanel pnlMemoryBar = new JPanel(new GridBagLayout());
        GridBagConstraints c2 = new GridBagConstraints();
        c2.insets = new Insets(10, 0, 10, 0);
        pnlMemoryBar.add(pgbMemoryUsage, c2);

        // Buttons

        btnGC = new JButton("Garbage Collect");
        btnGC.setMnemonic('G');
        btnGC.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.gc();
            }
        });

        btnClose = new JButton("Close");
        btnClose.setMnemonic('C');
        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MemoryUsageDialog.this.dispose();
            }
        });

        JPanel pnlButtons = new JPanel();
        pnlButtons.setLayout(new FlowLayout());
        pnlButtons.add(btnGC);
        pnlButtons.add(btnClose);

        // Initial label values.
        updateMemoryLabels();

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        add(pnlLabels);
        add(pnlMemoryBar);
        add(pnlButtons);
        pack();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        tmrUpdate = new Timer("Memory Usage Dialog", true);
        tmrUpdate.schedule(new TimerTask() {
            @Override
            public void run() {
                updateMemoryLabels();
            }
        }, 0, UPDATE_INTERVAL);
    }

    protected void updateMemoryLabels() {
        Runtime runtime = Runtime.getRuntime();

        long free = runtime.freeMemory() / BYTES_PER_MEGABYTE;
        long total = runtime.totalMemory() / BYTES_PER_MEGABYTE;
        long max = runtime.maxMemory() / BYTES_PER_MEGABYTE;

        lblUsedMemory.setText((total - free) + " MB");
        lblTotalMemory.setText(total + " MB");
        lblMaxMemory.setText(max + " MB");

        pgbMemoryUsage.setValue((int) (100 * (total - free) / max));
    }

    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        MemoryUsageDialog dlg = new MemoryUsageDialog((JFrame) null);
        dlg.setVisible(true);
    }
}
