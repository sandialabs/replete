package replete.ui.windows;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;

import replete.errors.ExceptionUtil;
import replete.io.FileUtil;
import replete.ui.GuiUtil;
import replete.ui.images.RepleteImageModel;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;
import replete.ui.sp.IconDescriptor;
import replete.ui.text.editor.REditor;
import replete.util.ReflectionUtil;



/**
 * The main reason new versions of these methods had to be
 * retyped in full is that key methods in JOptionPane are
 * private and not overridable.
 *
 * @author Derek Trumbo
 */

public class JOptionPaneSpecial extends JOptionPane {

    // These button captions will automatically have mnemonics
    // set on the first character.  Button text is controlled
    // by L&F, but this work in the vast majority of cases.

    protected static List<String> basicCaptions = new ArrayList<>();

    static {
        basicCaptions.add("Yes");
        basicCaptions.add("No");
        basicCaptions.add("OK");
        basicCaptions.add("Cancel");
    }

    /* Copy and subsequent modification of JOptionPane's

        public static int showOptionDialog(Component parentComponent,
            Object message, String title, int optionType, int messageType,
            Icon icon, Object[] options, Object initialValue)
            throws HeadlessException

     */

    public JOptionPaneSpecial() {
        super();
    }
    public JOptionPaneSpecial(Object message) {
        super(message);
    }
    public JOptionPaneSpecial(Object message, int messageType) {
        super(message, messageType);
    }
    public JOptionPaneSpecial(Object message, int messageType, int optionType) {
        super(message, messageType, optionType);
    }
    public JOptionPaneSpecial(Object message, int messageType, int optionType,
            Icon icon) {
        super(message, messageType, optionType, icon);
    }
    public JOptionPaneSpecial(Object message, int messageType, int optionType,
            Icon icon, Object[] options) {
        super(message, messageType, optionType, icon, options);
    }
    public JOptionPaneSpecial(Object message, int messageType, int optionType,
            Icon icon, Object[] options, Object initialValue) {
        super(message, messageType, optionType, icon, options, initialValue);
    }

    @Override
    public int getMaxCharactersPerLineCount() {
        //http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4104906
        return 80;
    }

    public static void showMessageDialog(Component parentComponent, Object message)
                                                                                   throws HeadlessException {
        Locale l = (parentComponent == null) ? Locale.getDefault() : parentComponent.getLocale();
        showMessageDialog(parentComponent, message, UIManager.getString(
            "OptionPane.messageDialogTitle", l), INFORMATION_MESSAGE);
    }

    public static void showMessageDialog(Component parentComponent, Object message, String title,
                                         int messageType) throws HeadlessException {
        showMessageDialog(parentComponent, message, title, messageType, null);
    }

    public static void showMessageDialog(Component parentComponent, Object message, String title,
                                         int messageType, Icon icon) throws HeadlessException {
        showOptionDialogSpecial(
            parentComponent, message, title,
            DEFAULT_OPTION, messageType, icon, null,
            null, null);
    }

    public static int showOptionDialogSpecial(Component parentComponent,
            Object message, String title, int optionType, int messageType,
            Icon icon, Object[] options, Object initialValue, Map<String, Icon> buttonIcons)
            throws HeadlessException {

        JOptionPaneSpecial pane = new JOptionPaneSpecial(
            message, messageType, optionType, icon, options, initialValue);

        if(buttonIcons != null) {
            for(String buttonCaption : buttonIcons.keySet()) {
                JButton btn = GuiUtil.findButton(pane, buttonCaption);
                if(btn != null) {
                    btn.setIcon(buttonIcons.get(buttonCaption));
                }
            }
        }

        pane.setInitialValue(initialValue);
        pane.setComponentOrientation(((parentComponent == null) ?
        getRootFrame() : parentComponent).getComponentOrientation());

        JDialog dialog = pane.createDialog(parentComponent, title);

        // Custom code (the whole reason JOptionPaneSpecial exists).
        GuiUtil.addMnemonics(dialog, basicCaptions);
        ////////////////////////////////////////////////////////////

        pane.selectInitialValue();
        dialog.setVisible(true);
        dialog.dispose();

        Object        selectedValue = pane.getValue();

        if(selectedValue == null) {
            return CLOSED_OPTION;
        }
        if(options == null) {
            if(selectedValue instanceof Integer) {
                return ((Integer)selectedValue).intValue();
            }
            return CLOSED_OPTION;
        }
        for(int counter = 0, maxCounter = options.length;
            counter < maxCounter; counter++) {
            if(options[counter].equals(selectedValue)) {
                return counter;
            }
        }
        return CLOSED_OPTION;
    }

