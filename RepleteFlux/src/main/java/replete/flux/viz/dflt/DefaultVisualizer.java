package replete.flux.viz.dflt;

import java.util.UUID;

import javax.swing.Box;
import javax.swing.JPanel;

import replete.flux.FluxPanelContext;
import replete.flux.streams.DataStream;
import replete.flux.viz.Visualizer;
import replete.text.StringUtil;
import replete.ui.lay.Lay;

public class DefaultVisualizer extends Visualizer<DefaultVisualizerParams, Void> {


    ////////////
    // FIELDS //
    ////////////

//    private TopUrls topUrls;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public DefaultVisualizer(DefaultVisualizerParams params, UUID trackedId, FluxPanelContext context) {
        super(params, trackedId, context);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void updateDataStreams() {
        for(String id : dataStreamIds) {
            if(context.getDataStreamModel().getDataStreams().containsKey(id)) {
                System.out.println(getClass().getSimpleName() + "/FOUND " + id);
            } else {
                System.out.println(getClass().getSimpleName() + "/NOTFOUND " + id);
            }
        }
    }

    @Override
    public JPanel getOrCreatePanel() {
        JPanel pnl = Lay.BxL(
            Lay.lb("Default Visualizer"),
            pnl("Tracked ID", params.getTrackedId(), 1),
            pnl("User Descriptor", userDescriptor, 1),
            pnl("Params Value", params.getValue(), 1),
            pnl("Data Streams", null, 1), "eb=5"
        );
        for(String id : dataStreamIds) {
            pnl.add(pnl(id, null, 2));
            DataStream stream = context.getDataStreamModel().getDataStreams().get(id);
            if(stream == null) {
                pnl.add(pnl("NOTFOUND", null, 3));
            } else {
                pnl.add(pnl("System Descriptor", stream.getSystemDescriptor(), 3));
                pnl.add(pnl("Size", stream.size(), 3));
            }
        }
        pnl.add(Box.createVerticalGlue());
        return Lay.BL("C", Lay.sp(pnl));
    }

    private JPanel pnl(String key, Object msg, int level) {
        String spaces = StringUtil.spaces(4 * level);
        return Lay.FL("L",
            Lay.lb(spaces + key + ": ", "dimw=160"),
            msg != null ? Lay.lb("" + msg, "!bold") : null,
            "alignx=0,nogap,maxh=16"
        );

    }

    // Persistent Controller

    @Override
    public Void createSummaryState() {       // Not sure if used yet
        return null;
    }

    @Override
    public boolean isPauseable() {
        return false;                 // Not pausable.
    }
    @Override
    public boolean isUpdatable() {
        return true;
    }
    @Override
    public boolean isResettable() {
        return false;
    }

    @Override
    protected boolean isPausedInner() {
        return false;                 // Always false.
    }

    @Override
    protected void startInner() {
        // Do nothing - Immediately usable after initialization.
    }

    @Override
    protected void pauseInner() {
        // Do nothing - Not pausable.
    }

    @Override
    protected void updateInner() {
    }

    @Override
    protected void resetInner() {

    }

    @Override
    protected void disposeInner() {
        // Nothing to clean up
    }
}
