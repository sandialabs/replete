package gotstyle.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import gotstyle.model.Ex;
import gotstyle.model.GotStyleModel;
import gotstyle.model.Pg;
import gotstyle.model.Status;
import gotstyle.ui.images.GotStyleImageModel;
import replete.io.FileUtil;
import replete.misc.ExpandToFitUtility;
import replete.misc.ExpandToFitUtility.Outputs;
import replete.text.StringUtil;
import replete.ui.fc.RFileChooser;
import replete.ui.fc.RFilterBuilder;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.lay.Lay;
import replete.ui.panels.RPanel;
import replete.ui.text.DocumentChangeListener;
import replete.ui.text.RTextArea;
import replete.ui.text.RTextPane;
import replete.ui.windows.Dialogs;
import replete.ui.windows.escape.EscapeFrame;
import replete.util.User;
import replete.xstream.XStreamWrapper;

public class GotStyleFrame extends EscapeFrame {


    ////////////
    // FIELDS //
    ////////////

    // Core

    private File albumPath;
    private GotStyleModel model;

    // UI

    private JPanel pnlTop, pnlBot;
    private JLabel lblTop, lblBot;
    private RTextArea txtSource;
    private RTextPane txtRendered;
    private ImagePanel pnlImage;
    private JPanel pnlCode;
    private JLabel lblModel;
    private JLabel lblModel2;
    private CardLayout layCode;
    private boolean rendered = false;
    private JPanel pnlNavParent;
    private JPanel pnlNav;
    private JList lstNav;
    private DefaultListModel mdlNav;
    private boolean navOn = true;
    private boolean suppressListSel = false;
    private JLabel lblExamples;
    private int fontSize = 12;
    private List<BufferedImage> images = new ArrayList<>();
    private JScrollPane spSource;
    private JScrollPane spRendered;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    private class MemeScrollPane extends JScrollPane {
        @Override
        public void paint(Graphics g) {
            super.paint(g);

            if(!images.isEmpty() && model.curExample != -1 &&
                            (model.curExample != 0 && model.curPage == 0) ||
                            (model.curExample == 0 && model.curPage == 5)) {
                BufferedImage image = images.get(model.curExample % images.size());
                ExpandToFitUtility.Inputs inputs = new ExpandToFitUtility.Inputs();
                int halfW = getWidth() / 2;
                int halfH = getHeight() / 2;
                inputs.availableWidth = halfW - 30;
                inputs.availableHeight = halfH - 30;
                inputs.desiredWidth = image.getWidth();
                inputs.desiredHeight = image.getHeight();

                Outputs outputs = ExpandToFitUtility.fit(inputs);

                g.drawImage(image,
                    halfW + outputs.fittedX,
                    halfH + outputs.fittedY,
                    outputs.fittedWidth, outputs.fittedHeight, null);
            }
        }
    }

