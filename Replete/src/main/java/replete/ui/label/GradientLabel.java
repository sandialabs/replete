package replete.ui.label;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;

import replete.ui.lay.Lay;
import replete.ui.text.RLabel;

public class GradientLabel extends RLabel {


    ////////////
    // FIELDS //
    ////////////

    public static final Color DEFAULT_LEFT_COLOR = Color.white;
    public static final Color DEFAULT_RIGHT_COLOR = Lay.clr("00137F");

    private Color leftColor = DEFAULT_LEFT_COLOR;
    private Color rightColor = DEFAULT_RIGHT_COLOR;
    private boolean useGradient = true;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public GradientLabel() {
        init();
    }
    public GradientLabel(Icon image, int horizontalAlignment) {
        super(image, horizontalAlignment);
        init();
    }
    public GradientLabel(Icon image) {
        super(image);
        init();
    }
    public GradientLabel(String text, Icon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
        init();
    }
    public GradientLabel(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
        init();
    }
    public GradientLabel(String text) {
        super(text);
        init();
    }

    private void init() {
        setOpaque(false);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public boolean isUseGradient() {
        return useGradient;
    }

    // Mutators (Builder)

    public GradientLabel setLeftColor(Color clr) {
        leftColor = clr;
        repaint();
        return this;
    }
    public GradientLabel setRightColor(Color clr) {
        rightColor = clr;
        repaint();
        return this;
    }
    public GradientLabel setColors(Color clrLeft, Color clrRight) {
        leftColor = clrLeft;
        rightColor = clrRight;
        repaint();
        return this;
    }
    public GradientLabel setUseGradient(boolean useGradient) {
        this.useGradient = useGradient;
        repaint();
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void setOpaque(boolean isOpaque) {
        super.setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if(useGradient) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setPaint(new GradientPaint(
                50, 0, leftColor,
                getWidth(), 0, rightColor));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        super.paintComponent(g);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        Lay.BLtg(Lay.fr("GL"),
            "N", Lay.lb("Test Gradient Label Paris France New Mexico", "gradient"),
            "S", Lay.lb("Test Gradient Label Paris France New Mexico",
                "gradient,gradclr1=red,gradclr2=blue"),
            "size=500,center,visible"
        );
    }
}
