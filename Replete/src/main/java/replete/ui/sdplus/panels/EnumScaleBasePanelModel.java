package replete.ui.sdplus.panels;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import replete.text.EmbeddedNumberComparator;
import replete.ui.sdplus.color.ColorMap;
import replete.ui.sdplus.color.ColorUtil;


/**
 * A model that backs an enumerated scale panel.  This
 * model contains all the values relevant to this scale,
 * some derived information from those values, and the
 * user's desired filtering of those values.  Also
 * contains values that are forced to be selected or
 * deselected.
 *
 * @author Derek Trumbo
 */

public abstract class EnumScaleBasePanelModel extends ScalePanelModel {

    ////////////
    // Fields //
    ////////////

    protected static EnumValueComparator valueComparator =
        new EnumValueComparator();

    // Model
    protected List<Object> allValues;
    protected List<Object> uniqueSortedValues;      // TreeSet not used so indexOf is available.
    protected Map<Object, Integer> uniqueValueCountMap;

    // Filter
    protected Set<Object> selValues;

    // Other
    protected Set<Object> forceChecked = new HashSet<Object>();
    protected Set<Object> forceUnchecked = new HashSet<Object>();

    //////////////////
    // Constructors //
    //////////////////

    // Starts with all objects selected.
    public EnumScaleBasePanelModel(String k, String nm, String un, String nt, List<Object>  av) {
        this(k, nm, un, nt, av, new HashSet<Object>(av));
    }

    public EnumScaleBasePanelModel(String k, String nm, String un, String nt, List<Object> av, Set<Object> sv) {
        super(k, nm, un, nt);

        setAllValues(av);    // Builds the derived instance variables as well.
        selValues = sv;
    }

    //////////////////////////
    // Accessors / Mutators //
    //////////////////////////

    // Accessors

    public List<Object> getAllValues() {
        return allValues;
    }
    public List<Object> getUniqueSortedValues() {
        return uniqueSortedValues;
    }
    public Map<Object, Integer> getUniqueValueCounts() {
        return uniqueValueCountMap;
    }
    public Set<Object> getSelectedValues() {
        return selValues;
    }

    // Remember if you add or remove values from these sets
    // you may need to update the selected values set as
    // well.  A different method structure could also be
    // set up to make sure this is done for the developer
    // (e.g. addForceCheckedValue, removeForceCheckedValue).
    public Set<Object> getForceChecked() {
        return forceChecked;
    }
    public Set<Object> getForceUnchecked() {
        return forceUnchecked;
    }

    // Mutators

    public void setAllValues(List<Object> av) {
        allValues = av;

        // Builds the derived instance variables.
        buildUniqueSortedValues();
        buildUniqueValueCounts();
    }

    // Create the unique and sorted list of values.  A TreeSet is not
    // used so that indexOf is available.
    protected void buildUniqueSortedValues() {
        uniqueSortedValues = new ArrayList<Object>();
        for(Object val : allValues) {
            if(!uniqueSortedValues.contains(val)) {
                uniqueSortedValues.add(val);
            }
        }
        Collections.sort(uniqueSortedValues, valueComparator);
    }

    // Unique value count map is similar to the unique sorted value list
    // but can't provide indexOf for ordering.
    protected void buildUniqueValueCounts() {
        uniqueValueCountMap = new HashMap<Object, Integer>();
        for(Object value : allValues) {
            Integer cnt = uniqueValueCountMap.get(value);
            if(cnt == null) {
                uniqueValueCountMap.put(value, new Integer(1));
            } else {
                uniqueValueCountMap.put(value, new Integer(cnt.intValue() + 1));
            }
        }
    }
    public void setSelectedValues(Set<Object> sv) {
        selValues = sv;
    }

    public void setForceChecked(Set<Object> force) {
        forceChecked = force;

        // Override selected values based on force values.
        for(Object f : forceChecked) {
            selValues.add(f);
        }
    }

    public void setForceUnchecked(Set<Object> force) {
        forceUnchecked = force;

        // Override selected values based on force values.
        for(Object f : forceUnchecked) {
            selValues.remove(f);
        }
    }

    ////////////
    // Colors //
    ////////////

