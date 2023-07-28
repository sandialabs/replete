package replete.diff;

import replete.ui.BeanPanel;

public class DifferParamsPanel<P extends DifferParams> extends BeanPanel<P> {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    // Generics Nuance: Since this class has placed further restrictions on
    // the generic parameter, these overrides propagate that change to these
    // methods' return type, eliminating need for some casts in client code.
    @Override
    public P get() {
        return super.get();
    }

    @Override
    public void set(P params) {
        super.set(params);
    }
}
