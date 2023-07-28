package replete.plugins;

// As currently designed, an instance of this class cannot be
// re-parameterized after construction.  A new instance must be
// constructed.

// Because this only represents a block of code, no need
// for Serializable!

// Subclasses will decide what actual methods needed to be added
// to enable this "process" to do what it's supposed to do.

// This class exists to provide a consistent, more rigorous
// structure for defining blocks of behavior, a very common
// practice in any piece of software.  Namely, that these
// blocks should be parameterized in a consistent manner and
// that there should be a clear delineation between state
// & code (parameters & methods).

public abstract class StatelessProcess<P> {


    ///////////
    // FIELD //
    ///////////

    protected P params;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public StatelessProcess(P params) {
        this.params = params;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public P getParams() {    // May not even be used by/needed for some sub classes ever.
        return params;
    }
}
