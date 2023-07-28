package replete.ui.sdplus.panels;

import java.awt.Color;

import replete.ui.sdplus.color.ColorMap;


/**
 * A model that backs a scale panel.  This model contains
 * all the generic information about a panel, but does
 * not contain any information about specific values or
 * users' filter criteria.
 *
 * @author Derek Trumbo
 */

public class ScalePanelModel {

    ////////////
    // Fields //
    ////////////

    protected boolean listedInTable;
    protected boolean open;

    protected String key;
    protected String name;
    protected String units;
    protected String note;

    protected VisualizationType visType;

    protected ColorMap overrideColorMap;
//  protected Shape[] shapeSequence = DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE;

    // Boolean field used only for when the base class scale panel
    // is used in a scale set panel (not going to happen often or ever
    // in practice).
    protected boolean defaultAcceptedValue;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    // Default settings for a new scale panel:
    // - Listed in table
    // - Closed
    // - No visualization type
    // - No override colors
    public ScalePanelModel(String k, String nm, String un, String nt) {
        listedInTable = true;
        open = false;

        key = k;
        name = nm;
        units = un;
        note = nt;

        visType = VisualizationType.NONE;

        overrideColorMap = null;  // Initially no local override colors defined.

        // Assume scale panel involved in an INTERSECTION
        // by default.
        defaultAcceptedValue = true;
    }

    //////////////////////////
    // Accessors / Mutators //
    //////////////////////////

    // Accessors

    public boolean isListedInTable() {
        return listedInTable;
    }
    public boolean isOpen() {
        return open;
    }
    public String getKey() {
        return key;
    }
    public String getName() {
        return name;
    }
    public String getUnits() {
        return units;
    }
    public String getNote() {
        return note;
    }
    public VisualizationType getVisualizationType() {
        return visType;
    }
    public ColorMap getOverrideColorMap() {
        return overrideColorMap;
    }
    public boolean getDefaultAcceptedValue() {
        return defaultAcceptedValue;
    }

    // Mutators

    public void setListedInTable(boolean v) {
        listedInTable = v;
    }
    public void setOpen(boolean op) {
        open = op;
    }
    public void setKey(String k) {
        key = k;
    }
    public void setName(String nm) {
        name = nm;
    }
    public void setUnits(String un) {
        units = un;
    }
    public void setNote(String nt) {
        note = nt;
    }
    public void setVisualizationType(VisualizationType type) {
        visType = type;
    }
    public void setOverrideColorMap(ColorMap map) {
        overrideColorMap = map;
    }
    public void setDefaultAcceptedValue(boolean accepted) {
        defaultAcceptedValue = accepted;
    }

    ////////////
    // Colors //
    ////////////

    public ColorMap buildActiveColorMap() {
        return null;
    }

    public Color getColor(Object o) {
        return null;
    }

    ////////////
    // Filter //
    ////////////

    // Whether or not a given value passes the filter
    // defined by the model.  Technically the default
    // value returned by this base class should be
    // true for INTERSECTION and false for UNION when
    // the panel is involved in a scale set panel.  This
    // is why this method doesn't return a literal value
    // but rather a configurable value.  However, this
    // will rarely or never be used in practice since
    // a base class scale panel is not very useful.
    public boolean isAcceptedValue(Object o) {
        return defaultAcceptedValue;
    }

    //////////////
    // toString //
    //////////////

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + internalString() + "]";
    }

    protected String internalString() {
        return "listedInTable=" + listedInTable +
            ", open=" + open +
            ", key=" + key +
            ", name=" + name +
            ", units=" + units +
            ", notes=" + note +
            ", visType=" + visType.name() +
            ", colors=" + (overrideColorMap == null ? "<no>" : "<yes>");
    }
}
