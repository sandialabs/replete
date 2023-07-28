package replete.jgraph.test;

import java.util.List;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel.mxGeometryChange;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;

import replete.jgraph.BasicGraph;
import replete.jgraph.ui.BasicGraphComponent;
import replete.jgraph.ui.BasicLayeredGraphPanel;

public class OrbweaverLayeredGraphPanel extends BasicLayeredGraphPanel {


    ////////////
    // FIELDS //
    ////////////

    private OrbweaverGraph graph;
    private OrbweaverGraphComponent pnlComponent;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public OrbweaverLayeredGraphPanel(OrbweaverGraph gr) {
        super(gr);
        graph = gr;

        graph.addListener(null, new mxIEventListener() {
            @Override
            public void invoke(Object sender, mxEventObject evt) {
                updateUI();
                pnlOutline.updateUI();
            }
        });
        graph.getSelectionModel().addListener("change", new mxIEventListener() {
            public void invoke(Object sender, mxEventObject evt) {
                Object[] cells = graph.getSelectionCells();
                pnlComponent.setSelected(cells);
            }
        });
        graph.getModel().addListener("change", new mxIEventListener() {
            public void invoke(Object sender, mxEventObject evt) {
                System.out.println("CHANGE!");
                if(evt.getProperties().containsKey("changes")) {
                    Object o = evt.getProperties().get("changes");
                    if(o instanceof List) {
                        List changes = (List) o;
                        for(Object elem : changes) {
                            if(elem instanceof mxGeometryChange) {
                                mxGeometryChange chg = (mxGeometryChange) elem;
                                mxGeometry geom = chg.getGeometry();
                                System.out.println(" NEW: " + geom);
                                evt.consume();
                            }
                        }
                    }
                }
            }
        });
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected BasicGraphComponent createGraphComponent(BasicGraph graph) {
        return pnlComponent = new OrbweaverGraphComponent(graph);
    }
}
