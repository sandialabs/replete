package finio.ui.multidlg;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

import finio.plugins.FinioPluginManager;
import finio.plugins.platform.FinioPlugin;
import finio.ui.images.FinioImageModel;
import gov.sandia.webcomms.http.Http;
import replete.plugins.ExtensionPoint;
import replete.plugins.PluginManager;
import replete.ui.GuiUtil;
import replete.ui.fc.RFileChooser;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.tabbed.RTabbedPane;
import replete.ui.windows.Dialogs;
import replete.ui.windows.ExceptionDetails;
import replete.ui.windows.escape.EscapeDialog;
import replete.util.User;
import replete.xstream.XStreamWrapper;

public class MultiInputChooserDialog extends EscapeDialog {


    ////////////
    // FIELDS //
    ////////////

    // Cheat so clients don't have to keep track of their own selected
    // panels (which is actually the correct design to implement)
    private static String selectedInputPanelClass = null;
    public static String getSelectedInputPanelClass() {
        return selectedInputPanelClass;
    }
    public static void setSelectedInputPanelClass(String selectedInputPanelClass) {
        MultiInputChooserDialog.selectedInputPanelClass = selectedInputPanelClass;
    }

    // Constants

    public static final int ACCEPT = 0;     // Result
    public static final int CANCEL = 1;

    // UI

    private int result = CANCEL;

    private RTabbedPane tabs;
    private InputBundleValidator validator;
    private InputBundle[] cachedDataBundles;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public MultiInputChooserDialog(Dialog dialog, String title) {
        super(dialog, title, true);
        init();
    }
    public MultiInputChooserDialog(Frame frame, String title) {
        super(frame, title, true);
        init();
    }

