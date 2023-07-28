package replete.ui.sdplus.color;

import java.awt.Color;

/**
 * Utility functions and data relating to color and color tables.
 *
 * @author rsimons
 */
public class ColorUtil {

    private static final Color defaultMinGradientColor = Color.blue;
    private static final Color defaultMaxGradientColor = Color.red;

    private static final Color[] defaultEnumColors = {
            new Color(0.0f, 0.0f, 1.0f),   // Blue
            new Color(0.0f, 1.0f, 0.0f),   // Green
            new Color(1.0f, 0.0f, 0.0f),   // Red
            new Color(0.0f, 1.0f, 1.0f),   // Aqua
            new Color(0.75f, 0.75f, 0.0f), // Dark Yellow
            new Color(0.5f, 0.0f, 0.0f),   // Dark Brown
            new Color(0.0f, 0.5f, 0.0f),   // Dark Green
            new Color(0.9f, 0.4f, 0.0f),   // Orange
            new Color(0.65f, 0.0f, 0.65f), // Magenta
            new Color(0.25f,0.25f,0.25f),  // Dark grey
            new Color(1.0f, 0.5f, 1.0f),   // Pink
            new Color(0.0f, 0.5f, 1.0f),   // Light Blue
            new Color(0.5f, 0.25f, 0.0f),  // Light Brown
            new Color(1.0f, 0.0f, 1.0f),   // Purple
            new Color(0.65f,0.65f,0.65f),  // Light grey
            new Color(1.0f, 0.5f, 0.5f)    // Peach
    };

    public static int getDefaultEnumScaleColorCount() {
        return defaultEnumColors.length;
    }
    public static Color getDefaultColor(int idx) {
        return defaultEnumColors[idx % defaultEnumColors.length];
    }

    public static Color getDefaultMinGradientColor() {
        return defaultMinGradientColor;
    }

    public static Color getDefaultMaxGradientColor() {
        return defaultMaxGradientColor;
    }

    /** Move each component halfway toward full intensity. */
    public static Color lighten(Color clr) {
        int red = clr.getRed();
        red = red + (255 - red) / 2;
        int green = clr.getGreen();
        green = green + (255 - green) / 2;
        int blue = clr.getBlue();
        blue = blue + (255 - blue) / 2;
        return new Color(red, green, blue);
    }

    public static Color lighten(Color color, float amount) {
      int red = (int) ((color.getRed() * (1 - amount) / 255 + amount) * 255);
      int green = (int) ((color.getGreen() * (1 - amount) / 255 + amount) * 255);
      int blue = (int) ((color.getBlue() * (1 - amount) / 255 + amount) * 255);
      return new Color(red, green, blue);
    }

    public static Color changeUniform(Color clr, int delta) {
        return new Color(
            Math.min(255, Math.max(0, clr.getRed() + delta)),
            Math.min(255, Math.max(0, clr.getGreen() + delta)),
            Math.min(255, Math.max(0, clr.getBlue() + delta)));
    }
}
