package replete.plugins;

import replete.ui.BeanPanel;

// This generator allows for the creation of a bean that is
// commonly thought of as the "current state" of some
// object/process/component, but which also allows for the
// creation of a panel using which you can configure that
// bean in a user interface.  This is only useful if the
// functionality you want to allow to be added extensibly to
// the platform corresponds to these semantics.  In that respect,
// this class is admittedly a little niche due to the word
// "State" in the class name.

// Should not implement ExtensionPoint itself, only subclasses
public abstract class StateAndPanelUiGenerator<S> extends StateUiGenerator<S> {


    //////////////
    // ABSTRACT //
    //////////////

    public abstract BeanPanel<S> createStatePanel(Object... args);
}
