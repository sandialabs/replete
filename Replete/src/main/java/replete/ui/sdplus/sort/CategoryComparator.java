package replete.ui.sdplus.sort;

import java.util.HashMap;
import java.util.Map;

import replete.ui.sdplus.panels.ContScalePanel;
import replete.ui.sdplus.panels.DateScalePanel;
import replete.ui.sdplus.panels.EnumScaleMultiPanel;
import replete.ui.sdplus.panels.EnumScaleSinglePanel;
import replete.ui.sdplus.panels.LongScalePanel;
import replete.ui.sdplus.panels.ScalePanel;


/**
 * Compares two scale panels by category first, then alphabetically.
 * The enumerated scale panels come first, then the continuous scale
 * panels.
 *
 * @author Derek Trumbo
 */

public class CategoryComparator extends AlphabeticalComparator {
    private static Map<Class<? extends ScalePanel>, Integer> orderMap;

    // Set up the numerical order in which the scale panel classes
    // will be compared.
    static {
        orderMap = new HashMap<Class<? extends ScalePanel>, Integer>();
        orderMap.put(EnumScaleMultiPanel.class, new Integer(1));
        orderMap.put(EnumScaleSinglePanel.class, new Integer(2));
        orderMap.put(DateScalePanel.class, new Integer(3));
        orderMap.put(ContScalePanel.class, new Integer(4));
        orderMap.put(LongScalePanel.class, new Integer(5));
        orderMap.put(ScalePanel.class, new Integer(6));
    }

    @Override
    public int compare(ScalePanel p1, ScalePanel p2) {

        // Check group membership first.
        int gCompare = compareGroups(p1, p2);
        if(gCompare != 0) {
            return gCompare;
        }

        Integer o1 = orderMap.get(p1.getClass());
        Integer o2 = orderMap.get(p2.getClass());

        // Neither should not be null or there is a problem
        // at design time.

        int result = o1.compareTo(o2);

        if(result != 0) {
            return result;
        }

        // Identical classes will be compared alphabetically.
        return super.compare(p1, p2);
    }
}
