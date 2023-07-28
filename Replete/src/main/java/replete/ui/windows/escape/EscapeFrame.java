package replete.ui.windows.escape;

import java.awt.GraphicsConfiguration;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.windows.Dialogs;
import replete.ui.windows.common.RFrame;
import replete.ui.windows.common.RWindowClosingEvent;
import replete.ui.windows.common.RWindowClosingListener;



/**
 * A frame that closes when the escape key is pressed.
 * Alternatively the escape key event can be captured
 * and its behavior overridden in subclasses.
 *
 * @author Derek Trumbo
 */

public class EscapeFrame extends RFrame {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public EscapeFrame() {
        super();
    }
    public EscapeFrame(String title) {
        super(title);
    }
    public EscapeFrame(GraphicsConfiguration gc) {
        super(gc);
    }
    public EscapeFrame(ImageIcon icon, GraphicsConfiguration gc) {
        super(icon, gc);
    }
    public EscapeFrame(ImageIcon icon) {
        super(icon);
    }
    public EscapeFrame(String title, GraphicsConfiguration gc) {
        super(title, gc);
    }
    public EscapeFrame(String title, ImageIcon icon, GraphicsConfiguration gc) {
        super(title, icon, gc);
    }
    public EscapeFrame(String title, ImageIcon icon) {
        super(title, icon);
    }
    public EscapeFrame(ImageModelConcept concept, GraphicsConfiguration gc) {
        super(concept, gc);
    }
    public EscapeFrame(ImageModelConcept concept) {
        super(concept);
    }
    public EscapeFrame(String title, ImageModelConcept concept, GraphicsConfiguration gc) {
        super(title, concept, gc);
    }
    public EscapeFrame(String title, ImageModelConcept concept) {
        super(title, concept);
    }


    ////////////////
    // ESCAPE KEY //
    ////////////////

    @Override
    protected JRootPane createRootPane() {
        JRootPane rp = super.createRootPane();

        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        rp.registerKeyboardAction(e -> escapePressed(), stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

        return rp;
    }

    // Subclasses can override this to perform any other shutdown
    // operations that they want before closing the window.
    protected void escapePressed() {
        close();
    }
    public void center() {
        setLocationRelativeTo(null);
    }

    protected ActionListener createCloseListener() {         // Minor convenience method
        return (ActionListener) e -> close();
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        final EscapeFrame f = new EscapeFrame();

        f.addAttemptToCloseListener(new RWindowClosingListener() {
            public void stateChanged(RWindowClosingEvent e) {
                System.out.println("attempt to close");
                if(!Dialogs.showConfirm(f, "Close?")) {
                    e.cancelClose();
                }
            }
        });
        f.addClosingListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                System.out.println("closing");
            }
        });

        f.setSize(600,600);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
