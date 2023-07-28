
package replete.ui.button;

import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;

/**
 * A class to make creating a button with just an icon easy.
 *
 * @author Derek Trumbo
 */

public class IconToggleButton extends RToggleButton {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public IconToggleButton(Icon icon) {
        this(icon, null, -1, null);
    }

    public IconToggleButton(Icon icon, ActionListener initialListener) {
        this(icon, null, -1, initialListener);
    }

    public IconToggleButton(Icon icon, String ttt) {
        this(icon, ttt, -1, null);
    }

    public IconToggleButton(Icon icon, String ttt, ActionListener initialListener) {
        this(icon, ttt, -1, initialListener);
    }

    public IconToggleButton(Icon icon, int insets) {
        this(icon, null, insets, null);
    }

    public IconToggleButton(Icon icon, int insets, ActionListener initialListener) {
        this(icon, null, insets, initialListener);
    }

    public IconToggleButton(Icon icon, String ttt, int insets) {
        this(icon, ttt, insets, null);
    }

    public IconToggleButton(Icon icon, String ttt, int insets, ActionListener initialListener) {
        super(icon);
        if(initialListener != null) {
            addActionListener(initialListener);
        }
        if(insets < 0) {
            insets = 0;
        }
        Insets margins = new Insets(insets, insets, insets, insets);
        setMargin(margins);
        if(ttt != null) {
            setToolTipText(ttt);
        }
        setVerticalTextPosition(BOTTOM);
        setHorizontalTextPosition(CENTER);
    }

    public void setInsets(int insets) {
        Insets margins = new Insets(insets, insets, insets, insets);
        setMargin(margins);
    }


    //////////
    // MISC //
    //////////

    public IconToggleButton toImageOnly() {
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder());
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return this;
    }
}
