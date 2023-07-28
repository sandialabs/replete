package replete.ui.sp;

import java.awt.Color;

public class RangeHighlightDescriptor extends RulerDescriptor {


    ///////////
    // FIELD //
    ///////////

    private Color color;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RangeHighlightDescriptor(Color color) {
        this(color, null, null);
    }
    public RangeHighlightDescriptor(Color color, String toolTip) {
        this(color, toolTip, null);
    }
    public RangeHighlightDescriptor(Color color, RulerClickListener clickListener) {
        this(color, null, clickListener);
    }
    public RangeHighlightDescriptor(Color color, String toolTip, RulerClickListener clickListener) {
        super(toolTip, clickListener);
        this.color = color;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public Color getColor() {
        return color;
    }
}
