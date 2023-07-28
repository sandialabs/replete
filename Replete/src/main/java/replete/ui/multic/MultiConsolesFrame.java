package replete.ui.multic;

import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;

import replete.ui.button.RButton;
import replete.ui.button.RCheckBox;
import replete.ui.combo.RComboBox;
import replete.ui.fc.RFileChooser;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.windows.Dialogs;
import replete.ui.windows.escape.EscapeDialog;
import replete.ui.windows.escape.EscapeFrame;
import replete.util.OsUtil;


public class MultiConsolesFrame extends EscapeFrame {

    // TODO: Ctrl + S performs stop all

    // Here are two test scripts that can be used to test
    // this application, one for Unix, another for Windows.

    /*

    Unix:

        #!/bin/bash

        echo Test Script \(Args=$*\)
        echo Message One
        sleep 6
        echo Message Two
        sleep 6
        echo Message Three \(stderr\) 1>&2
        sleep 6
        echo Last Message

    Windows:

        @echo off
        echo Test Script (Args=%*)
        echo Message One
        sleep 6
        echo Message Two
        sleep 6
        echo Message Three (stderr) 1>&2
        sleep 6
        echo Last Message

    */

    private JButton btnCommandLaunch;
    private RComboBox cboCommand;
    private JButton btnCommandEdit;
    private JButton btnCommandRemove;
    private JTextField txtRows;
    private JTextField txtCols;
    private JTextField txtLabel;
    private JTextField txtTOffset;
    private JTextField txtWorkingDir;
    private JButton btnWDChange;
    private JCheckBox chkTopLabels;
    private JCheckBox chkFixedWidth;
//    private JButton btnLaf;
    private RButton btnToggleOptions;
    private RButton btnStopAll;

    private JPanel pnlOptions;
    private JPanel pnlConsoles;

    private ConsolePanel[][] consolePanels;
    private DefaultComboBoxModel cmdModel = new DefaultComboBoxModel();
//    private JPopupMenu lafPopup = LafManager.createLafPopupMenu();
//
    private boolean optionsShowing = true;

    // Returns false if the user chose to cancel the process.
    public boolean stopProcesses() {
        if(isProcessRunning()) {
            if(Dialogs.showConfirm(this, "Cancel all running processes?", "Cancel processes?", true)) {
                for(int r = 0; r < consolePanels.length; r++) {
                    for(int c = 0; c < consolePanels[0].length; c++) {
                        consolePanels[r][c].stopProcess();
                    }
                }
                return true;
            }
            return false;
        }
        return true;
    }

    public void saveState() {
        pushCommand();
        AppState state = AppState.getState();
        List<String> newCommands = new ArrayList<>();
        for(int c = 0; c < cmdModel.getSize(); c++) {
            newCommands.add((String) cmdModel.getElementAt(c));
        }
        state.setCommands(newCommands);
        state.setRows(txtRows.getText());
        state.setCols(txtCols.getText());
        state.setLabel(txtLabel.getText());
        state.setTOffset(txtTOffset.getText());
        state.setWorkingDir(txtWorkingDir.getText());
        state.setTopLabels(chkTopLabels.isSelected());
        state.setFixedWidth(chkFixedWidth.isSelected());
        state.setMainFrameSize(getSize());
        state.setMainFrameLoc(getLocation());
        state.setMainFrameExtState(getExtendedState());
    }

