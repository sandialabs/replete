package replete.jgraph.ui.test;

import javax.swing.JPanel;

import replete.jgraph.test.StageWrapper;
import replete.pipeline.Stage;
import replete.ui.lay.Lay;

public abstract class StageParameterPanel extends JPanel {


    ////////////
    // FIELDS //
    ////////////

    protected StageWrapper wrapper;
    protected Stage stage;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public StageParameterPanel(StageWrapper wrapper) {
        this.wrapper = wrapper;
        stage = wrapper.getStage();
        Lay.hn(this, "bg=250");
    }
}
