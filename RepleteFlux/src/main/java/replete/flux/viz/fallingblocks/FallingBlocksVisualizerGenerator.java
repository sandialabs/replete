package replete.flux.viz.fallingblocks;

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

public class FallingBlocksVisualizerGenerator
        extends VisualizerGenerator<FallingBlocksVisualizerParams, Void> {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "Falling Blocks";
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
            FallingBlocksVisualizerParams.class,
            FallingBlocksVisualizerParamsPanel.class,
            FallingBlocksVisualizer.class
        };
    }

    @Override
    public FallingBlocksVisualizerParams createParams() {
        return new FallingBlocksVisualizerParams();
    }

    @Override
    public FallingBlocksVisualizerParamsPanel createParamsPanel(Object... args) {
        return new FallingBlocksVisualizerParamsPanel();
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
    public FallingBlocksVisualizer createVisualizer(UUID trackedId, FallingBlocksVisualizerParams params, FluxPanelContext context) {
        return new FallingBlocksVisualizer(params, trackedId, context);
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
