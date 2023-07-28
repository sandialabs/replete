package replete.ui.cursors;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import replete.ui.lay.Lay;
import replete.util.ReflectionUtil;

public class CursorUtil {

    private static Map<Component, Cursor> previousDesiredCursors = new HashMap<>();

    public static void changeCursor(Component cmp, Cursor newCursor) {
        Cursor previousDesiredCursor = getDesiredCursor(cmp);
        if(previousDesiredCursor != newCursor) {
            previousDesiredCursors.put(cmp, previousDesiredCursor);
            cmp.setCursor(newCursor);
        }
    }

    public static void revertCursor(Component cmp) {
        if(previousDesiredCursors.containsKey(cmp)) {
            Cursor previousDesiredCursor = previousDesiredCursors.get(cmp);
            cmp.setCursor(previousDesiredCursor);
            previousDesiredCursors.remove(cmp);
        }
    }

    public static Cursor getDesiredCursor(Component cmp) {
        return ReflectionUtil.get(cmp, "cursor");
    }
    public static Cursor getDisplayedCursor(Component cmp) {
        return cmp.getCursor();
    }

    public static Cursor getCustomCursor(ImageIcon icon, Dimension size, Point hotSpot) {
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension dim = kit.getBestCursorSize(size.width, size.height);  // Desired
        int w = dim.width;
        int h = dim.height;
        BufferedImage buffered = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = buffered.createGraphics();
        g2.drawImage(icon.getImage(), 0, 0, w, h, null);
        return kit.createCustomCursor(icon.getImage(), hotSpot, "myCursor");
    }

    public static Cursor getCustomCursor() {
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension dim = kit.getBestCursorSize(48, 48);
        BufferedImage buffered = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
        Shape circle = new Ellipse2D.Float(0, 0, dim.width - 1, dim.height - 1);
        Graphics2D g = buffered.createGraphics();
        g.setColor(Color.BLUE);
        g.draw(circle);
        g.setColor(Color.RED);
        int centerX = (dim.width - 1) /2;
        int centerY = (dim.height - 1) / 2;
        g.drawLine(centerX, 0, centerX, dim.height - 1);
        g.drawLine(0, centerY, dim.height - 1, centerY);
        g.dispose();
        return kit.createCustomCursor(buffered, new Point(centerX, centerY), "myCursor");
    }


    //////////
    // TEST //
    //////////

    public static void mainx(String[] args) {
        JFrame fr;
        Lay.BLtg(fr = Lay.fr("Asdfs"),
            "N", Lay.FL("L", Lay.lb("asdfasdf"), Lay.tx("asdfasf", 22)),
            "C", null,
            "S", Lay.FL("R", Lay.btn("adfasf")),
            "size=400,center,visible"
        );
        fr.setCursor(getCustomCursor());
    }
}
