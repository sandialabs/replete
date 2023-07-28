package replete.ui.debug;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * @author Derek Trumbo
 */

public class DebugFocusListener extends DebugListener implements FocusListener {

    @Override
    public void focusGained(FocusEvent e) {
        debug("focusGained", e);
    }

    @Override
    public void focusLost(FocusEvent e) {
        debug("focusLost", e);
    }

    private void debug(String evName, FocusEvent e) {
        String debugStr = evName + "{" + e + "}";
        output(debugStr);
    }
}
