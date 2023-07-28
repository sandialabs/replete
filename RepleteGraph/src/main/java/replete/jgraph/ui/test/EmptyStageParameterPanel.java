package replete.jgraph.ui.test;

import replete.jgraph.test.StageWrapper;
import replete.ui.lay.Lay;

public class EmptyStageParameterPanel extends StageParameterPanel {
    public EmptyStageParameterPanel(StageWrapper wrapper) {
        super(wrapper);
        Lay.GBLtg(this,
            Lay.lb("No Stage Parameters")
        );
    }
}
