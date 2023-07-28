package replete.ui.windows.common;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;
import replete.event.ExtChangeNotifier;
import replete.threads.SwingTimerManager;
import replete.ui.GuiUtil;
import replete.ui.cursors.CursorUtil;
import replete.ui.debug.DebugComponentListener;
import replete.ui.images.concepts.ImageLib;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.uiaction.UIActionMenuBar;


/**
 * @author Derek Trumbo
 */

public class RFrame extends JFrame implements RWindow, ChildWindowManager {


    ////////////
    // FIELDS //
    ////////////

    private ChangeNotifier waitOnNotifier = new ChangeNotifier(this);
    private ChangeNotifier waitOffNotifier = new ChangeNotifier(this);
    private ChangeNotifier closingNotifier = new ChangeNotifier(this);
    private ExtChangeNotifier<RWindowClosingListener> attemptToCloseNotifier = new ExtChangeNotifier<>();
    private ChildWindowManager cwManager = new DefaultChildWindowManager();
    private Dimension lastSize = null;
    private boolean shutdownSwingTimersOnClose = false;
    private boolean printSize = false;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RFrame() {
        init(null);
    }
    public RFrame(String title) {
        super(title);
        init(null);
    }
    public RFrame(GraphicsConfiguration gc) {
        super(gc);
        init(null);
    }
    public RFrame(String title, GraphicsConfiguration gc) {
        super(title, gc);
        init(null);
    }
    public RFrame(ImageIcon icon) {
        init(icon);
    }
    public RFrame(String title, ImageIcon icon) {
        super(title);
        init(icon);
    }
    public RFrame(ImageIcon icon, GraphicsConfiguration gc) {
        super(gc);
        init(icon);
    }
    public RFrame(String title, ImageIcon icon, GraphicsConfiguration gc) {
        super(title, gc);
        init(icon);
    }
    public RFrame(ImageModelConcept concept) {
        init(ImageLib.get(concept));
    }
    public RFrame(String title, ImageModelConcept concept) {
        super(title);
        init(ImageLib.get(concept));
    }
    public RFrame(ImageModelConcept concept, GraphicsConfiguration gc) {
        super(gc);
        init(ImageLib.get(concept));
    }
    public RFrame(String title, ImageModelConcept concept, GraphicsConfiguration gc) {
        super(title, gc);
        init(ImageLib.get(concept));
    }

