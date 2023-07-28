package replete.plugins;

// This generator allows for the creation of a bean that is
// commonly thought of as the "parameters" for some other
// object/process/component.  This is only useful if the
// functionality you want to allow to be added extensibly to
// the platform corresponds to these semantics.  In that respect,
// this class is admittedly a little niche due to the word
// "Params" in the class name.

// Parameter (+ Panel) generators might be used to
//  1. Allow the user to simply configure some stand alone
//     "metadata" or "configuration" objects that are passed to
//     the system for inspection or to be attached to some other
//     objects.
//  2. Allow the user to parameterize some "stateless process",
//     which is just a block of code that runs according to some
//     parameters but which itself does not maintain any persistent
//     between executions.

// Should not implement ExtensionPoint itself, only subclasses
public abstract class ParamsUiGenerator<P> extends UiGenerator {


    //////////////
    // ABSTRACT //
    //////////////

    public abstract P createParams();
}
