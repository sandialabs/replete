package replete.ui.debug;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;

import javax.swing.SwingUtilities;

/**
 * @author Derek Trumbo
 */

public class DebugMouseListener extends DebugListener implements MouseListener {

    public DebugMouseListener() {}
    public DebugMouseListener(String evNames) {
        super(evNames);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        debug("mouseClicked", e);
    }
    @Override
    public void mouseEntered(MouseEvent e) {
        debug("mouseEntered", e);
    }
    @Override
    public void mouseExited(MouseEvent e) {
        debug("mouseExited", e);
    }
    @Override
    public void mousePressed(MouseEvent e) {
        debug("mousePressed", e);
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        debug("mouseReleased", e);
    }

    @Override
    public DebugMouseListener setContext(String context) {
        return (DebugMouseListener) super.setContext(context);
    }

    protected void debug(String evName, MouseEvent e) {
        if(!acceptEvent(evName)) {
            return;
        }

        String cl;
        int cc = e.getClickCount();

        if(cc == 0) {
            cl = "None";
        } else if(cc == 1) {
            cl = "Single";
        } else if(cc == 2) {
            cl = "Double";
        } else if(cc == 3) {
            cl = "Triple";
        } else {
            cl = "Multi";
        }

        String debugStr = evName + "{PUT-" + (e.isPopupTrigger() ? "Right" : "Left") + "-" + cl +
            " (" + e.getX() + "," + e.getY() + ") [" +
            (e.isControlDown() ? "Ctrl," : "") +
            (e.isAltDown() ? "Alt," : "") +
            (e.isShiftDown() ? "Shift," : "") +
            (e.isMetaDown() ? "Meta," : "") + "] /" + e.getWhen() + "} ";

        debugStr += "(SU-" + (SwingUtilities.isLeftMouseButton(e) ? "LEFT" : "") +
                             (SwingUtilities.isRightMouseButton(e) ? "RIGHT" : "") +
                             (SwingUtilities.isMiddleMouseButton(e) ? "MIDDLE" : "") + ")";

        if(evName.equals("mouseWheelMoved")) {
            debugStr += ", wheelRotation: " + ((MouseWheelEvent) e).getWheelRotation();
        }

        output(debugStr);
    }
}
