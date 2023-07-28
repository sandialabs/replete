package finio.core.it;

import java.util.Iterator;

import finio.core.FUtil;
import finio.core.KeyValue;
import finio.core.errors.UnsupportedObjectTypeException;

public class IterableKeyValueIterator extends KeyValueIterator {


    ///////////
    // FIELD //
    ///////////

    private Iterator<?> iterableIterator;
    private int I = 0;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public IterableKeyValueIterator(Object O) {
        if(canHandle(O)) {
            Iterable iterable = (Iterable) O;
            iterableIterator = iterable.iterator();
        } else {
            throw new UnsupportedObjectTypeException("Object must be an Iterable (and non-null).", O);
        }
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    // Iterator

    @Override
    public boolean hasNext() {
        return iterableIterator.hasNext();
    }
    @Override
    public KeyValue next() {
        Object V = iterableIterator.next();
        return new KeyValue(I++, V);
    }

    // KeyValueIterator

    @Override
    public boolean canHandle(Object O) {
        return FUtil.isJavaIterable(O);
    }
}
