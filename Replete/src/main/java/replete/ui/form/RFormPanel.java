package replete.ui.form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;

import replete.event.ChangeNotifier;
import replete.ui.GuiUtil;
import replete.ui.button.RButton;
import replete.ui.form.FileSelectionPanel.DialogType;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.nofire.NoFireComboBox;
import replete.ui.panels.RPanel;
import replete.ui.tabbed.RTabbedPane;
import replete.ui.text.DocumentChangeListener;
import replete.ui.text.RLabel;
import replete.ui.uidebug.UiDebugUtil;
import replete.ui.windows.Dialogs;
import replete.ui.windows.escape.EscapeFrame;


/**
 * @author Derek Trumbo
 */

public abstract class RFormPanel extends RPanel {
    public static final int MAX_WIDTH = 100000;
    public static final int DEFAULT_CAPTION_WIDTH = 100;
    public static final int DEFAULT_TOP_MARGIN = 7;

    public int captionWidth;
    private boolean dirty = false;

    public boolean isDirty() {
        return dirty;
    }

    private Dimension d(int a, int b) {
        return new Dimension(a, b);
    }



    protected RButton btnSave;
    protected RButton btnCancel;
    protected Object bean;
    protected RTabbedPane tabs = null;

    public RTabbedPane getTabs() {
        return tabs;
    }

    protected ChangeNotifier saveNotifier = new ChangeNotifier(this);
    protected ChangeNotifier cancelNotifier = new ChangeNotifier(this);

    public void addSaveListener(ChangeListener l) {
        saveNotifier.addListener(l);
    }

    public void addCancelListener(ChangeListener l) {
        cancelNotifier.addListener(l);
    }

    protected void fireSaveNotifier() {
        saveNotifier.fireStateChanged();
    }

    protected void fireCancelNotifier() {
        cancelNotifier.fireStateChanged();
    }


    public void setSaveButtonDefault() {
        SwingUtilities.getRootPane(this).setDefaultButton(btnSave);
    }

    /////////////////////////////

    protected Map<String, DetailPanelPane> panes = new LinkedHashMap<>();

    public RFormPanel() {
        this(DEFAULT_CAPTION_WIDTH);
    }
    public RFormPanel(int cWidth) {
        captionWidth = cWidth;
        createButtons();
    }

    protected void init() {
        setLayout(new BorderLayout());

        // String title = getTitle();

        // if(title != null) {
        // JLabel lblTitle = new JLabel("<html><center><b><u>" + title +
        // "</u></b></center></html>");
        // Border bt = BorderFactory.createEmptyBorder();
        // JPanel pnlTitle = GuiUtil.addBorderedComponent(this, lblTitle, bt);
        // GuiUtil.setSize(pnlTitle, new Dimension(100000, 25));
        // lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        // }

        addFields();

        if(panes.size() == 0) {
            return;
        }

        for(String paneName : panes.keySet()) {

            DetailPanelPane pane = panes.get(paneName);

            JPanel pnlPane = new JPanel();
            BoxLayout boxLayout = new BoxLayout(pnlPane, BoxLayout.Y_AXIS);
            pnlPane.setLayout(boxLayout);

            if(pane != null) {
                for(FieldDescriptor field : pane.fields) {
                    FieldPanel pnlField = new FieldPanel(field, captionWidth);
                    field.pnlField = pnlField;
                    pnlField.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

                    pnlPane.add(pnlField);

                    // If there is help text to be shown below the component...
                    if(field.helpText != null) {
                        JPanel pnlFieldHelp = new RPanel(new BorderLayout());
                        pnlFieldHelp.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                        if(field.caption != null) {
                            JLabel lbl = new RLabel(" ");
                            lbl.setPreferredSize(new Dimension(captionWidth, 5));
                            lbl.setVerticalAlignment(SwingConstants.TOP);
                            pnlFieldHelp.add(lbl, BorderLayout.WEST);
                        }
                        JLabel lblHelp = new JLabel("<html>" + field.helpText + "</html>");
                        lblHelp.setPreferredSize(new Dimension(captionWidth, 5));
                        lblHelp.setVerticalAlignment(SwingConstants.TOP);
                        pnlFieldHelp.add(lblHelp, BorderLayout.CENTER);
                        pnlPane.add(pnlFieldHelp);

                        pnlFieldHelp.setPreferredSize(d(MAX_WIDTH, 5));

                        Dimension helpPanelSize = d(MAX_WIDTH, 20);
                        pnlFieldHelp.setPreferredSize(helpPanelSize);
                        pnlFieldHelp.setMinimumSize(helpPanelSize);
                        pnlFieldHelp.setMaximumSize(helpPanelSize);
                    }
                }

                if(showSaveButton() || showCancelButton()) {
                    JPanel pnlButtons = new RPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));

                    if(showSaveButton()) {
                        pnlButtons.add(btnSave);
                    }
                    if(showCancelButton()) {
                        if(showSaveButton()) {
                            Border b = BorderFactory.createEmptyBorder(0, 5, 0, 0);
                            GuiUtil.addBorderedComponent(pnlButtons, btnCancel, b);
                        } else {
                            pnlButtons.add(btnCancel);
                        }

                    }

                    Border brdProps = BorderFactory.createEmptyBorder(10, 10, 10, 10);
                    JPanel pnlButtonsBorder = GuiUtil.addBorderedComponent(pnlPane, pnlButtons, brdProps);
                    pnlButtonsBorder.setMaximumSize(new Dimension(100000, 50));
                    pnlButtonsBorder.setMinimumSize(new Dimension(100000, 50));
                }
            }

