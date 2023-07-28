package finio.plugins.extpoints;

import java.awt.event.KeyListener;

import javax.swing.event.ChangeListener;

import finio.ui.fpanel.FPanel;
import replete.event.ChangeNotifier;

public abstract class JavaObjectEditorPanel extends FPanel {


    ///////////
    // FIELD //
    ///////////

    protected Object O;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public Object getObject() {
        return O;
    }

    // Mutators

    public void setObject(Object O) {
        this.O = O;
    }
    public void setKeyListener(KeyListener keyListener) {
        // Total hack for now
    }


    //////////////
    // ABSTRACT //
    //////////////

    public abstract boolean isValidState();   // TODO: Use this sometime...
    public abstract boolean isReturnsNewObject();
    public abstract boolean allowsEdit();

    public void selectAll(boolean reverse) {}


    ///////////////
    // NOTIFIERS //
    ///////////////

    private ChangeNotifier preferredSizeChangedNotifier = new ChangeNotifier(this);
    public void addPreferredSizeChangedListener(ChangeListener listener) {
        preferredSizeChangedNotifier.addListener(listener);
    }
    protected void firePreferredSizeChangedNotifier() {
        preferredSizeChangedNotifier.fireStateChanged();
    }
    private ChangeNotifier childFocusNotifier = new ChangeNotifier(this);
    public void addChildFocusListener(ChangeListener listener) {
        childFocusNotifier.addListener(listener);
    }
    protected void fireChildFocusNotifier() {
        childFocusNotifier.fireStateChanged();
    }

    // Strange to have here... not very generic
    private ChangeNotifier hitLeftNotifier = new ChangeNotifier(this);
    public void addHitLeftListener(ChangeListener listener) {
        hitLeftNotifier.addListener(listener);
    }
    protected void fireHitLeftNotifier() {
        hitLeftNotifier.fireStateChanged();
    }

    private ChangeNotifier hitRightNotifier = new ChangeNotifier(this);
    public void addHitRightListener(ChangeListener listener) {
        hitRightNotifier.addListener(listener);
    }
    protected void fireHitRightNotifier() {
        hitRightNotifier.fireStateChanged();
    }
}
