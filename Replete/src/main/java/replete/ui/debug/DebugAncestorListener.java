package replete.ui.debug;

import java.awt.Component;

import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 * @author Derek Trumbo
 */

public class DebugAncestorListener extends DebugListener implements AncestorListener {

    public DebugAncestorListener() {}
    public DebugAncestorListener(String evNames) {
        super(evNames);
    }

    @Override
    public void ancestorAdded(AncestorEvent e) {
        debug("ancestorAdded", e);
    }
    @Override
    public void ancestorRemoved(AncestorEvent e) {
        debug("ancestorRemoved", e);
    }
    @Override
    public void ancestorMoved(AncestorEvent e) {
        debug("ancestorMoved", e);
    }

    private void debug(String evName, AncestorEvent e) {
        if(!acceptEvent(evName)) {
            return;
        }

        String debugStr = evName +
            "{anc=" + w(e.getAncestor()) +
            ",ancp=" + w(e.getAncestorParent()) +
            ",cmp=" + w(e.getComponent()) + "}";

        output(debugStr);
    }

    private String w(Component c) {
        if(c == null) {
            return "(none)";
        }
        return c.getClass().getSimpleName() + "@" + c.hashCode();
    }
}
