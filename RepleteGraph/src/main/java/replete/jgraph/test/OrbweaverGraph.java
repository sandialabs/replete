package replete.jgraph.test;

import java.util.HashMap;
import java.util.Map;

import com.mxgraph.model.mxCell;

import replete.jgraph.BasicGraph;
import replete.pipeline.Pipeline;
import replete.pipeline.Stage;
import replete.pipeline.events.LinkEvent;
import replete.pipeline.events.LinkListener;
import replete.pipeline.events.StageContainerListener;
import replete.pipeline.events.StageEvent;
import replete.ui.GuiUtil;

public class OrbweaverGraph extends BasicGraph {


    ////////////
    // FIELDS //
    ////////////

    private Pipeline pipeline;
    private Map<StageWrapper, mxCell> stageCellMap = new HashMap<StageWrapper, mxCell>();


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public OrbweaverGraph(Pipeline pipeline) {
        this.pipeline = pipeline;

        // This class is just interested in structural changes to the pipeline.
        // However, because we can't be sure from which thread the pipeline
        // changes might come from, we should enforce that the changes to
        // JGraph happen on the UI thread.

        pipeline.addStageAddedListener(new StageContainerListener() {
            public void stateChanged(final StageEvent e) {
                GuiUtil.safeSync(new Runnable() {
                    @Override
                    public void run() {
                        addVertex(e.getSource());
                    }
                });
            }
        });
        pipeline.addStageRemovedListener(new StageContainerListener() {
            public void stateChanged(StageEvent e) {
                // Remove vertex from the graph: UI Thread
            }
        });
        pipeline.addLinkAddedListener(new LinkListener() {
            public void stateChanged(LinkEvent e) {
                // Add edges to the graph: UI Thread
            }
        });
        pipeline.addLinkRemovedListener(new LinkListener() {
            public void stateChanged(LinkEvent e) {
                // Remove edges from the graph: UI Thread
            }
        });

// DOES THE Orbweaver graph care about these changes?
//        pipeline.addOutputChangeListener(new OutputChangeListener() {
//            public void stateChanged(OutputChangeEvent e) {
//            }
//        });
//        pipeline.addInputChangeListener(new InputChangeListener() {
//            public void stateChanged(InputChangeEvent e) {
//
//            }
//        });
    }

    private void addVertex(Stage stage) {
        // TODO: check to make sure all incoming and outgoing edges point to stages also in the graph
        if(stageCellMap.containsKey(stage)) {
            throw new RuntimeException("Stage already exists");
        }

        getModel().beginUpdate();
        try {
            StageWrapper wrapper = new StageWrapper(stage);
            mxCell parent = (mxCell) getDefaultParent();     // Root Cell has null value.
            mxCell vertex = (mxCell) insertVertex(parent,
                null, wrapper,
                20, 20, 300, 200, "");
            // TODO: These numbers must not be hard coded in future
            stageCellMap.put(wrapper, vertex);
        } finally {
            getModel().endUpdate();
        }
    }
}
