package replete.params.hier;

import java.util.LinkedHashMap;
import java.util.Map;

public class PropertySet extends LinkedHashMap<String, PropertyParams> {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public PropertySet() {
        super();
    }
    public PropertySet(Map<? extends String, ? extends PropertyParams> m) {
        super(m);
    }


    //////////
    // MISC //
    //////////

    public void print() {
        System.out.println("PropertySet:");
        for(String s : keySet()) {
            PropertyParams params = get(s);
            System.out.println("  " + s + " = " + params);
        }
    }
}
