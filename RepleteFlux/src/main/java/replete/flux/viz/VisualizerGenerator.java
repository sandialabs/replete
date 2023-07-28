package replete.flux.viz;

import java.util.UUID;

import replete.flux.FluxPanelContext;
import replete.flux.streams.DataStream;
import replete.plugins.ExtensionPoint;
import replete.plugins.ParamsStateAndPanelsUiGenerator;

public abstract class VisualizerGenerator
        <P extends VisualizerParams, S>
            extends ParamsStateAndPanelsUiGenerator<P, S>
                implements ExtensionPoint {


    //////////////
    // ABSTRACT //
    //////////////

    public abstract VisualizerType getType();
    public abstract <A extends Visualizer<P, ?>> A createVisualizer(UUID trackedId, P params, FluxPanelContext context);
    public abstract boolean appliesToDataStream(DataStream stream);
    public abstract int[] getMinMaxDataStreams();


    ////////////////
    // OVERRIDDEN //
    ////////////////

    // Generics Nuance: Since this class has placed further restrictions on
    // the generic parameter, these overrides propagate that change to these
    // methods' return type, eliminating need for some casts in client code.
    @Override
    public abstract P createParams();
    @Override
    public abstract VisualizerParamsPanel<P> createParamsPanel(Object... args);
}
