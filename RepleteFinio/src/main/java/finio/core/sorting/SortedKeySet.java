package finio.core.sorting;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import finio.core.NonTerminal;

public class SortedKeySet extends AbstractSet {


    ////////////
    // FIELDS //
    ////////////

    private NonTerminal M;
    private Set sorter;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public SortedKeySet(NonTerminal M, Set sorter) {
        this.M = M;
        this.sorter = sorter;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int size() {
        return M.size();
    }
    @Override
    public boolean contains(Object o) {
        return M.containsKey(o);
    }
    @Override
    public void clear() {
        M.clear();
    }
    @Override
    public boolean remove(Object K) {
        boolean willBeRemoved = M.has(K);
        M.removeByKey(K);
        return willBeRemoved;
    }
    @Override
    public boolean retainAll(Collection c) {
        return super.retainAll(c);
    }
    @Override
    public Iterator iterator() {
        return new SortedKeySetIterator(M, sorter.iterator());
    }
}
