package finio.core.it;

import finio.core.FUtil;
import finio.core.KeyValue;
import finio.core.errors.UnsupportedObjectTypeException;

public class NonTerminalLikeKeyValueIterator extends KeyValueIterator {


    ///////////
    // FIELD //
    ///////////

    private KeyValueIterator kvIterator;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public NonTerminalLikeKeyValueIterator(Object O) {
        if(canHandle(O)) {
            if(FUtil.isJavaArray(O)) {
                kvIterator = new ArrayKeyValueIterator(O);
            } else if(FUtil.isJavaIterable(O)) {
                kvIterator = new IterableKeyValueIterator(O);
            } else {
                kvIterator = new MapKeyValueIterator(O);
            }
        } else {
            throw new UnsupportedObjectTypeException("Object must be NonTerminal-like (and non-null).", O);
        }
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    // Iterator

    @Override
    public boolean hasNext() {
        return kvIterator.hasNext();
    }
    @Override
    public KeyValue next() {
        return kvIterator.next();
    }

    // KeyValueIterator

    @Override
    public boolean canHandle(Object O) {
        return FUtil.isNonTerminalLike(O);
    }
}
