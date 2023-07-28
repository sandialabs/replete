package replete.ui.sdplus.color;

import java.util.HashMap;

/**
 * A map to manage multiple maps that have different
 * meanings.
 *
 * @author Derek Trumbo
 */

public class ColorMapMap extends HashMap<String, ColorMap> /*implements XMLizable*/ {

    /**
     * For accessing common color maps.
     */
    public static final String ENUM_SCALE_MAP_KEY = "enumeratedScaleColors";
    public static final String CONT_SCALE_MAP_KEY = "continuousScaleColors";

    public ColorMapMap() {}

    // **** Implement XMLizable Methods ****************************************

/*    public void toXML(XMLWriter xmlWriter) throws IOException {
        if (xmlWriter.writeObjectHeader(this)) {
            return;
        }
        xmlWriter.writeObjectHeaderClose();
        xmlWriter.writeHashMap("colorMapMap", this);
        xmlWriter.writeObjectTail(this);
    }

    public void setObject(String name, Object obj) {
        if(name.equals("colorMapMap")) {
            XMLizableEntry entry = (XMLizableEntry) obj;
            put(entry.getKey(), (ColorMap) entry.getValue());
        }
    }*/

    public ColorMapMap copy() {
        ColorMapMap copy = new ColorMapMap();
        for(String key : keySet()) {
            copy.put(key, get(key).copy());
        }
        return copy;
    }
}
