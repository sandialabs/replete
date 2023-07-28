package replete.ui.sdplus.panels;

import java.util.List;

/**
 * A model that backs an integral scale panel.  This
 * model contains all the values relevant to this scale,
 * some derived information from those values, and the
 * user's desired filtering of those values.
 *
 * @author Derek Trumbo
 */

public class LongScalePanelModel extends ContScalePanelModel {

    //////////////////
    // Constructors //
    //////////////////

    // Starts with no range subselected or nulls excluded.
    public LongScalePanelModel(String k, String nm, String un, String nt, List<Object> av) {
        super(k, nm, un, nt, av);
    }

    public LongScalePanelModel(String k, String nm, String un, String nt, List<Object> av, double lv, double hv, boolean nls) {
        super(k, nm, un, nt, av, lv, hv, nls);
    }

    /////////////////////////////
    // Conversion & Validation //
    /////////////////////////////

    @Override
    protected String convertNumericToString(double val) {
        if(Double.isNaN(val)) {
            return "NaN";
        }

        return String.valueOf((long) val);
    }

    @Override
    protected boolean isValidString(String val) {
        try {
            Long.parseLong(val);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }
}
