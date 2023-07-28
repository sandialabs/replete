package replete.flux.viz.list;

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

public class ListVisualizerGenerator
        extends VisualizerGenerator<ListVisualizerParams, Void> {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "List";
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
            ListVisualizerParams.class,
            ListVisualizerParamsPanel.class,
            ListVisualizer.class
        };
    }

    @Override
    public ListVisualizerParams createParams() {
        return new ListVisualizerParams();
    }

    @Override
    public ListVisualizerParamsPanel createParamsPanel(Object... args) {
        return new ListVisualizerParamsPanel();
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
    public ListVisualizer createVisualizer(UUID trackedId, ListVisualizerParams params, FluxPanelContext context) {
        return new ListVisualizer(params, trackedId, context);
    }

    @Override
    public boolean appliesToDataStream(DataStream stream) {
        return stream.getSystemDescriptor().getName().contains("n");
    }

    @Override
    public int[] getMinMaxDataStreams() {
        return new int[] {1, Integer.MAX_VALUE};
    }
}
