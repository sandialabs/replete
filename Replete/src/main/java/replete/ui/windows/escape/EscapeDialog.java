package replete.ui.windows.escape;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.windows.common.RDialog;


/**
 * A dialog that closes when the escape key is pressed.
 * Alternatively the escape key event can be captured
 * and its behavior overridden in subclasses.
 *
 * @author Derek Trumbo
 */

public class EscapeDialog extends RDialog {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    // Just provide all constructors that base class provides.

    public EscapeDialog() {
        super();
    }
    public EscapeDialog(String title) {
        super((JFrame) null, title, true);
    }
    public EscapeDialog(Dialog owner) {
        super(owner);
    }
    public EscapeDialog(Dialog owner, boolean modal) {
        super(owner, modal);
    }
    public EscapeDialog(Dialog owner, String title) {
        super(owner, title);
    }
    public EscapeDialog(Dialog owner, String title, boolean modal) {
        super(owner, title, modal);
    }
    public EscapeDialog(Dialog owner, String title, boolean modal, ImageIcon icon) {
        super(owner, title, modal, icon);
    }
    public EscapeDialog(Dialog owner, String title, boolean modal, ImageModelConcept concept) {
        super(owner, title, modal, concept);
    }
    public EscapeDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
    }
    public EscapeDialog(Frame owner) {
        super(owner);
    }
    public EscapeDialog(Frame owner, boolean modal) {
        super(owner, modal);
    }
    public EscapeDialog(Frame owner, String title) {
        super(owner, title);
    }
    public EscapeDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
    }
    public EscapeDialog(Frame owner, String title, boolean modal, ImageIcon icon) {
        super(owner, title, modal, icon);
    }
    public EscapeDialog(Frame owner, String title, boolean modal, ImageModelConcept concept) {
        super(owner, title, modal, concept);
    }
    public EscapeDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
    }
    public EscapeDialog(Window owner) {
        super(owner);
    }
    public EscapeDialog(Window owner, ModalityType modalityType) {
        super(owner, modalityType);
    }
    public EscapeDialog(Window owner, String title) {
        super(owner, title);
    }
    public EscapeDialog(Window owner, String title, ModalityType modalityType) {
        super(owner, title, modalityType);
    }
    public EscapeDialog(Window owner, String title, boolean modal) {
        super(owner, title, modal);
    }
    public EscapeDialog(Window owner, String title, boolean modal, ImageIcon icon) {
        super(owner, title, modal, icon);
    }
    public EscapeDialog(Window owner, String title, boolean modal, ImageModelConcept concept) {
        super(owner, title, modal, concept);
    }
    public EscapeDialog(Window owner, String title, ModalityType modalityType, GraphicsConfiguration gc) {
        super(owner, title, modalityType, gc);
    }


    ////////////////
    // ESCAPE KEY //
    ////////////////

    @Override
    protected JRootPane createRootPane() {
        JRootPane rp = super.createRootPane();

        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                escapePressed();
            }
        };

        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        rp.registerKeyboardAction(actionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

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
}
