package finio.core.it;

import java.util.Iterator;

import finio.core.KeyValue;

// This class defines how to iterate over the key-value
// pairs in any kind of non-terminal.

public abstract class KeyValueIterator implements Iterator<KeyValue> {
    // public Class<?>[] getHandledClasses()    // needed?
    public abstract boolean canHandle(Object O);
}
