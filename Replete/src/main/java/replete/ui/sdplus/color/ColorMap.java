package replete.ui.sdplus.color;

import java.awt.Color;
import java.util.LinkedHashMap;

/**
 * Order is important in this map, so a LinkedHashMap is used.
 *
 * @author Derek Trumbo
 */

public class ColorMap extends LinkedHashMap<Object, Color> /*implements XMLizable*/ {

    /**
     * For accessing the two colors for a continuous-scale color map.
     */
    public static final String MIN_GRADIENT_KEY = "Minimum Gradient Value";
    public static final String MAX_GRADIENT_KEY = "Maximum Gradient Value";

    // **** Implement XMLizable Methods ****************************************

/*    public void toXML(XMLWriter xmlWriter) throws IOException {
        if (xmlWriter.writeObjectHeader(this)) {
            return;
        }
        xmlWriter.writeObjectHeaderClose();
        Map<String, XMLizableColor> xMap = new LinkedHashMap<String, XMLizableColor>();
        for(String key : keySet()) {
            xMap.put(key, new XMLizableColor(get(key)));
        }
        xmlWriter.writeHashMap("colorMap", xMap);
        xmlWriter.writeObjectTail(this);
    }

    public void setObject(String name, Object obj) {
        if(name.equals("colorMap")) {
            XMLizableEntry entry = (XMLizableEntry) obj;
            put(entry.getKey(), ((XMLizableColor) entry.getValue()).getColor());
        }
    }*/

    public ColorMap copy() {
        ColorMap copy = new ColorMap();
        for(Object key : keySet()) {
            copy.put(key, get(key));
        }
        return copy;
    }
}
