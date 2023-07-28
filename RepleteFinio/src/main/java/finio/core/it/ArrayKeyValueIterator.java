package finio.core.it;

import java.lang.reflect.Array;

import finio.core.FUtil;
import finio.core.KeyValue;
import finio.core.errors.UnsupportedObjectTypeException;

public class ArrayKeyValueIterator extends KeyValueIterator {


    ///////////
    // FIELD //
    ///////////

    private Object O;
    private int Z;
    private int I = 0;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ArrayKeyValueIterator(Object O) {
        if(canHandle(O)) {
            this.O = O;
            Z = Array.getLength(O);
        } else {
            throw new UnsupportedObjectTypeException("Object must be an array (and non-null).", O);
        }
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    // Iterator

    @Override
    public boolean hasNext() {
        return I < Z;
    }
    @Override
    public KeyValue next() {
        Object V = Array.get(O, I);
        return new KeyValue(I++, V);
    }

    // KeyValueIterator

    @Override
    public boolean canHandle(Object O) {
        return FUtil.isJavaArray(O);
    }
}
