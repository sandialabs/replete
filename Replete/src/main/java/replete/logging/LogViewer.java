package replete.logging;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.text.DefaultCaret;

import replete.ui.GuiUtil;
import replete.ui.list.EmptyMessageList;
import replete.ui.windows.Dialogs;
import replete.ui.windows.escape.EscapeDialog;
import replete.util.Application;


/**
 * @author Derek Trumbo
 */

public class LogViewer extends EscapeDialog {
    protected EmptyMessageList lst;
    protected JTextArea txt;
    protected JScrollPane scrLst;
    protected JScrollPane scrTxt;

    public LogViewer(JFrame parent) {
        super(parent, "Log Viewer", true);

        List<LogEntry> entries = LogManager.readLogEntries();

        lst = new EmptyMessageList(entries.toArray(new LogEntry[0]), "No Log Messages");
        lst.setCellRenderer(new MyCellRenderer());
        txt = new JTextArea();
        txt.setEditable(false);
        txt.setTabSize(2);
        scrLst = new JScrollPane(lst);
        scrTxt = new JScrollPane(txt);

        // This is important because if it is not used, when setText is called
        // on the text area, the caret will be pushed to the bottom and the
        // text area will be constantly scrolled to the bottom when the user
        // switches between log entries (assuming there's enough text to
        // cause the scroll bars to appear).
        // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4834399
        ((DefaultCaret) txt.getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

        lst.addListSelectionListener(e -> {
            if(lst.getSelectedValue() == null) {
                txt.setText("");
            } else {
                txt.setText(((LogEntry) lst.getSelectedValue()).text);

                // Reset the scroll bars.
                scrTxt.getHorizontalScrollBar().setValue(0);
                scrTxt.getVerticalScrollBar().setValue(0);
            }
        });

        String where;
        if(LogManager.logFile == null) {
            where = "There currently is no log file registered.";
        } else {
            where = "The application log is located at:<BR>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<B>" +
                LogManager.logFile.getAbsolutePath() + "</B>";
        }

        JLabel lblDesc = new JLabel("<HTML>The Log Viewer displays all messages (informational, " +
                "warning, and error messages) in the application log along with the full text " +
                "associated with each.  " + where + "</HTML>");

        // Construct buttons panel.
        JPanel pnlButtons = new JPanel();
        final JButton btnClear = new JButton("Clear Log");
        JButton btnClose = new JButton("Close");
        btnClear.addActionListener(e -> {
            try {
                // This get and re-register seems like terrible code...
                if(Dialogs.showConfirm(
                        LogViewer.this, "Are you sure you want to clear the application log?",
                        "Clear Log?", true)) {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(LogManager.logFile));
                    writer.close();
                    lst.setListData(new LogEntry[0]);
                    btnClear.setEnabled(false);
                }
            } catch(IOException ioe) {
                ioe.printStackTrace();
            }
        });
        btnClear.setMnemonic('L');
        btnClear.setDisplayedMnemonicIndex(6);
        btnClose.addActionListener(e -> close());
        btnClose.setMnemonic('C');
        getRootPane().setDefaultButton(btnClose);
        pnlButtons.add(btnClear);
        pnlButtons.add(new JLabel(""));
        pnlButtons.add(btnClose);

        if(entries.size() != 0) {
            lst.setSelectedIndex(0);
        } else {
            btnClear.setEnabled(false);
        }

        Border topBorder = BorderFactory.createEmptyBorder(10, 10, 0, 10);
        Border leftBorder = BorderFactory.createEmptyBorder(10, 10, 0, 10);
        Border centerBorder = BorderFactory.createEmptyBorder(10, 0, 0, 10);
        Border bottomBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);

        // Add components.
        setLayout(new BorderLayout());
        GuiUtil.addBorderedComponent(this, lblDesc, topBorder, BorderLayout.NORTH);
        GuiUtil.addBorderedComponent(this, scrLst, leftBorder, BorderLayout.WEST);
        GuiUtil.addBorderedComponent(this, scrTxt, centerBorder, BorderLayout.CENTER);
        GuiUtil.addBorderedComponent(this, pnlButtons, bottomBorder, BorderLayout.SOUTH);

        setSize(800, 400);
        setLocationRelativeTo(getOwner());
    }

    class MyCellRenderer extends JLabel implements ListCellRenderer {
        public Component getListCellRendererComponent(
                JList list,
                Object value,            // value to display
                int index,               // cell index
                boolean isSelected,      // is the cell selected
                boolean cellHasFocus)    // the list and the cell have the focus
        {
            LogEntry entry = (LogEntry) value;
            setText(entry.toString());
            setIcon(entry.type.getIcon());
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
            else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
            return this;
        }
    }

    ///////////////
    // Test Main //
    ///////////////

    public static void main(String[] args) {
        Application.setName("LogViewer Test");
        LogManager.setLogFile(new File(System.getProperty("user.home"), "testlog.log"));
        LogManager.log(null, LogEntryType.INFO, "Test Info Message",null, false);
        LogManager.log(null, LogEntryType.WARNING, "Test Info Message",null, false);
        LogManager.log(null, LogEntryType.ERROR, "Test Info Message",null, false);
        LogManager.log(null, LogEntryType.FATAL_ERROR, "Test Info Message",null, false);
        new LogViewer(null).setVisible(true);
    }
}
