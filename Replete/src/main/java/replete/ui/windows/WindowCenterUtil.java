package replete.ui.windows;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JDialog;

// TODO make more generic with CommonWindow
public class WindowCenterUtil {
    private static Map<JDialog, Point> preCenters = new HashMap<JDialog, Point>();
    public static void register(JDialog win) {
        int preCenterX = win.getX() + win.getWidth() / 2;
        int preCenterY = win.getY() + win.getHeight() / 2;
        preCenters.put(win, new Point(preCenterX, preCenterY));
    }
    public static void recenter(JDialog win) {
        Point preCenter = preCenters.get(win);
        if(preCenter != null) {
            int postCenterX = win.getX() + win.getWidth() / 2;
            int postCenterY = win.getY() + win.getHeight() / 2;
            int deltaX = (postCenterX - preCenter.x);
            int deltaY = (postCenterY - preCenter.y);
            int newX = win.getX() - deltaX;
            int newY = win.getY() - deltaY;
            win.setLocation(newX, newY);
        }
    }
}
