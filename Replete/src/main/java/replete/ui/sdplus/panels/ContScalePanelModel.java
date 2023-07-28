package replete.ui.sdplus.panels;

import java.awt.Color;
import java.util.List;

import replete.ui.sdplus.color.ColorMap;
import replete.ui.sdplus.color.ColorUtil;


/**
 * A model that backs a continuous scale panel.  This
 * model contains all the values relevant to this scale,
 * some derived information from those values, and the
 * user's desired filtering of those values.
 *
 * @author Derek Trumbo
 */

public class ContScalePanelModel extends ScalePanelModel {

    ////////////
    // Fields //
    ////////////

    // Model
    protected List<Object> allValues;
    protected int validValuesCount;        // Derived from allValues
    protected Double[] validValuesRange;   // Derived from allValues

    // Filter
    protected double filterLowerValue;
    protected double filterUpperValue;
    protected boolean filterIncludeNulls;

    //////////////////
    // Constructors //
    //////////////////

    // Starts with no range subselected or nulls excluded.
    public ContScalePanelModel(String k, String nm, String un, String nt, List<Object> av) {
        this(k, nm, un, nt, av, Double.NaN, Double.NaN, true);
    }

    public ContScalePanelModel(String k, String nm, String un, String nt, List<Object> av, double lv, double hv, boolean nls) {
        super(k, nm, un, nt);

        setAllValues(av);       // Builds the derived instance variables as well.
        filterLowerValue = lv;
        filterUpperValue = hv;
        filterIncludeNulls = nls;
    }

    //////////////////////////
    // Accessors / Mutators //
    //////////////////////////

    // Accessors

    public List<Object> getAllValues() {
        return allValues;
    }

    public int getValidValuesCount() {
        return validValuesCount;
    }

    public Double[] getValidValuesRange() {
        return validValuesRange;
    }

    public double getFilterLowerValue() {
        return filterLowerValue;
    }

    public double getFilterUpperValue() {
        return filterUpperValue;
    }

    public boolean isFilterIncludeNulls() {
        return filterIncludeNulls;
    }

    // Mutators

    public void setAllValues(List<Object> av) {
        allValues = av;

        // Build the derived instance variables.
        buildValidValuesCount();
        buildValidValuesRange();
    }

    protected void buildValidValuesCount() {
        validValuesCount = 0;
        for(Object value : allValues) {
            if(value instanceof Number) {
                Number num = (Number) value;
                double d = num.doubleValue();
                if(!Double.isNaN(d)) {
                    validValuesCount++;
                }
            }
        }
    }

    protected void buildValidValuesRange() {
        double minVal = Double.POSITIVE_INFINITY;
        double maxVal = Double.NEGATIVE_INFINITY;

        for(Object value : allValues) {
            if(value != null) {
                if(value instanceof Number) {
                    Number num = (Number) value;
                    double d = num.doubleValue();
                    if(d < minVal) {
                        minVal = d;
                    }
                    if(d > maxVal) {
                        maxVal = d;
                    }
                }
            }
        }

        // If not even one number was found above then reset values to NaN's.
        if(minVal == Double.POSITIVE_INFINITY) {
            minVal = Double.NaN;
            maxVal = Double.NaN;
        }

        validValuesRange = new Double[] {minVal, maxVal};
    }

    public void setFilterLowerValue(double lv) {
        filterLowerValue = lv;
    }

    public void setFilterUpperValue(double hv) {
        filterUpperValue = hv;
    }

    public void setFilterIncludeNulls(boolean nls) {
        filterIncludeNulls = nls;
    }

    /////////////////////////////
    // Conversion & Validation //
    /////////////////////////////

    protected String convertNumericToString(double val) {
        return String.valueOf(val);
    }

    protected double convertStringToNumeric(String val) {
        return Double.parseDouble(val);
    }

