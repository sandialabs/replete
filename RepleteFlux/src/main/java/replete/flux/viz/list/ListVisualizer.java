package replete.flux.viz.list;

import java.util.UUID;

import javax.swing.JPanel;

import replete.flux.FluxPanelContext;
import replete.flux.viz.Visualizer;
import replete.ui.lay.Lay;

public class ListVisualizer extends Visualizer<ListVisualizerParams, Void> {


    ////////////
    // FIELDS //
    ////////////

//    private TopUrls topUrls;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ListVisualizer(ListVisualizerParams params, UUID trackedId, FluxPanelContext context) {
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
        return Lay.temp("list blank");
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
