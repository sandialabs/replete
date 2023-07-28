package replete.ui.panels;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import replete.ui.GuiUtil;
import replete.ui.SelectionStateSavable;
import replete.ui.lay.Lay;
import replete.ui.uidebug.UiDebugUtil;
import replete.ui.validation.Validatable;
import replete.ui.validation.ValidationContext;
import replete.ui.validation.ValidationUtil;
import replete.ui.windows.common.RWindow;

public class RPanel extends JPanel implements Validatable, UiDebuggable, SelectionStateSavable {


    ////////////
    // FIELDS //
    ////////////

    private int del = 0;                 // Debug
    private int mouseX;                  // Debug
    private int mouseY;                  // Debug
    private boolean debugColorEnabled;   // Debug
    private boolean debugTicksEnabled;   // Debug
    private Color debugBackgroundColor;  // Debug
    private boolean debugMouseEnabled;   // Debug
    private Double transparency;
    private Runnable readyOp;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RPanel() {
        super();
        init();
    }
    public RPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
        init();
    }
    public RPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
        init();
    }
    public RPanel(LayoutManager layout) {
        super(layout);
        init();
    }

    private void init() {
        if(UiDebugUtil.isColorEnabled()) {
            setDebugColorEnabled(true);
        }
        if(UiDebugUtil.isTicksEnabled()) {
            setDebugTicksEnabled(true);
        }
    }


    ///////////
    // READY //
    ///////////

    // Ready is a container for actions that need to be performed
    // AFTER a panel has been properly placed into its proper
    // UI hierarchy within a frame or dialog.  During a panel's
    // constructor, for example, the parent container is not
    // set yet.  Secondly, sometimes panel construction can take
    // some time and is often performed on background threads
    // only to be placed into its proper place after the background
    // thread is over and control returned to the UI thread.
    // Ready only needs to be overridden in cases where operations
    // need to wait until the panel has been added to its windows
    // hierarchy but where those operations still need to be performed
    // near the beginning of the panel's lifecycle.
    // NOTE: The term 'ready' was chosen only because no appropriate
    // term could be immediately found (e.g. "postPanelPlacedInWindow"???).

    // Also, since some panels are ONLY used for structural layout
    // and it would be annoying to have to create subclasses just
    // to have a chance to invoke this, default behavior options
    // are provided in the form of 1) simply recursively crawling
    // the component tree of this RPanel and calling ready on any
    // and all RPanels found and 2) allowing a custom operation to
    // set in the form of a field and invoked if set.

    // Simple usage: You call 'ready' on an outer panel on the UI
    // thread once it has been added to a window or frame and then
    // on any subpanels that need this special post-construction
    // initialization, you override 'readyInner'.  The relationship
    // between 'ready' and 'readyInner' is similar to that between
    // JComponents' 'paint' and 'paintComponent'.  You almost never
    // need to override 'ready' unless you really need to.

    public void ready() {
        readyTree(this);
    }

    private void readyTree(Container c) {
        if(c instanceof RPanel) {
            ((RPanel) c).readyInner();
        }
        for(Component cc : c.getComponents()) {
            if(cc instanceof Container) {
                readyTree((Container) cc);
            }
        }
    }

    public void readyInner() {
        if(readyOp != null) {
            readyOp.run();
        }
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public boolean isDebugColorEnabled() {
        return debugColorEnabled;
    }
    public boolean isDebugTicksEnabled() {
        return debugTicksEnabled;
    }
    public boolean isDebugMouseEnabled() {
        return debugMouseEnabled;
    }
    public Double getTransparency() {
        return transparency;
    }
    public Runnable getReadyOp() {
        return readyOp;
    }

    // Mutators

    public void setDebugColorEnabled(boolean debugColorEnabled) {
        this.debugColorEnabled = debugColorEnabled;
        if(debugColorEnabled) {
            debugBackgroundColor = GuiUtil.deriveColor(UiDebugUtil.nextColor(), del, del, del);
        }
        repaint();
    }
    public void setDebugColor(Color debugBackgroundColor) {
        this.debugBackgroundColor = debugBackgroundColor;
        repaint();
    }
    public void setDebugTicksEnabled(boolean debugTicksEnabled) {
        this.debugTicksEnabled = debugTicksEnabled;
        repaint();
    }
    public void setDebugMouseEnabled(boolean debugMouseEnabled) {
        this.debugMouseEnabled = debugMouseEnabled;
        if(debugMouseEnabled) {
            addMouseMotionListener(debugMouseMotionListener);
        } else {
            removeMouseMotionListener(debugMouseMotionListener);
        }
        repaint();
    }
    public void setTransparency(Double transparency) {
        this.transparency = transparency;
        repaint();
    }
    public RPanel setReadyOp(Runnable readyOp) {
        this.readyOp = readyOp;
        return this;
    }


    //////////
    // MISC //
    //////////

    private MouseMotionListener debugMouseMotionListener = new MouseMotionAdapter() {
        @Override
        public void mouseMoved(MouseEvent e) {
            if(debugTicksEnabled) {
                mouseX = e.getX();
                mouseY = e.getY();
                repaint();
            }
        }
    };

    // Subclasses can define where the focus should be if the
    // panel is asked to take the focus.  This could be
    // implemented as this.requestFocusInWindow() if this.isFocusable()
    // is true, or could call requestFocusInWindow or focus on
    // the proper subcomponent.
    public void focus() {

    }

    public void setHandCursor() {
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    public void setDefaultCursor() {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public Window getWindow() {     // Get window as a Component (physical object, Swing needs for parents often)
        return GuiUtil.win(this);
    }
    public RWindow getRWindow() {      // Get window as an interface (representation of advanced window functionality)
        return (RWindow) getWindow();
    }
    public <P> P getParent(Class clazz) {
        Component cmp = this;
        while(cmp != null && !clazz.isAssignableFrom(cmp.getClass())) {
            cmp = cmp.getParent();
        }
        return (P) cmp;
    }

    // Convenience method for a specific layout manager that is otherwise
    // somewhat annoying to use.
    public void showCard(String paneName) {
        LayoutManager m = getLayout();
        CardLayout c = (CardLayout) m;
        c.show(this, paneName);
    }


    ////////////////
    // VALIDATION //
    ////////////////

    @Override
    public void validateInput(ValidationContext context) {
        // This is one possible default implementation that mimics the one
        // in RWindow.  Since so many panels are simply wrappers around
        // other panels with some decoration, delegating "validateInput"
        // to these panels does make sense, as it would eliminate the need
        // for otherwise boilerplate-ish validateInput methods.  However,
        // it almost might make things *too* convenient, and thus a little
        // *too* opaque.  At that point it might get a little complicated
        // to even understand how the UI validation framework operates -
        // but that's just speculation obviously.

//        if(getLayout() instanceof BorderLayout) {
//            BorderLayout bl = (BorderLayout) getLayout();
//            Component c = ReflectionUtil.get(bl, "center");
//            if(c instanceof Validatable) {
//                context.check((Validatable) c);
//            }
//        }
    }
    public boolean checkValidationPass() {
        Window window = getWindow();
        return ValidationUtil.checkValidationPass(window, this);
    }


    ///////////
    // PAINT //
    ///////////

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if(transparency != null) {
            g.setColor(new Color(255, 255, 255, (int) (255 * transparency)));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        UiDebugUtil.drawColor(this, g, debugColorEnabled, debugBackgroundColor);
        UiDebugUtil.drawTicks(this, g, debugTicksEnabled);
        UiDebugUtil.drawMouse(this, g, debugMouseEnabled, mouseX, mouseY);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        UiDebugUtil.enableColor();
        UiDebugUtil.enableTicks();
        Lay.BLtg(Lay.fr("Debug Panel Test"),
            "C", Lay.FL(Lay.lb("Test Label"), Lay.chk("Test Check")),
            "size=600,center,visible"
        );
    }
}
