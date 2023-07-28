package replete.diff;

import replete.plugins.StatelessProcess;

public abstract class Differ<P extends DifferParams, T>
        extends StatelessProcess<P> {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public Differ(P params) {
        super(params);
    }


    //////////////
    // ABSTRACT //
    //////////////


    public abstract DiffResult diff(T o1, T o2);
}
