package finio.extractors.jo;

import finio.core.KeyPath;
import finio.core.it.KeyValueIterator;
import replete.plugins.ExtensionPoint;

public interface KeyValueIteratorCreator extends ExtensionPoint {
    KeyValueIterator create(Object O, KeyPath P);
}