    public MultiConsolesFrame() {
        super("Multi Consoles");
        setIcon(CommonConcepts.CONSOLE);

        cboCommand = new RComboBox(cmdModel);
        cboCommand.setSelectAll(true);
        cboCommand.setEditable(true);
        cboCommand.getEditor().getEditorComponent().addKeyListener(rebuildListener);
        Lay.hn(cboCommand, "maxW=250,prefW=250");
        txtRows = Lay.tx("", 3, "selectall", rebuildListener);
        txtCols = Lay.tx("", 3, "selectall", rebuildListener);
        txtLabel = Lay.tx("", 8, "selectall", rebuildListener);
        txtTOffset = Lay.tx("", 4, "selectall", rebuildListener);
        txtWorkingDir = Lay.tx("", 25, "selectall", rebuildListener);
        chkTopLabels = new RCheckBox("Labels On &Top?");
        chkTopLabels.addKeyListener(rebuildListener);
        chkFixedWidth = new RCheckBox("&Fixed-Width Fonts?");
        chkFixedWidth.addKeyListener(rebuildListener);

        btnCommandEdit = Lay.btn(CommonConcepts.EDIT, 2, "ttt=Edit-Command...", rebuildListener,
            (ActionListener) e -> {
                EscapeDialog dlg = new EscapeDialog(MultiConsolesFrame.this, "Edit Current Command", true);
                JTextPane txt = new JTextPane();
                txt.setText(((JTextComponent) cboCommand.getEditor().getEditorComponent()).getText());
                JButton btnSave = Lay.btn("&Save", CommonConcepts.SAVE, (ActionListener) ev -> {
                    ((JTextComponent) cboCommand.getEditor().getEditorComponent()).setText(txt.getText());
                    cboCommand.requestFocusInWindow();
                    dlg.close();
                });
                JButton btnCancel = Lay.btn("&Cancel", CommonConcepts.CANCEL, (ActionListener) ev -> dlg.close());
                Lay.BLtg(dlg,
                    "C", Lay.sp(txt),
                    "S", Lay.FL("R", btnSave, btnCancel),
                    "eb=5,size=[600,600],center,vgap=5"
                );
                dlg.setVisible(true);
            }
        );

        btnCommandRemove = Lay.btn(
            CommonConcepts.REMOVE, 2, "ttt=Remove-Command", rebuildListener,
            (ActionListener) e -> removeCommand()
        );

        btnCommandLaunch = Lay.btn(
            "&Launch", CommonConcepts.LAUNCH,
            "ttt=Launch-command-with-current-configuration", rebuildListener,
            (ActionListener) e -> rebuildAndLaunch()
        );
        btnCommandLaunch.setHorizontalTextPosition(SwingConstants.LEFT);

        btnWDChange = Lay.btn(CommonConcepts.OPEN, 2, "ttt=Change-Working-Directory...",
            (ActionListener) e -> {
                RFileChooser chooser = RFileChooser.getChooser(
                    "Choose Working Directory", JFileChooser.DIRECTORIES_ONLY);
                if(chooser.showOpen(this)) {
                    txtWorkingDir.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        );
        chkTopLabels.addActionListener(e -> {
            if(consolePanels != null) {
                for(int r = 0; r < consolePanels.length; r++) {
                    for(int c = 0; c < consolePanels[0].length; c++) {
                        ConsolePanel cPanel = consolePanels[r][c];
                        cPanel.changeLabel(chkTopLabels.isSelected());
                    }
                }
            }
        });
        chkFixedWidth.addActionListener(e -> {
            if(consolePanels != null) {
                for(int r = 0; r < consolePanels.length; r++) {
                    for(int c = 0; c < consolePanels[0].length; c++) {
                        ConsolePanel cPanel = consolePanels[r][c];
                        cPanel.changeFont(chkFixedWidth.isSelected());
                    }
                }
            }
        });
//        btnLaf = new IconButton(ImageLib.get(CommonConcepts.LOOK_AND_FEEL), 2);
//        btnLaf.setToolTipText("Change look & feel");
//        btnLaf.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent arg0) {
//                lafPopup.show(btnLaf, btnLaf.getWidth() / 2, btnLaf.getHeight() / 2);
//            }
//        });
        btnStopAll = Lay.btn(
            CommonConcepts.STOP_ALL, 2, "ttt=Stop-All-Processes...",
            (ActionListener) e -> stopProcesses()
        );

        btnToggleOptions = Lay.btn(CommonConcepts.COLLAPSE_ALL, 2, "ttt=Toggle-Option-Visibility",
            (ActionListener) e -> {
                if(!optionsShowing) {
                    showOptions();
                    btnToggleOptions.setIcon(CommonConcepts.COLLAPSE_ALL);
                } else {
                    hideOptions();
                    btnToggleOptions.setIcon(CommonConcepts.EXPAND_ALL);
                }
                optionsShowing = !optionsShowing;
            }
        );

        pnlOptions = Lay.p();

        showOptions();

        Lay.BLtg(this,
            "N", pnlOptions,
            "C", pnlConsoles = Lay.p()
        );

        AppState state = AppState.getState();
        List<String> commands = state.getCommands();
        for(String cmd : commands) {
            cmdModel.addElement(cmd);
        }
        txtRows.setText(state.getRows());
        txtCols.setText(state.getCols());
        txtLabel.setText(state.getLabel());
        txtTOffset.setText(state.getTOffset());
        txtWorkingDir.setText(state.getWorkingDir());
        chkTopLabels.setSelected(state.isTopLabels());
        chkFixedWidth.setSelected(state.isFixedWidth());
        if(state.getMainFrameSize() != null) {
            setSize(state.getMainFrameSize());
        } else {
            setSize(800, 600);
        }
        if(state.getMainFrameLoc() != null) {
            setLocation(state.getMainFrameLoc());
        } else {
            setLocationRelativeTo(null);
        }
        setExtendedState(state.getMainFrameExtState());
    }

    private void hideOptions() {
        pnlOptions.removeAll();
        Lay.eb(pnlOptions, "0");
        Lay.WLtg(pnlOptions, "L",
            btnToggleOptions,
            "bg=120,augb=mb(2b,black)"
        );
        pnlOptions.updateUI();
    }
    private void showOptions() {
        /*Lay.hn(btnCommandLaunch, btnStopAll, btnCommandEdit,
            btnCommandRemove, btnWDChange, btnToggleOptions,
            "bg=120");  // Looks maybe a little weird on JTattoo LAF's, but not horrible.*/
        pnlOptions.removeAll();
        Lay.eb(pnlOptions, "0");
        Lay.WLtg(pnlOptions, "L",
            btnCommandLaunch, btnStopAll,
            Lay.lb("Command:",     "fg=white,bold"), cboCommand, btnCommandEdit, btnCommandRemove,
            Lay.lb("Rows:",        "fg=white,bold"), txtRows,
            Lay.lb("Cols:",        "fg=white,bold"), txtCols,
            Lay.lb("Labels:",      "fg=white,bold"), txtLabel,
            Lay.lb("T-Offset:",    "fg=white,bold"), txtTOffset,
            Lay.lb("Working Dir:", "fg=white,bold"), txtWorkingDir, btnWDChange,
            Lay.hn(chkTopLabels,   "fg=white,bold,opaque"),
            Lay.hn(chkFixedWidth,  "fg=white,bold,opaque"),
            btnToggleOptions,
            "bg=120,augb=mb(2b,black)"
        );
        pnlOptions.updateUI();
    }

    @Override
    public void setVisible(boolean state) {
        if(!isVisible() && state) {
            super.setVisible(state);
            cboCommand.requestFocusInWindow();
        }
    }

    private KeyListener rebuildListener = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                rebuildAndLaunch();
                if(e.getSource() instanceof JTextComponent) {
                    ((JTextComponent) e.getSource()).selectAll();
                }
            }
        }
    };

    public boolean isProcessRunning() {
        if(consolePanels != null) {
            boolean running = false;
            for(int r = 0; r < consolePanels.length; r++) {
                for(int c = 0; c < consolePanels[0].length; c++) {
                    running = running || consolePanels[r][c].isProcessRunning();
                }
            }
            return running;
        }
        return false;
    }

    private void removeCommand() {
        String newCommand = ((JTextComponent) cboCommand.getEditor().getEditorComponent()).getText().trim();
        int index = cmdModel.getIndexOf(newCommand);
        if(index != -1) {
            cmdModel.removeElementAt(index);
            if(index < cmdModel.getSize()) {
                cmdModel.setSelectedItem(cmdModel.getElementAt(index));
            }
        } else {
            if(cmdModel.getSize() > 0) {
                cmdModel.setSelectedItem(cmdModel.getElementAt(0));
            } else {
                ((JTextComponent) cboCommand.getEditor().getEditorComponent()).setText("");
            }
        }
    }

    private void pushCommand() {
        String newCommand = ((JTextComponent) cboCommand.getEditor().getEditorComponent()).getText().trim();
        int index = cmdModel.getIndexOf(newCommand);
        if(index != -1) {
            cmdModel.removeElementAt(index);
        }
        cmdModel.insertElementAt(newCommand, 0);
        cmdModel.setSelectedItem(newCommand);
    }

    private void rebuildAndLaunch() {
        btnCommandLaunch.setEnabled(false);

        try {
            String newCommand = ((JTextComponent) cboCommand.getEditor().getEditorComponent()).getText().trim();
            if(newCommand.equals("")) {
                String error = "You must supply a value for 'Command'.";
                Dialogs.showError(this, error);
                cboCommand.requestFocusInWindow();
                return;
            }

            pushCommand();

            txtRows.setText(txtRows.getText().trim());
            txtCols.setText(txtCols.getText().trim());
            txtLabel.setText(txtLabel.getText().trim());
            txtTOffset.setText(txtTOffset.getText().trim());
            txtWorkingDir.setText(txtWorkingDir.getText().trim());

            if(!validateArgs()) {
                return;
            }

            if(!stopProcesses()) {
                return;
            }

            int rows = Integer.parseInt(txtRows.getText());
            int cols = Integer.parseInt(txtCols.getText());
            int toff;
            if(txtTOffset.getText().equals("")) {
                toff = 0;
            } else {
                toff = Integer.parseInt(txtTOffset.getText());
            }
            File workingDir;
            if(txtWorkingDir.getText().equals("")) {
                workingDir = new File("").getAbsoluteFile();
            } else {
                workingDir = new File(txtWorkingDir.getText());
            }

            // Construct the new console panels.
            consolePanels = new ConsolePanel[rows][cols];
            int t = 0;
            for(int r = 0; r < consolePanels.length; r++) {
                for(int c = 0; c < consolePanels[0].length; c++, t++) {

                    // Create label string.
                    String label = txtLabel.getText();
                    if(label.equals("")) {
                        label = "Console %T";
                    }
                    label = label.replaceAll("%R", r + "");
                    label = label.replaceAll("%C", c + "");
                    label = label.replaceAll("%T", (t + toff) + "");

                    // Create command string.
                    String command = (String) cboCommand.getSelectedItem();
                    command = command.replaceAll("%R", r + "");
                    command = command.replaceAll("%C", c + "");
                    command = command.replaceAll("%T", (t + toff) + "");
                    command = resolveWindowsCommand(command);

                    ConsolePanel cPanel =  new ConsolePanel(label, command, workingDir,
                        chkTopLabels.isSelected(), chkFixedWidth.isSelected());
                    consolePanels[r][c] = cPanel;
                }
            }

            // Add all the new console panels at once.
            pnlConsoles.removeAll();
            Lay.GLtg(pnlConsoles, rows, cols);
            for(int r = 0; r < consolePanels.length; r++) {
                for(int c = 0; c < consolePanels[0].length; c++) {
                    pnlConsoles.add(consolePanels[r][c]);
                }
            }
            Lay.eb(pnlConsoles, "5br");
            pnlConsoles.updateUI();

            // Start all the processes in 1 second.
            for(int r = 0; r < consolePanels.length; r++) {
                for(int c = 0; c < consolePanels[0].length; c++) {
                    consolePanels[r][c].startProcess();
                }
            }

        } finally {
            btnCommandLaunch.setEnabled(true);
        }
    }

    private boolean validateArgs() {
        String error = null;
        if(txtRows.getText().equals("")) {
            error = "You must supply a value for 'Rows'.";
            txtRows.requestFocusInWindow();
        } else if(!isInt(txtRows.getText())) {
            error = "You must supply an integer value for 'Rows'.";
            txtRows.requestFocusInWindow();
        } else if(txtCols.getText().equals("")) {
            error = "You must supply a value for 'Cols'.";
            txtCols.requestFocusInWindow();
        } else if(!isInt(txtCols.getText())) {
            error = "You must supply an integer value for 'Cols'.";
            txtCols.requestFocusInWindow();
        } else if(!txtTOffset.getText().equals("") && !isInt(txtTOffset.getText())) {
            error = "You must supply an integer value for 'T-Offset'.";
            txtTOffset.requestFocusInWindow();
        }
        if(error != null) {
            Dialogs.showError(this, error);
            return false;
        }
        return true;
    }

    private boolean isInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }

    private String resolveWindowsCommand(String cmd) {
        if(OsUtil.isWindows()) {
            int sp = cmd.indexOf(' ');
            if(sp == -1 && isCmdCommand(cmd) || sp != -1 && isCmdCommand(cmd.substring(0, sp))) {
                return "cmd /c " + cmd;
            }
        }
        return cmd;
    }
    private static String[] cmdCommands = new String[] {"echo", "dir", "date"};
    private boolean isCmdCommand(String cmd) {
        if(cmd.endsWith(".bat")) {
            return true;
        }
        return Arrays.asList(cmdCommands).contains(cmd);
    }
}