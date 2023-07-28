package finio.extractors.jo;

import java.util.Set;

import finio.core.KeyPath;
import finio.core.NonTerminal;
import finio.core.pop.NonTerminalPopulator;
import replete.plugins.ExtensionPoint;

public interface PostFieldsModifier extends ExtensionPoint {

    public boolean canHandle(Object O);

    public void modify(NonTerminal M, Object O,
                       NonTerminalPopulator populator,
                       Set<Object> visited, KeyPath P,
                       PopulateParamsProvider params);

}
