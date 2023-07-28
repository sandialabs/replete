package replete.flux;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;
import replete.flux.streams.FluxDataStreamModel;
import replete.flux.viz.Visualizer;
import replete.flux.viz.VisualizerGenerator;
import replete.flux.viz.VisualizerParams;
import replete.flux.viz.VisualizerType;
import replete.plugins.Generator;
import replete.text.StringUtil;
import replete.ui.lay.Lay;
import replete.ui.panels.RPanel;
import replete.ui.tabbed.RNotifPanel;
import replete.ui.tabbed.RTabbedPane;

// Right now, the presence of data streams is not considered part
// of the panel's "parameters".  Whether or not a data stream can
// even be attached to the panel and how it might be constructed
// or what data it would hold would seem to be up to the surrounding
// system/client code that wants to make this flux visualization
// panel available to its users.  That doesn't mean that the user
// couldn't have control over whether data streams are attached
// to the panel, but it would not be in an extensible manner.
// Rather, there might be a companion configuration dialog outside
// this panel designed specifically for the user to have control
// over whether the app should attach or remove data sets specific
// to the context of the application.  This doesn't mean that
// data streams themselves might not have parameters, just that
// due to the usage differences, those parameters are not necessarily
// what we consider to be flux panel's parameters with respect to
// what behavior the user has primary control over.

public class FluxPanel extends RNotifPanel implements FluxPanelContext {


    ////////////
    // FIELDS //
    ////////////

    private FluxPanelParams params = new FluxPanelParams();
    private FluxDataStreamModel dataStreamModel;
    private List<Visualizer> visualizers = new ArrayList<>();
    private JPanel pnlContainer;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public FluxPanel() {
        this(new FluxDataStreamModel());
    }
    public FluxPanel(FluxDataStreamModel dataStreamModel) {
        this.dataStreamModel = dataStreamModel;                // Can't be null
        Lay.BLtg(this,
            "C", pnlContainer = Lay.p(),
            "size=800,center"
        );
    }


    ////////////
    // UPDATE //
    ////////////

    private void rebuildVisualizers() {
        List<Visualizer> newVisualizers = new ArrayList<>();
        for(VisualizerCompoundParams vParams : params.getVisualizerCompoundParams()) {
            if(!vParams.isEnabled()) {
                continue;                // Basically pretends that doesn't exist, so it gets removed below
            }
            VisualizerParams cParams = vParams.getParams();
            UUID newVisId = cParams.getTrackedId();
            Visualizer visualizer = getVisualizer(newVisId);
            if(visualizer != null) {
                if(visualizer.isUpdatable()) {
                    visualizer.setParams(cParams);
                    visualizer.update();
                }
                visualizers.remove(visualizer);
            } else {
                VisualizerGenerator generator = Generator.lookup(cParams.getClass());
                visualizer = generator.createVisualizer(newVisId, cParams, this);
            }
            visualizer.setUserDescriptor(vParams.getUserDescriptor());
            visualizer.setDataStreamIds(vParams.getDataStreamIds());
            newVisualizers.add(visualizer);
        }
        for(Visualizer removeVis : visualizers) {
            removeVis.dispose();
        }
        visualizers = newVisualizers;
    }

    private void rebuildFramingPanels() {
        Component cmp;
        if(params.getCombineMethod() == VisualizerCombineMethod.TABS) {
            RTabbedPane tabs = Lay.TBL();
            for(Visualizer visualizer : visualizers) {
                JPanel pnlAdd = getPanelForVisualizer(visualizer);
                String title = computeTabTitle(visualizer);
                tabs.addTab(title, pnlAdd, visualizer.getTrackedId());
            }
            cmp = tabs;

        } else if(params.getCombineMethod() == VisualizerCombineMethod.VERTICAL_STACK) {
            RPanel pnl = Lay.GL(visualizers.size(), 1);
            for(Visualizer visualizer : visualizers) {
                JPanel pnlAdd = getPanelForVisualizer(visualizer);
                pnl.add(pnlAdd);
            }
            cmp = pnl;

        } else if(params.getCombineMethod() == VisualizerCombineMethod.HORIZ_STACK) {
            RPanel pnl = Lay.GL(1, visualizers.size());
            for(Visualizer visualizer : visualizers) {
                JPanel pnlAdd = getPanelForVisualizer(visualizer);
                pnl.add(pnlAdd);
            }
            cmp = pnl;

        } else {        // Must all be JAVA_2D visualizers
            Java2DBasedContainerPanel pnl = new Java2DBasedContainerPanel();
            for(Visualizer visualizer : visualizers) {
                pnl.add(visualizer);
            }
            cmp = pnl;
        }

        pnlContainer.removeAll();
        pnlContainer.add(cmp, BorderLayout.CENTER);
        pnlContainer.updateUI();
    }

