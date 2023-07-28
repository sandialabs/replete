package replete.ui.debug;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Derek Trumbo
 */

public class DebugActionListener extends DebugListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        debug("actionPerformed", e);
    }

    private void debug(String evName, ActionEvent e) {
        String debugStr = evName + "{" + e + "}";
        output(debugStr);
    }
}
