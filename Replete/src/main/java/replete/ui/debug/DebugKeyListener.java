package replete.ui.debug;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @author Derek Trumbo
 */

public class DebugKeyListener extends DebugListener implements KeyListener {

    public DebugKeyListener() {}
    public DebugKeyListener(String evNames) {
        super(evNames);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        debug("keyPressed", e);
    }
    @Override
    public void keyReleased(KeyEvent e) {
        debug("keyReleased", e);
    }
    @Override
    public void keyTyped(KeyEvent e) {
        debug("keyTyped", e);
    }

    private void debug(String evName, KeyEvent e) {
        if(!acceptEvent(evName)) {
            return;
        }

        String debugStr = evName + "{" + e.getKeyChar() + "/" + e.getKeyCode() + " [" +
            (e.isControlDown() ? "Ctrl," : "") +
            (e.isAltDown() ? "Alt," : "") +
            (e.isShiftDown() ? "Shift," : "") +
            (e.isMetaDown() ? "Meta," : "") + "]}";

        output(debugStr);
    }
}
