package replete.plugins;

// This generator allows for the creation of a bean that is
// commonly thought of as the "current state" of some
// object/process/component.  This class doesn't have a
// method that generates the state object itself, because
// that's up to the object/process/component to provide.
// However, it's possible that one could have this extension
// provide a "default" state object that could be used before
// the state was first generated.  This is only useful if the
// functionality you want to allow to be added extensibly to
// the platform corresponds to these semantics.  In that respect,
// this class is admittedly a little niche due to the word
// "State" in the class name.

// State (+ Panel) generators might be used to
//  1. Extract current state from components that don't also
//     get parameterized by the user.  For example, if a
//     State generator wrapped some OS diagnostics information
//     that would be an example of not being able to parameterize
//     the component generating the state, but still capturing
//     that state and returning it.

// Should not implement ExtensionPoint itself, only subclasses
public abstract class StateUiGenerator<S> extends UiGenerator {


    //////////////
    // ABSTRACT //
    //////////////

    // State is fairly different than parameters in that it's
    // not the user that is needing to create the bean on
    // the fly in the user interface, but rather already
    // loaded extensible code components will decide when
    // these beans will be created and how they are configured.
    // Thus, we don't pretend that the framework would ever need
    // to create a state bean dynamically.  However, there is
    // the *slight* chance that some day we might want to be
    // able to create a "default" bean that could be placed
    // into a "blank" state panel before a fully-populated
    // state bean was loaded into it.  Thus, this class only
    // exists for symmetry with the ParamsUiGenerator hierarchy.
//    public abstract S createDefaultState();

    // Typically subclasses will add another custom abstract
    // method that creates the object/process/component whose
    // state is constructed and returned.
    // public abstract <C extends StatefulComponent<S>> C createXxxxxx(/* ParamsNotFromUser */);
}
