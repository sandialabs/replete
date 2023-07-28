package replete.flux.viz;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.JPanel;

import replete.flux.FluxPanelContext;
import replete.plugins.HumanDescriptor;
import replete.plugins.TrackedPersistentController;

// This class does not extend PersistentConfiguredPanel because the
// visualizer was needed to be one level of abstraction above panels
// in order to support integrated Java2D planes concurrently with
// Swing panels.  But, a PersistentController still provides us
// some decent structure to the role of this class.

public abstract class Visualizer
        <P extends VisualizerParams, S>
            extends TrackedPersistentController<P, S> {


    ////////////
    // FIELDS //
    ////////////

    // Like view, because we'll allow user to add their own names in this case
    // because multiple of same type can be instantiated.
    protected FluxPanelContext context;
    protected HumanDescriptor userDescriptor = new HumanDescriptor();
    protected List<String> dataStreamIds = new ArrayList<>();


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public Visualizer(P params, UUID trackedId, FluxPanelContext context) {
        super(params, trackedId);
        this.context = context;
        context.getDataStreamModel().addDataStreamModelChangeListener(e -> updateDataStreams());
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public HumanDescriptor getUserDescriptor() {
        return userDescriptor;
    }
    public List<String> getDataStreamIds() {
        return dataStreamIds;
    }

    // Mutators

    public Visualizer setUserDescriptor(HumanDescriptor userDescriptor) {
        this.userDescriptor = userDescriptor;
        return this;
    }
    public Visualizer setDataStreamIds(List<String> dataStreamIds) {
        this.dataStreamIds = dataStreamIds;
        updateDataStreams();
        return this;
    }


    //////////////
    // ABSTRACT //
    //////////////

    public abstract void updateDataStreams();


    //////////
    // MISC //
    //////////

    public JPanel getOrCreatePanel() {
        throw new UnsupportedOperationException();     // Only for visualizers of type VisualizerType.JAVA_SWING
    }
    public void paint(Graphics g) {
        throw new UnsupportedOperationException();     // Only for visualizers of type VisualizerType.JAVA_2D
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    // Generics Nuance: Since this class has placed further restrictions on
    // the generic parameter, these overrides propagate that change to these
    // methods' return type, eliminating need for some casts in client code.
    @Override
    public P getParams() {
        return super.getParams();
    }
    @Override
    public abstract S createSummaryState();
}
