package replete.ui.debug;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * @author Derek Trumbo
 */

public class DebugMouseMotionListener extends DebugMouseListener implements MouseMotionListener {

    public DebugMouseMotionListener() {}
    public DebugMouseMotionListener(String evNames) {
        super(evNames);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        debug("mouseDragged", e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        debug("mouseMoved", e);
    }
}
