
package replete.ui.button;

import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;

import replete.ui.images.concepts.ImageModelConcept;

/**
 * A class to make creating a button with just an icon easy.
 *
 * @author Derek Trumbo
 */

public class IconButton extends RButton {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public IconButton(Icon icon) {
        this(icon, null, -1, null);
    }
    public IconButton(ImageModelConcept concept) {
        this(concept, null, -1, null);
    }
    public IconButton(Icon icon, ActionListener initialListener) {
        this(icon, null, -1, initialListener);
    }
    public IconButton(Icon icon, String ttt) {
        this(icon, ttt, -1, null);
    }
    public IconButton(ImageModelConcept concept, String ttt) {
        this(concept, ttt, -1, null);
    }
    public IconButton(Icon icon, String ttt, ActionListener initialListener) {
        this(icon, ttt, -1, initialListener);
    }
    public IconButton(Icon icon, int insets) {
        this(icon, null, insets, null);
    }
    public IconButton(ImageModelConcept concept, int insets) {
        this(concept, null, insets, null);
    }
    public IconButton(Icon icon, int insets, ActionListener initialListener) {
        this(icon, null, insets, initialListener);
    }
    public IconButton(Icon icon, String ttt, int insets) {
        this(icon, ttt, insets, null);
    }
    public IconButton(ImageModelConcept concept, String ttt, int insets) {
        this(concept, ttt, insets, null);
    }
    public IconButton(Icon icon, String ttt, int insets, ActionListener initialListener) {
        super(icon);
        init(ttt, insets, initialListener);
    }
    public IconButton(ImageModelConcept concept, String ttt, int insets, ActionListener initialListener) {
        super(concept);
        init(ttt, insets, initialListener);
    }

    private void init(String ttt, int insets, ActionListener initialListener) {
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

    public IconButton toImageOnly() {
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder());
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return this;
    }
}
