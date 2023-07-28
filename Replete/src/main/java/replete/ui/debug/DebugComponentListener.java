package replete.ui.debug;

import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * @author Derek Trumbo
 */

public class DebugComponentListener extends DebugListener implements ComponentListener {

    public DebugComponentListener() {}
    public DebugComponentListener(String evNames) {
        super(evNames);
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        debug("componentHidden", e);
    }
    @Override
    public void componentMoved(ComponentEvent e) {
        debug("componentMoved", e);
    }
    @Override
    public void componentResized(ComponentEvent e) {
        debug("componentResized", e);
    }
    @Override
    public void componentShown(ComponentEvent e) {
        debug("componentShown", e);
    }

    private void debug(String evName, ComponentEvent e) {
        if(!acceptEvent(evName)) {
            return;
        }

        Component cmp = e.getComponent();
        String debugStr = evName + "{vis=" + cmp.isVisible() +
            ", pos=(" + cmp.getX() + "," + cmp.getY() + ")" +
            ", size=[" + cmp.getWidth() + "," + cmp.getHeight() + "]}";

        output(debugStr);
    }
}
