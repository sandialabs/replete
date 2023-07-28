package replete.ui.web;

import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.net.URL;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import replete.event.ChangeNotifier;
import replete.numbers.NumUtil;
import replete.ui.button.IconButton;
import replete.ui.combo.RecentComboBox;
import replete.ui.images.RepleteImageModel;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.lay.Lay;
import replete.ui.tabbed.RNotifPanel;
import replete.ui.text.RLabel;
import replete.ui.windows.Dialogs;

public class BrowserPanel extends RNotifPanel {


    ////////////
    // FIELDS //
    ////////////

    private JavaFxBrowserPanel pnlBrowser;
    private RecentComboBox<String> cboAddress;
    private DefaultComboBoxModel<String> mdlAddress;
    private String url;
    private String lastHtml;
    private RLabel lblStatus;
    private Throwable lastError;
    private boolean inProg;
    private double pct;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public BrowserPanel() {
        this((String) null);
    }
    public BrowserPanel(URL initialUrl) {
        this(initialUrl == null ? null : initialUrl.toString());
    }
    public BrowserPanel(String initialUrl) {
        IconButton btnAccept;
        JLabel lblLink;
        Lay.BLtg(this,
            "N", Lay.BL(
                "W", lblLink = Lay.lb(CommonConcepts.LINK, "font=Helvetica,size=14,eb=7r"),
                "C", cboAddress = Lay.cb(
                    mdlAddress = new DefaultComboBoxModel<>(),
                    "recent,selectall,editable,size=16,visrows=10"
                ),
                "E", Lay.FL(
                    lblStatus = Lay.lb(CommonConcepts.HELP, "ttt=Preparing..."),
                    Lay.hs(3),
                    btnAccept = (IconButton) Lay.btn(CommonConcepts.ACCEPT),
                    "hgap=0,eb=5l"
                ),
                "eb=5,augb=mb(2b,black)"
            ),
            "C", pnlBrowser = new JavaFxBrowserPanel()
        );

        cboAddress.setEditable(true);
        btnAccept.toImageOnly();
        lblLink.setDisplayedMnemonic(KeyEvent.VK_D);
        lblLink.setLabelFor(cboAddress);
        btnAccept.setFocusable(false);

        btnAccept.addActionListener(e -> {
            Platform.runLater(new Runnable() {
                public void run() {
                    WebEngine engine = pnlBrowser.getEngine();
                    lastHtml = (String) engine.executeScript("document.documentElement.outerHTML");
                    fireAcceptNotifier();
                }
            });
        });

        pnlBrowser.addInitListener(e -> fireInitNotifier());
        pnlBrowser.addInitListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                addEngineListeners();
            }
        });

        if(initialUrl != null) {
            mdlAddress.addElement(initialUrl);
            pnlBrowser.navigate(initialUrl);
            url = initialUrl;
        }

        cboAddress.focus();

        cboAddress.addAcceptanceListener(e -> {
            navigate(cboAddress.getSelected().trim());
        });
        pnlBrowser.addNavigationListener(e -> {
            String newUrl = pnlBrowser.getUrl();
            mdlAddress.removeElement(newUrl);
            mdlAddress.insertElementAt(newUrl, 0);
            cboAddress.setSelectedIndex(0);
            cboAddress.getTextField().setText(newUrl);
            cboAddress.getTextField().selectAll();
            url = newUrl;
            fireUrlChangeNotifier();
        });
    }

    public void navigate(String urlText) {
        if(!urlText.matches("^[a-z]+://.*$")) {
            urlText = "http://" + urlText;
        }
        pnlBrowser.navigate(urlText);
    }

    public void refresh() {
        navigate(url);
    }

    public WebView getView() {
        return pnlBrowser.getView();
    }
    public WebEngine getEngine() {
        return pnlBrowser.getEngine();
    }

    public String getUrl() {
        return url;
    }
    public String getLastHtml() {
        return lastHtml;
    }


    //////////
    // MISC //
    //////////

    public void scrollToBottom() {
        pnlBrowser.scrollToBottom();
    }

    protected void setProgress(boolean inProg, double pct) {
        this.inProg = inProg;
        this.pct = pct;
        rebuildStatusLabel();
    }

    protected void setError(Throwable e) {
        lastError = e;
        rebuildStatusLabel();
        lblStatus.setIcon(ImageLib.get(CommonConcepts.ERROR));
        lblStatus.setToolTipText("Error");
    }

    private void rebuildStatusLabel() {
        ImageModelConcept concept;
        if(lastError != null) {
            concept = CommonConcepts.ERROR;
        } else if(inProg) {
            concept = RepleteImageModel.MANUAL_RESULTS_INP;
        } else {
            concept = RepleteImageModel.MANUAL_RESULTS_CMPL;
        }
        lblStatus.setIcon(concept);

        String ttt = inProg ? "Page Loading..." : "Page Loaded";
        if(lastError != null) {
            ttt += " (with error)";
        }

        lblStatus.setToolTipText(ttt);
        lblStatus.setText(pct >= 0 && pct <= 1.0 && inProg ? NumUtil.pct(pct, 1.0) : "");

        if(lastError != null) {
            lblStatus.addMouseListener(statusErrorMouseListener);
            lblStatus.setCursorHand();
        } else {
            lblStatus.removeMouseListener(statusErrorMouseListener);
            lblStatus.setCursorDefault();
        }
    }

    private MouseListener statusErrorMouseListener = new MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent e) {
            Dialogs.showDetails(getWindow(), "An error has occurred loading this page.", "Browsing Error", lastError);
        };
    };

    protected void addEngineListeners() {
        final WebEngine engine = pnlBrowser.getEngine();
        Worker<Void> loadWorker = engine.getLoadWorker();
        loadWorker.progressProperty().addListener(
            new ChangeListener<Number>() {
                public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
                    setProgress(true, newValue.doubleValue());
                }
            }
        );

        loadWorker.exceptionProperty().addListener(
            new ChangeListener<Throwable>() {
                public void changed(ObservableValue<? extends Throwable> ov, Throwable oldValue, final Throwable newValue) {
                    setError(newValue);
                }
            }
        );

        loadWorker.stateProperty().addListener(stateListener);
    }

    private ChangeListener<State> stateListener = new ChangeListener<State>() {
        public void changed(ObservableValue<? extends State> ov, State oldValue, State newValue) {
            if(newValue == Worker.State.RUNNING) {
                setProgress(true, 0.0);
            } else if(newValue == Worker.State.SUCCEEDED) {
                setProgress(false, 1.00);
            }
        }
    };


    ///////////////
    // NOTIFIERS //
    ///////////////

    private ChangeNotifier initNotifier = new ChangeNotifier(this);
    public void addInitListener(javax.swing.event.ChangeListener listener) {
        initNotifier.addListener(listener);
    }
    private void fireInitNotifier() {
        initNotifier.fireStateChanged();
    }

    private ChangeNotifier urlChangeNotifier = new ChangeNotifier(this);
    public void addUrlChangeListener(javax.swing.event.ChangeListener listener) {
        urlChangeNotifier.addListener(listener);
    }
    private void fireUrlChangeNotifier() {
        urlChangeNotifier.fireStateChanged();
    }

    private ChangeNotifier acceptNotifier = new ChangeNotifier(this);
    public void addAcceptListener(javax.swing.event.ChangeListener listener) {
        acceptNotifier.addListener(listener);
    }
    private void fireAcceptNotifier() {
        acceptNotifier.fireStateChanged();
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
//        System.setProperty("http.proxyHost", "proxy.sandia.gov");
//        System.setProperty("http.proxyPort", "80");

        Lay.BLtg(Lay.fr("Browser Panel Test"),
            "C", new BrowserPanel(),
            "size=600,center,visible"
        );

//        DefaultComboBoxModel<String> x = new DefaultComboBoxModel<>(new String[] {"a", "b", "c"});
//        RComboBox<String> c = new RComboBox<>(x);
//        c.getTextField().addActionListener(e -> {
////            System.out.println("TXTACT");
//        });
//        c.setEditable(true);
//        c.addActionListener(e -> {
////            System.out.println("ACTION: " + c.getSelected() + " " + c.getSelectedIndex());
//        });
//        c.addItemListener(e -> {
////            System.out.println("ITEM:   " + e.getStateChange() + " " + e.getItem() + " " + c.getSelectedIndex());
//        });
//        c.addMouseListener(new DebugMouseListener());
//        c.addAcceptanceListener(e -> {
//            System.out.println("ACC");
//        });
//        Component[] components = c.getComponents();
//        for(Component component : components) {
//            if(component instanceof JButton) {
////                ((JButton) component).addActionListener(e -> System.out.println("BTN CLICK"));
//            }
////            if(component instanceof CellRendererPane) {
////                DebugUtil.printObjectDetails(component);
////                component.addMouseListener(new DebugMouseListener());
////            }
////            component.addMouseMotionListener(new DebugMouseMotionListener());
//        }
//        c.addPopupMenuListener(new PopupMenuListener() {
//
//            @Override
//            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
////                System.out.println(" -> VIS " + c.isPopupVisible());
//            }
//
//            @Override
//            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
////                System.out.println(" -> INVIS " + c.isPopupVisible());
//            }
//
//            @Override
//            public void popupMenuCanceled(PopupMenuEvent e) {
////                System.out.println(" -> CAN");
//            }
//        });
//        Lay.BLtg(Lay.fr("aasdfsf"),
//            "N", c,
//            "size=200,loc=[400,200],visible"
//        );
    }

    // [F] Typing ENTER in text field for an item already in the list: ACTION: <item> 0
    // [F] Typing in an item in the list (diff from last val):         ITEM: 2 b,    ITEM:   1 a,     ACTION: a,     ACTION: a
    // [F] Typing in an item not in the list:                          ITEM: 2 b -1, ITEM:   1 bx -1, ACTION: bx -1, ACTION: bx -1
    // [F] Expanding list, clicking on one that is already in the text field: ACTION: <item> 0
    // [F] Expanding list, selecting one not in the text field: ITEM:   2 b 0, ITEM:   1 a 0, ACTION: a 0
    //     Expanding list, hovering over items: <no events!>
    //     Typing DOWN opens list, highlighting last selected item, regardless of text: NO EVENT
    //      - Typing UP or DOWN after that:                     ITEM:   2 c, ITEM:   1 b, ACTION: b
}
