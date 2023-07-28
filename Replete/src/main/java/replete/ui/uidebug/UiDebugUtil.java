package replete.ui.uidebug;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

import replete.ui.GuiUtil;

/**
 * @author Derek Trumbo
 */

public class UiDebugUtil {


    ////////////
    // FIELDS //
    ////////////

    // Constants

    private static Color[] COLOR_TABLE = new Color[] {
        new Color(250, 250, 210),
        new Color(210, 166, 121),
        new Color(255, 217, 179),
        new Color(179, 217, 255),
        new Color(204, 255, 204),
        new Color(246, 204, 255),
        new Color(255, 148, 184)
    };

    // User-modifiable

    // These global booleans affect components when those components
    // are first constructed.  After construction, components' debug
    // properties can be individually modified.  These global properties
    // provide developers a way to start applications with every debuggable
    // component with certain debug features enabled.  New components
    // created dynamically at runtime will obey these properties, regardless
    // if an RWindow.toggleDebugX() method has been called previously to affect
    // the other components on that window.
    private static boolean enableColor = false;
    private static boolean enableTicks = false;   // Affects components when they are first constructed

    // Internally managed

    private static int colorIdx = 0;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public static boolean isColorEnabled() {
        return enableColor;
    }
    public static boolean isTicksEnabled() {
        return enableTicks;
    }

    // Mutators

    public static void enableColor() {
        enableColor = true;
    }
    public static void enableTicks() {
        enableTicks = true;
    }


    //////////
    // MISC //
    //////////

    public static Color nextColor() {
        Color clr = COLOR_TABLE[colorIdx++];
        if(colorIdx == COLOR_TABLE.length) {
            colorIdx = 0;
        }
        return clr;
    }


    /////////////
    // DRAWING //
    /////////////

    public static void drawColor(Component c, Graphics g, boolean debugColorEnabled, Color debugBackgroundColor) {
        if(debugColorEnabled) {
            g.setColor(debugBackgroundColor);
            g.fillRect(0, 0, c.getWidth(), c.getHeight());
        }
    }

    public static void drawTicks(Component c, Graphics g, boolean debugTicksEnabled) {
        if(debugTicksEnabled) {
            int w = c.getWidth();
            int h = c.getHeight();

            Color g1 = new Color(225, 225, 225);
            Color g2 = new Color(245, 245, 245);

            for(int x = 0; x < w; x++) {
                if(((x + 1) % 100) == 0) {
                    g.setColor(g1);
                    g.drawLine(x, 0, x, h);
                } else if(((x + 1) % 10) == 0) {
                    g.setColor(g2);
                    g.drawLine(x, 0, x, h);
                }
            }

            for(int y = 0; y < h; y++) {
                if(((y + 1) % 100) == 0) {
                    g.setColor(g1);
                    g.drawLine(0, y, w, y);
                } else if(((y + 1) % 10) == 0) {
                    g.setColor(g2);
                    g.drawLine(0, y, w, y);
                }
            }

            g.setColor(Color.black);
            g.drawLine(0, 0, w, 0);
            for(int x = 0; x < w; x++) {
                if(((x + 1) % 100) == 0) {
                    g.drawLine(x, 0, x, 8);
                    int nw = GuiUtil.stringWidth(g, "" + x);
                    g.drawString("" + x, x - nw/2, 20);
                } else if(((x + 1) % 10) == 0) {
                    g.drawLine(x, 0, x, 4);
                }
            }

            g.drawLine(0, 0, 0, h);
            for(int y = 0; y < h; y++) {
                if(((y + 1) % 100) == 0) {
                    g.drawLine(0, y, 8, y);
                    g.drawString("" + y, 10, y + 5);
                } else if(((y + 1) % 10) == 0) {
                    g.drawLine(0, y, 4, y);
                }
            }

            g.setColor(Color.black);
            g.setFont(g.getFont().deriveFont(Font.PLAIN));
            g.drawString("w" + c.getWidth(), 10, 20);
            g.drawString("h" + c.getHeight(), 10, 36);
        }
    }

    public static void drawMouse(Component c, Graphics g,
                                   boolean debugMouseEnabled, int mouseX, int mouseY) {
        if(debugMouseEnabled) {
            int w = c.getWidth();
            int h = c.getHeight();

            g.setColor(Color.black);
            String pos = "mouse(" + mouseX + ", " + mouseY + ")";
            g.drawString(pos, c.getWidth() - GuiUtil.stringWidth(g, pos) - 10, c.getHeight() - 10);
            pos = "panel(" + w + ", " + h + ")";
            g.drawString(pos, c.getWidth() - GuiUtil.stringWidth(g, pos) - 10, c.getHeight() - 30);

            // Graphics.drawRect draws from x to x + width and from y and y + height.
            // So this actually draws a rectangle with dimensions 101x101.  (from pixels [99,199]).
            Rectangle r = new Rectangle(99, 99, 100, 100);
            g.drawRect(r.x, r.y, r.width, r.height);
            g.drawString("Rect(99, 99,", r.x + 10, r.y + 20);
            g.drawString("    100, 100) = ", r.x + 10, r.y + 40);
            g.drawString("101 x 101 Rect", r.x + 10, r.y + 60);

            // However, the contains method's extent does not extend that extra pixel.  So,
            // only the pixels [99,198] are valid (or an extent of x + width - 1).
            if(r.contains(mouseX, mouseY)) {
                g.setColor(Color.red);
                g.drawRect(r.x + 2, r.y + 2, r.width - 4, r.height - 4);
            }
        }
    }
}
