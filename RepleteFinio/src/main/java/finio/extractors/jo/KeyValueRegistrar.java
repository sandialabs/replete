package finio.extractors.jo;

import java.util.Set;

import finio.core.KeyPath;
import finio.core.NonTerminal;
import finio.core.it.KeyValueIterator;
import finio.core.pop.NonTerminalPopulator;
import replete.plugins.ExtensionPoint;

public interface KeyValueRegistrar extends ExtensionPoint {

    void addValue(NonTerminal M, Object O,
                  Object K, Object V,
                  Set<Object> visited, KeyPath P,
                  PopulateParamsProvider paramsProvider,
                  KeyValueIterator kvIterator, NonTerminalPopulator populator);

}
