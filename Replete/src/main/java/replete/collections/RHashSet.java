package replete.collections;

import java.util.Collection;
import java.util.HashSet;

public class RHashSet<V> extends HashSet<V> {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RHashSet() {
        super();
    }
    public RHashSet(Collection<? extends V> c) {
        super(c);
    }
    public RHashSet(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }
    public RHashSet(int initialCapacity) {
        super(initialCapacity);
    }
    public RHashSet(V... init) {
        for(int i = 0; i < init.length; i++) {
            add(init[i]);
        }
    }
}