    private void init() {
        setIcon(FinioImageModel.JSON);

        Lay.BLtg(this,
            "C", tabs = Lay.TBL(
                "borders"
            ),
            "bg=100,size=[700,500],center"
        );

        tabs.addChangeListener(e -> {
            InputSourcePanel pnlSelected = tabs.getSelectedComp();
            pnlSelected.postActivate();
        });

        int selectedIndex = 0;
        List<ExtensionPoint> exts = PluginManager.getExtensionsForPoint(InputSourcePanelCreator.class);
        if(exts.size() != 0) {
            for(ExtensionPoint ext : exts) {
                InputSourcePanelCreator inputPanelCreator = (InputSourcePanelCreator) ext;
                InputSourcePanel pnlInput = inputPanelCreator.createPanel(this);
                pnlInput.addAcceptListener(e -> accept());
                pnlInput.addCancelListener(e -> closeDialog());
                if(pnlInput.getClass().getName().equals(selectedInputPanelClass)) {
                    selectedIndex = tabs.getTabCount();
                }
                tabs.addTab(pnlInput.getTitle(), pnlInput.geIcon(), pnlInput);
            }
        }

        int fIndex = selectedIndex;
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                InputSourcePanel pnlInput = (InputSourcePanel) tabs.getSelectedComp();
                pnlInput.focus();
                tabs.setSelectedIndex(fIndex);
                pnlInput = (InputSourcePanel) tabs.getSelectedComp();
                pnlInput.focus();
            }
        });

        addAttemptToCloseListener(e -> {
            for(int i = 0; i < tabs.getTabCount(); i++) {
                InputSourcePanel pnlInput = tabs.getCompAt(i);
                pnlInput.cleanUp();
            }
            InputSourcePanel pnlInput = tabs.getSelectedComp();
            selectedInputPanelClass = pnlInput.getClass().getName();
        });
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public int getResult() {
        return result;
    }
    private void clearCachedDataBundles() {
        cachedDataBundles = null;
    }
    public InputBundle[] getDataBundles() {
        if(cachedDataBundles != null) {
            return cachedDataBundles;
        }
        InputSourcePanel pnlSelected = tabs.getSelectedComp();
        cachedDataBundles = pnlSelected.getDataBundles();
        if(cachedDataBundles == null) {
            throw new IllegalStateException("Invalid mode detected.");
        }
        return cachedDataBundles;
    }
    private void accept() {
        clearCachedDataBundles();

        InputBundle[] bundles = getDataBundles();

        if(validator != null) {
            InputBundleValidationProblem[] problems = validator.validate(bundles);
            if(problems.length != 0) {
                String message;
                boolean useDetails = false;
                if(bundles.length == 1) {
                    message = "There is a problem with the selected input:";
                    useDetails = true;
                } else if(problems.length == 1) {
                    message = "One of the selected inputs had problems:";
                    useDetails = true;
                } else {
                    message = "Multiple selected inputs had problems:";
                }
                for(InputBundleValidationProblem prob : problems) {
                    message += "\n\n" + prob.getDataBundle().getLabel() +
                        "\n    " + prob.getError().getMessage();
                }
                if(useDetails) {
                    Dialogs.showDetails(this,
                        new ExceptionDetails()
                            .setError(problems[0].getError())
                            .setMessage(message)
                            .setTitle("Parse Error")
                    );
                } else {
                    Dialogs.showWarning(this, message, "Parse Error");
                }
                return;
            }
        }
        result = ACCEPT;
        closeDialog();
    }

    public <T extends InputSourcePanel> T getInputPanel(Class<? extends InputSourcePanel> clazz) {
        return (T) tabs.getFirstCompOfType(clazz);
    }

    public MultiInputChooserDialog setValidator(InputBundleValidator validator) {
        this.validator = validator;
        return this;
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) throws IOException {
        Http.getInstance().useSandiaProxy();
        File prefs = createPrefsFile();

        Http.getInstance().setUserAgent(
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.83 Safari/537.1");

        PluginManager.initialize(FinioPlugin.class);
        FinioPluginManager.initialize();

        List<File> recentFiles = null;
        List<String> recentUrls = null;
        String lastUrl = "";
        try {
            Object[] recentLists = XStreamWrapper.loadTarget(prefs);
            MultiInputChooserDialog.setSelectedInputPanelClass((String) recentLists[0]);
            recentFiles = (List<File>) recentLists[1];
            recentUrls = (List<String>) recentLists[2];
            lastUrl = (String) recentLists[3];
        } catch(Exception e) {
            e.printStackTrace();
        }

        if(recentFiles == null) {
            recentFiles = new ArrayList<>();
        }
        if(recentUrls == null) {
            recentUrls = new ArrayList<>();
        }

        if(recentFiles.isEmpty()) {
            recentFiles.add(User.getDesktop());
        }
        if(recentUrls.isEmpty()) {
            recentUrls.add("http://www.procato.com/my+headers/");
        }

        MultiInputChooserDialog dialog =
            new MultiInputChooserDialog((Dialog) null, "Import");
        dialog.setIcon(CommonConcepts.OPEN);

        RFileChooser chooser = RFileChooser.getChooser(true, JFileChooser.DIRECTORIES_ONLY);
        RFileChooser.whiteCombos(chooser);
        GuiUtil.addMnemonics(chooser, RFileChooser.basicCaptions);

        FileInputSourcePanel pnlFileInput = dialog.getInputPanel(FileInputSourcePanel.class);
        pnlFileInput.setChooser(chooser);
        pnlFileInput.getChooser().setRecentList(recentFiles);

        WebInputSourcePanel pnlWebInput = dialog.getInputPanel(WebInputSourcePanel.class);
        pnlWebInput.setRecentList(recentUrls);
        pnlWebInput.setUrl(lastUrl);

        dialog.setVisible(true);
        if(dialog.getResult() == MultiInputChooserDialog.ACCEPT) {
            InputBundle[] bundles = dialog.getDataBundles();
            for(InputBundle bundle : bundles) {
                System.out.println(bundle);
            }
        }

        Object[] recentLists = new Object[] {
            MultiInputChooserDialog.getSelectedInputPanelClass(),
            pnlFileInput.getChooser().getRecentList(),
            pnlWebInput.getRecentList(),
            pnlWebInput.getUrl()
        };

        XStreamWrapper.writeToFile(recentLists, prefs);
    }
    private static File createPrefsFile() {
        File replete = User.getHome(".replete");
        replete.mkdirs();
        return new File(replete, "multidlg.xml");
    }
}
