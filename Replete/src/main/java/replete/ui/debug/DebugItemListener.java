package replete.ui.debug;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Derek Trumbo
 */

public class DebugItemListener extends DebugListener implements ItemListener {

    public DebugItemListener() {}
    public DebugItemListener(String evNames) {
        super(evNames);
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        debug("itemStateChanged", e);
    }

    private void debug(String evName, ItemEvent e) {
        if(!acceptEvent(evName)) {
            return;
        }

        String debugStr = evName + "{" +
        e.getStateChange() + ", " +
        e.getItem() + "}";

        output(debugStr);
    }
}
