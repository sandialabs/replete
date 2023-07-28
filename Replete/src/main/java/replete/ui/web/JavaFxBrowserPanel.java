package replete.ui.web;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.lang.reflect.Field;
import java.net.URL;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;

import com.sun.webkit.WebPage;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import replete.event.ChangeNotifier;
import replete.ui.ColorLib;
import replete.ui.GuiUtil;
import replete.ui.button.RButton;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.text.DocumentChangeListener;
import replete.ui.text.RTextField;
import replete.util.SystemUtil;

//http://www.javaworld.com/community/node/8270
//http://docs.oracle.com/javafx/2/webview/jfxpub-webview.htm
//http://docs.oracle.com/javafx/2/api/javafx/scene/web/WebEngine.html
//http://docs.oracle.com/javafx/2/swing/swing-fx-interoperability.htm#CHDIEEJE

// WebView/WebEngine specific proxies:
// https://stackoverflow.com/questions/24691472/set-proxy-on-javafx-webengine
// https://stackoverflow.com/questions/28152323/javafx-webview-set-proxy
// https://gist.github.com/thomasdarimont/310c3839bc63501680e6#file-urlstreamfactorycustomizer-java-L59
// Not sure if it's possible.

public class JavaFxBrowserPanel extends JPanel {


    ////////////
    // FIELDS //
    ////////////

    private JFXPanel      pnlFx;
    private BrowserRegion browserRegion;
    private RTextField    txtFind;
    private RButton       btnNext;
    private RButton       btnPrev;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public JavaFxBrowserPanel() {
        Platform.setImplicitExit(false);

        Lay.BLtg(this,
            "N", Lay.FL("R",
                Lay.lb(CommonConcepts.SEARCH, "Find:"),
                txtFind = Lay.tx("", "prefw=75, prefh=26"),
                btnPrev = Lay.btn(CommonConcepts.COLLAPSE_UP, "prefw=26, prefh=26, bg=EEEEEE"),
                btnNext = Lay.btn(CommonConcepts.COLLAPSE_DOWN, "prefw=26, prefh=26, bg=EEEEEE")
            ),
            "C", pnlFx = new JFXPanel()
        );

        txtFind.setBackground(Color.white);
        btnPrev.setEnabled(false);
        btnNext.setEnabled(false);

        txtFind.getDocument().addDocumentListener(new DocumentChangeListener() {
            @Override
            public void documentChanged(DocumentEvent e) {
                update();
            }
        });

        txtFind.addActionListener(e -> find(Direction.FORWARD));
        btnNext.addActionListener(e -> find(Direction.FORWARD));
        btnPrev.addActionListener(e -> find(Direction.BACKWARD));

        Platform.runLater(() -> initFX(pnlFx));
    }

    public void update() {                                                              //This enables/disables the search buttons
        String input = txtFind.getText();
        boolean enabled = !input.isEmpty();
        btnPrev.setEnabled(enabled);
        btnNext.setEnabled(enabled);
        switchFindColor(Color.white);
    }

    private void find(Direction dir) {
        Platform.runLater(() -> {
            boolean success = browserRegion.search(txtFind.getText(), true, dir);
            switchFindColor(success ? Color.white : ColorLib.RED_LIGHT);
       });
    }

    protected void switchFindColor(Color clr) {
        SwingUtilities.invokeLater(() -> txtFind.setBackground(clr));
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if(browserRegion != null &&
                browserRegion.engine != null &&
                !browserRegion.engine.isJavaScriptEnabled()) {
            drawWarningMsg(g, true);
            drawWarningMsg(g, false);
        }
    }