    /* Copy and subsequent modification of JOptionPane's

        public static Object showInputDialog(Component parentComponent,
            Object message, String title, int messageType, Icon icon,
            Object[] selectionValues, Object initialSelectionValue)
            throws HeadlessException

     */

    public static Object showInputDialogSpecial(Component parentComponent,
            Object message, String title, int messageType, Icon icon,
            Object[] selectionValues, Object initialSelectionValue)
            throws HeadlessException {

        JOptionPane pane = new JOptionPane(message, messageType,
                                           OK_CANCEL_OPTION, icon,
                                           null, null);

        pane.setWantsInput(true);
        pane.setSelectionValues(selectionValues);
        pane.setInitialSelectionValue(initialSelectionValue);
        pane.setComponentOrientation(((parentComponent == null) ?
        getRootFrame() : parentComponent).getComponentOrientation());

        JDialog dialog = pane.createDialog(parentComponent, title);

        // Custom code (the whole reason JOptionPaneSpecial exists).
        GuiUtil.addMnemonics(dialog, basicCaptions);
        ////////////////////////////////////////////////////////////

        pane.selectInitialValue();
        dialog.setVisible(true);
        dialog.dispose();

        Object value = pane.getInputValue();

        if (value == UNINITIALIZED_VALUE) {
            return null;
        }
        return value;
    }

    /* Copy and subsequent modification of showOptionDialogSpecial. */

    // We write all this custom code instead of just writing a separate
    // dialog class that does the same thing so that we can leverage
    // all that JOptionPane already provides like icons, arbitrary
    // message components, and return values.

    public static int showDetailsDialog(Component parentComponent,
        Object message, String title, int optionType, int messageType,
        Icon icon, Object[] options, Object initialValue, String detailsMessage,
        ExceptionDetails details)
        throws HeadlessException {

        JOptionPane pane = new JOptionPane(message, messageType,
                                           optionType, icon,
                                           options, initialValue);

        pane.setInitialValue(initialValue);
        pane.setComponentOrientation(((parentComponent == null) ?
        getRootFrame() : parentComponent).getComponentOrientation());

        JDialog dialog = pane.createDialog(parentComponent, title);

        // Custom code (the whole reason JOptionPaneSpecial exists).
        try {
            GuiUtil.addMnemonics(dialog, basicCaptions);
            bootstrapWithDetails(dialog, detailsMessage, details);
        } catch(Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "JOptionPaneSpecial Error: [" + e.getClass().getSimpleName() +
                "] " + e.getMessage());
        }
        ////////////////////////////////////////////////////////////

        pane.selectInitialValue();
        dialog.setVisible(true);
        dialog.dispose();

        Object selectedValue = pane.getValue();

