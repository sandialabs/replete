package replete.jgraph.test;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraphView;

import replete.jgraph.BasicGraph;
import replete.jgraph.ui.BasicGraphComponent;
import replete.jgraph.ui.test.SleepStageParameterPanel;
import replete.jgraph.ui.test.StageParameterPanel;
import replete.jgraph.ui.test.StageWrapperPanel;
import replete.pipeline.Stage;
import replete.ui.lay.Lay;

public class OrbweaverGraphComponent extends BasicGraphComponent {


    ///////////
    // FIELD //
    ///////////

    private Map<mxCell, StageWrapperPanel> cellPanels = new HashMap<mxCell, StageWrapperPanel>();
    private Map<StageWrapperPanel, mxCell> panelCells = new HashMap<StageWrapperPanel, mxCell>();


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public OrbweaverGraphComponent(BasicGraph gr) {
        super(gr);

        Lay.hn(getViewport(), "bg=190");
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Component[] createComponents(mxCellState state) {
        mxCell cell = (mxCell) state.getCell();

        // If this cell is a vertex (not an edge or the root),
        // or this class has not been properly initialized
        // return null.
        if(!getGraph().getModel().isVertex(state.getCell()) || cellPanels == null) {
            return null;
        }

        StageWrapperPanel pnlCell = cellPanels.get(cell);

        if(pnlCell == null) {
            StageWrapper wrapper = (StageWrapper) cell.getValue();
            Stage stage = wrapper.getStage();
            StageParameterPanel pnlParams = null;
            if(stage instanceof SleepStage) {
                pnlParams = new SleepStageParameterPanel(wrapper);
            }
            final StageWrapperPanel pnlCellFinal = new StageWrapperPanel(wrapper, pnlParams);
            pnlCellFinal.addCollapseExpandListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    mxCell cell = panelCells.get(pnlCellFinal);
                    if(pnlCellFinal.isCollapsed()) {
                        moveCell(cell, 32);
                    } else {
                        moveCell(cell, 200);
                    }
                }
            });
            pnlCell = pnlCellFinal;
            cellPanels.put(cell, pnlCell);
            panelCells.put(pnlCell, cell);
        }

        return new Component[] {pnlCell};
    }

    public void setSelected(Object[] cells) {

        // Unselect all the stage panels.
        for(StageWrapperPanel pnlStage : cellPanels.values()) {
            pnlStage.setSelected(false);
        }

        // Select only the stage panels that were selected.
        for(Object cell : cells) {
            mxCell cell2 = (mxCell) cell;
            StageWrapperPanel pnlStage = cellPanels.get(cell2);
            if(pnlStage == null) {
                throw new RuntimeException("Should not have been null!");
            }
            pnlStage.setSelected(true);
        }
    }


    private void moveCell(mxCell cell, int height) {
        mxGraphModel model = (mxGraphModel) graph.getModel();
        mxGraphView view = getGraph().getView();
        model.beginUpdate();
        try {
            mxGeometry geom = cell.getGeometry();
            mxGeometry geom2 = (mxGeometry) geom.clone();
            geom2.setRect(geom.getX(), geom.getY(), geom.getWidth(), height);
            model.setGeometry(cell, geom2);
        } finally {
            model.endUpdate();
        }
    }
}
