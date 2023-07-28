package replete.ui;

import java.awt.Color;

import replete.ui.lay.Lay;

// This class attempts to keep track of colors
// that are commonly used in constructing
// user interfaces.

public class ColorLib {
    public static final Color DEFAULT         = Lay.clr("238,238,238");  // Swing default

    public static final Color BLUE_DARK       = Lay.clr("0,28,188");
    public static final Color BLUE_BRIGHT     = Color.blue;
    public static final Color BLUE_LIGHT      = Lay.clr("216,255,255");  // OR 190,255,255
    public static final Color BLUE_VERY_LIGHT = Lay.clr("240,255,255");  // OR 190,255,255

    public static final Color GRAY_DARK       = Lay.clr("100");

    public static final Color GREEN_LIGHT     = Lay.clr("205,255,205");  // OR 190,255,190
    public static final Color GREEN_STRONG    = Lay.clr("0,170,0");

    public static final Color RED_LIGHT       = Lay.clr("255,205,205");  // OR 255,225,205; 255,190,190; 255, 216, 216
    public static final Color RED_BRIGHT      = Color.RED;
    public static final Color RED_STRONG      = Lay.clr("170,0,0");

    public final static Color ORANGE          = Lay.clr("255,125,0");    // Good for "warning" foreground text (Color.orange too bright generally)

    public static final Color YELLOW_DARK     = Lay.clr("198,104,15");
    public static final Color YELLOW_LIGHT    = Lay.clr("255,255,190");  // OR 255,255,135
}
