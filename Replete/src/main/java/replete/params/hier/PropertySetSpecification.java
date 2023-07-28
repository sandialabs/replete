package replete.params.hier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PropertySetSpecification implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    private List<PropertySlot> slots = new ArrayList<>();


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors (Computed)

    public Set<String> getKeys() {
        Set<String> keys = new LinkedHashSet<>();
        for(PropertySlot slot : slots) {
            keys.add(slot.getKey());
        }
        return keys;
    }
    public PropertySlot getSlot(String key) {
        for(PropertySlot slot : slots) {
            if(slot.getKey().equals(key)) {
                return slot;
            }
        }
        return null;
    }

    // Mutators

    public PropertySetSpecification add(PropertySlot slot) {
        slots.add(slot);
        return this;
    }
}
