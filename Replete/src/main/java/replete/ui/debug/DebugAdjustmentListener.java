package replete.ui.debug;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

/**
 * @author Derek Trumbo
 */

public class DebugAdjustmentListener extends DebugListener implements AdjustmentListener {
    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        debug("adjustmentValueChanged", e);
    }

    private void debug(String evName, AdjustmentEvent e) {
        String debugStr = evName + "{" + e + "}";
        output(debugStr);
    }
}
