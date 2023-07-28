package replete.flux.viz;

import replete.ui.BeanPanel;

public abstract class VisualizerParamsPanel
        <P extends VisualizerParams> extends BeanPanel<P> {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public VisualizerParamsPanel() {
        // Remove if turns out not needed
    }


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
}
