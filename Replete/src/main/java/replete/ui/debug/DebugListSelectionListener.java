package replete.ui.debug;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * @author Derek Trumbo
 */

public class DebugListSelectionListener extends DebugListener implements ListSelectionListener {

    public DebugListSelectionListener() {}
    public DebugListSelectionListener(String evNames) {
        super(evNames);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        debug("valueChanged", e);
    }

    private void debug(String evName, ListSelectionEvent e) {
        if(!acceptEvent(evName)) {
            return;
        }

        String debugStr = evName + "{" + e.getFirstIndex() + "-" +
        e.getLastIndex() + ", isAdj: " +
        e.getValueIsAdjusting() + "}";

        output(debugStr);
    }
}
