package replete.ui.params.hier;

import replete.params.hier.PropertyParams;
import replete.plugins.ExtensionPoint;
import replete.plugins.ParamsAndPanelUiGenerator;

public abstract class PropertyGenerator<P extends PropertyParams>
        extends ParamsAndPanelUiGenerator<P> implements ExtensionPoint {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    // Generics Nuance: Since this class has placed further restrictions on
    // the generic parameter, these overrides propagate that change to these
    // methods' return type, eliminating need for some casts in client code.
    @Override
    public abstract P createParams();
    @Override
    public abstract PropertyParamsPanel<P> createParamsPanel(Object... args);
}
