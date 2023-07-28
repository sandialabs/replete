package finio.core.sorting;

import java.util.Iterator;

import finio.core.KeyValue;

public class SortedValuesIterator implements Iterator {
    private Iterator iterator;
    public SortedValuesIterator(Iterator iterator) {
        this.iterator = iterator;
    }
    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }
    @Override
    public Object next() {
        KeyValue KV = (KeyValue) iterator.next();
        return KV.getV();
    }
    @Override
    public void remove() {
        // TODO ?
    }
}
