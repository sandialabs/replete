package finio.core.it;

import java.util.Iterator;
import java.util.Map;

import finio.core.FUtil;
import finio.core.KeyValue;
import finio.core.errors.UnsupportedObjectTypeException;

public class MapKeyValueIterator extends KeyValueIterator {


    ///////////
    // FIELD //
    ///////////

    private Iterator<Map.Entry<Object, Object>> entrySetIterator;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public MapKeyValueIterator(Object O) {
        if(canHandle(O)) {
            Map Mjava = (Map) O;
            entrySetIterator = Mjava.entrySet().iterator();
        } else {
            throw new UnsupportedObjectTypeException("Object must be a Map (and non-null).", O);
        }
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    // Iterator

    @Override
    public boolean hasNext() {
        return entrySetIterator.hasNext();
    }
    @Override
    public KeyValue next() {
        Map.Entry<Object, Object> entry = entrySetIterator.next();
        return new KeyValue(entry.getKey(), entry.getValue());
    }

    // KeyValuePairIterator

    @Override
    public boolean canHandle(Object O) {
        return FUtil.isJavaMap(O);
    }
}