    @Override
    public ColorMap buildActiveColorMap() {
        ColorMap map = new ColorMap();
        for(Object o : uniqueSortedValues) {
            map.put(o, getColor(o));
        }
        return map;
    }

    @Override
    public Color getColor(Object val) {

        ///////////////////////
        // OVERRIDDEN COLORS //
        ///////////////////////

        // Try the user-specified color map first.
        if(overrideColorMap != null) {
            Color c = overrideColorMap.get(val);
            if(c != null) {
                return c;
            }
        }

        ///////////////////
        // SPECIAL CASES //
        ///////////////////

        /*if (units != null && units.equals("Pass Fail")) {
            // Color waveforms in the plot based on their category. Categories
            // "Pass", "Fail", and other are colored green, red, and yellow,
            // respectively.
            if (val.startsWith("Pass")) {
                return Color.green;
            } else if (val.startsWith("Fail")) {
                return Color.red;
            }
            return new Color(0.8f, 0.8f, 0.0f); // Dark Yellow
        }

        // This locks Color Code to only be useable by Hierarchical Clustering.
        // If other scales need to use color coding, they must be added to this
        // if statement as well.
        if (units != null && units.equals("Color Code")) {

            // Color waveforms in the plot based on their category. Categories
            // encode the color to be used: " A" maps to the first color in the
            // colormap, " B" maps to the second color, etc.
            int idx = getIndexForCategory(val);
            if (idx >= 0) {
                return ColorUtil.getDefaultColor(idx);
            }
            if (idx == -1) {
                return Color.BLACK;
            }
        }*/

        /////////////////////////////
        // CALCULATE VALUE'S INDEX //
        /////////////////////////////

        int idx;
        if(uniqueSortedValues.size() > 0) {

            // Default coloring, map color table to sorted valid values.
            idx = uniqueSortedValues.indexOf(val); //validValues.headSet(val).size();
        } else {

            // Backup coloring, in case valid values haven't been set.
            // Use hash code to pick a color from the given list.
            // This guarantees the same val will always get the same color.
            idx = val.hashCode();
        }

        ///////////////////////
        // GET DEFAULT COLOR //
        ///////////////////////

        ColorMap cScaleMap = null;

        /// TEMPORARY ///
        cScaleMap = new ColorMap();
        for(int c = 0; c < ColorUtil.getDefaultEnumScaleColorCount(); c++) {
            Color clr = ColorUtil.getDefaultColor(c);
            cScaleMap.put("" + (c + 1), clr);
        }
        /////////////////

/*
        // This code can be executed before the top-level
        // window has been constructed.
        if(Wave.getAppWin() != null && !GlobalSettings.getSettings().isUseLocalColors()) {
            cScaleMap = Wave.getAppWin().getProject()
                .getColorMapMap().get(ColorMapMap.ENUM_SCALE_MAP_KEY);
        } else {
            cScaleMap = GlobalSettings.getSettings()
                .getColorMapMap().get(ColorMapMap.ENUM_SCALE_MAP_KEY);
        }
*/
        int colorIdx = idx % cScaleMap.size();

        return cScaleMap.get("" + (colorIdx + 1));
    }

    ////////////
    // Filter //
    ////////////

    // Whether or not a given value passes the filter
    // defined by the model.
    @Override
    public boolean isAcceptedValue(Object o) {
        return selValues.contains(o);
    }

    //////////////
    // toString //
    //////////////

    @Override
    protected String internalString() {
        return super.internalString() +
            ", #allValues=" + allValues.size() +
            ", #uniqValues=" + uniqueSortedValues.size() +
            ", #selValues=" + selValues.size();
    }

    //////////////////////
    // Value Comparator //
    //////////////////////

    protected static class EnumValueComparator implements Comparator<Object> {
        protected EmbeddedNumberComparator eic = new EmbeddedNumberComparator(true);
        public int compare(Object o1, Object o2) {
            if(o1 == null && o2 == null) {
                return 0;
            } else if(o1 == null) {
                return -1;
            } else if(o2 == null) {
                return 1;
            } else {
                return eic.compare(o1.toString(), o2.toString());
            }
        }
    }
}
