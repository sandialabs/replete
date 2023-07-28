package replete.ui.debug;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * @author Derek Trumbo
 */

public class DebugWindowListener extends DebugListener implements WindowListener {

    public DebugWindowListener() {}
    public DebugWindowListener(String evNames) {
        super(evNames);
    }

    @Override
    public void windowActivated(WindowEvent e) {
        debug("windowActivated", e);
    }

    @Override
    public void windowClosed(WindowEvent e) {
        debug("windowClosed", e);
    }

    @Override
    public void windowClosing(WindowEvent e) {
        debug("windowClosing", e);
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        debug("windowDeactivated", e);
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        debug("windowDeiconified", e);
    }

    @Override
    public void windowIconified(WindowEvent e) {
        debug("windowIconified", e);
    }

    @Override
    public void windowOpened(WindowEvent e) {
        debug("windowOpened", e);
    }

    private void debug(String evName, WindowEvent e) {
        if(!acceptEvent(evName)) {
            return;
        }

        Window w = e.getWindow();
        String title = null;

        if(w instanceof Dialog) {
            title = ((Dialog) w).getTitle();
        } else if(w instanceof Frame) {
            title = ((Frame) w).getTitle();
        }

        String debugStr = evName +
            (title != null ? "/" + title : "") +
            "{OldState=" + e.getOldState() + ", NewState=" + e.getNewState() + "}";

        output(debugStr);
    }

}
