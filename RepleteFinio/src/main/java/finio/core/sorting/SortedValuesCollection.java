package finio.core.sorting;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.Set;

import finio.core.NonTerminal;

public class SortedValuesCollection extends AbstractCollection {
    private NonTerminal M;
    private Set sorter;
    public SortedValuesCollection(NonTerminal M, Set sorter) {
        this.M = M;
        this.sorter = sorter;
    }
    @Override
    public Iterator iterator() {
        return new SortedValuesIterator(sorter.iterator());
    }
    @Override
    public int size() {
        return M.size();
    }
    @Override
    public boolean contains(Object o) {
        return M.containsValue(o);
    }
    @Override
    public void clear() {
        M.clear();
    }
}