    private void drawWarningMsg(Graphics g, boolean upperLeft) {
        String msg = "JS Disabled";
        g.setFont(new Font("Monospaced", Font.BOLD, 12));
        g.setColor(Color.red);
        int w = GuiUtil.stringWidth(g, msg) + 3 + 3;
        int h = GuiUtil.stringHeight(g);
        int rt = getHeight() - h;
        int y = upperLeft ? 0 : rt;
        g.fillRect(0, y, w, h);
        g.setColor(Color.black);
        g.drawRect(0, y, w - 1, h - 1);
        g.setColor(Color.white);
        g.drawString(msg, 3, y + 12);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors (Computed)

    public WebView getView() {
        return browserRegion.view;
    }
    public WebEngine getEngine() {
        return browserRegion.engine;
    }
    public String getUrl() {
        return browserRegion.engine.locationProperty().get();
    }
    public boolean isJavaScriptEnabled() {
        return
            browserRegion != null &&
            browserRegion.engine != null &&
            browserRegion.engine.isJavaScriptEnabled()
        ;
    }

    // Mutators

    public void navigate(URL url) {
        navigate(url.toString());
    }
    public void navigate(final String url) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                browserRegion.engine.load(url);
            }
        });
    }
    public void setHTMLContent(final String html) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                browserRegion.engine.loadContent(html);
            }
        });
    }
    public void load(String uri) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                browserRegion.engine.load(uri);
            }
        });
    }
    public void setJavaScriptEnabled(boolean enabled) {
        if(browserRegion != null && browserRegion.engine != null) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    browserRegion.engine.setJavaScriptEnabled(enabled);
                    GuiUtil.safe(new Runnable() {
                        @Override
                        public void run() {
                            JavaFxBrowserPanel.this.repaint();
                        }
                    });
                }
            });
        }
    }


    //////////
    // MISC //
    //////////

    private void initFX(JFXPanel fxPanel) {
        browserRegion = new BrowserRegion();
        Scene scene = new Scene(browserRegion);
        pnlFx.setScene(scene);

        fireInitNotifier();
    }

    public void scrollToBottom() {
        browserRegion.scrollToBottom();
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private class BrowserRegion extends Region {


        ////////////
        // FIELDS //
        ////////////

        private WebView view = new WebView();
        private WebEngine engine = view.getEngine();


        //////////////////
    // CONSTRUCTORS //
    //////////////////

        public BrowserRegion() {
            view = new WebView();
            engine = view.getEngine();

            if(SystemUtil.isSystemPropertySet("nojs")) {
                engine.setJavaScriptEnabled(false);
            }

            getStyleClass().add("browser");        // Apply the styles
            getChildren().add(view);               // Add the web view to the scene
            addDebugListeners();
        }


        //////////
        // MISC //
        //////////

        public boolean search(String query, boolean wrapSearch, Direction dir) {
            try {
                Field pageField = engine.getClass().getDeclaredField("page");
                pageField.setAccessible(true);                                                      //Uses reflection to get page, which
                WebPage page = (WebPage)pageField.get(engine);                                      //has private find function, which
                synchronized(this) {                                                                //finds and highlights text.
                    boolean isTrue = page.find(query, dir == Direction.FORWARD, wrapSearch, false);
                    return isTrue;
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        public void scrollToBottom() {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    engine.executeScript("window.scrollTo(0, document.body.scrollHeight);");
                }
            });
        }

        private void addDebugListeners() {
            engine.locationProperty().addListener(new ChangeListener<String>(){
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//                    System.out.println("changed " + oldValue + " " + newValue);
                    fireNavigationNotifier();
                }
            });

            final Worker<Void> loadWorker = engine.getLoadWorker();
//            loadWorker.stateProperty().addListener(
//                new ChangeListener<State>() {
//                    public void changed(ObservableValue<? extends State> ov, State oldValue, State newValue) {
//                        if(newValue == Worker.State.SUCCEEDED) {
//                            Document doc = engine.getDocument();
//                            try {
////                                Transformer transformer = TransformerFactory.newInstance().newTransformer();
////                                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
////                                transformer.setOutputProperty(OutputKeys.METHOD, "xml");
////                                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
////                                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
////                                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
//
////                                transformer.transform(new DOMSource(doc),
////                                        new StreamResult(new OutputStreamWriter(System.out, "UTF-8")));
//                            } catch (Exception ex) {
//                                ex.printStackTrace();
//                            }
//                        }
//                    }
//                });
            loadWorker.exceptionProperty().addListener(
                new ChangeListener<Throwable>() {
                    @Override
                    public void changed(ObservableValue<? extends Throwable> ov,
                                        Throwable oldValue,
                                        Throwable newValue)
                    {
                        System.err.printf("Exception changed, old: %s, new: %s%n",
                                oldValue, newValue);
                    }
                }
            );
        }


        ////////////////
        // OVERRIDDEN //
        ////////////////

        @Override
        protected void layoutChildren() {
            double w = getWidth();
            double h = getHeight();
            layoutInArea(view,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
        }
    }

    private enum Direction {
        FORWARD,
        BACKWARD
    }


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

    protected ChangeNotifier navigationNotifier = new ChangeNotifier(this);
    public void addNavigationListener(javax.swing.event.ChangeListener listener) {
        navigationNotifier.addListener(listener);
    }
    protected void fireNavigationNotifier() {
        navigationNotifier.fireStateChanged();
    }
}
