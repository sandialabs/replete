package replete.flux.viz.dflt;

import java.util.UUID;

import javax.swing.ImageIcon;

import replete.flux.FluxPanelContext;
import replete.flux.streams.DataStream;
import replete.flux.viz.VisualizerGenerator;
import replete.flux.viz.VisualizerType;
import replete.ui.BeanPanel;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;

public class DefaultVisualizerGenerator
        extends VisualizerGenerator<DefaultVisualizerParams, Void> {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "Default";
    }

    @Override
    public String getDescription() {
        return
            "This visualizer serves as a trivial diagnostics visualizer to demonstrate the " +
            "Flux framework is properly working.  It accepts all data streams and displays " +
            "basic information about itself and any attached streams.";
    }

    @Override
    public ImageIcon getIcon() {
        return ImageLib.get(CommonConcepts.FAVORITE);
    }

    @Override
    public Class<?>[] getCoordinatedClasses() {
        return new Class[] {
            DefaultVisualizerParams.class,
            DefaultVisualizerParamsPanel.class,
            DefaultVisualizer.class
        };
    }

    @Override
    public DefaultVisualizerParams createParams() {
        return new DefaultVisualizerParams();
    }

    @Override
    public DefaultVisualizerParamsPanel createParamsPanel(Object... args) {
        return new DefaultVisualizerParamsPanel();
    }

    @Override
    public BeanPanel<Void> createStatePanel(Object... args) {
        return null;
    }

    @Override
    public VisualizerType getType() {
        return VisualizerType.JAVA_SWING;
    }

    @Override
    public DefaultVisualizer createVisualizer(UUID trackedId, DefaultVisualizerParams params, FluxPanelContext context) {
        return new DefaultVisualizer(params, trackedId, context);
    }

    @Override
    public boolean appliesToDataStream(DataStream stream) {
        return stream.getSystemDescriptor().getName().contains("a") || stream.getSystemDescriptor().getName().contains("u");
    }

    @Override
    public int[] getMinMaxDataStreams() {
        return new int[] {0, 2};
    }
}
