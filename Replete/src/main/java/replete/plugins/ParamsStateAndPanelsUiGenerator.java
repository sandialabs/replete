package replete.plugins;

import replete.ui.BeanPanel;

// This generator combines both the spirit of the
// params and state generator, coupled with the ability
// to render/configure those objects using a UI panel.
// The words "params" and "state" refer to an object/
// process/component that can both be parameterized by
// a user and whose current state can be fetched at
// any time.  This class of course is only useful to
// those extension points that closely map to the
// semantics of "params" and "state".

// Should not implement ExtensionPoint itself, only subclasses
public abstract class ParamsStateAndPanelsUiGenerator<P, S> extends ParamsAndPanelUiGenerator<P> {


    //////////////
    // ABSTRACT //
    //////////////

    public abstract BeanPanel<S> createStatePanel(Object... args);

    // Typically subclasses will add another custom abstract
    // method that creates the object/process/component whose
    // state is constructed and returned.
    // public abstract <C extends StatefulComponent<S>> C createXxxxxx(/* ParamsFromUser */);
}
