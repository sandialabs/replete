package replete.ui.sdplus.panels;

import javax.swing.ImageIcon;

import replete.ui.sdplus.ScaleSetPanel;
import replete.ui.sdplus.UiDefaults;


/**
 * Displays the range and filter criteria for a continuous
 * scale for integral numbers.
 *
 * @author Derek Trumbo
 */

public class LongScalePanel extends ContScalePanel {

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public LongScalePanel(ScaleSetPanel p, LongScalePanelModel m) {
        super(p, m);
    }

    ///////////////
    // Pre-Build //
    ///////////////

    @Override
    protected ImageIcon getTitleIcon() {
        return UiDefaults.LONG_ICON;
    }

    //////////
    // Misc //
    //////////

    @Override
    protected String getDeselectAllLowerValue() {
        return "1";
    }

    @Override
    protected String getDeselectAllUpperValue() {
        return "0";
    }
}
