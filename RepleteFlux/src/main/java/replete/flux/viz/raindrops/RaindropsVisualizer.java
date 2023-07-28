package replete.flux.viz.raindrops;

import java.awt.Color;
import java.awt.Graphics;
import java.util.UUID;

import replete.flux.FluxPanelContext;
import replete.flux.viz.Visualizer;

public class RaindropsVisualizer extends Visualizer<RaindropsVisualizerParams, Void> {


    ////////////
    // FIELDS //
    ////////////

//    private TopUrls topUrls;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RaindropsVisualizer(RaindropsVisualizerParams params, UUID trackedId, FluxPanelContext context) {
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
    public void paint(Graphics g) {
        g.setColor(Color.red);
        g.fillOval(30, 30, 30, 30);
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
