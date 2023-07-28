package replete.plugins.ui;

import replete.ui.tabbed.RNotifPanel;

// This class represents a panel whose display depends on some
// parameterization and which can have a fairly actively changing
// state.  That state can be captured for persistence but only
// represents a snap shot of the state on that panel at the time
// of capture.  Sometimes the content on this panel can be backed
// by remote data, so a refresh mechanism is built into this base
// class.

// This class is roughly analogous to the "PersistentController" class.
//  - But have not found a need for "reset" or "dispose" in the panel yet.
//  - Added concept of an externally accessible refresh to the panel that
//    makes less sense in the controller class.
//  - "Summary state" makes more sense in the controller sense, but "cached
//    state" makes a little more sense in the panel sense.
// The "BeanPanel" class is roughly analogous to the "StatelessProcess" class.
//  - Both are objects that can be created, do their job, and then thrown away.

public abstract class PersistentConfiguredPanel<P, S> extends RNotifPanel {


    ////////////
    // FIELDS //
    ////////////

    // Descriptor (name/description) fields here??   If so, would probably need a method 'boolean canEditDescriptor()' to accompany them
    protected P params;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public PersistentConfiguredPanel(P params) {
        this.params = params;
    }


    //////////
    // CORE //
    //////////

    public final synchronized void update() {
        if(!isUpdatable()) {
            throw new IllegalStateException("PersistentConfiguredPanel cannot be updated");
        }
        updateInner();
    }

    public final synchronized void refresh() {
        if(!isRefreshable()) {
            throw new IllegalStateException("PersistentConfiguredPanel cannot be reset");
        }
        refreshInner();
    }
    public /*final*/ synchronized S createCachedState() {    // Want to make this final since its implementation should not change, but the
        if(!isStateCacheable()) {                            // desire to allow subclasses to promote the return type takes precedence.
            throw new IllegalStateException("PersistentConfiguredPanel cannot provide cached state");
        }
        return createCachedStateInner();
    }
    public final synchronized void setFromCachedState(S cachedState) {
        if(!isStateCacheable()) {
            throw new IllegalStateException("PersistentConfiguredPanel cannot accept cached state");
        }
        setFromCachedStateInner(cachedState);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Mutators

    public P getParams() {
        return params;
    }

    // Mutator

    public void setParams(P params) {
        this.params = params;
    }


    //////////////
    // ABSTRACT //
    //////////////

    // All of these methods MUST be abstract. Do not remove 'abstract' for convenience.
    // Reasoning is to force the controller designer to consciously decide and KNOW
    // how their controller works.

    public abstract boolean isUpdatable();        // Refers to being updated via new parameters
    public abstract boolean isRefreshable();      // Refers to updating the panel state according to the current parameters
    public abstract boolean isStateCacheable();

    protected abstract void updateInner();
    protected abstract void refreshInner();
    public abstract S createCachedStateInner();
    public abstract void setFromCachedStateInner(S cachedState);

}