            if(tabs == null) {
                tabs = new RTabbedPane();
            }
            tabs.addTab(paneName, pnlPane);
        }

        if(panes.size() == 1) {
            add(tabs.getComponent(0), BorderLayout.CENTER);
        } else {
            add(tabs, BorderLayout.CENTER);
        }

        makeClean();
    }

    public void focusFirstComponent() {}

    protected void addFields() {}
    protected FieldDescriptor addField(Component cmp) {
        return addField("Main", null, cmp, 40, false, null, null);
    }
    protected FieldDescriptor addField(Component cmp, int height) {
        return addField("Main", null, cmp, height, false, null, null);
    }
    protected FieldDescriptor addField(String caption, Component cmp) {
        return addField("Main", caption, cmp, 40, false, null, null);
    }
    protected FieldDescriptor addField(String caption, Component cmp, Icon icon) {
        return addField("Main", caption, cmp, 40, false, null, icon);
    }
    protected FieldDescriptor addField(String paneName, String caption, Component cmp) {
        return addField(paneName, caption, cmp, 40, false, null, null);
    }
    protected FieldDescriptor addField(String caption, Component cmp, int height) {
        return addField("Main", caption, cmp, height, false, null, null);
    }
    protected FieldDescriptor addField(String caption, Component cmp, int height, String helpText) {
        return addField("Main", caption, cmp, height, false, helpText, null);
    }
    protected FieldDescriptor addField(String caption, Component cmp, String helpText) {
        return addField("Main", caption, cmp, 40, false, helpText, null);
    }
    protected FieldDescriptor addField(String caption, Component cmp, String helpText, Icon icon) {
        return addField("Main", caption, cmp, 40, false, helpText, icon);
    }
    protected FieldDescriptor addField(String paneName, String caption, Component cmp, String helpText) {
        return addField(paneName, caption, cmp, 40, false, helpText, null);
    }
    protected FieldDescriptor addField(String paneName, String caption, Component cmp, int height) {
        return addField(paneName, caption, cmp, height, false, null, null);
    }
    protected FieldDescriptor addField(String paneName, String caption, Component cmp, int height, boolean expandable) {
        return addField(paneName, caption, cmp, height, expandable, null, null);
    }
    protected FieldDescriptor addField(String paneName, String caption, Component cmp, int height, boolean expandable, Icon icon) {
        return addField(paneName, caption, cmp, height, expandable, null, icon);
    }
    protected FieldDescriptor addField(String caption, Component cmp, int height, boolean expandable, String helpText) {
        return addField("Main", caption, cmp, height, expandable, helpText, null);
    }
    protected FieldDescriptor addField(String paneName, String caption, Component cmp, int height, boolean expandable, String helpText) {
        return addField(paneName, caption, cmp, height, expandable, helpText, null);
    }
    protected FieldDescriptor addField(String paneName, String caption, Component cmp, int height, boolean expandable, String helpText, Icon icon) {
        DetailPanelPane pane = panes.get(paneName);

        if(pane == null) {
            pane = new DetailPanelPane();
            panes.put(paneName, pane);
        }

        FieldDescriptor fd = new FieldDescriptor(caption, icon, cmp, height, expandable, helpText);
        pane.fields.add(fd);
        return fd;
    }

    protected class DetailPanelPane {
        protected List<FieldDescriptor> fields = new ArrayList<>();
    }
    /////////////////////////////////////

    protected boolean showSaveButton() {
        return true;
    }

    protected boolean showCancelButton() {
        return true;
    }

    public RButton getSaveButton() {
        return btnSave;
    }
    public RButton getCancelButton() {
        return btnCancel;
    }

    protected boolean saveButtonDisabledOnClean() {
        return true;
    }

    protected boolean cancelButtonDisabledOnClean() {
        return true;
    }

    public boolean trySave() {
        String errMsg = validationMessage();
        if(errMsg == null) {
            saveForm();
            fireSaveNotifier();
            return true;
        }
        Window win = GuiUtil.win(this);
        Dialogs.showError(win, errMsg);
        return false;
    }

    protected void createButtons() {
        btnSave   = Lay.btn("&Save",   CommonConcepts.SAVE,   (ActionListener) e -> trySave());
        btnCancel = Lay.btn("&Cancel", CommonConcepts.CANCEL, (ActionListener) e -> {
            setBean(bean);
            fireCancelNotifier();
        });
    }

    protected String validationMessage() {
        return null;
    }

    protected void saveForm() {
        fieldsToBean();
        makeClean();
    }

    protected void makeDirty() {
        btnSave.setEnabled(true);
        btnCancel.setEnabled(true);
        dirty = true;
    }

    protected void makeClean() {
        if(saveButtonDisabledOnClean()) {
            btnSave.setEnabled(false);
        }
        if(cancelButtonDisabledOnClean()) {
            btnCancel.setEnabled(false);
        }
        dirty = false;
    }

    public void setBean(Object b) {
        bean = b;
        beanToFields();
        makeClean();
    }

    public Object getBean() {
        return bean;
    }

    protected void beanToFields() {}
    protected void fieldsToBean() {}


    ///////////////////////////////
    // STANDARD CONTROL CREATION //
    ///////////////////////////////

    protected JTextField createTextField() {
        JTextField txt = Lay.tx("", "selectall");
        txt.getDocument().addDocumentListener(new DocumentChangeListener() {
            public void documentChanged(DocumentEvent e) {
                makeDirty();
            }
        });
        return txt;
    }

    protected JTextArea createTextArea() {
        JTextArea txt = new JTextArea();
        txt.getDocument().addDocumentListener(new DocumentChangeListener() {
            public void documentChanged(DocumentEvent e) {
                makeDirty();
            }
        });
        return txt;
    }

    protected JTextPane createTextPane() {
        JTextPane txt = new JTextPane();
        txt.getDocument().addDocumentListener(new DocumentChangeListener() {
            public void documentChanged(DocumentEvent e) {
                makeDirty();
            }
        });
        return txt;
    }

    protected JComboBox createComboBox() {
        return createComboBox(false);
    }
    protected JComboBox createComboBox(boolean noFire) {
        JComboBox cbo = noFire ? new NoFireComboBox() : new JComboBox();
        cbo.setBackground(Color.white);
        cbo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                makeDirty();
            }
        });
        return cbo;
    }

    //////////
    // TEST //
    //////////

    private static int winXLoc = 50;

    public static void main(String[] args) {
        UiDebugUtil.enableColor();
        UiDebugUtil.enableTicks();
        boolean all = false;
        open("N2A", 0, all);
        open("DD", 1, all);
        open("Deebee", 2, all);
    }

    public static void open(String title, int which, boolean fx) {
        TestEditPanel p = new TestEditPanel(which);
        JFrame f = new EscapeFrame();
        f.setTitle(title);
        f.add(p);
        f.setSize(600, 500);
        f.setLocation(winXLoc, 50);
        f.setVisible(true);
        winXLoc += 600;
    }

    private static class TestEditPanel extends RFormPanel {
        int which;

        public TestEditPanel(int w) {
            which = w;
            init();
        }

        @Override
        protected void addFields() {
            String MAIN_PANE = "Main";

            switch(which) {
                case 0:  // N2A
                    String testHT = "alsdkfjslf lkas lflks flasf asl fskf slak fjkslf dlka fdkl fal fkd faslf slkf asklf aslkf sklf asklf asklf klas flas flk dfa flsd kf flask fslak fs";
                    addField(MAIN_PANE, "Name",      createTextField(), 40, false);
                    addField(MAIN_PANE, "Parent",    createComboBox(), 40, false, testHT);
                    addField(MAIN_PANE, "Equations", new JScrollPane(new JTable()), 150, true);
                    addField(MAIN_PANE, "Notes",     new JScrollPane(createTextArea()), 75, false);
                    addField(MAIN_PANE, "File",      new FileSelectionPanel(null, "Choose File", null, DialogType.SAVE, JFileChooser.FILES_ONLY), 40, false);
                    break;
                case 1:  // DD
                    addField(MAIN_PANE, "Output Type", createComboBox(), 40, false);
                    addField(MAIN_PANE, "Min Speed",   createTextField(), 40, false);
                    addField(MAIN_PANE, "Subjects",    createTextField(), 40, true, "<i>(comma- and hyphen-delimited ranges, e.g '1-6, 10-19' or '*')</i>");
                    addField(MAIN_PANE, "Source Dir",  new FileSelectionPanel(SwingUtilities.getRoot(this), "Choose Source Dir...", null, DialogType.OPEN, JFileChooser.DIRECTORIES_ONLY), 40, false);
                    addField(MAIN_PANE, "Dest File",   new FileSelectionPanel(SwingUtilities.getRoot(this), "Choose Dest File...", null, DialogType.SAVE, JFileChooser.FILES_ONLY), 40, false);
                    break;
                case 2:  // Deebee
                    addField(MAIN_PANE, "Name",     new RLabel("aslk laks lask d"), 40, false, "etes");
                    addField(MAIN_PANE, "Driver",   new RLabel("aslk laks lask d"), 40, false);
                    addField(MAIN_PANE, "URL",      new RLabel("aslk laks lask d"), 40, false);
                    addField(MAIN_PANE, "Catalogs", new RLabel("aslk laks lask d"), 40, false);
                    addField(MAIN_PANE, "Schemas",  new RLabel("aslk laks lask d"), 40, false);
                    addField(MAIN_PANE, "User",     new RLabel("aslk laks lask d"), 40, false);
                    addField(MAIN_PANE, "Password", new RLabel("aslk laks lask d"), 40, false);
                    addField("other", null, createTextArea(), 200, true);
                    break;
            }
        }
    }
}
