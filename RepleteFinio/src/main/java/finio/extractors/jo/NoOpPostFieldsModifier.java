package finio.extractors.jo;

import java.util.Set;

import finio.core.KeyPath;
import finio.core.NonTerminal;
import finio.core.pop.NonTerminalPopulator;

public class NoOpPostFieldsModifier implements PostFieldsModifier {

    @Override
    public boolean canHandle(Object O) {
        return true;
    }

    @Override
    public void modify(NonTerminal M, Object O,
                       NonTerminalPopulator populator,
                       Set<Object> visited, KeyPath P,
                       PopulateParamsProvider params) {
        // Do nothing
    }

    @Override
    public String toString() {
        return "No-Op Post Fields Modifier";
    }

}
