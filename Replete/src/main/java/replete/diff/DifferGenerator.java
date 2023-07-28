package replete.diff;

import replete.plugins.ExtensionPoint;
import replete.plugins.ParamsAndPanelUiGenerator;

public abstract class DifferGenerator<P extends DifferParams, T>
        extends ParamsAndPanelUiGenerator<P> implements ExtensionPoint{

    public abstract <A extends Differ<P, T>> A createDiffer(P params);

    public abstract boolean canDiff(Class<?> clazz);


    ////////////////
    // OVERRIDDEN //
    ////////////////

    // Generics Nuance: Since this class has placed further restrictions on
    // the generic parameter, these overrides propagate that change to these
    // methods' return type, eliminating need for some casts in client code.
    @Override
    public abstract DifferParamsPanel<P> createParamsPanel(Object... args);

    @Override
    public abstract P createParams();

}
