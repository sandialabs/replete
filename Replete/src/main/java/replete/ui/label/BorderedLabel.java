package replete.ui.label;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JLabel;

import replete.ui.ColorLib;
import replete.ui.images.concepts.ImageLib;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.lay.Lay;
import replete.ui.text.RLabel;

// The width of this label is 12 more than a normal label,
// 6 on each side.

// The height of this label is 6 more than a normal label,
// 3 on the top and bottom.

public class BorderedLabel extends RLabel {


    ////////////
    // FIELDS //
    ////////////

    public static final Color DEFAULT_BUBBLE_FOREGROUND_COLOR = Color.BLACK;
    public static final Color DEFAULT_BUBBLE_BACKGROUND_COLOR = Color.WHITE;
    public static final Color DEFAULT_BUBBLE_BORDER_COLOR     = Color.BLACK;

    private Color bubbleBackgroundColor;
    private Color bubbleBorderColor;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public BorderedLabel() {
        this(null, (Icon) null, null);
    }
    public BorderedLabel(String text) {
        this(text, (Icon) null, null);
    }
    public BorderedLabel(Icon icon) {
        this(null, icon, null);
    }
    public BorderedLabel(ImageModelConcept concept) {
        this(null, ImageLib.get(concept), null);
    }
    public BorderedLabel(String text, Icon icon) {
        this(text, icon, null);
    }
    public BorderedLabel(String text, ImageModelConcept concept) {
        this(text, ImageLib.get(concept), null);
    }
    public BorderedLabel(String text, Icon icon, String hints) {
        setText(text);
        setIcon(icon);
        Lay.hn(this, hints, "eb=3tb6lr,opaque=false");
    }
    public BorderedLabel(String text, ImageModelConcept concept, String hints) {
        setText(text);
        setIcon(concept);
        Lay.hn(this, hints, "eb=3tb6lr,opaque=false");
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public Color getBubbleForegroundColor() {
        return getForeground();
    }
    public Color getBubbleBackgroundColor() {
        return bubbleBackgroundColor;
    }
    public Color getBubbleBorderColor() {
        return bubbleBorderColor;
    }

    // Mutators

    public BorderedLabel setBubbleForegroundColor(Color bubbleForegroundColor) {
        setForeground(bubbleForegroundColor);
        return this;
    }
    public BorderedLabel setBubbleBackgroundColor(Color bubbleBackgroundColor) {
        this.bubbleBackgroundColor = bubbleBackgroundColor;
        repaint();
        return this;
    }
    public BorderedLabel setBubbleBorderColor(Color bubbleBorderColor) {
        this.bubbleBorderColor = bubbleBorderColor;
        repaint();
        return this;
    }

    public BorderedLabel setDefaultColors() {
        setBubbleForegroundColor(DEFAULT_BUBBLE_FOREGROUND_COLOR);
        setBubbleBackgroundColor(DEFAULT_BUBBLE_BACKGROUND_COLOR);
        setBubbleBorderColor(DEFAULT_BUBBLE_BORDER_COLOR);
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected void paintComponent(Graphics g) {
        int arc = 10;

        // Although this label is not opaque by default, in case
        // client code makes it opaque again, we need to properly
        // deal with the opaque property.
        if(isOpaque()) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        g.setColor(
            bubbleBackgroundColor == null ? DEFAULT_BUBBLE_BACKGROUND_COLOR : bubbleBackgroundColor
        );
        g.fillRoundRect(0, 0, getWidth() - 1, getHeight(), arc, arc);

        g.setColor(
            bubbleBorderColor == null ? DEFAULT_BUBBLE_BORDER_COLOR : bubbleBorderColor
        );
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

        boolean opaque = isOpaque();
        setOpaque(false);
        super.paintComponent(g);     // Paints the text only
        setOpaque(opaque);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
//        JLabel lbl = new JLabel("ABC"); // new StatusLabel("Hi");
//        lbl.setBackground(ColorLib.RED_LIGHT);
//        lbl.setOpaque(true);
        JLabel lbl = new BorderedLabel((String) null);
        lbl.setBackground(ColorLib.RED_LIGHT);
        lbl.setForeground(ColorLib.RED_STRONG);
//        Lay.hn(lbl, "eb=3tb6lr");
        lbl.setOpaque(true);

        Lay.BLtg(Lay.fr("adfs"),
            "N", Lay.FL(lbl, "bg=yellow"),
            "size=600,center,visible"
        );
    }
}
