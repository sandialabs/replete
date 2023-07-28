package replete.ui.sdplus.sort;

import replete.text.EmbeddedNumberComparator;
import replete.ui.sdplus.panels.ScalePanel;

/**
 * Compares two scale panels alphabetically based on their title labels.
 * A scale panel's title label generally holds the 'name' property of
 * the scale panel's model and other scale properties.  This comparator
 * uses an embedded integer comparator to sort 'Some Scale 9' before
 * 'Some Scale 10'.
 *
 * @author Derek Trumbo
 */

public class AlphabeticalComparator extends ScalePanelComparator {
    protected static EmbeddedNumberComparator eic =
        new EmbeddedNumberComparator(true);

    @Override
    public int compare(ScalePanel p1, ScalePanel p2) {

        // Check group membership first.
        int gCompare = compareGroups(p1, p2);
        if(gCompare != 0) {
            return gCompare;
        }

        // Compare using the embedded integer comparator.
        return eic.compare(p1.getTitle(), p2.getTitle());
    }
}
