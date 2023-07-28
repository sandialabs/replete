package replete.ui.sdplus.panels;

import java.util.List;
import java.util.Set;

/**
 * Model for a single-select enumerated scale.  The
 * only difference between this model and the base
 * class model is that there can only be one value
 * in the selected values set.
 *
 * @author Derek Trumbo
 */

public class EnumScaleSinglePanelModel extends EnumScaleBasePanelModel {

    //////////////////
    // Constructors //
    //////////////////

    // Starts with all objects selected.
    public EnumScaleSinglePanelModel(String k, String nm, String un, String nt, List<Object> av) {
        super(k, nm, un, nt, av);
        restrictSelectedToOneValue();
    }

    public EnumScaleSinglePanelModel(String k, String nm, String un, String nt, List<Object> av, Set<Object> sv) {
        super(k, nm, un, nt, av, sv);
        restrictSelectedToOneValue();
    }

    // Assumes that the selected values are all contained within the
    // unique sorted values by this point.
    protected void restrictSelectedToOneValue() {
        if(selValues.size() > 1) {
            Object minValue = null;
            int minIdx = Integer.MAX_VALUE;
            for(Object selValue : selValues) {
                int idx = uniqueSortedValues.indexOf(selValue);
                if(idx < minIdx) {
                    minIdx = idx;
                    minValue = selValue;
                }
            }
            selValues.clear();
            selValues.add(minValue);
        }
    }

    //////////////////////////
    // Accessors / Mutators //
    //////////////////////////

    // Mutators

    @Override
    public void setSelectedValues(Set<Object> sv) {
        selValues = sv;
        restrictSelectedToOneValue();
    }
}
