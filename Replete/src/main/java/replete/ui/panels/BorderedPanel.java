package replete.ui.panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.LayoutManager;

import replete.ui.lay.Lay;

// The width of this label is 12 more than a normal label,
// 6 on each side.

// The height of this label is 6 more than a normal label,
// 3 on the top and bottom.

public class BorderedPanel extends RPanel {


    ////////////
    // FIELDS //
    ////////////

    private static final Color DEFAULT_BUBBLE_BACKGROUND_COLOR = Color.WHITE;
    private static final Color DEFAULT_BUBBLE_BORDER_COLOR     = Color.BLACK;
    private static final int   DEFAULT_MARGIN_SIZE             = 5;

    private Color bubbleBackgroundColor;
    private Color bubbleBorderColor;
    private int cornerArc = 15;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public BorderedPanel() {
        super();
        init(DEFAULT_MARGIN_SIZE);
    }
    public BorderedPanel(int margin) {
        super();
        init(margin);
    }
    public BorderedPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
        init(DEFAULT_MARGIN_SIZE);
    }
    public BorderedPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
        init(DEFAULT_MARGIN_SIZE);
    }
    public BorderedPanel(LayoutManager layout) {
        super(layout);
        init(DEFAULT_MARGIN_SIZE);
    }
    public BorderedPanel(LayoutManager layout, int margin) {
        super(layout);
        init(margin);
    }
    private void init(int margin) {
        if(margin < 0) {
            margin = DEFAULT_MARGIN_SIZE;
        }
        Lay.hn(this, "eb=" + margin + ",opaque=false");
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public Color getBubbleBackgroundColor() {
        return bubbleBackgroundColor;
    }
    public Color getBubbleBorderColor() {
        return bubbleBorderColor;
    }
    public int getCornerArc() {
        return cornerArc;
    }

    // Mutators

    public BorderedPanel setBubbleBackgroundColor(Color bubbleBackgroundColor) {
        this.bubbleBackgroundColor = bubbleBackgroundColor;
        repaint();
        return this;
    }
    public BorderedPanel setBubbleBorderColor(Color bubbleBorderColor) {
        this.bubbleBorderColor = bubbleBorderColor;
        repaint();
        return this;
    }
    public BorderedPanel setCornerArc(int cornerArc) {
        this.cornerArc = cornerArc;
        repaint();
        return this;
    }
    public BorderedPanel setBorderMargin(int margin) {
        if(margin >= 0) {
            Lay.hn(this, "eb=" + margin);
        }
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected void paintComponent(Graphics g) {

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
        g.fillRoundRect(0, 0, getWidth() - 1, getHeight(), cornerArc, cornerArc);

        g.setColor(
            bubbleBorderColor == null ? DEFAULT_BUBBLE_BORDER_COLOR : bubbleBorderColor
        );
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerArc, cornerArc);

        boolean opaque = isOpaque();
        setOpaque(false);
        super.paintComponent(g);     // Paints the text only
        setOpaque(opaque);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
//        BorderedPanel pnl = new BorderedPanel();
//        pnl.setBubbleBackgroundColor(ColorLib.RED_LIGHT);
//        pnl.setBubbleBorderColor();
//        pnl.setCornerArc(10);
//
//        BorderedPanel pnl2 = new BorderedPanel();
//        pnl2.setBubbleBackgroundColor(ColorLib.GREEN_LIGHT);
//        pnl2.setBubbleBorderColor(ColorLib.GREEN_STRONG);
//        pnl2.setCornerArc(30);
//        pnl2.setBorderMargin(10);
//
//        Lay.BLtg(pnl2, "C", Lay.BL("N", Lay.lb("AAAAAA", "bg=yellow"), "bg=blue"));
//
//        Lay.BLtg(pnl, "C", pnl2);

        Lay.BLtg(Lay.fr("adfs"),
            "C", Lay.BL(
                "C", Lay.BL(
                    "C", Lay.BL("N", Lay.lb("AAAAAA", "bg=yellow"), "bg=blue"),
                    "bordered,arc=30,bmg=10,bbg=green_light,bbd=green_strong"
                ),
                "bordered,arc=10,bmg=70,bbg=red_light,bbd=red_bright"
            ),
            "size=600,center,visible"
        );
    }
}
