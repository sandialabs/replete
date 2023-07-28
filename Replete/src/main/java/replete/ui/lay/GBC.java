package replete.ui.lay;

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class GBC extends GridBagConstraints {


    ///////////////
    // CONSTANTS //
    ///////////////

    public static final int RL  = RELATIVE;
    public static final int RM  = REMAINDER;
    public static final int NO  = NONE;
    public static final int BO  = BOTH;
    public static final int H   = HORIZONTAL;
    public static final int V   = VERTICAL;
    public static final int C   = CENTER;
    public static final int N   = NORTH;
    public static final int NE  = NORTHEAST;
    public static final int E   = EAST;
    public static final int SE  = SOUTHEAST;
    public static final int S   = SOUTH;
    public static final int SW  = SOUTHWEST;
    public static final int W   = WEST;
    public static final int NW  = NORTHWEST;
    public static final int PS  = PAGE_START;
    public static final int PE  = PAGE_END;
    public static final int LS  = LINE_START;
    public static final int LE  = LINE_END;
    public static final int FLS = FIRST_LINE_START;
    public static final int FLE = FIRST_LINE_END;
    public static final int LLS = LAST_LINE_START;
    public static final int LLE = LAST_LINE_END;
    public static final int B   = BASELINE;
    public static final int BL  = BASELINE_LEADING;
    public static final int BT  = BASELINE_TRAILING;
    public static final int AB  = ABOVE_BASELINE;
    public static final int ABL = ABOVE_BASELINE_LEADING;
    public static final int ABT = ABOVE_BASELINE_TRAILING;
    public static final int BB  = BELOW_BASELINE;
    public static final int BBL = BELOW_BASELINE_LEADING;
    public static final int BBT = BELOW_BASELINE_TRAILING;


    /////////////
    // CREATOR //
    /////////////

    public static GBC c() {
        return new GBC();
    }


    //////////////
    // MUTATORS //   (Modified Builder)
    //////////////

    public GBC gx(int gridx) {
        this.gridx = gridx;
        return this;
    }
    public GBC gy(int gridy) {
        this.gridy = gridy;
        return this;
    }
    public GBC gw(int gridw) {
        gridwidth = gridw;
        return this;
    }
    public GBC gh(int gridh) {
        gridheight = gridh;
        return this;
    }
    public GBC wx(double weightx) {
        this.weightx = weightx;
        return this;
    }
    public GBC wy(double weighty) {
        this.weighty = weighty;
        return this;
    }
    public GBC a(int anchor) {
        this.anchor = anchor;
        return this;
    }
    public GBC f(int fill) {
        this.fill = fill;
        return this;
    }
    public GBC i(Insets insets) {
        this.insets = insets;
        return this;
    }
    public GBC ix(int ipadx) {
        this.ipadx = ipadx;
        return this;
    }
    public GBC iy(int ipady) {
        this.ipady = ipady;
        return this;
    }
}