package replete.ui.sdplus.panels;

import java.awt.Color;

import javax.swing.ImageIcon;

import replete.ui.sdplus.ScaleSetPanel;
import replete.ui.sdplus.UiDefaults;


/**
 * Displays the range and filter criteria for a continuous
 * scale for date values.
 *
 * @author Derek Trumbo
 */

public class DateScalePanel extends LongScalePanel {

    ////////////
    // Fields //
    ////////////

    // Model
    protected DateScalePanelModel dmodel;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public DateScalePanel(ScaleSetPanel p, DateScalePanelModel m) {
        super(p, m);
    }

    ///////////////
    // Pre-Build //
    ///////////////

    @Override
    protected void initBeforeBuild() {
        super.initBeforeBuild();
        dmodel = (DateScalePanelModel) model;
    }

    @Override
    protected ImageIcon getTitleIcon() {
        return UiDefaults.DATE_ICON;
    }

    ////////////
    // Update //
    ////////////

    // Updates the date scale panel's view based on
    // - The date format
    // This should be called any time one of the above is changed.
    public void updateDateFormat() {
        updateRangeLabels();

        // If the filter text fields have text in them and are
        // accepted, reformat their contents.
        if(txtFilterLowerValue.getBackground().equals(Color.white) &&
                        !txtFilterLowerValue.getText().equals(SEL_ALL)) {
            txtFilterLowerValue.setText(cmodel.convertNumericToString(cmodel.getFilterLowerValue()));
            txtFilterLowerValue.setBackground(Color.white);
        }
        if(txtFilterUpperValue.getBackground().equals(Color.white) &&
                        !txtFilterUpperValue.getText().equals(SEL_ALL)) {
            txtFilterUpperValue.setText(cmodel.convertNumericToString(cmodel.getFilterUpperValue()));
            txtFilterUpperValue.setBackground(Color.white);
        }
    }

    //////////
    // Misc //
    //////////

    @Override
    protected String getDeselectAllLowerValue() {
        long l = -2208877200000L;  // Date: 1900/1/2
        return dmodel.convertNumericToString(l);
    }

    @Override
    protected String getDeselectAllUpperValue() {
        long l = -2208963600000L;  // Date: 1900/1/1
        return dmodel.convertNumericToString(l);
    }

    @Override
    protected int getTextFieldLength() {
        return 7;
    }
}