    protected boolean isValidString(String val) {
        try {
            Double.parseDouble(val);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }

    ////////////
    // Colors //
    ////////////

    @Override
    public ColorMap buildActiveColorMap() {
        ColorMap map = new ColorMap();
        Color[] minMaxColors = getMinMaxColors();
        map.put(ColorMap.MIN_GRADIENT_KEY, minMaxColors[0]);
        map.put(ColorMap.MAX_GRADIENT_KEY, minMaxColors[1]);
        return map;
    }

    @Override
    public Color getColor(Object o) {
        if(o == null) {
            return getMinMaxColors()[0];
        }
        double val = ((Number) o).doubleValue();
        double scaledVal = scaleOn0To1(val);
        return getColorScaled(scaledVal);
    }

    protected double scaleOn0To1(double val) {
        double lower = validValuesRange[0];
        double upper = validValuesRange[1];
        double proportion = 0.0;
        if(!Double.isNaN(lower) && !Double.isNaN(upper)) {
            double rangeDiff = upper - lower;
            double valDiff = val - lower;
            proportion = valDiff / rangeDiff;
        }
        return proportion;
    }

    // Takes in a value between 0 and 1 representing the fraction of the distance
    // from the minimum to the maximum value of all elements in the model.
    protected Color getColorScaled(double val) {

        Color[] minMaxColors = getMinMaxColors();

        Color cMin = minMaxColors[0];
        Color cMax = minMaxColors[1];

        // Choose a color value somewhere in between the two
        // values, depending on where the data element's value
        // is for this scale between that scale's minimum
        // and maximum values across all data elements.
        int newRed = (int) (cMax.getRed() * val - cMin.getRed() * val + cMin.getRed());
        int newGreen = (int) (cMax.getGreen() * val - cMin.getGreen() * val + cMin.getGreen());
        int newBlue = (int) (cMax.getBlue() * val - cMin.getBlue() * val + cMin.getBlue());

        return new Color(newRed, newGreen, newBlue);
    }

    public Color[] getMinMaxColors() {
        Color cMin = null;
        Color cMax = null;

        // Try the user-specified color map first.
        if(overrideColorMap != null) {
            cMin = overrideColorMap.get(ColorMap.MIN_GRADIENT_KEY);
            cMax = overrideColorMap.get(ColorMap.MAX_GRADIENT_KEY);
        }

        // If the user has not specifically overridden the colors
        // on this scale, look to either the project or local
        // settings for default colors.
        if(cMin == null || cMax == null) {

            ColorMap cScaleMap = null;

            /// TEMPORARY ///
            cScaleMap = new ColorMap();
            cScaleMap.put(ColorMap.MIN_GRADIENT_KEY, ColorUtil.getDefaultMinGradientColor());
            cScaleMap.put(ColorMap.MAX_GRADIENT_KEY, ColorUtil.getDefaultMaxGradientColor());
            /////////////////

            /*
            // This code can be executed before the top-level
            // window has been constructed.
            if(Wave.getAppWin() != null && !GlobalSettings.getSettings().isUseLocalColors()) {
                cScaleMap = Wave.getAppWin().getProject()
                    .getColorMapMap().get(ColorMapMap.CONT_SCALE_MAP_KEY);
            } else {
                cScaleMap = GlobalSettings.getSettings()
                    .getColorMapMap().get(ColorMapMap.CONT_SCALE_MAP_KEY);
            }
            */

            // cScaleMap cannot be null at this point because of how
            // the GlobalSettings and Project objects are constructed.

            cMin = cScaleMap.get(ColorMap.MIN_GRADIENT_KEY);
            cMax = cScaleMap.get(ColorMap.MAX_GRADIENT_KEY);
        }

        return new Color[] {cMin, cMax};
    }

    ////////////
    // Filter //
    ////////////

    // Whether or not a given value passes the filter
    // defined by the model.
    @Override
    public boolean isAcceptedValue(Object o) {
        if(o == null) {
            return filterIncludeNulls;
        }
        if(!(o instanceof Number)) {
            return false;
        }
        double val = ((Number) o).doubleValue();
        if(!Double.isNaN(filterLowerValue) && val < filterLowerValue) {
            return false;
        }
        if(!Double.isNaN(filterUpperValue) && val > filterUpperValue) {
            return false;
        }
        return true;
    }

    //////////////
    // toString //
    //////////////

    @Override
    protected String internalString() {
        return super.internalString() +
            ", lowerVal=" + convertNumericToString(filterLowerValue) +
            ", upperVal=" + convertNumericToString(filterUpperValue) +
            ", inclNulls=" + filterIncludeNulls +
            ", #allValues=" + allValues.size();
    }
}
