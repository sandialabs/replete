package replete.ui.sdplus.color;

import java.awt.Color;

import replete.ui.sdplus.panels.ScalePanel;


/**
 * Used only as the object stored in the ColorDialog's list.
 * Not related to the ColorMap object.
 *
 * @author Derek Trumbo
 */

public class ColorMapping {
    protected Object key;       // Can be null since enumerated scale panels hold null values.
    protected Color value;

    public ColorMapping(Object k, Color v) {
        key = k;
        value = v;
    }

    @Override
    public String toString() {
        if(key == null) {
            return ScalePanel.NO_VALUE_TEXT;
        }
        return key.toString();
    }
}
