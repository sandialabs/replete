package replete.ui.windows.notifications;

import java.awt.Color;

import javax.swing.border.Border;

public class ProgressBarStyle {


    ////////////
    // FIELDS //
    ////////////

    private Color foreground;
    private Color background;
    private Border border;
    private Color completedForeground;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ProgressBarStyle(Color foreground, Color background, Border border, Color completedForeground) {
        this.foreground = foreground;
        this.background = background;
        this.border = border;
        this.completedForeground = completedForeground;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public Color getForeground() {
        return foreground;
    }
    public Color getBackground() {
        return background;
    }
    public Border getBorder() {
        return border;
    }
    public Color getCompletedForeground() {
        return completedForeground;
    }
}