        if(selectedValue == null) {
            return CLOSED_OPTION;
        }
        if(options == null) {
            if(selectedValue instanceof Integer) {
                return ((Integer)selectedValue).intValue();
            }
            return CLOSED_OPTION;
        }
        for(int counter = 0, maxCounter = options.length;
            counter < maxCounter; counter++) {
            if(options[counter].equals(selectedValue)) {
                return counter;
            }
        }
        return CLOSED_OPTION;
    }

    private static void bootstrapWithDetails(JDialog dialog, String detailsMessage, ExceptionDetails details) {
        JButton btnDetails = GuiUtil.findButton(dialog,
            DetailsInfo.SHOW_DETAILS.replaceAll("&", ""));

        if(btnDetails != null) {

            // Customize the dialog
            dialog.setMinimumSize(new Dimension(400, dialog.getSize().height));
            dialog.setResizable(true);
            dialog.setIconImage(ImageLib.get(CommonConcepts.ERROR).getImage());

            // Save pertinent information
            final DetailsInfo detailsInfo = new DetailsInfo();
            detailsInfo.details = details;
            detailsInfo.detailsDialog = dialog;
            detailsInfo.detailsDialogOrigWidth = dialog.getSize().width;
            detailsInfo.detailsDialogOrigHeight = dialog.getSize().height;
            detailsInfo.detailsMessage = detailsMessage;
            detailsInfo.pnlDetails = null;
            detailsInfo.btn = btnDetails;

            // Customize the button
            ActionListener[] listeners = btnDetails.getActionListeners();
            for(int x = listeners.length - 1; x >= 0; x--) {
                btnDetails.removeActionListener(listeners[x]);
            }
            btnDetails.setMnemonic('D');
            btnDetails.setText("Details");
            btnDetails.setIcon(ImageLib.get(RepleteImageModel.DETAILS_EXPAND));
            btnDetails.setHorizontalTextPosition(SwingConstants.LEFT);
            btnDetails.addActionListener(e -> {
                if(!detailsInfo.expanded) {
                    detailsInfo.open();
                } else {
                    detailsInfo.close();
                }
            });

            // Show the details if desired
            if(details.isInitiallyOpen()) {
                detailsInfo.open();
            }

            // Center the dialog
            dialog.setLocationRelativeTo(dialog.getOwner());
        }
    }

    public static class DetailsInfo {
        public static final String SHOW_DETAILS = "Details >>";
        protected static final int MORE_WIDTH = 200;
        protected static final int MORE_HEIGHT = 200;

        protected ExceptionDetails details;
        protected JOptionPane optPane;
        protected JDialog detailsDialog;
        protected int detailsDialogOrigWidth;
        protected int detailsDialogOrigHeight;
        protected String detailsMessage;
        protected JPanel pnlDetails;
        protected JButton btn;
        protected boolean expanded = false;
        protected int height = MORE_HEIGHT;

        public void open() {
            Container cont = detailsDialog.getContentPane();

            // Add text area.
            if(pnlDetails == null) {

                // Change where the option pane is located
                optPane = ReflectionUtil.get(cont.getLayout(), "center");
                cont.remove(optPane);
                cont.add(optPane, BorderLayout.NORTH);

                JScrollPane scr = Lay.sp(Lay.txa(detailsMessage, "editable=false"));
                Border b = BorderFactory.createEmptyBorder(0, 10, 10, 10);
                JComponent cmpCenter = scr;

                if(details.getSourceDirs() != null && details.getError() != null) {
                    if(details.getError().getStackTrace() != null) {
                        if(details.getError().getStackTrace().length != 0) {
                            String sourceFile = null;
                            int num = 0;
                            for(int s = 0; s < details.getError().getStackTrace().length; s++) {
                                StackTraceElement elem = details.getError().getStackTrace()[s];
                                String cl = elem.getClassName();
                                if(cl.startsWith("finio") || cl.startsWith("replete") || cl.startsWith("gov.sandia")) {
                                    sourceFile = elem.getFileName();
                                    num = elem.getLineNumber();
                                    break;
                                }
                            }
                            if(sourceFile != null && num > 0) {
                                height *= 2;
                                final REditor edSource;
                                cmpCenter = Lay.GL(2, 1,
                                    scr,
                                    edSource = Lay.ed("Searching...", "font=Courier-New")
                                );
                                final int lineSurrounding = 5;
                                edSource.setStartLineNumber(Math.max(1, num - lineSurrounding));
                                edSource.setShowStatusLine(false);
                                edSource.getTextPane().setAllowHorizScroll(true);
                                final String fSourceFile = sourceFile;
                                final int fNum = num;
                                new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            List<File> found = FileUtil.find(details.getSourceDirs(), new FileFilter() {
                                                public boolean accept(File file) {
                                                    return
                                                        (file.isDirectory() && !file.getName().startsWith(".")) ||  // Directory recurse rule
                                                        (file.isFile() && file.getName().equals(fSourceFile));      // File match rule
                                                }
                                            });
                                            if(found.isEmpty()) {
                                                set("Could not find file: " + fSourceFile);
                                                return;
                                            }
                                            set("Parsing...");
                                            String source = FileUtil.getTextContent(found.get(0));
                                            final List<String> lines = new ArrayList<>();
                                            int theLine = 0;
                                            int n = 1;
                                            try(BufferedReader reader = new BufferedReader(new StringReader(source))) {
                                                String line;
                                                while((line = reader.readLine()) != null) {
                                                    int delta = n - fNum;
                                                    if(Math.abs(delta) <= lineSurrounding) {
                                                        if(delta == 0) {
                                                            theLine = lines.size();
                                                        }
                                                        lines.add(line);
                                                    }
                                                    n++;
                                                }
                                            }
                                            final int fTheLine = theLine;
                                            GuiUtil.safe(new Runnable() {
                                                public void run() {
                                                    edSource.getTextPane().clear();
                                                    int caret = 0;
                                                    for(int i = 0; i < lines.size(); i++) {
                                                        if(i == fTheLine) {
                                                            edSource.getTextPane().append(lines.get(i), new Font("Courier New", Font.BOLD, 12), Color.red);
                                                        } else {
                                                            edSource.getTextPane().append(lines.get(i), Color.black);
                                                        }
                                                        if(i <= fTheLine) {
                                                            caret += lines.get(i).length();
                                                        }
                                                        if(i != lines.size() - 1) {
                                                            edSource.getTextPane().append("\n");
                                                            if(i < fTheLine) {
                                                                caret++;
                                                            }
                                                        }
                                                    }
                                                    edSource.getTextPane().setCaretPosition(caret);
                                                    edSource.getScrollPane().setShowRuler(true);
                                                    edSource.getScrollPane().setShowRangesAndIcons(true);
                                                    edSource.getScrollPane().getRulerModel().addIcon("ErrorIcon", fNum - 1,
                                                        new IconDescriptor(ImageLib.get(RepleteImageModel.ERROR_LINE),
                                                            details.getError().getClass().getSimpleName()));
                                                }
                                            });
                                        } catch(Exception e) {
                                            set("An error occurred while searching or parsing: " + fSourceFile +
                                                "\n" + ExceptionUtil.toCompleteString(e, 4));
                                        }
                                    }
                                    private void set(final String msg) {
                                        GuiUtil.safe(new Runnable() {
                                            public void run() {
                                                edSource.setText(msg);
                                                edSource.getTextPane().setCaretPosition(0);
                                            }
                                        });
                                    }
                                }.start();
                            }
                        }
                    }
                }

                pnlDetails =
                    GuiUtil.addBorderedComponent(detailsDialog,
                        cmpCenter, b, BorderLayout.CENTER);

                Dimension dimPanel = new Dimension(
                    detailsDialog.getWidth(),
                    height);

                pnlDetails.setMinimumSize(dimPanel);
                pnlDetails.setPreferredSize(dimPanel);
                pnlDetails.setMaximumSize(dimPanel);

            } else {
                cont.remove(optPane);
                cont.add(optPane, BorderLayout.NORTH);
                cont.add(pnlDetails, BorderLayout.CENTER);
            }

            // Expand to larger size.
            detailsDialog.setLocation(
                detailsDialog.getX() - MORE_WIDTH / 2,
                detailsDialog.getY()
            );
            detailsDialog.setSize(
                detailsDialogOrigWidth + MORE_WIDTH,
                detailsDialogOrigHeight + height
            );

            btn.setIcon(ImageLib.get(RepleteImageModel.DETAILS_COLLAPSE));

            expanded = true;
        }

        public void close() {
            Container cont = detailsDialog.getContentPane();

            // Remove text area.
            detailsDialog.remove(pnlDetails);
            cont.remove(optPane);
            cont.add(optPane, BorderLayout.CENTER);

            // Shrink to original size.
            detailsDialog.setSize(
                detailsDialogOrigWidth,
                detailsDialogOrigHeight
            );
            detailsDialog.setLocation(
                detailsDialog.getX() + MORE_WIDTH / 2,
                detailsDialog.getY()
            );

            btn.setIcon(ImageLib.get(RepleteImageModel.DETAILS_EXPAND));

            expanded = false;
        }
    }
}
