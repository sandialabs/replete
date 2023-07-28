package replete.ui.button;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;

import darrylbu.renderer.CheckBoxIcon;
import replete.ui.GuiUtil;
import replete.ui.lay.Lay;
import replete.ui.mnemonics.Mnemonics;
import replete.ui.panels.UiDebuggable;
import replete.ui.uidebug.UiDebugUtil;


public class RCheckBox extends JCheckBox implements UiDebuggable {


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

    public RCheckBox() {
        init();
    }
    public RCheckBox(Icon icon) {
        super(icon);
        init();
    }
    public RCheckBox(String text) {
        super(text);
        init();
    }
    public RCheckBox(Action action) {
        super(action);
        init();
    }
    public RCheckBox(Icon icon, boolean selected) {
        super(icon, selected);
        init();
    }
    public RCheckBox(String icon, boolean selected) {
        super(icon, selected);
        init();
    }
    public RCheckBox(String text, Icon icon) {
        super(text, icon);
        init();
    }
    public RCheckBox(String text, Icon icon, boolean selected) {
        super(text, icon, selected);
        init();
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

    // All of the accessors/mutators code below is to support the
    // debug framework.  It is identical to the code in RLabel.

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

    public void setCompoundIcon(Icon icon) {
        setIcon(new CheckBoxIcon(this, icon));
    }

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
    public void setText(String text) {
        Mnemonics mn = Mnemonics.resolve(text);
        super.setText(mn.getResolvedText());
        Mnemonics.apply(mn, this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        UiDebugUtil.drawTicks(this, g, debugTicksEnabled);
        UiDebugUtil.drawMouse(this, g, debugMouseEnabled, mouseX, mouseY);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        Lay.FLtg(Lay.fr("RCheckBox Test"),
            new RCheckBox("&Saturn && Jupiter"),
            new RCheckBox("M&ars"),
            "size=600,center,visible"
        );
    }
}
