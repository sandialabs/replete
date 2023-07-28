package finio.extractors.jo;

import finio.core.KeyPath;
import finio.core.it.FieldReflectionKeyValueIterator;
import finio.core.it.KeyValueIterator;

public class FieldReflectionKeyValueIteratorCreator implements KeyValueIteratorCreator {
    public KeyValueIterator create(Object O, KeyPath P) {
        return new FieldReflectionKeyValueIterator(O, true);
    }

    @Override
    public String toString() {
        return "Field Reflection Key Value Iterator Creator";
    }
}
