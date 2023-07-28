package replete.ui.debug;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

/**
 * @author Derek Trumbo
 */

public class DebugWindowStateListener extends DebugListener implements WindowStateListener {

    public DebugWindowStateListener() {}
    public DebugWindowStateListener(String evNames) {
        super(evNames);
    }

    @Override
    public void windowStateChanged(WindowEvent e) {
        debug("windowStateChanged", e);
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
