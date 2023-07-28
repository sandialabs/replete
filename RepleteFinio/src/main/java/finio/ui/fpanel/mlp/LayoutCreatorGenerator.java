package finio.ui.fpanel.mlp;

import javax.swing.JPanel;

import replete.plugins.ExtensionPoint;
import replete.plugins.ParamsAndPanelUiGenerator;

public abstract class LayoutCreatorGenerator<P extends LayoutParams>
        extends ParamsAndPanelUiGenerator<P> implements ExtensionPoint {


    //////////////
    // ABSTRACT //
    //////////////

    public abstract <C extends LayoutCreator<P>> C createLayoutCreator(P params, JPanel pnl);


    ////////////////
    // OVERRIDDEN //
    ////////////////

    // Generics Nuance: Since this class has placed further restrictions on
    // the generic parameter, these overrides propagate that change to these
    // methods' return type, eliminating need for some casts in client code.
    @Override
    public abstract P createParams();
    @Override
    public abstract LayoutParamsPanel<P> createParamsPanel(Object... args);
}
