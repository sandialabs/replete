package finio.extractors.jo;

import finio.core.KeyPath;
import finio.core.it.KeyValueIterator;
import finio.core.it.NonTerminalLikeKeyValueIterator;

public class NonTerminalLikeKeyValueIteratorCreator implements KeyValueIteratorCreator {
    public KeyValueIterator create(Object O, KeyPath P) {
        return new NonTerminalLikeKeyValueIterator(O);
    }

    @Override
    public String toString() {
        return "Non-Terminal Like Key Value Iterator Creator";
    }
}
