package replete.ui;

import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;
import replete.ui.tabbed.RNotifPanel;

// This class is roughly analogous to the "StatelessProcess" class.
//  - Both are objects that can be created, do their job, and then thrown away.
// The "PersistentConfiguredPanel" class is roughly analogous to the "PersistentController" class.
//  - But have not found a need for "reset" or "dispose" in the panel yet.
//  - Added concept of an externally accessible refresh to the panel that
//    makes less sense in the controller class.
//  - "Summary state" makes more sense in the controller sense, but "cached
//    state" makes a little more sense in the panel sense.

public abstract class BeanPanel<T> extends RNotifPanel {


    ////////////
    // FIELDS //
    ////////////

    // Optional to use this - usually the memory is just copied
    // into the UI controls via set, and then a BRAND NEW bean
    // is constructed via get.  That pattern is actually quite
    // clean.  Usage of lastSetBean could actually confuse
    // matters if not used properly.  It should only be used
    // to have access to immutable fields in the bean that
    // for which you don't want to make hidden controls or
    // private fields for.  It is not recommended to return
    // this field from get, nor call mutators on this object
    // anywhere in the panel.  It should be considered
    // "advanced usage" to use lastSetBean.
    protected T lastSetBean = null;             // Read-only


    ///////////////
    // GET & SET //
    ///////////////

    // TODO: implement getLastSetBean() and/or better define
    // relationship with get() semantics.
    public T getLastSetBean() { // Advanced usage right now
        return lastSetBean;     // get() will generally construct a brand new object, this just returns a retained reference
    }

    // This method traditionally would be an abstract method in
    // this pattern but changed slightly to support lastSetBean.
    // Providing this implementation may make "state" panels
    // more convenient to implement even though it might confuse
    // matters if developers accidentally call this when they
    // should rather be constructing a brand new bean from the
    // controls on the panel (which is the more common pattern).
    public /*abstract*/ T get() {
        return lastSetBean;
    }

    // This method traditionally would be an abstract method in
    // this pattern but changed slightly to support lastSetBean.
    // Must call super.set(bean) if you want to use lastSetBean.
    public /*abstract*/ void set(T bean) {
        lastSetBean = bean;
    }


    ///////////////
    // ACCESSORS //   // Activation is really just an experimental idea, not used right now
    ///////////////

    // Computed

    public boolean isActivated() {   // Meant to be optionally overridden
        return false;
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    private ChangeNotifier activationChangedNotifier = new ChangeNotifier(this);
    public void addActivationChangedListener(ChangeListener listener) {
        activationChangedNotifier.addListener(listener);
    }
    protected void fireActivationChangedNotifier() {
        activationChangedNotifier.fireStateChanged();
    }
}
