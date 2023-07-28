package replete.jgraph;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.SwingWorker;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphComponent.mxGraphControl;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphView;

import replete.jgraph.test.OrbweaverGraph;
import replete.jgraph.test.OrbweaverLayeredGraphPanel;
import replete.jgraph.test.SleepStage;
import replete.jgraph.ui.BasicLayeredGraphPanel;
import replete.pipeline.Pipeline;
import replete.ui.lay.Lay;
import replete.ui.lay.LayHints;
import replete.ui.text.editor.REditor;
import replete.ui.windows.Dialogs;
import replete.ui.windows.escape.EscapeFrame;

public class GraphDemoFrame extends EscapeFrame {


    ////////////
    // FIELDS //
    ////////////

    public static REditor ed;
    private SleepStage testStage;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public GraphDemoFrame() {
        super("Title Here");
        LayHints.addGlobalHints("nodebug");

        final Pipeline pipeline = new Pipeline("BLAH BLAH");
        OrbweaverGraph graph = new OrbweaverGraph(pipeline);

        JButton btnAdd = new JButton("Add Stage");
        JButton btnDirty = new JButton("Execute / Change Input");
        JButton btnAlign = new JButton("Align");

        final JSplitPane spl;
        final BasicLayeredGraphPanel pnlGraph;

        Lay.BLtg(this,
            "N", Lay.FL(btnAdd, btnDirty, btnAlign),
            "C", spl = Lay.SPL(
                pnlGraph = new OrbweaverLayeredGraphPanel(graph),
                ed = Lay.ed()
            ),
            "size=[1000,800],center=2"
        );

        ed.setShowStatusLine(false);
        ed.setEditable(false);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                spl.setDividerLocation(500);
            }
        });
        btnDirty.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!testStage.isDirty()) {
//                    testStage.setInput("millis", 1000);
                } else {
                    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            testStage.execute();
                            return null;
                        }
                        @Override
                        protected void done() {
                            try {
                                get();
                            } catch(Exception e) {
                                Dialogs.showDetails(GraphDemoFrame.this, "An error has occurred executing the stage.", "Error", e);
                            }
                        }
                    };
                    worker.execute();
                }
            }
        });
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pipeline.addStage(testStage = new SleepStage("Sleep Stage"));
            }
        });
        btnAlign.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              mxGraph graph = pnlGraph.getGraph();
              mxGraphModel model = (mxGraphModel) graph.getModel();
              mxGraphView view = pnlGraph.getGraph().getView();
              mxGraphComponent cmp = pnlGraph.getGraphComponent();
              mxGraphControl ctrl = pnlGraph.getGraphControl();
              mxCell dp = (mxCell) pnlGraph.getGraph().getDefaultParent();

              model.beginUpdate();
              try {
//                  mxCellStatePreview preview = new mxCellStatePreview(cmp, false);
                  for(int i = 0; i < dp.getChildCount(); i++) {
                      mxCell child = (mxCell) dp.getChildAt(i);
                      mxCellState state = view.getState(child);
                      System.out.println(state.getX() + "x" + state.getY());
                      mxGeometry geom = child.getGeometry();
                      mxGeometry geom2 = (mxGeometry) geom.clone();
                      geom2.setRect(50 + i *200, geom2.getY(), geom2.getWidth(), geom2.getHeight());
                      model.setGeometry(child, geom2);
                      System.out.println(state.getX() + "x" + state.getY());
                      System.out.println();
//                      preview.moveState(state, 100, 0);
//                      graph.moveCells(new Object[] {child}, 50, 50);
                  }
//                  mxRectangle dirty = preview.show();
//                  view.revalidate();
//                  ctrl.repaint(dirty.getRectangle());

//                  System.out.println("DIR=" + dirty);

              } finally {
                  model.endUpdate();
              }
//              pnlGraph.getGraphControl().repaint();
              pnlGraph.updateUI();
            }
        });
    }
}
