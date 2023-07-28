package replete.ui.sdplus.sort;

import replete.ui.sdplus.panels.ScalePanel;

/**
 * Compares two scale panels first by whether or not they are
 * subselected, placing the subselected ones before the non-
 * subselected ones, secondly by category, and third
 * alphabetically.
 *
 * @author Derek Trumbo
 */

public class SubselectedComparator extends CategoryComparator {
    @Override
    public int compare(ScalePanel p1, ScalePanel p2) {

        // Check group membership first.
        int gCompare = compareGroups(p1, p2);
        if(gCompare != 0) {
            return gCompare;
        }

        // Both either subselected or not subselected.
        if(p1.isSubselected() == p2.isSubselected()) {
            return super.compare(p1, p2);
        }

        if(p1.isSubselected()) {
            return -1;              // Subselected first.
        }
        return 1;
    }
}
