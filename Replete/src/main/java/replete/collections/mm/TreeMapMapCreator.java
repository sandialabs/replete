package replete.collections.mm;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

public class TreeMapMapCreator<E, F> implements MapCreator<E, F>, Serializable {
    @Override
    public Map<E, F> create() {
        return new TreeMap<E, F>();
    }
}
