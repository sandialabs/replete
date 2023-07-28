package replete.ui.debug;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * @author Derek Trumbo
 */

public class DebugMouseWheelListener extends DebugMouseListener implements MouseWheelListener {

    public DebugMouseWheelListener() {}
    public DebugMouseWheelListener(String evNames) {
        super(evNames);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        debug("mouseWheelMoved", e);
    }
}
