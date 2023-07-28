package replete.ui.debug;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

/**
 * @author Derek Trumbo
 */

public class DebugInternalFrameListener extends DebugListener implements InternalFrameListener {

    public DebugInternalFrameListener() {}
    public DebugInternalFrameListener(String evNames) {
        super(evNames);
    }

    @Override
    public void internalFrameOpened(InternalFrameEvent e) {
        debug("internalFrameOpened", e);
    }

    @Override
    public void internalFrameClosing(InternalFrameEvent e) {
        debug("internalFrameClosing", e);
    }

    @Override
    public void internalFrameClosed(InternalFrameEvent e) {
        debug("internalFrameClosed", e);
    }

    @Override
    public void internalFrameIconified(InternalFrameEvent e) {
        debug("internalFrameIconified", e);
    }

    @Override
    public void internalFrameDeiconified(InternalFrameEvent e) {
        debug("internalFrameDeiconified", e);
    }

    @Override
    public void internalFrameActivated(InternalFrameEvent e) {
        debug("internalFrameActivated", e);
    }

    @Override
    public void internalFrameDeactivated(InternalFrameEvent e) {
        debug("internalFrameDeactivated", e);
    }

    private void debug(String evName, InternalFrameEvent e) {
        if(!acceptEvent(evName)) {
            return;
        }

        JInternalFrame frame = e.getInternalFrame();
        String title = frame.getTitle();

        String debugStr = evName +
            (title != null ? "/" + title : "");

        output(debugStr);
    }
}
