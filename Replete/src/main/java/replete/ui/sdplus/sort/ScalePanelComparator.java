package replete.ui.sdplus.sort;

import java.util.Comparator;
import java.util.Map;

import replete.ui.sdplus.panels.GroupPanelModel;
import replete.ui.sdplus.panels.ScalePanel;


/**
 * Compares two scale panels using some criteria.
 *
 * @author Derek Trumbo
 */

public abstract class ScalePanelComparator implements Comparator<ScalePanel> {

    ////////////
    // Groups //
    ////////////

    // When this group map is set (non null), subclasses should always
    // sort panels first by their group.
    protected Map<String, GroupPanelModel> groupMap;
    public void setUseGroupsMap(Map<String, GroupPanelModel> gmap) {
        groupMap = gmap;
    }

    // Utility method that subclasses can use to compare scale panels
    // based on the groups that they're in.
    protected int compareGroups(ScalePanel p1, ScalePanel p2) {

        // Identical if not using groups right now.
        if(groupMap == null) {
            return 0;
        }

        GroupPanelModel g1 = groupMap.get(p1.getScalePanelModel().getKey());
        GroupPanelModel g2 = groupMap.get(p2.getScalePanelModel().getKey());

        if(g1 == null) {
            if(g2 == null) {
                return 0;       // Both don't have a group.
            }
            return 1;
        } else if(g2 == null) {
            return -1;
        }

        // Both have a group so compare alphabetically by their
        // groups' names.
        return g1.getName().compareTo(g2.getName());
    }

    /////////////
    // Compare //
    /////////////

    // Compare method must be implemented by subclasses.  The
    // method has access to each of the scale panel models by
    // calling the getScalePanelModel method.

    public abstract int compare(ScalePanel p1, ScalePanel p2);
}
