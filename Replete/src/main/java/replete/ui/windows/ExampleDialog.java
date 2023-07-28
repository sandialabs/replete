package replete.ui.windows;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;

import replete.ui.lay.Lay;
import replete.ui.windows.escape.EscapeDialog;


public class ExampleDialog extends EscapeDialog {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    // Just provide all constructors that base class provides.

    public ExampleDialog() {
        super((Frame) null, "Example Dialog", false);
        init();
    }
    public ExampleDialog(Dialog owner, boolean modal) {
        super(owner, modal);
        init();
    }
    public ExampleDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        init();
    }
    public ExampleDialog(Dialog owner, String title, boolean modal) {
        super(owner, title, modal);
        init();
    }
    public ExampleDialog(Dialog owner, String title) {
        super(owner, title);
        init();
    }
    public ExampleDialog(Dialog owner) {
        super(owner);
        init();
    }
    public ExampleDialog(Frame owner, boolean modal) {
        super(owner, modal);
        init();
    }
    public ExampleDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        init();
    }
    public ExampleDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        init();
    }
    public ExampleDialog(Frame owner, String title) {
        super(owner, title);
        init();
    }
    public ExampleDialog(Frame owner) {
        super(owner);
        init();
    }
    public ExampleDialog(Window owner, ModalityType modalityType) {
        super(owner, modalityType);
        init();
    }
    public ExampleDialog(Window owner, String title, ModalityType modalityType,
                        GraphicsConfiguration gc) {
        super(owner, title, modalityType, gc);
        init();
    }
    public ExampleDialog(Window owner, String title, ModalityType modalityType) {
        super(owner, title, modalityType);
        init();
    }
    public ExampleDialog(Window owner, String title) {
        super(owner, title);
        init();
    }
    public ExampleDialog(Window owner) {
        super(owner);
        init();
    }


    //////////
    // INIT //
    //////////

    private void init() {
        Lay.hn(this, "size=[600,600],center,dco=dispose");
    }
}
