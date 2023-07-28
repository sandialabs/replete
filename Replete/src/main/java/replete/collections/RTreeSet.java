package replete.collections;

import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public class RTreeSet<T> extends TreeSet<T> {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RTreeSet() {
        super();
    }
    public RTreeSet(Collection<? extends T> c) {
        super(c);
    }
    public RTreeSet(Comparator<? super T> comparator) {
        super(comparator);
    }
    public RTreeSet(Collection<? extends T> c, Comparator<? super T> comparator) {
        super(comparator);
        addAll(c);
    }
    public RTreeSet(SortedSet<T> s) {
        super(s);
    }
    public RTreeSet(Object... init) {
        for(int i = 0; i < init.length; i++) {
            add((T) init[i]);
        }
    }
}
