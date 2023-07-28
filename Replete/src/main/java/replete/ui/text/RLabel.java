package replete.ui.text;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import replete.ui.GuiUtil;
import replete.ui.images.concepts.ImageLib;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.panels.UiDebuggable;
import replete.ui.uidebug.UiDebugUtil;

public class RLabel extends JLabel implements UiDebuggable {


    ////////////
    // FIELDS //
    ////////////

    // All to support debug framework
    private int del = -30;
    private int mouseX;                  // Less relevant for labels/checkboxes, but kept for consistency
    private int mouseY;                  // Less relevant for labels/checkboxes, but kept for consistency
    private boolean debugColorEnabled;
    private boolean debugTicksEnabled;   // Less relevant for labels/checkboxes, but kept for consistency
    private Color debugBackgroundColor;
    private boolean debugMouseEnabled;   // Less relevant for labels/checkboxes, but kept for consistency
    private boolean savePrevOpaque;
    private Color savePrevBackgroundColor;
    boolean internalDebugCall = false;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RLabel() {
        init();
    }
    public RLabel(String text) {
        super(text);
        init();
    }
    public RLabel(Icon image) {
        super(image);
        init();
    }
    public RLabel(ImageModelConcept concept) {
        super();
        setIcon(concept);
        init();
    }
    public RLabel(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
        init();
    }
    public RLabel(Icon image, int horizontalAlignment) {
        super(image, horizontalAlignment);
        init();
    }
    public RLabel(String text, Icon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
        init();
    }
    public RLabel(String text, Icon icon) {
        this(text, icon, SwingConstants.CENTER);        // This does not call super
    }

    private void init() {
        savePrevOpaque = isOpaque();
        savePrevBackgroundColor = getBackground();

        if(UiDebugUtil.isColorEnabled()) {
            setDebugColorEnabled(true);
        }
        if(UiDebugUtil.isTicksEnabled()) {
            setDebugTicksEnabled(true);
        }
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // The majority of the accessors/mutators code below is to support
    // the debug framework.  It is identical to the code in RCheckBox.

    // Accessors

    public boolean isDebugColorEnabled() {
        return debugColorEnabled;
    }
    public boolean isDebugTicksEnabled() {    // Less relevant for labels/checkboxes, but kept for consistency
        return debugTicksEnabled;
    }
    public boolean isDebugMouseEnabled() {    // Less relevant for labels/checkboxes, but kept for consistency
        return debugMouseEnabled;
    }

    // Mutators

    // Unfortunately, this method has to be much more complicated for non-RPanel
    // components.  We can't just paint over what the super class paints or we
    // would lose a label's already painted text, or a check box's already
    // painted square and text, etc.  Thus, we'll instead rely on these components'
    // own background color to enable the debug color functionality.  Sadly
    // this means that we need to respect the control's own existing background
    // color and opaque status.  Synchronization is not employed (though doing so
    // would make the code more correct) due to the fairly small chance that
    // background threads would be setting these components' background color or
    // opaque status.  Operations on the EDT all happen serially.
    public void setDebugColorEnabled(boolean debugColorEnabled) {
        this.debugColorEnabled = debugColorEnabled;
        if(debugColorEnabled) {
            debugBackgroundColor = GuiUtil.deriveColor(UiDebugUtil.nextColor(), del, del, del);   // Choose new color
            internalDebugCall = true;
            setOpaque(true);
            setBackground(debugBackgroundColor);
            internalDebugCall = false;
        } else {
            internalDebugCall = true;
            setOpaque(savePrevOpaque);
            setBackground(savePrevBackgroundColor);
            internalDebugCall = false;
        }
        repaint();
    }
    public void setDebugColor(Color debugBackgroundColor) {
        this.debugBackgroundColor = debugBackgroundColor;
        if(debugColorEnabled) {
            internalDebugCall = true;
            setBackground(debugBackgroundColor);
            internalDebugCall = false;
        }
        repaint();
    }
    public void setDebugTicksEnabled(boolean debugTicksEnabled) {    // Less relevant for labels/checkboxes, but kept for consistency
        this.debugTicksEnabled = debugTicksEnabled;
        repaint();
    }
    public void setDebugMouseEnabled(boolean debugMouseEnabled) {    // Less relevant for labels/checkboxes, but kept for consistency
        this.debugMouseEnabled = debugMouseEnabled;
        if(debugMouseEnabled) {
            addMouseMotionListener(debugMouseMotionListener);
        } else {
            removeMouseMotionListener(debugMouseMotionListener);
        }
        repaint();
    }

    @Override
    public void setOpaque(boolean isOpaque) {    // Reason for this method's complexity explained above
        if(!debugColorEnabled || internalDebugCall) {
            super.setOpaque(isOpaque);
        }
        if(!internalDebugCall) {
            savePrevOpaque = isOpaque;
        }
    }

    @Override
    public void setBackground(Color bg) {        // Reason for this method's complexity explained above
        if(!debugColorEnabled || internalDebugCall) {
            super.setBackground(bg);
        }
        if(!internalDebugCall) {
            savePrevBackgroundColor = bg;
        }
    }

    public void clear() {
        setText("");
    }
    public void setText(Object o) {
        if(o == null) {
            setText(null);
        } else {
            setText(o.toString());
        }
    }
    public void setTextF(String format, Object... args) {
        setText(String.format(format, args));     ///      setText + println   setTextF + printf
    }

    public void setIcon(ImageModelConcept concept) {
        super.setIcon(ImageLib.get(concept));
    }
    public void setCursorHand() {
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    public void setCursorDefault() {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
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


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        UiDebugUtil.drawTicks(this, g, debugTicksEnabled);
        UiDebugUtil.drawMouse(this, g, debugMouseEnabled, mouseX, mouseY);
    }
}