    public GotStyleFrame(File albumPath) {
        setIcon(GotStyleImageModel.GOTSTYLE_APP);
        this.albumPath = albumPath;
        updateTitle();

        txtSource = new MyTextArea();

        spSource = new JScrollPane(); //new MemeScrollPane();
        spRendered = new MemeScrollPane();

        spSource.getVerticalScrollBar().addAdjustmentListener(e -> spSource.repaint());
        spRendered.getVerticalScrollBar().addAdjustmentListener(e -> spRendered.repaint());

        Lay.hn(txtSource, "editable=false,font=Monospaced");
        txtSource.setMargin(new Insets(3, 3, 3, 3));

        JButton btnOpen, btnSaveAs;
        Lay.BLtg(this,
            "C", pnlNavParent = Lay.BL(
                "N", Lay.BL(
                    "W", Lay.FL("L",
                        Lay.lb("Got Style?  ", "size=14,fg=blue,italic"),
                        btnOpen   = Lay.btn(CommonConcepts.OPEN, "ttt=Open...,icon"),
                        btnSaveAs = Lay.btn(CommonConcepts.SAVE_AS, "ttt=Save-As...,icon"),
                        Lay.lb(" "), Lay.lb(" "),
                        createToolButton(GotStyleImageModel.TOGGLE_NAV,   "Toggle-Navigation",     e -> toggleNav()),
                        createToolButton(GotStyleImageModel.TOGGLE_CODE,  "Toggle-Code",           e -> toggleCode()),
                        createToolButton(GotStyleImageModel.CHANGE_TITLE, "Change-Title...",       e -> changeTitle()),
                        createToolButton(GotStyleImageModel.CHANGE_MSG,   "Change-Message...",     e -> changeMessage()),
                        createToolButton(GotStyleImageModel.CHANGE_EX_ST, "Change-Example-Status", e -> changeExampleStatus(-1)),
                        createToolButton(GotStyleImageModel.CHANGE_PG_ST, "Change-Page-Status",    e -> changePageStatus(1)),
                        createToolButton(GotStyleImageModel.DEINDENT,     "Deindent",              e -> changeIndent(-1)),
                        createToolButton(GotStyleImageModel.INDENT,       "Indent",                e -> changeIndent(1)),

                        createToolButton(GotStyleImageModel.SEL_EX_FS, "Select-First-Example",    e -> selectExampleHome()),
                        createToolButton(GotStyleImageModel.SEL_EX_PR, "Select-Previous-Example", e -> selectExample(-1)),
                        createToolButton(GotStyleImageModel.SEL_EX_NX, "Select-Next-Example",     e -> selectExample(1)),
                        createToolButton(GotStyleImageModel.SEL_EX_LS, "Select-Last-Example",     e -> selectExampleEnd()),
                        createToolButton(GotStyleImageModel.SEL_PG_FS, "Select-First-Page",       e -> selectPageHome()),
                        createToolButton(GotStyleImageModel.SEL_PG_PR, "Select-Previous-Page",    e -> selectPage(-1)),
                        createToolButton(GotStyleImageModel.SEL_PG_NX, "Select-Next-Page",        e -> selectPage(1)),
                        createToolButton(GotStyleImageModel.SEL_PG_LS, "Select-Last-Page",        e -> selectPageEnd()),

                        createToolButton(GotStyleImageModel.INS_EX_BF,   "Insert-Example-Before", e -> insertExample(false)),
                        createToolButton(GotStyleImageModel.INS_EX_AF,   "Insert-Example-After",  e -> insertExample(true)),
                        createToolButton(GotStyleImageModel.INS_PG_BF,   "Insert-Page-Before",    e -> insertPage(false)),
                        createToolButton(GotStyleImageModel.INS_PG_AF,   "Insert-Page-After",     e -> insertPage(true)),
                        createToolButton(CommonConcepts.DELETE,          "Delete-Example",        e -> deleteExample()),
                        createToolButton(CommonConcepts.ERROR,           "Delete-Page",           e -> deletePage()),
                        createToolButton(GotStyleImageModel.IMAGE_SET,   "Set-Page-Image...",     e -> setPageImage()),
                        createToolButton(GotStyleImageModel.IMAGE_CLEAR, "Clear-Page-Image",      e -> clearPageImage()),

                        createToolButton(GotStyleImageModel.MOVE_EX_UP, "Move-Example-Up",        e -> moveExampleUp()),
                        createToolButton(GotStyleImageModel.MOVE_EX_DN, "Move-Example-Down",      e -> moveExampleDown()),
                        createToolButton(GotStyleImageModel.MOVE_PG_LF, "Move-Page-Left",         e -> movePageLeft()),
                        createToolButton(GotStyleImageModel.MOVE_PG_RT, "Move-Page-Right",        e -> movePageRight()),

                        createToolButton(CommonConcepts.FONT_INCREASE, "Increase-Font", e -> changeFont(1)),
                        createToolButton(CommonConcepts.FONT_DECREASE, "Decrease-Font", e -> changeFont(-1))
                    ),
                    "E", Lay.GBL(
                        Lay.FL(
                            lblModel  = Lay.lb("", GotStyleImageModel.EXAMPLES, "size=14,eb=5r"),
                            lblModel2 = Lay.lb("", GotStyleImageModel.PAGES, "size=14,eb=5r")
                        )
                    )
                ),
                "W", pnlNav = Lay.BL(
                    "N", lblExamples = Lay.lb("Examples", "bg=2D26FF,fg=white,center"),
                    "C", Lay.sp(lstNav = Lay.lst(
                        mdlNav = new DefaultListModel(), new ExWrapperRenderer(), "seltype=single"
                    )),
                    "prefw=200"
                ),
                "C", Lay.BL(
                    "N", pnlTop = Lay.FL(lblTop = Lay.lb(" ", "size=16,fg=white"), "bg=100"),
                    "C", pnlCode = Lay.CL(
                        "Source", spSource,
                        "Rendered", spRendered,
                        "Image", pnlImage = new ImagePanel()
                    ),
                    "S", pnlBot = Lay.FL(lblBot = Lay.lb(" ", "size=16,fg=white"), "bg=100")
                )
            ),
            "size=[1100,800],center"
        );
        spSource.setViewportView(txtSource);
        spRendered.setViewportView(txtRendered = Lay.txp("", "editable=false,font=Monospaced"));

        txtRendered.setAllowHorizScroll(true);
        lstNav.addListSelectionListener(e -> {
            if(!suppressListSel && !e.getValueIsAdjusting()) {
                setExampleFromListSel(lstNav.getSelectedIndex());
            }
        });

        layCode = (CardLayout) pnlCode.getLayout();

        addKeyAction(KeyEvent.VK_R,      false, e -> toggleCode());
        addKeyAction(KeyEvent.VK_T,      false, e -> changeTitle());
        addKeyAction(KeyEvent.VK_F,      false, e -> changeMessage());
        addKeyAction(KeyEvent.VK_Q,      false, e -> toggleNav());

        addKeyAction(KeyEvent.VK_S, false, e -> changeExampleStatus(1));
        addKeyAction(KeyEvent.VK_X, false, e -> changePageStatus(1));

        addKeyAction(KeyEvent.VK_COMMA,  false, e -> changeIndent(-1));
        addKeyAction(KeyEvent.VK_PERIOD, false, e -> changeIndent(1));

        addKeyAction(KeyEvent.VK_UP,   false, e -> selectExample(-1));
        addKeyAction(KeyEvent.VK_DOWN, false, e -> selectExample(1));
        addKeyAction(KeyEvent.VK_UP,   true,  e -> selectExampleHome());
        addKeyAction(KeyEvent.VK_DOWN, true,  e -> selectExampleEnd());

        addKeyAction(KeyEvent.VK_LEFT,  false, e -> selectPage(-1));
        addKeyAction(KeyEvent.VK_RIGHT, false, e -> selectPage(1));
        addKeyAction(KeyEvent.VK_LEFT,  true,  e -> selectPageHome());
        addKeyAction(KeyEvent.VK_RIGHT, true,  e -> selectPageEnd());

        addKeyActionSpec(KeyEvent.VK_LEFT,  e -> selectPageSpecial(-1));
        addKeyActionSpec(KeyEvent.VK_RIGHT, e -> selectPageSpecial(1));

        addKeyAction(KeyEvent.VK_I, true,  e -> insertExample(false));
        addKeyAction(KeyEvent.VK_J, true,  e -> insertExample(true));
        addKeyAction(KeyEvent.VK_I, false, e -> insertPage(false));
        addKeyAction(KeyEvent.VK_J, false, e -> insertPage(true));

        addKeyAction(KeyEvent.VK_D, true,  e -> deleteExample());
        addKeyAction(KeyEvent.VK_D, false, e -> deletePage());

        addKeyAction(KeyEvent.VK_Y, false, e -> setPageImage());

        addKeyActionCtrl(KeyEvent.VK_MINUS,  e -> changeFont(-1));
        addKeyActionCtrl(KeyEvent.VK_EQUALS, e -> changeFont(1));

        loadAlbum();
        editCode();
        updateView();
        updateNav();

        btnOpen.addActionListener(e -> {
            RFileChooser chooser = RFileChooser.getChooser("Open Album");
            RFilterBuilder builder = new RFilterBuilder(chooser, false);
            builder.append("Album Files (*.xml)", "xml");
            if(chooser.showOpen(this)) {
                File newFile = chooser.getSelectedFile();
                if(FileUtil.isReadableFile(newFile)) {
                    this.albumPath = newFile;
                    updateTitle();
                    loadAlbum();
                    editCode();
                    updateView();
                    updateNav();
                } else {
                    Dialogs.showWarning(this, "Could not read file.");
                }
            }
        });

        btnSaveAs.addActionListener(e -> {
            RFileChooser chooser = RFileChooser.getChooser("Save Album As");
            RFilterBuilder builder = new RFilterBuilder(chooser, false);
            builder.append("Album Files (*.xml)", "xml");
            if(chooser.showSave(this)) {
                File newFile = chooser.getSelectedFile();
                this.albumPath = newFile;
                updateTitle();
                saveAlbum();
            }
        });

        txtSource.addChangeListener((DocumentChangeListener) e -> {
            if(model.curExample == -1) {
                return;
            }
            Ex ex = cur();
            Pg pg = ex.pages.get(model.curPage);
            pg.src = txtSource.getText();
            setRendered(pg.src);
        });

        setCur(model.curExample);

        if(User.getName().equals("dtrumbo")) {
            File neoDir = new File("C:\\Users\\dtrumbo\\work\\eclipse-release-3.6.0\\GotStyle\\src\\gotstyle\\ui\\images");
            File[] files = neoDir.listFiles((FileFilter) path -> path.getName().startsWith("neo-"));
            try {
                for(File file : files) {
                    images.add(ImageIO.read(file));
                }
            } catch(IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private JButton createToolButton(ImageModelConcept concept, String ttt, ActionListener listener) {
        JButton btn = Lay.btn(concept, "ttt=" + ttt + ",icon");
        btn.addActionListener(listener);
        return btn;
    }

    private void addKeyAction(int keyCode, boolean shift, ActionListener listener) {
        JRootPane rp = getRootPane();
        KeyStroke stroke = KeyStroke.getKeyStroke(keyCode, InputEvent.ALT_MASK | (shift ? InputEvent.SHIFT_MASK : 0));
        rp.registerKeyboardAction(listener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void addKeyActionSpec(int keyCode, ActionListener listener) {
        JRootPane rp = getRootPane();
        KeyStroke stroke = KeyStroke.getKeyStroke(keyCode, 0);
        rp.registerKeyboardAction(listener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void addKeyActionCtrl(int keyCode, ActionListener listener) {
        JRootPane rp = getRootPane();
        KeyStroke stroke = KeyStroke.getKeyStroke(keyCode, InputEvent.CTRL_MASK);
        rp.registerKeyboardAction(listener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    private void updateTitle() {
        String path = albumPath == null ? "(NEW)" : albumPath.getAbsolutePath();
        setTitle("Got Style? - " + path);
    }
    public File getAlbumPath() {
        return albumPath;
    }
    public GotStyleModel getModel() {
        return model;
    }
    private Ex cur() {
        return model.examples.get(model.curExample);
    }
    private void setCur(int nextCurExample) {
        model.curExample = nextCurExample;
        suppressListSel = true;
        lstNav.setSelectedIndex(nextCurExample);
        lstNav.ensureIndexIsVisible(nextCurExample);
        suppressListSel = false;
    }


    //////////
    // FONT //
    //////////

    private void changeFont(int del) {
        fontSize += del;
        Lay.hn(txtSource,   "size=" + fontSize);
        Lay.hn(txtRendered, "size=" + fontSize);
        MutableAttributeSet attrs = txtRendered.getInputAttributes();
        StyleConstants.setFontSize(attrs, fontSize);
        StyledDocument doc = txtRendered.getStyledDocument();
        doc.setCharacterAttributes(0, doc.getLength() + 1, attrs, false);
    }


    /////////////////
    // LOAD / SAVE //
    /////////////////

    private void loadAlbum() {
        if(albumPath == null) {
            model = new GotStyleModel();
        } else if(!FileUtil.isReadableFile(albumPath)) {
            model = new GotStyleModel();
        } else {
            try {
                model = XStreamWrapper.loadTarget(albumPath);
            } catch(Exception e) {
                model = new GotStyleModel();
            }
        }
    }

    private void saveAlbum() {
        try {
            XStreamWrapper.writeToFile(model, albumPath);
        } catch(Exception e) {
            Dialogs.showDetails(this, "Error saving album.", e);
        }
    }


    //////////
    // MOVE //
    //////////

    private void moveExampleUp() {
        if(model.curExample == -1 || model.curExample == 0) {
            return;
        }
        int p = model.curExample - 1;
        Ex prev = model.examples.get(p);
        Ex cur = cur();
        model.examples.set(model.curExample, prev);
        model.examples.set(p, cur);
        setCur(p);
        model.curPage = 0;
        updateView();
        updateNav();
        setCur(p);
    }

    private void moveExampleDown() {
        if(model.curExample == -1 || model.curExample == model.examples.size() - 1) {
            return;
        }
        int n = model.curExample + 1;
        Ex next = model.examples.get(n);
        Ex cur = cur();
        model.examples.set(model.curExample, next);
        model.examples.set(n, cur);
        setCur(n);
        model.curPage = 0;
        updateView();
        updateNav();
        setCur(n);
    }

    private void movePageLeft() {
        if(model.curExample == -1 || model.curPage == 0) {
            return;
        }
        Ex cur = cur();
        int p = model.curPage - 1;
        Pg curp = cur.pages.get(model.curPage);
        Pg prev = cur.pages.get(p);
        cur.pages.set(model.curPage, prev);
        cur.pages.set(p, curp);
        model.curPage = p;
        updateView();
    }

    private void movePageRight() {
        if(model.curExample == -1) {
            return;
        }
        Ex cur = cur();
        if(model.curPage == cur.pages.size() - 1) {
            return;
        }
        int n = model.curPage + 1;
        Pg curp = cur.pages.get(model.curPage);
        Pg next = cur.pages.get(n);
        cur.pages.set(model.curPage, next);
        cur.pages.set(n, curp);
        model.curPage = n;
        updateView();
    }


    //////////
    // MOVE //
    //////////

    private void selectExample(int del) {
        if(model.curExample == -1) {
            return;
        }
        int prevCur = model.curExample;
        int nextCur = prevCur;
        nextCur += del;
        if(nextCur == model.examples.size()) {
            nextCur = 0;
        }
        if(nextCur == -1) {
            nextCur = model.examples.size() - 1;
        }
        if(nextCur != prevCur) {
            setCur(nextCur);
            model.curPage = 0;
            updateView();
        }
    }

    private void selectExampleEnd() {
        if(model.curExample == -1) {
            return;
        }
        int prevCur = model.curExample;
        int nextCur = prevCur;
        int lastIdx = model.examples.size() - 1;
        nextCur = lastIdx;
        if(nextCur != prevCur) {
            setCur(nextCur);
            updateView();
        }
    }

    private void selectExampleHome() {
        if(model.curExample == -1) {
            return;
        }
        int prevCur = model.curExample;
        int nextCur = 0;
        if(nextCur != prevCur) {
            setCur(nextCur);
            updateView();
        }
    }

    private void setExampleFromListSel(int index) {
        if(model.curExample == -1 || index == -1) {
            return;
        }
        int prevCur = model.curExample;
        int nextCur = index;
        if(nextCur > model.examples.size() - 1) {
            nextCur = 0;
        }
        if(nextCur < 0) {
            nextCur = model.examples.size() - 1;
        }
        if(nextCur != prevCur) {
            model.curExample = nextCur;
            model.curPage = 0;
            updateView();
        }
    }

    private void selectPage(int del) {
        if(model.curExample == -1) {
            return;
        }
        Ex ex = cur();
        model.curPage += del;
        if(model.curPage == ex.pages.size()) {
            model.curPage = 0;
        }
        if(model.curPage == -1) {
            model.curPage = ex.pages.size() - 1;
        }
        updateView();
    }

    private void selectPageEnd() {
        if(model.curExample == -1) {
            return;
        }
        Ex ex = cur();
        int lastIdx = ex.pages.size() - 1;
        if(model.curPage != lastIdx) {
            model.curPage = lastIdx;
            updateView();
        }
    }

    private void selectPageHome() {
        if(model.curExample == -1) {
            return;
        }
        if(model.curPage != 0) {
            model.curPage = 0;
            updateView();
        }
    }

    private void selectPageSpecial(int i) {
        if(model.curExample == -1 || !rendered) {
            return;
        }
        if(i == 1) {
            Ex ex = cur();
            if(model.curPage == ex.pages.size() - 1) {
                if(model.curExample == model.examples.size() - 1) {
                    // Do nothing, at end!
                } else {
                    setCur(model.curExample + 1);
                    model.curPage = 0;
                }
            } else {
                model.curPage++;
            }
        } else {
            if(model.curPage == 0) {
                if(model.curExample == 0) {
                    // Do nothing, at front!
                } else {
                    setCur(model.curExample - 1);
                    Ex ex = cur();
                    model.curPage = ex.pages.size() - 1;
                }
            } else {
                model.curPage--;
            }
        }
        updateView();
    }


    ////////////
    // DELETE //
    ////////////

    private void deleteExample() {
        if(model.curExample == -1) {
            return;
        }
        if(!Dialogs.showConfirm(this, "Delete Example?")) {
            return;
        }
        model.examples.remove(model.curExample);
        if(model.curExample == model.examples.size()) {
            setCur(model.curExample - 1);
        }
        updateView();
        updateNav();
        setCur(model.curExample);
    }

    private void deletePage() {
        if(model.curExample == -1) {
            return;
        }
        if(!Dialogs.showConfirm(this, "Delete Page?")) {
            return;
        }
        Ex ex = cur();
        if(ex.pages.size() == 1) {
            ex.pages.get(0).clear();
        } else {
            ex.pages.remove(model.curPage);
            if(model.curPage == ex.pages.size()) {
                model.curPage--;
            }
        }
        updateView();
    }


    ////////////
    // INSERT //
    ////////////

    private void insertExample(boolean after) {
        after = after || model.curExample == -1;
        model.examples.add(model.curExample + (after ? 1 : 0), new Ex());
        if(after) {
            setCur(model.curExample + 1);
        }
        model.curPage = 0;
        updateView();
        updateNav();
        setCur(model.curExample);
        txtSource.focus();
        txtSource.selectAll();
    }

    private void insertPage(boolean after) {
        if(model.curExample == -1) {
            return;
        }
        Ex example = cur();
        example.pages.add(model.curPage + (after ? 1 : 0), new Pg());
        if(after) {
            model.curPage++;
        }
        updateView();
        txtSource.focus();
        txtSource.selectAll();
    }

    private void changeExampleStatus(int del) {
        if(model.curExample == -1) {
            return;
        }
        Ex ex = cur();
        int s = ex.status.ordinal();
        s += del;
        if(s == Status.values().length) {
            s = 0;
        }
        if(s == -1) {
            s = Status.values().length - 1;
        }
        ex.status = Status.values()[s];
        updateNav();
    }

    private void changePageStatus(int del) {
        if(model.curExample == -1) {
            return;
        }
        Ex ex = cur();
        Pg pg = ex.pages.get(model.curPage);
        int s = pg.status.ordinal();
        s += del;
        if(s == Status.values().length) {
            s = 0;
        }
        if(s == -1) {
            s = Status.values().length - 1;
        }
        pg.status = Status.values()[s];
        updateLabels();
    }


    ////////////
    // INDENT //
    ////////////

    private void changeIndent(int del) {
        if(model.curExample == -1) {
            return;
        }
        Ex ex = cur();
        Pg pg = ex.pages.get(model.curPage);
        String src = pg.src;
        if(del > 0) {
            int chSpCount = del * 4;
            pg.src = StringUtil.padNewLines(src, chSpCount, true);
        } else {
            int chSpCount = -del * 4;
            StringBuilder buffer = new StringBuilder();
            boolean foundNl = false;
            for(int i = 0; i < src.length(); i++) {
                char ch = src.charAt(i);
                if(ch == '\r') {
                    buffer.append('\r');

                    if(i < src.length() - 1) {
                        if(src.charAt(i + 1) == '\n') {
                            buffer.append("\n");
                            i++;
                        }
                    }
                    foundNl = true;

                } else if(ch == '\n') {
                    buffer.append(ch);
                    foundNl = true;

                } else if(i == 0) {
                    foundNl = true;

                } else {
                    buffer.append(ch);
                }

                if(foundNl) {
                    int remove = 0;
                    while(i < src.length() - 1 && src.charAt(i + 1) == ' ' && remove < chSpCount) {
                        // Ignore this space
                        i++;
                        remove++;
                    }
                    foundNl = false;
                }
            }
            pg.src = buffer.toString();
        }
        updateCodeOrImage();
    }


    ////////////
    // TITLES //
    ////////////

    private void changeMessage() {
        if(model.curExample == -1) {
            return;
        }
        Ex example = cur();
        Pg pg = example.pages.get(model.curPage);
        String input = Dialogs.showInput(this, "Change Page Message", "Change Page Message", pg.msg);
        if(input != null) {
            pg.msg = input;
            updateLabels();
        }
    }

    private void changeTitle() {
        if(model.curExample == -1) {
            return;
        }
        Ex example = cur();
        String input = Dialogs.showInput(this, "Change Example Title", "Change Example Title", example.title);
        if(input != null) {
            example.title = input;
            updateNav();
        }
    }


    ///////////
    // IMAGE //
    ///////////

    private void setPageImage() {
        if(model.curExample == -1) {
            return;
        }
        RFileChooser chooser = RFileChooser.getChooser("Set Page Image");
        if(albumPath != null) {
            File parentDir = albumPath.getParentFile();
            File imageDir = new File(parentDir, "images");
            File curDir;
            if(FileUtil.isReadableDir(imageDir)) {
                curDir = imageDir;
            } else {
                curDir = parentDir;
            }
            chooser.setCurrentDirectory(curDir);
        }
        RFilterBuilder builder = new RFilterBuilder(chooser, false);
        builder.append("Image Files (*.png, *.jpg, *.gif, *.tif)", "png", "jpg", "gif", "tif");
        if(chooser.showOpen(this)) {
            File newFile = chooser.getSelectedFile();
            if(FileUtil.isReadableFile(newFile)) {
                Ex ex = cur();
                Pg pg = ex.pages.get(model.curPage);
                pg.img = newFile.getName();
                updateCodeOrImage();
            } else {
                Dialogs.showWarning(this, "Could not read image file.");
            }
        }
    }

    private void clearPageImage() {
        if(model.curExample == -1) {
            return;
        }
        Ex ex = cur();
        Pg pg = ex.pages.get(model.curPage);
        pg.img = null;
        updateCodeOrImage();
    }


    ////////////////
    // COMPONENTS //
    ////////////////

    private void toggleNav() {
        if(navOn) {
            pnlNavParent.remove(pnlNav);
        } else {
            pnlNavParent.add(pnlNav, BorderLayout.WEST);
        }
        navOn = !navOn;
        pnlNavParent.updateUI();
    }

    private void toggleCode() {
        boolean hasImg = false;
        if(model.curExample != -1) {
            Ex ex = cur();
            Pg pg = ex.pages.get(model.curPage);
            if(pg.img != null) {
                hasImg = true;
            }
        }

        if(!hasImg) {
            if(rendered) {
                layCode.show(pnlCode, "Source");
            } else {
                layCode.show(pnlCode, "Rendered");
            }
            rendered = !rendered;
        } else {
            layCode.show(pnlCode, "Image");
        }

        updateModelLabelProgressBar();
    }

    private void editCode() {
        boolean hasImg = false;
        if(model.curExample != -1) {
            Ex ex = cur();
            Pg pg = ex.pages.get(model.curPage);
            if(pg.img != null) {
                hasImg = true;
            }
        }
        if(!hasImg) {
            if(rendered) {
                layCode.show(pnlCode, "Source");
                rendered = !rendered;
            }
            txtSource.focus();
        }
        updateModelLabelProgressBar();
    }

    private void setVisibleComponent() {
        boolean hasImg = false;
        if(model.curExample != -1) {
            Ex ex = cur();
            Pg pg = ex.pages.get(model.curPage);
            if(pg.img != null) {
                hasImg = true;
            }
        }
        if(!hasImg) {
            if(rendered) {
                layCode.show(pnlCode, "Rendered");
            } else {
                layCode.show(pnlCode, "Source");
            }
        } else {
            layCode.show(pnlCode, "Image");
        }
    }


    ////////////
    // UPDATE //
    ////////////

    private void updateView() {
        updateLabels();
        updateCodeOrImage();
        updateModelLabelProgressBar();
        spSource.repaint();
        spRendered.repaint();
    }

    private void updateNav() {
        mdlNav.clear();
        int i = 0;
        for(Ex ex : model.examples) {
            mdlNav.addElement(new ExWrapper(i, ex));
            i++;
        }
        lblExamples.setText("Examples (" + model.examples.size() + ")");
    }

    private void updateLabels() {
        if(model.curExample == -1) {
            pnlTop.setBackground(Lay.clr("100"));
            pnlBot.setBackground(Lay.clr("100"));
            lblTop.setText(" ");
            lblBot.setText(" ");
        } else {
            Ex ex = cur();
            Pg pg = ex.pages.get(model.curPage);
            pnlTop.setBackground(pg.status.getColor());
            pnlBot.setBackground(pg.status.getColor());
            lblTop.setText(pg.msg);
            lblBot.setText(pg.msg);
        }
    }

    private void updateCodeOrImage() {
        if(model.curExample == -1) {
            txtSource.clear();
            txtSource.setEditable(false);
            txtRendered.clear();
        } else {
            Ex ex = cur();
            Pg pg = ex.pages.get(model.curPage);
            if(pg.img == null) {
                String src = pg.src;
                txtSource.setText(src, true);
                txtSource.setEditable(true);
                setRendered(src);
            } else {
                pnlImage.setImage(pg.img);
            }
            setVisibleComponent();
        }
    }

    private void setRendered(String src) {
        txtRendered.clear();
        try {
            txtRendered.appendHtmlLike(src);
            txtRendered.setCaretPosition(0);
        } catch(Exception e2) {
            txtRendered.append("(Cannot Parsing Source)");
        }
    }

    private void updateModelLabelProgressBar() {
        String s1 = "Examples: ";
        String s2 = "";
        if(model.curExample == -1) {
            s1 += "NONE";

            lblModel.setText(s1);
            lblModel.setVisible(!rendered);
            lblModel2.setVisible(false);
        } else {
            s1 += (model.curExample + 1) + "/" + model.examples.size();
            s2 += "Pages: " + (model.curPage + 1) + "/" + cur().pages.size();

            lblModel.setText(s1);
            lblModel2.setText(s2);

            lblModel.setVisible(!rendered);
            lblModel2.setVisible(!rendered);
        }
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private class MyTextArea extends RTextArea {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(ImageLib.get(CommonConcepts.EDIT).getImage(), 2, 2, null);
        }
    }

    private class ExWrapper {
        int i;
        Ex ex;
        public ExWrapper(int i, Ex ex) {
            this.i = i;
            this.ex = ex;
        }
        @Override
        public String toString() {
            return (i + 1) + ":" + (ex.title == null ? "" : " " + ex.title);
        }
    }

    private class ImagePanel extends RPanel {
        private String img;
        public ImagePanel() {
            setBackground(Lay.clr("EFFEFF"));
        }
        public void setImage(String img) {
            this.img = img;
            repaint();
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if(albumPath != null && img != null) {
                File imageDir = new File(albumPath.getParentFile(), "images");
                File imageFile = new File(imageDir, img);
                try {
                    BufferedImage bi;
                    bi = ImageIO.read(imageFile);
                    g.drawImage(bi, 0, 0, null);
                } catch(IOException e) {
                }
            }
        }
    }

    public class ExWrapperRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            ExWrapper e = (ExWrapper) value;
            if(e.ex.status != Status.PENDING) {
                lbl.setForeground(e.ex.status.getColor());
            } else {

            }
            return this;
        }
    }
}
