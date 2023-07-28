package replete.ui.windows;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;

import javax.swing.ImageIcon;

import replete.ui.windows.notifications.NotificationDialog;

// Since nearly every dialog I create has had a separate result
// integer and associated constants, this class experiments with
// how that can be blended into the class itself.  This class
// also enables an ad-hoc use of dialogs by not requiring that
// subclasses be created when you'd rather just create one and
// build its UI within an event call back method directly.

public class ResultDialog extends NotificationDialog {


    ////////////
    // FIELDS //
    ////////////

    public static final int CANCEL = 0;
    public static final int OK     = 1;

    private int result = CANCEL;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ResultDialog() {
    }
    public ResultDialog(String title) {
        super(title);
    }
    public ResultDialog(Dialog owner) {
        super(owner);
    }
    public ResultDialog(Dialog owner, boolean modal) {
        super(owner, modal);
    }
    public ResultDialog(Dialog owner, String title) {
        super(owner, title);
    }
    public ResultDialog(Dialog owner, String title, boolean modal) {
        super(owner, title, modal);
    }
    public ResultDialog(Dialog owner, String title, boolean modal, ImageIcon icon) {
        super(owner, title, modal, icon);
    }
    public ResultDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
    }
    public ResultDialog(Frame owner) {
        super(owner);
    }
    public ResultDialog(Frame owner, boolean modal) {
        super(owner, modal);
    }
    public ResultDialog(Frame owner, String title) {
        super(owner, title);
    }
    public ResultDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
    }
    public ResultDialog(Frame owner, String title, boolean modal, ImageIcon icon) {
        super(owner, title, modal, icon);
    }
    public ResultDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
    }
    public ResultDialog(Window owner) {
        super(owner);
    }
    public ResultDialog(Window owner, ModalityType modalityType) {
        super(owner, modalityType);
    }
    public ResultDialog(Window owner, String title) {
        super(owner, title);
    }
    public ResultDialog(Window owner, String title, ModalityType modalityType) {
        super(owner, title, modalityType);
    }
    public ResultDialog(Window owner, String title, boolean modal) {
        super(owner, title, modal);
    }
    public ResultDialog(Window owner, String title, ModalityType modalityType,
                        GraphicsConfiguration gc) {
        super(owner, title, modalityType, gc);
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public int getResult() {
        return result;
    }


    //////////
    // MISC //
    //////////

    public void close(int result) {
        this.result = result;
        close();
    }
}
