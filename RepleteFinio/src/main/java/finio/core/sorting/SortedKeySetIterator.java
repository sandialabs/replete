package finio.core.sorting;

import java.util.Iterator;

import finio.core.KeyValue;
import finio.core.NonTerminal;

public class SortedKeySetIterator implements Iterator {


    ////////////
    // FIELDS //
    ////////////

    private NonTerminal M;
    private Iterator iterator;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public SortedKeySetIterator(NonTerminal M, Iterator iterator) {
        this.M = M;
        this.iterator = iterator;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    private KeyValue KVcurrent;

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }
    @Override
    public Object next() {
        KVcurrent = (KeyValue) iterator.next();
        return KVcurrent.getK();
    }
    @Override
    public void remove() {
        // TODO ?
//        iterator.remove();
//        M.removeByKey(M);
    }
}