    private void init(ImageIcon icon) {
        if(icon != null) {
            setIcon(icon);
        }
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent arg0) {
                close();
            }
            @Override
            public void windowOpened(WindowEvent e) {
                ensureOnScreen();
            }
            @Override
            public void windowActivated(WindowEvent e) {
                ensureOnScreen();
            }
        });
        getContentPane().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                emergencyResize();

                // Debugging
                if(printSize) {
                    System.out.println(getTitle() + ": " + getSize());
                }
            }
        });

        addClosingListener(e -> {
            if(shutdownSwingTimersOnClose) {
                SwingTimerManager.shutdown();
            }
        });
    }
    private void emergencyResize() {
        if(getSize().width > 5000 || getSize().height > 4000) {
            if(lastSize == null) {
                lastSize = new Dimension(700, 700);
            }
            setSize(lastSize);
        } else {
            lastSize = getSize();
        }
    }


    /////////////////
    // CLOSE FRAME //
    /////////////////

    public boolean close() {
        return closeFrame();
    }
    public boolean closeFrame() {
        RWindowClosingEvent e = new RWindowClosingEvent(this, -1);
        fireAttemptToClose(e);
        if(!e.isCancelClose()) {
            List<RWindow> childWins = getAllChildWindows();
            for(RWindow childWin : childWins) {
                if(!childWin.close()) {
                    return false;
                }
            }
            setVisible(false);    // To ensure that the frame is not visible
                                  // before closing events fire, since
                                  // ComponentListener.componentHidden is not
                                  // fired if just dispose is called.
            fireClosingNotifier();
            dispose();
            return true;
        }
        return false;
    }
    public void closeWindowNoFire() {
        closeFrameNoFire();
    }
    public void closeFrameNoFire() {
        setVisible(false);
        dispose();
    }


    /////////////////////////
    // TOGGLE DEBUG COLORS //
    /////////////////////////

    // The following code allows a user to enable and disable various
    // debugging features within ANY and EVERY RFrame without the
    // need for visible controls to be provided by the developer
    // of said RFrame.  Debugging features are meant to be able to
    // be enabled in ANY environment, even those not typically
    // considered to be "development".  This is because often times
    // UI problems only manifest themselves in certain environments
    // (e.g. Swing's default L&F on Ubuntu has nuanced differences from
    // other operating systems) and we want to be able to allow
    // developers to always have access to these "backdoor" features.
    // It also is difficult to make access to these features dependent
    // on having first set a "debug mode" system property, because it
    // would require restarting the software if you didn't already
    // think to have this property enabled.  However, to balance the
    // availability, we've made the features non-obvious to access so
    // unexpecting users don't accidentally trigger them.
    //     CTRL+ALT+SHIFT+D - Toggle Debug Background Colors
    //     CTRL+ALT+SHIFT+E - Toggle Debug Tick Marks
    //     CTRL+ALT+SHIFT+F - Toggle Debug Mouse
    // Perhaps in the future, if the number of debugging features grow,
    // we instead provide access to a hidden "debug menu" instead of
    // trying to assign special key strokes to each one.

    @Override
    protected JRootPane createRootPane() {
        JRootPane rp = super.createRootPane();

        int all3 = InputEvent.SHIFT_DOWN_MASK + InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK;
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_D, all3);
        rp.registerKeyboardAction(e -> toggleDebugColors(), stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

        stroke = KeyStroke.getKeyStroke(KeyEvent.VK_F, all3);
        rp.registerKeyboardAction(e -> toggleDebugTicks(), stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

        stroke = KeyStroke.getKeyStroke(KeyEvent.VK_G, all3);
        rp.registerKeyboardAction(e -> toggleDebugMouse(), stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

        stroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, all3);
        rp.registerKeyboardAction(e -> printWinDetails(), stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

        return rp;
    }

    private boolean debugColorsLastEnabled = false;
    private boolean debugTicksLastEnabled = false;
    private boolean debugMouseLastEnabled = false;

    public void toggleDebugColors() {
        debugColorsLastEnabled = !debugColorsLastEnabled;
        GuiUtil.setDebugColors(getContentPane(), debugColorsLastEnabled);
    }
    public void toggleDebugTicks() {
        debugTicksLastEnabled = !debugTicksLastEnabled;
        GuiUtil.setDebugTicks(getContentPane(), debugTicksLastEnabled);
    }
    public void toggleDebugMouse() {
        debugMouseLastEnabled = !debugMouseLastEnabled;
        GuiUtil.setDebugMouse(getContentPane(), debugMouseLastEnabled);
    }
    public void printWinDetails() {
        System.out.println("Window Details:");
        System.out.println("  Class:    " + getClass().getName());
        System.out.println("  Title:    " + getTitle());
        System.out.println("  Size:     " + getSize());
        System.out.println("  Location: " + getLocation());
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    public void addWaitOnListener(ChangeListener listener) {
        waitOnNotifier.addListener(listener);
    }
    public void addWaitOffListener(ChangeListener listener) {
        waitOffNotifier.addListener(listener);
    }
    public void addAttemptToCloseListener(RWindowClosingListener listener) {
        attemptToCloseNotifier.addListener(listener);
    }
    public void addClosingListener(ChangeListener listener) {
        closingNotifier.addListener(listener);
    }

    private void fireWaitOnNotifier() {
        waitOnNotifier.fireStateChanged();
    }
    private void fireWaitOffNotifier() {
        waitOffNotifier.fireStateChanged();
    }
    private void fireAttemptToClose(RWindowClosingEvent e) {
        attemptToCloseNotifier.fireStateChanged(e);
    }
    private void fireClosingNotifier() {
        closingNotifier.fireStateChanged();
    }


    //////////
    // MISC //
    //////////

    @Override
    public void requestFocus() {
        super.requestFocus();
        requestFocusInWindow();
    }
    public void setDefaultButton(JButton btn) {
        getRootPane().setDefaultButton(btn);
    }
    public void setIcon(ImageIcon icon) {
        super.setIconImage(icon.getImage());
    }
    public void setIcon(ImageModelConcept concept) {
        super.setIconImage(ImageLib.get(concept).getImage());
    }
    public void focus(Component cmp) {
        cmp.requestFocusInWindow();
    }
    @Override
    public void updateUI() {
        ((JPanel) getContentPane()).updateUI();
    }
    @Override
    public void enableDiagnostics() {
        addComponentListener(new DebugComponentListener());
    }
    @Override
    public boolean isShutdownSwingTimersOnClose() {
        return shutdownSwingTimersOnClose;
    }
    @Override
    public void setShutdownSwingTimersOnClose(boolean shutdownSwingTimersOnClose) {
        this.shutdownSwingTimersOnClose = shutdownSwingTimersOnClose;
    }
    public UIActionMenuBar getUiActionMenuBar() {    // Convenience if you know that
        return (UIActionMenuBar) getJMenuBar();      // your window is using this type
    }                                                // of menu bar.
    public boolean isPrintSize() {
        return printSize;
    }
    public void setPrintSize(boolean printSize) {
        this.printSize = printSize;
    }


    //////////
    // WAIT //
    //////////

    public void waitOn() {
        CursorUtil.changeCursor(this, Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        fireWaitOnNotifier();
    }
    public void waitOff() {
        CursorUtil.revertCursor(this);
        fireWaitOffNotifier();
    }


    ///////////////////
    // CHILD WINDOWS //
    ///////////////////

    public void addChildWindowListener(ChangeListener listener) {
        cwManager.addChildWindowListener(listener);
    }
    public void addChildWindowCreationHandler(String typeId, ChildWindowCreationHandler handler) {
        cwManager.addChildWindowCreationHandler(typeId, handler);
    }
    public RWindow createChildWindow(String typeId, String uniqueId, Object... args) {
        return cwManager.createChildWindow(typeId, uniqueId, args);
    }
    public RWindow openChildWindow(String typeId, String uniqueId, Object... args) {
        return cwManager.openChildWindow(typeId, uniqueId, args);
    }
    public void showChildWindow(String typeId) {
        cwManager.showChildWindow(typeId);
    }
    public void showChildWindow(String typeId, String uniqueId) {
        cwManager.showChildWindow(typeId, uniqueId);
    }
    public void showChildWindow(RWindow win) {
        cwManager.showChildWindow(win);
    }
    public void hideChildWindow(String typeId) {
        cwManager.hideChildWindow(typeId);
    }
    public void hideChildWindow(String typeId, String uniqueId) {
        cwManager.hideChildWindow(typeId, uniqueId);
    }
    public void hideChildWindow(RWindow win) {
        cwManager.hideChildWindow(win);
    }
    public void destroyChildWindow(String typeId) {
        cwManager.destroyChildWindow(typeId);
    }
    public void destroyChildWindow(String typeId, String uniqueId) {
        cwManager.destroyChildWindow(typeId, uniqueId);
    }
    public void destroyChildWindow(RWindow win) {
        cwManager.destroyChildWindow(win);
    }
    public RWindow getChildWindow(String typeId) {
        return cwManager.getChildWindow(typeId);
    }
    public RWindow getChildWindow(String typeId, String uniqueId) {
        return cwManager.getChildWindow(typeId, uniqueId);
    }
    public boolean existsChildWindow(String typeId) {
        return cwManager.existsChildWindow(typeId);
    }
    public boolean existsChildWindow(String typeId, String uniqueId) {
        return cwManager.existsChildWindow(typeId, uniqueId);
    }
    public String getTypeIdOfWindow(RWindow win) {
        return cwManager.getTypeIdOfWindow(win);
    }
    public String getUniqueIdOfWindow(RWindow win) {
        return cwManager.getUniqueIdOfWindow(win);
    }
    public String[] getRegisteredTypeIds() {
        return cwManager.getRegisteredTypeIds();
    }
    public String[] getAllTypeIds() {
        return cwManager.getAllTypeIds();
    }
    public Map<String, String[]> getAllUniqueIds() {
        return cwManager.getAllUniqueIds();
    }
    public List<RWindow> getAllChildWindows() {
        return cwManager.getAllChildWindows();
    }
    public List<RWindow> getVisibleChildWindows() {
        return cwManager.getVisibleChildWindows();
    }
    public <T extends RWindow> T getOrCreate(String typeId, String uniqueId, Object... args) {
        return cwManager.getOrCreate(typeId, uniqueId, args);
    }
    public void ensureOnScreen() {
        GuiUtil.ensureOnScreen(this);
    }
    public void ensureOnScreen(boolean completely) {
        GuiUtil.ensureOnScreen(this, completely);
    }
}