    private String computeTabTitle(Visualizer visualizer) {
        String title = visualizer.getUserDescriptor().getName();
        if(StringUtil.isBlank(title)) {
            title = "(Unnamed)";
        }
        VisualizerGenerator generator = Generator.lookup(visualizer);
        title = generator.getName() + ": " + title;
        return title;
    }

    private JPanel getPanelForVisualizer(Visualizer visualizer) {
        JPanel pnlAdd;
        VisualizerGenerator generator = Generator.lookup(visualizer);
        if(generator.getType() == VisualizerType.JAVA_2D) {
            Java2DBasedContainerPanel pnl = new Java2DBasedContainerPanel();
            pnl.add(visualizer);
            pnlAdd = pnl;
        } else {
            JavaSwihgBasedContainerPanel pnl =
                new JavaSwihgBasedContainerPanel(visualizer, visualizer.getOrCreatePanel());
            pnlAdd = pnl;
        }
        return pnlAdd;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public FluxPanelParams getParams() {
        return params;
    }
    public FluxDataStreamModel getDataStreamModel() {
        return dataStreamModel;
    }

    // Mutators

    public FluxPanel setParams(FluxPanelParams params) {
        this.params = params;
        rebuildVisualizers();
        rebuildFramingPanels();
        return this;
    }
    public FluxPanel setDataStreamModel(FluxDataStreamModel dataStreamModel) {
        this.dataStreamModel.removeDataStreamModelChangeListener(internalDataStreamModelChangeListener);
        this.dataStreamModel = dataStreamModel;
        this.dataStreamModel.addDataStreamModelChangeListener(internalDataStreamModelChangeListener);
        fireDataStreamModelChangeNotifier();
        return this;
    }


    //////////
    // MISC //
    //////////

    public Visualizer getVisualizer(UUID visualizerTrackedId) {
        for(Visualizer visualizer : visualizers) {
            if(visualizer.getTrackedId().equals(visualizerTrackedId)) {
                return visualizer;
            }
        }
        return null;
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    private transient ChangeNotifier dataStreamModelChangeNotifier = new ChangeNotifier(this);
    public void addDataStreamModelChangeListener(ChangeListener listener) {
        dataStreamModelChangeNotifier.addListener(listener);
    }
    public void removeDataStreamModelChangeListener(ChangeListener listener) {
        dataStreamModelChangeNotifier.removeListener(listener);
    }
    private void fireDataStreamModelChangeNotifier() {
        dataStreamModelChangeNotifier.fireStateChanged();
    }

    private ChangeListener internalDataStreamModelChangeListener = e -> fireDataStreamModelChangeNotifier();


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private class Java2DBasedContainerPanel extends RPanel {
        private List<Visualizer> visualizers = new ArrayList<>();
        public void add(Visualizer visualizer) {
            visualizers.add(visualizer);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            for(Visualizer visualizer : visualizers) {   // Painted in params order, 0->(N-1)
                visualizer.paint(g);                     // which is also front-to-back.
            }
        }
    }

    private class JavaSwihgBasedContainerPanel extends RPanel {
        private Visualizer visualizer;
        private JavaSwihgBasedContainerPanel(Visualizer visualizer, JPanel pnlChild) {
            this.visualizer = visualizer;
            Lay.BLtg(this, "C", pnlChild);
        }
    }
}
