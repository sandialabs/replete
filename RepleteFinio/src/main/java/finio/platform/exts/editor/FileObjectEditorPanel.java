package finio.platform.exts.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;

import finio.ui.actions.validation.AActionValidator;
import finio.ui.actions.validation.SpecificTypeValueActionValidator;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import gov.sandia.orbweaver.OrbweaverAppMain;
import gov.sandia.orbweaver.ui.OrbweaverFrame;
import gov.sandia.orbweaver.ui.images.OrbweaverImageModel;
import replete.ui.GuiUtil;
import replete.ui.button.RButton;
import replete.ui.fc.RFileChooser;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.UIAction;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;
import replete.ui.uiaction.UIActionPopupMenu;
import replete.util.OsUtil;

public class FileObjectEditorPanel extends StringObjectEditorPanel {


    ////////////
    // FIELDS //
    ////////////

    // Model

    private File F;

    // UI

    private AppContext ac;
    private RButton btnFileOpen;
    private RButton btnDirOpen;
    private RButton btnOptions;

    private ROActionMap actionMap;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public FileObjectEditorPanel(AppContext ac) {
        this.ac = ac;

        actionMap = new ROActionMap();

        btnFileOpen = Lay.btn(FinioImageModel.SET_FILE, "icon");
        btnDirOpen = Lay.btn(FinioImageModel.SET_DIR, "icon");
        btnOptions = Lay.btn(CommonConcepts.OPTIONS, "icon");

        btnFileOpen.setToolTipText("Set To File...");
        btnDirOpen.setToolTipText("Set To Directory...");
        btnOptions.setToolTipText("File Actions");

        add(Lay.p(btnFileOpen, "eb=2l"));
        add(Lay.p(btnDirOpen, "eb=5l"));
        add(Lay.p(btnOptions, "eb=5l"));

        btnFileOpen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RFileChooser chooser = RFileChooser.getChooser("Choose File Object");
                chooser.setSelectedFile((File) getObject());
                chooser.setIcon(CommonConcepts.FILE);
                JFrame parent = GuiUtil.fra(FileObjectEditorPanel.this);
                if(chooser.showOpen(parent)) {
                    F = chooser.getSelectedFile();
                    setObject(F);
                }
            }
        });

        btnDirOpen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RFileChooser chooser =
                    RFileChooser.getChooser("Choose Directory Object",
                        JFileChooser.DIRECTORIES_ONLY);
                chooser.setSelectedFile((File) getObject());
                chooser.setIcon(CommonConcepts.OPEN);
                JFrame parent = GuiUtil.fra(FileObjectEditorPanel.this);
                if(chooser.showOpen(parent)) {
                    F = chooser.getSelectedFile();
                    setObject(F);
                }
            }
        });

        btnOptions.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                JPopupMenu mnuPopup = new UIActionPopupMenu(actionMap);
                mnuPopup.show(btnOptions, e.getX(), e.getY());
            }
        });
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void setObject(Object O) {
        super.setObject(O);
        F = (File) O;
    }

    @Override
    protected String convertObjectToString(Object O) {
        File F = (File) O;
        return F.getAbsolutePath();
    }

    @Override
    public Object getObject() {
        if(txt.isShowNull()) {
            return null;
        }
        return new File(txt.getText());
    }

    @Override
    public boolean isValidState() {
        return true;                       // TODO: No validation performed here yet
    }

    @Override
    public boolean isReturnsNewObject() {
        return true;                       // File objects are immutable
    }

    @Override
    public boolean allowsEdit() {
        return true;
    }


    /////////////////
    // INNER CLASS //
    /////////////////

    public class ATreeActionListener implements UIActionListener {
        private Runnable runnable;
        public ATreeActionListener(Runnable runnable) {
            this.runnable = runnable;
        }
        @Override
        public void actionPerformed(ActionEvent e, UIAction action) {
            runnable.run();
        }
    }

    private class ROActionMap extends UIActionMap {
        public ROActionMap() {

            // Open Actions

            createAction("open")
                .addDescriptor(
                    new PopupMenuActionDescriptor()
                        .setText("&Open")
                        .setLabelMenu(true));

            Runnable runnable = new Runnable() {
                public void run() {
                    File F = (File) getObject();
                    OsUtil.openExplorer(F);
                }
            };
            String explorer = OsUtil.isWindows() ? "Windows Explorer" : "Finder";
            AActionValidator validator = new SpecificTypeValueActionValidator(ac, File.class);
            createAction("windows-explorer", new ATreeActionListener(runnable), validator)
                .addDescriptor(
                    new PopupMenuActionDescriptor()
                        .setPath("open")
                        .setText("In " + explorer)
                        .setIcon(CommonConcepts.FILE_NAVIGATOR));

            runnable = new Runnable() {
                public void run() {
                    File F = (File) getObject();
                    if(F.isFile()) {
                        File parentFile = F.getParentFile();
                        if(parentFile != null) {
                            F = parentFile;
                        }
                    }
                    OsUtil.openTerminal(F);
                }
            };
            String term = OsUtil.isWindows() ? "Command Prompt" : "Terminal";
            validator = new SpecificTypeValueActionValidator(ac, File.class);
            createAction("console", new ATreeActionListener(runnable), validator)
                .addDescriptor(
                    new PopupMenuActionDescriptor()
                        .setPath("open")
                        .setText("In " + term)
                        .setIcon(CommonConcepts.CONSOLE));

            runnable = new Runnable() {
                public void run() {
                    OsUtil.openSystemEditor((File) getObject());
                }
            };
            validator = new SpecificTypeValueActionValidator(ac, File.class);
            createAction("system-editor", new ATreeActionListener(runnable), validator)
                .addDescriptor(
                    new PopupMenuActionDescriptor()
                        .setPath("open")
                        .setText("With System Editor")
                        .setIcon(CommonConcepts.SYSTEM));

            runnable = new Runnable() {
                public void run() {
                    OrbweaverFrame frame = OrbweaverAppMain.getOrbFrame();
                    frame.getModel().addDocumentTab((File) getObject());
                    frame.setVisible(true);
                }
            };
            validator = new SpecificTypeValueActionValidator(ac, File.class);
            createAction("orbweaver-browser", new ATreeActionListener(runnable), validator)
                .addDescriptor(
                    new PopupMenuActionDescriptor()
                        .setPath("open")
                        .setText("With Orbweaver")
                        .setIcon(OrbweaverImageModel.ORBWEAVER_LOGO));
        }
    }
}
