package replete.flux.viz.raindrops;

import java.util.UUID;

import javax.swing.ImageIcon;

import replete.flux.FluxPanelContext;
import replete.flux.streams.DataStream;
import replete.flux.viz.VisualizerGenerator;
import replete.flux.viz.VisualizerType;
import replete.text.StringUtil;
import replete.ui.BeanPanel;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;

public class RaindropsVisualizerGenerator
        extends VisualizerGenerator<RaindropsVisualizerParams, Void> {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "Raindrops";
    }

    @Override
    public String getDescription() {
        return StringUtil.createMissingText("Description");
    }

    @Override
    public ImageIcon getIcon() {
        return ImageLib.get(CommonConcepts.FAVORITE);
    }

    @Override
    public Class<?>[] getCoordinatedClasses() {
        return new Class[] {
            RaindropsVisualizerParams.class,
            RaindropsVisualizerParamsPanel.class,
            RaindropsVisualizer.class
        };
    }

    @Override
    public RaindropsVisualizerParams createParams() {
        return new RaindropsVisualizerParams();
    }

    @Override
    public RaindropsVisualizerParamsPanel createParamsPanel(Object... args) {
        return new RaindropsVisualizerParamsPanel();
    }

    @Override
    public BeanPanel<Void> createStatePanel(Object... args) {
        return null;
    }

    @Override
    public VisualizerType getType() {
        return VisualizerType.JAVA_2D;
    }

    @Override
    public RaindropsVisualizer createVisualizer(UUID trackedId, RaindropsVisualizerParams params, FluxPanelContext context) {
        return new RaindropsVisualizer(params, trackedId, context);
    }

    @Override
    public boolean appliesToDataStream(DataStream stream) {
        return stream.getSystemDescriptor().getName().contains("a");
    }

    @Override
    public int[] getMinMaxDataStreams() {
        return new int[] {1, 1};
    }
}
